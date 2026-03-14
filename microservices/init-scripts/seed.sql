USE csms_auth;
GO

-- Insert default roles if they don't exist
IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_ADMIN', 'Administrator Role', GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER')
BEGIN
    INSERT INTO roles (name, description, created_at, updated_at)
    VALUES ('ROLE_USER', 'Standard User Role', GETDATE(), GETDATE());
END
GO

-- Insert default users if they don't exist
IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')
BEGIN
    INSERT INTO users (username, password, email, enabled, created_at, updated_at)
    VALUES ('admin', '$2a$10$bV9Y0GM1q/y3UcV3eEawhui4sdJH4R5cL/WVruqH9dG9gEnGKyhsW', 'admin@csms.com', 1, GETDATE(), GETDATE());
END
GO

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'user')
BEGIN
    INSERT INTO users (username, password, email, enabled, created_at, updated_at)
    VALUES ('user', '$2a$10$bV9Y0GM1q/y3UcV3eEawhui4sdJH4R5cL/WVruqH9dG9gEnGKyhsW', 'user@csms.com', 1, GETDATE(), GETDATE());
END
GO

-- Assign roles to users
IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin') AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'))
BEGIN
    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
END
GO

IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin') AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER'))
BEGIN
    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_USER';
END
GO

IF NOT EXISTS (SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'user') AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER'))
BEGIN
    INSERT INTO user_roles (user_id, role_id)
    SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'user' AND r.name = 'ROLE_USER';
END
GO
