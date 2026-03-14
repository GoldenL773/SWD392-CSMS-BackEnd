USE csms_product;
GO

-- Insert default categories if they don't exist
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Beverages')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at)
    VALUES ('Beverages', 'Soft drinks, coffees, teas, beers, and ales', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Desserts')
BEGIN
    INSERT INTO categories (name, description, created_at, updated_at)
    VALUES ('Desserts', 'Desserts and sweets', GETDATE(), GETDATE());
END
GO

-- Insert default products if they don't exist
IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Black Coffee')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Black Coffee', 'Classic brewed black coffee', 2.50, (SELECT id FROM categories WHERE name = 'Beverages'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Cappuccino')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Cappuccino', 'Espresso-based coffee drink', 3.50, (SELECT id FROM categories WHERE name = 'Beverages'), 1, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM products WHERE name = 'Chocolate Cake')
BEGIN
    INSERT INTO products (name, description, price, category_id, available, version, created_at, updated_at)
    VALUES ('Chocolate Cake', 'Decadent chocolate layer cake', 4.50, (SELECT id FROM categories WHERE name = 'Desserts'), 1, 0, GETDATE(), GETDATE());
END
GO
