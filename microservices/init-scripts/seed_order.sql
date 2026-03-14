USE csms_order;
GO

IF NOT EXISTS (SELECT 1 FROM orders WHERE status = 'PENDING')
BEGIN
    INSERT INTO orders (user_id, order_date, status, total_amount, note, version, created_at, updated_at)
    VALUES (2, GETDATE(), 'PENDING', 7.00, 'Test Order 1', 0, GETDATE(), GETDATE());
    
    -- Using IDENT_CURRENT to get the newly inserted order id
    DECLARE @OrderID INT = IDENT_CURRENT('orders');
    
    INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
    VALUES (@OrderID, 1, 'Black Coffee', 1, 2.50, 2.50, GETDATE(), GETDATE());
    
    INSERT INTO order_items (order_id, product_id, product_name, quantity, unit_price, subtotal, created_at, updated_at)
    VALUES (@OrderID, 3, 'Chocolate Cake', 1, 4.50, 4.50, GETDATE(), GETDATE());
END
GO
