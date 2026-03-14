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

IF NOT EXISTS (SELECT * FROM categories WHERE name = 'Coffee')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Coffee', 'All coffee based drinks', GETDATE(), GETDATE());
    DECLARE @CatId INT = SCOPE_IDENTITY();
    
    INSERT INTO products (name, description, price, category_id, available, created_at, updated_at) 
    VALUES ('Espresso', 'Strong black coffee', 25000, @CatId, 1, GETDATE(), GETDATE());
    
    INSERT INTO products (name, description, price, category_id, available, created_at, updated_at) 
    VALUES ('Latte', 'Espresso with milk', 35000, @CatId, 1, GETDATE(), GETDATE());
END
GO


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
    
    -- Link products to ingredients (Assuming product IDs from Product Service: 1 = Espresso, 2 = Latte)
    -- Espresso needs 0.018 kg of Coffee Beans
    INSERT INTO product_ingredients (product_id, ingredient_id, quantity, created_at, updated_at) 
    VALUES (1, (SELECT id FROM ingredients WHERE name = 'Coffee Beans'), 0.018, GETDATE(), GETDATE());
    
    -- Latte needs 0.018 kg Coffee Beans and 0.2 liters Milk
    INSERT INTO product_ingredients (product_id, ingredient_id, quantity, created_at, updated_at) 
    VALUES (2, (SELECT id FROM ingredients WHERE name = 'Coffee Beans'), 0.018, GETDATE(), GETDATE());
    
    INSERT INTO product_ingredients (product_id, ingredient_id, quantity, created_at, updated_at) 
    VALUES (2, (SELECT id FROM ingredients WHERE name = 'Milk'), 0.2, GETDATE(), GETDATE());
END
GO


-- ==========================================
-- 4. EMPLOYEE SERVICE DATA MIGRATION
-- ==========================================
USE csms_employee;
GO

IF NOT EXISTS (SELECT * FROM employees WHERE user_id = 1) -- Assuming Admin has user_id = 1
BEGIN
    INSERT INTO employees (user_id, first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES (1, 'Admin', 'User', 'Manager', GETDATE(), '0123456789', '123 Main St', GETDATE(), GETDATE());
END
GO
