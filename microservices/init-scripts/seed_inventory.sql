USE csms_inventory;
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Coffee Beans')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Coffee Beans', 'kg', 50.00, 10.00, 15.00, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Milk')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Milk', 'liters', 20.00, 5.00, 1.50, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Teabags')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Teabags', 'pieces', 200, 50, 0.10, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Chocolate Syrup')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Chocolate Syrup', 'ml', 5000, 1000, 0.05, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Flour')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Flour', 'kg', 25.00, 5.00, 1.20, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Butter')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Butter', 'kg', 5.00, 1.00, 8.00, 0, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Eggs')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Eggs', 'pieces', 60, 12, 0.20, 0, GETDATE(), GETDATE());
END
GO
