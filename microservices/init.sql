-- Create Databases for Microservices
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_auth') CREATE DATABASE csms_auth;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_product') CREATE DATABASE csms_product;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_inventory') CREATE DATABASE csms_inventory;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_employee') CREATE DATABASE csms_employee;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_order') CREATE DATABASE csms_order;
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'csms_report') CREATE DATABASE csms_report;
GO

-- ==========================================
-- 1. AUTH SERVICE DATA MIGRATION
-- ==========================================
USE csms_auth;
GO

-- Create Roles
IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_ADMIN')
    INSERT INTO roles (name, description, created_at, updated_at) VALUES ('ROLE_ADMIN', 'Administrator Role', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_MANAGER')
    INSERT INTO roles (name, description, created_at, updated_at) VALUES ('ROLE_MANAGER', 'Manager Role', GETDATE(), GETDATE());

IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_STAFF')
    INSERT INTO roles (name, description, created_at, updated_at) VALUES ('ROLE_STAFF', 'Staff Role', GETDATE(), GETDATE());

-- Create Admin User (Password is 'password' encoded with BCrypt)
IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, password, email, enabled, created_at, updated_at) 
    VALUES ('admin', '$2a$10$wY1twJwX.oE0qE0wVfK/..V/2wZ3jP4Vw/3k0jL9Q7QYqK2l0n9yO', 'admin@csms.com', 1, GETDATE(), GETDATE());
    
    DECLARE @AdminId INT = SCOPE_IDENTITY();
    DECLARE @AdminRoleId INT = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN');
    
    INSERT INTO user_roles (user_id, role_id) VALUES (@AdminId, @AdminRoleId);
END
GO


-- ==========================================
-- 2. PRODUCT SERVICE DATA MIGRATION
-- ==========================================
USE csms_product;
GO

-- Categories
IF NOT EXISTS (SELECT * FROM categories WHERE name = 'Coffee')
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Coffee', 'All coffee based drinks', GETDATE(), GETDATE());
IF NOT EXISTS (SELECT * FROM categories WHERE name = 'Food')
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Food', 'Snacks and meals', GETDATE(), GETDATE());
IF NOT EXISTS (SELECT * FROM categories WHERE name = 'Pastry')
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Pastry', 'Cakes and breads', GETDATE(), GETDATE());

-- Products
IF NOT EXISTS (SELECT * FROM products WHERE name = 'Latte')
BEGIN
    DECLARE @CoffeeCatId INT = (SELECT id FROM categories WHERE name = 'Coffee');
    
    INSERT INTO products (name, description, price, category_id, available, created_at, updated_at) 
    VALUES ('Latte', 'Espresso with steamed milk', 35000, @CoffeeCatId, 1, GETDATE(), GETDATE());
    DECLARE @LatteId INT = SCOPE_IDENTITY();

    -- Variants for Latte
    INSERT INTO product_variants (product_id, name, price, size, temperature, created_at, updated_at)
    VALUES (@LatteId, 'Small Latte', 35000, 'S', 'Hot', GETDATE(), GETDATE());
    INSERT INTO product_variants (product_id, name, price, size, temperature, created_at, updated_at)
    VALUES (@LatteId, 'Medium Latte', 45000, 'M', 'Hot', GETDATE(), GETDATE());
    INSERT INTO product_variants (product_id, name, price, size, temperature, created_at, updated_at)
    VALUES (@LatteId, 'Large Iced Latte', 55000, 'L', 'Cold', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT * FROM products WHERE name = 'Croissant')
BEGIN
    DECLARE @PastryCatId INT = (SELECT id FROM categories WHERE name = 'Pastry');
    INSERT INTO products (name, description, price, category_id, available, created_at, updated_at) 
    VALUES ('Croissant', 'Buttery flaky pastry', 20000, @PastryCatId, 1, GETDATE(), GETDATE());
END

-- Combos
IF NOT EXISTS (SELECT * FROM combos WHERE name = 'Morning Boost')
BEGIN
    INSERT INTO combos (name, description, price, status, created_at, updated_at)
    VALUES ('Morning Boost', 'Start your day with a Latte and Croissant', 45000, 'AVAILABLE', GETDATE(), GETDATE());
    DECLARE @ComboId INT = SCOPE_IDENTITY();
    
    DECLARE @LatteId INT = (SELECT id FROM products WHERE name = 'Latte');
    DECLARE @CroissantId INT = (SELECT id FROM products WHERE name = 'Croissant');
    
    INSERT INTO combo_items (combo_id, product_id, quantity) VALUES (@ComboId, @LatteId, 1);
    INSERT INTO combo_items (combo_id, product_id, quantity) VALUES (@ComboId, @CroissantId, 1);
END

-- Promotions
IF NOT EXISTS (SELECT * FROM promotions WHERE name = 'Grand Opening')
BEGIN
    INSERT INTO promotions (name, description, discount_type, discount_value, apply_to, target_id, start_date, end_date, status, created_at, updated_at)
    VALUES ('Grand Opening', '20% off on all orders', 'PERCENTAGE', 20.0, 'ALL', NULL, GETDATE(), DATEADD(month, 1, GETDATE()), 'ACTIVE', GETDATE(), GETDATE());
END


-- ==========================================
-- 3. INVENTORY SERVICE DATA MIGRATION
-- ==========================================
USE csms_inventory;
GO

IF NOT EXISTS (SELECT * FROM ingredients WHERE name = 'Coffee Beans')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, created_at, updated_at) 
    VALUES ('Coffee Beans', 'kg', 10.5, 2.0, 150000, GETDATE(), GETDATE());
    
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, created_at, updated_at) 
    VALUES ('Milk', 'liter', 20.0, 5.0, 30000, GETDATE(), GETDATE());
    
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, created_at, updated_at) 
    VALUES ('Flour', 'kg', 50.0, 10.0, 20000, GETDATE(), GETDATE());
    
    -- Recipes/Product Ingredients
    -- Latte (Product ID 1 assumed from above order)
    INSERT INTO product_ingredients (product_id, ingredient_id, quantity, created_at, updated_at) 
    VALUES (1, (SELECT id FROM ingredients WHERE name = 'Coffee Beans'), 0.018, GETDATE(), GETDATE());
    INSERT INTO product_ingredients (product_id, ingredient_id, quantity, created_at, updated_at) 
    VALUES (1, (SELECT id FROM ingredients WHERE name = 'Milk'), 0.2, GETDATE(), GETDATE());
    
    -- Croissant (Product ID 2 assumed)
    INSERT INTO product_ingredients (product_id, ingredient_id, quantity, created_at, updated_at) 
    VALUES (2, (SELECT id FROM ingredients WHERE name = 'Flour'), 0.1, GETDATE(), GETDATE());
END
GO


-- ==========================================
-- 4. EMPLOYEE SERVICE DATA MIGRATION
-- ==========================================
USE csms_employee;
GO

IF NOT EXISTS (SELECT * FROM employees WHERE user_id = 1)
BEGIN
    INSERT INTO employees (user_id, first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES (1, 'Admin', 'User', 'Manager', GETDATE(), '0123456789', '123 Main St', GETDATE(), GETDATE());
END
GO
