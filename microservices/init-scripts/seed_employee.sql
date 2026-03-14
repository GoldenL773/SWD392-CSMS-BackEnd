USE csms_employee;
GO

IF NOT EXISTS (SELECT 1 FROM employees WHERE phone = '0123456789')
BEGIN
    -- Linking to user_id = 1 (admin)
    INSERT INTO employees (user_id, first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES (1, 'Admin', 'User', 'Manager', '2024-01-01', '0123456789', '123 Coffee St', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM employees WHERE phone = '0987654321')
BEGIN
    -- Linking to user_id = 2 (user)
    INSERT INTO employees (user_id, first_name, last_name, position, hire_date, phone, address, created_at, updated_at)
    VALUES (2, 'Staff', 'User', 'Barista', '2024-02-01', '0987654321', '456 Milk St', GETDATE(), GETDATE());
END
GO
