USE csms_product;
GO

-- Insert default categories if they don't exist
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Coffee')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at)
    VALUES ('Coffee', 'Freshly brewed coffee and espresso drinks', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Tea')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at)
    VALUES ('Tea', 'Hot and cold tea varieties', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Cakes')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at)
    VALUES ('Cakes', 'Decadent cakes and desserts', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Pastries')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at)
    VALUES ('Pastries', 'Freshly baked pastries and croissants', GETDATE(), GETDATE());
END
GO

-- Insert default products if they don't exist
-- Coffee
IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Black Coffee')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Black Coffee', 'Classic brewed black coffee', 2.50, (SELECT id FROM categories WHERE name = 'Coffee'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cappuccino')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Cappuccino', 'Rich espresso with steamed milk and foam', 3.50, (SELECT id FROM categories WHERE name = 'Coffee'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Latte')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Latte', 'Espresso with steamed milk and a thin layer of foam', 3.75, (SELECT id FROM categories WHERE name = 'Coffee'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Mocha')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Mocha', 'Espresso with chocolate and steamed milk', 4.00, (SELECT id FROM categories WHERE name = 'Coffee'), 1, 0, GETDATE(), GETDATE());
END
GO

-- Tea
IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Green Tea')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Green Tea', 'Refreshing organic green tea', 2.25, (SELECT id FROM categories WHERE name = 'Tea'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'English Breakfast')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('English Breakfast', 'Robust black tea', 2.25, (SELECT id FROM categories WHERE name = 'Tea'), 1, 0, GETDATE(), GETDATE());
END
GO

-- Cakes
IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Chocolate Cake')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Chocolate Cake', 'Decadent chocolate layer cake', 4.50, (SELECT id FROM categories WHERE name = 'Cakes'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cheesecake')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Cheesecake', 'Classic New York style cheesecake', 4.75, (SELECT id FROM categories WHERE name = 'Cakes'), 1, 0, GETDATE(), GETDATE());
END
GO

-- Pastries
IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Croissant')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Croissant', 'Buttery and flaky classic croissant', 2.75, (SELECT id FROM categories WHERE name = 'Pastries'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Pain au Chocolat')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Pain au Chocolat', 'Classic pastry with chocolate filling', 3.00, (SELECT id FROM categories WHERE name = 'Pastries'), 1, 0, GETDATE(), GETDATE());
END
GO
