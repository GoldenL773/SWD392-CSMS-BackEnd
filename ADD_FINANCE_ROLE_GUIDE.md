# Guide: Adding ROLE_FINANCE to the Database

## Overview
This guide explains how to add the ROLE_FINANCE role to your Spring Boot backend database and create a test user with Finance role.

---

## Method 1: Using SQL Script (Recommended)

### Step 1: Connect to your SQL Server database

Use SQL Server Management Studio (SSMS) or Azure Data Studio to connect to your database.

### Step 2: Run the SQL script

Execute the following SQL commands:

```sql
-- Add ROLE_FINANCE to the roles table
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

-- Verify the role was added
SELECT * FROM roles;
GO
```

### Step 3: Create a test Finance user (Optional)

```sql
-- Create a test finance user
-- Password: 'finance123' (BCrypt hash below)
DECLARE @financePassword NVARCHAR(255) = '$2a$10$YourBCryptHashHere';

IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'finance')
BEGIN
    -- Insert user
    INSERT INTO users (username, password) 
    VALUES ('finance', @financePassword);
    
    DECLARE @userId BIGINT = SCOPE_IDENTITY();
    DECLARE @roleId INT = (SELECT id FROM roles WHERE name = 'ROLE_FINANCE');
    
    -- Assign ROLE_FINANCE to user
    INSERT INTO user_roles (user_id, role_id) VALUES (@userId, @roleId);
    
    -- Create employee record
    INSERT INTO employees (full_name, position, phone, email, hire_date, salary, status, user_id)
    VALUES ('Finance Manager', 'Accountant', '0123456789', 'finance@csms.com', GETDATE(), 15000000, 'Active', @userId);
    
    PRINT 'Finance user created successfully';
    PRINT 'Username: finance';
    PRINT 'Password: finance123';
END
ELSE
BEGIN
    PRINT 'Finance user already exists';
END
GO
```

**Note:** To generate a BCrypt hash for the password, you can use an online BCrypt generator or run this Java code:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "finance123";
        String hashedPassword = encoder.encode(password);
        System.out.println("BCrypt hash: " + hashedPassword);
    }
}
```

---

## Method 2: Using Spring Boot Application (Alternative)

### Option A: Let Hibernate create the role automatically

If you have `spring.jpa.hibernate.ddl-auto=update` in your `application.properties`, you can create a `DataInitializer` component:

Create a new file: `src/main/java/fu/se/swd392csms/config/DataInitializer.java`

```java
package fu.se.swd392csms.config;

import fu.se.swd392csms.entity.Role;
import fu.se.swd392csms.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initializeRole("ROLE_ADMIN");
        initializeRole("ROLE_MANAGER");
        initializeRole("ROLE_STAFF");
        initializeRole("ROLE_FINANCE");
    }
    
    private void initializeRole(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(role);
            System.out.println("Created role: " + roleName);
        }
    }
}
```

### Option B: Use the registration endpoint

You can use the `/api/auth/register` endpoint to create a user with ROLE_FINANCE:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "finance",
    "password": "finance123",
    "fullName": "Finance Manager",
    "email": "finance@csms.com",
    "phone": "0123456789",
    "roles": ["ROLE_FINANCE"]
  }'
```

**Note:** This requires that ROLE_FINANCE already exists in the roles table.

---

## Method 3: Using Postman or Frontend

### Step 1: Ensure ROLE_FINANCE exists in the database

Run the SQL script from Method 1, Step 2.

### Step 2: Register a new user with ROLE_FINANCE

Use Postman or your frontend registration form to create a user with the Finance role.

**Request:**
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "finance",
  "password": "finance123",
  "fullName": "Finance Manager",
  "email": "finance@csms.com",
  "phone": "0123456789",
  "roles": ["ROLE_FINANCE"]
}
```

---

## Verification Steps

### 1. Verify ROLE_FINANCE exists in the database

```sql
SELECT * FROM roles WHERE name = 'ROLE_FINANCE';
```

Expected result:
```
id | name
---|-------------
4  | ROLE_FINANCE
```

### 2. Verify user has ROLE_FINANCE

```sql
SELECT u.username, r.name as role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'finance';
```

Expected result:
```
username | role_name
---------|-------------
finance  | ROLE_FINANCE
```

### 3. Test login with Finance user

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "finance",
    "password": "finance123"
  }'
```

Expected response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 4,
  "username": "finance",
  "fullName": "Finance Manager",
  "roles": ["ROLE_FINANCE"]
}
```

### 4. Test accessing Finance-only endpoints

```bash
# Get daily reports (requires ROLE_FINANCE)
curl -X GET http://localhost:8080/api/reports/daily \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Expected: 200 OK with report data

```bash
# Try to access employees (requires ROLE_ADMIN or ROLE_MANAGER)
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

Expected: 403 Forbidden (Finance role doesn't have access)

---

## Troubleshooting

### Issue: "Role not found" error when registering

**Solution:** Make sure ROLE_FINANCE exists in the roles table. Run the SQL script from Method 1, Step 2.

### Issue: 403 Forbidden when accessing endpoints

**Solution:** 
1. Verify the user has the correct role assigned
2. Check the JWT token is valid and not expired
3. Verify the endpoint's `@PreAuthorize` annotation includes the user's role

### Issue: Frontend not showing Finance menu items

**Solution:**
1. Clear browser localStorage and login again
2. Check browser console for errors
3. Verify the user data in localStorage includes the roles array

---

## Summary

After completing these steps, you should have:
- ✅ ROLE_FINANCE added to the roles table
- ✅ A test Finance user created (optional)
- ✅ Ability to login with Finance role
- ✅ Access to Finance-specific endpoints (Dashboard, Reports, Finance, Settings)
- ✅ Restricted access to other endpoints (Inventory, Employees, Menu)

---

## Next Steps

1. Test the Finance user in the frontend application
2. Verify role-based sidebar navigation works correctly
3. Test all API endpoints with different roles
4. Update user documentation with the new Finance role

