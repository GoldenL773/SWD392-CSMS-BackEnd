USE csms_order;
GO

-- Completed Orders
DECLARE @Order1 INT;
INSERT INTO orders (user_id, order_date, status, total_amount, note, version, created_at, updated_at)
VALUES (2, DATEADD(DAY, -1, GETDATE()), 'COMPLETED', 10.25, 'Yesterday''s order', 0, GETDATE(), GETDATE());
SET @Order1 = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
VALUES (@Order1, 2, 'Cappuccino', 2, 3.50, 7.00, GETDATE(), GETDATE());
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
VALUES (@Order1, 5, 'Green Tea', 1, 2.25, 2.25, GETDATE(), GETDATE());
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
VALUES (@Order1, 10, 'Pain au Chocolat', 1, 3.00, 3.00, GETDATE(), GETDATE());

DECLARE @Order2 INT;
INSERT INTO orders (user_id, order_date, status, total_amount, note, version, created_at, updated_at)
VALUES (2, DATEADD(HOUR, -2, GETDATE()), 'COMPLETED', 6.50, 'Morning rush', 0, GETDATE(), GETDATE());
SET @Order2 = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
VALUES (@Order2, 3, 'Latte', 1, 3.75, 3.75, GETDATE(), GETDATE());
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
VALUES (@Order2, 9, 'Croissant', 1, 2.75, 2.75, GETDATE(), GETDATE());

-- More pending orders
INSERT INTO orders (user_id, order_date, status, total_amount, note, version, created_at, updated_at)
VALUES (2, GETDATE(), 'PENDING', 4.00, 'Iced Mocha', 0, GETDATE(), GETDATE());
DECLARE @Order3 INT = SCOPE_IDENTITY();
INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
VALUES (@Order3, 4, 'Mocha', 1, 4.00, 4.00, GETDATE(), GETDATE());
GO
