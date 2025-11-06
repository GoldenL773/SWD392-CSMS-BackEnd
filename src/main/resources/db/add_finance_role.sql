-- Add ROLE_FINANCE to the roles table
-- This script adds the Finance/Accountant role to the system

-- Check if ROLE_FINANCE already exists, if not insert it
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_FINANCE')
BEGIN
    INSERT INTO roles (name) VALUES ('ROLE_FINANCE');
    PRINT 'ROLE_FINANCE added successfully';
END
ELSE
BEGIN
    PRINT 'ROLE_FINANCE already exists';
END
GO

-- Optional: Create a test finance user (uncomment if needed)
-- IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'finance')
-- BEGIN
--     -- Password is 'finance123' (BCrypt hash)
--     INSERT INTO users (username, password) 
--     VALUES ('finance', '$2a$10$YourBCryptHashHere');
--     
--     DECLARE @userId BIGINT = SCOPE_IDENTITY();
--     DECLARE @roleId INT = (SELECT id FROM roles WHERE name = 'ROLE_FINANCE');
--     
--     INSERT INTO user_roles (user_id, role_id) VALUES (@userId, @roleId);
--     
--     INSERT INTO employees (full_name, position, hire_date, status, user_id)
--     VALUES ('Finance Manager', 'Accountant', GETDATE(), 'Active', @userId);
--     
--     PRINT 'Finance user created successfully';
-- END
-- GO

