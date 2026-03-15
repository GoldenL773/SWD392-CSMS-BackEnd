USE csms_employee;
GO

IF NOT EXISTS (SELECT 1 FROM employees WHERE phone = '0123456789')
BEGIN
    -- Linking to user_id = 1 (admin)
    INSERT INTO employees (user_id, first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES (1, 'Admin', 'User', 'Manager', '2024-01-01', '0123456789', '123 Coffee St', GETDATE(), GETDATE());
END
GO

-- Link to user_id 3 (manager)
IF NOT EXISTS (SELECT 1 FROM employees WHERE first_name = 'James' AND last_name = 'Wilson')
BEGIN
    INSERT INTO employees (user_id, first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES (3, 'James', 'Wilson', 'Manager', '2024-03-01', '0112233445', '789 Oak Ave', GETDATE(), GETDATE());
END
GO

-- Staff members without specific user accounts yet
IF NOT EXISTS (SELECT 1 FROM employees WHERE first_name = 'Sarah' AND last_name = 'Miller')
BEGIN
    INSERT INTO employees (first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES ('Sarah', 'Miller', 'Barista', '2024-03-15', '0556677889', '101 Pine St', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM employees WHERE first_name = 'Robert' AND last_name = 'Taylor')
BEGIN
    INSERT INTO employees (first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES ('Robert', 'Taylor', 'Barista', '2024-04-01', '0667788990', '202 Maple Dr', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM employees WHERE first_name = 'Emily' AND last_name = 'Davis')
BEGIN
    INSERT INTO employees (first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES ('Emily', 'Davis', 'Cashier', '2024-04-10', '0778899001', '303 Birch Ln', GETDATE(), GETDATE());
END
GO
