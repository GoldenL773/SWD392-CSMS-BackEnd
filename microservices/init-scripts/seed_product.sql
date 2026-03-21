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

-- 2. Safe reseed: reseed each table to MAX(id) to avoid PK collisions
DECLARE @maxId BIGINT;

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[combo_items];
DBCC CHECKIDENT ('[dbo].[combo_items]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[product_variants];
DBCC CHECKIDENT ('[dbo].[product_variants]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[recipe_ingredients];
DBCC CHECKIDENT ('[dbo].[recipe_ingredients]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[recipes];
DBCC CHECKIDENT ('[dbo].[recipes]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[combos];
DBCC CHECKIDENT ('[dbo].[combos]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[promotions];
DBCC CHECKIDENT ('[dbo].[promotions]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[products];
DBCC CHECKIDENT ('[dbo].[products]', RESEED, @maxId);

SELECT @maxId = ISNULL(MAX(id), 0) FROM [dbo].[categories];
DBCC CHECKIDENT ('[dbo].[categories]', RESEED, @maxId);
GO