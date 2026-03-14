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

IF NOT EXISTS (SELECT 1 FROM ingredients WHERE name = 'Sugar')
BEGIN
    INSERT INTO ingredients (name, unit, current_stock, min_stock, unit_cost, version, created_at, updated_at)
    VALUES ('Sugar', 'kg', 10.00, 2.00, 2.00, 0, GETDATE(), GETDATE());
END
GO
