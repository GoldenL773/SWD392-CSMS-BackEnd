USE [csms_product]
GO

-- 1. Optional: Delete data if you want a clean state every time
-- DELETE FROM [dbo].[combo_items];
-- DELETE FROM [dbo].[product_variants];
-- ... (rest of deletes)
GO

-- Categories
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Coffee')
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Coffee', 'All coffee based drinks', GETDATE(), GETDATE());
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Food')
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Food', 'Snacks and meals', GETDATE(), GETDATE());
IF NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Pastry')
    INSERT INTO categories (name, description, created_at, updated_at) VALUES ('Pastry', 'Cakes and breads', GETDATE(), GETDATE());
GO

-- 2. Reset the Identity (ID) seeds back to 0 so the next inserts start at 1
DBCC CHECKIDENT ('[dbo].[combo_items]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[product_variants]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[recipe_ingredients]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[recipes]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[combos]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[promotions]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[products]', RESEED, 0);
DBCC CHECKIDENT ('[dbo].[categories]', RESEED, 0);
GO