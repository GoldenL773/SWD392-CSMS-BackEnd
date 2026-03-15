USE csms_auth;
GO

-- Insert default roles if they don't exist
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_ADMIN', 'Administrator Role', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_STAFF')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_STAFF', 'Shop Staff Role', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_BARISTA')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_BARISTA', 'Barista Role', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_MANAGER')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_MANAGER', 'Manager Role', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_FINANCE')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_FINANCE', 'Finance Role', GETDATE(), GETDATE());
END
GO

-- Standard users
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'manager')
BEGIN
    INSERT INTO users (username, password, email, enabled, created_at, updated_at)
    VALUES ('manager', '$2a$10$bV9Y0GM1q/y3UcV3eEawhui4sdJH4R5cL/WVruqH9dG9gEnGKyhsW', 'manager@csms.com', 1, GETDATE(), GETDATE());
    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'manager' AND r.name = 'ROLE_MANAGER';
END
GO

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff')
BEGIN
    INSERT INTO users (username, password, email, enabled, created_at, updated_at)
    VALUES ('staff', '$2a$10$bV9Y0GM1q/y3UcV3eEawhui4sdJH4R5cL/WVruqH9dG9gEnGKyhsW', 'staff@csms.com', 1, GETDATE(), GETDATE());
    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'staff' AND r.name = 'ROLE_STAFF';
END
GO
