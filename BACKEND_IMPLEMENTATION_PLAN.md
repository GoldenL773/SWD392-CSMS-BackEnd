# 🚀 CSMS Backend Implementation Plan

**Technology Stack:** Spring Boot + SQL Server  
**Date:** November 6, 2025  
**Status:** Planning Phase

---

## 📋 TABLE OF CONTENTS

1. [Project Structure](#project-structure)
2. [Database Schema](#database-schema)
3. [API Endpoints](#api-endpoints)
4. [Security & Authentication](#security--authentication)
5. [Implementation Phases](#implementation-phases)
6. [Dependencies](#dependencies)

---

## 🏗️ PROJECT STRUCTURE

```
SWD392-CSMS-BackEnd/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/csms/backend/
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── CorsConfig.java
│   │   │       │   └── SwaggerConfig.java
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── ProductController.java
│   │   │       │   ├── IngredientController.java
│   │   │       │   ├── OrderController.java
│   │   │       │   ├── EmployeeController.java
│   │   │       │   ├── ReportController.java
│   │   │       │   └── DashboardController.java
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   │   ├── LoginRequest.java
│   │   │       │   │   ├── CreateProductRequest.java
│   │   │       │   │   ├── CreateOrderRequest.java
│   │   │       │   │   └── ...
│   │   │       │   └── response/
│   │   │       │       ├── LoginResponse.java
│   │   │       │       ├── ProductResponse.java
│   │   │       │       └── ...
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   ├── Employee.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── Ingredient.java
│   │   │       │   ├── Order.java
│   │   │       │   ├── OrderItem.java
│   │   │       │   ├── ProductIngredient.java
│   │   │       │   ├── IngredientTransaction.java
│   │   │       │   ├── Attendance.java
│   │   │       │   ├── Salary.java
│   │   │       │   ├── SalaryUpdatedHistory.java
│   │   │       │   └── DailyReport.java
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── EmployeeRepository.java
│   │   │       │   ├── ProductRepository.java
│   │   │       │   ├── IngredientRepository.java
│   │   │       │   ├── OrderRepository.java
│   │   │       │   └── ...
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── ProductService.java
│   │   │       │   ├── IngredientService.java
│   │   │       │   ├── OrderService.java
│   │   │       │   ├── EmployeeService.java
│   │   │       │   ├── ReportService.java
│   │   │       │   └── ...
│   │   │       ├── security/
│   │   │       │   ├── JwtTokenProvider.java
│   │   │       │   ├── JwtAuthenticationFilter.java
│   │   │       │   └── UserDetailsServiceImpl.java
│   │   │       ├── exception/
│   │   │       │   ├── GlobalExceptionHandler.java
│   │   │       │   ├── ResourceNotFoundException.java
│   │   │       │   └── BadRequestException.java
│   │   │       └── util/
│   │   │           ├── DateUtil.java
│   │   │           └── ValidationUtil.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/
│           └── com/csms/backend/
│               ├── controller/
│               ├── service/
│               └── repository/
├── pom.xml
└── README.md
```

---

## 🗄️ DATABASE SCHEMA

### SQL Server Database: `CSMS_DB`

#### 1. Users Table
```sql
CREATE TABLE Users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL, -- ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF
    employee_id BIGINT UNIQUE,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (employee_id) REFERENCES Employees(id)
);
```

#### 2. Employees Table
```sql
CREATE TABLE Employees (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    full_name NVARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    gender NVARCHAR(10) NOT NULL,
    phone NVARCHAR(15) NOT NULL,
    position NVARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL, -- ACTIVE, INACTIVE
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
```

#### 3. Products Table
```sql
CREATE TABLE Products (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    category NVARCHAR(50) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL, -- AVAILABLE, UNAVAILABLE
    description NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
```

#### 4. Ingredients Table
```sql
CREATE TABLE Ingredients (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    unit NVARCHAR(20) NOT NULL,
    quantity DECIMAL(18,2) NOT NULL,
    reorder_level DECIMAL(18,2) NOT NULL,
    supplier NVARCHAR(100),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
```

#### 5. ProductIngredients Table (Many-to-Many)
```sql
CREATE TABLE Product_Ingredients (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    product_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    quantity_required DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES Products(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(id) ON DELETE CASCADE,
    UNIQUE(product_id, ingredient_id)
);
```

#### 6. Orders Table
```sql
CREATE TABLE Orders (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_date DATETIME2 DEFAULT GETDATE(),
    total_amount DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL, -- PENDING, PREPARING, COMPLETED, CANCELLED
    employee_id BIGINT NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (employee_id) REFERENCES Employees(id)
);
```

#### 7. OrderItems Table
```sql
CREATE TABLE Order_Items (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(id)
);
```

#### 8. IngredientTransactions Table
```sql
CREATE TABLE Ingredient_Transactions (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    ingredient_id BIGINT NOT NULL,
    type NVARCHAR(20) NOT NULL, -- IMPORT, EXPORT
    quantity DECIMAL(18,2) NOT NULL,
    transaction_date DATETIME2 DEFAULT GETDATE(),
    employee_id BIGINT NOT NULL,
    notes NVARCHAR(500),
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(id),
    FOREIGN KEY (employee_id) REFERENCES Employees(id)
);
```

#### 9. Attendance Table
```sql
CREATE TABLE Attendance (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    check_in_time TIME,
    check_out_time TIME,
    working_hours DECIMAL(5,2) NOT NULL,
    status NVARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LATE
    FOREIGN KEY (employee_id) REFERENCES Employees(id),
    UNIQUE(employee_id, date)
);
```

#### 10. Salaries Table
```sql
CREATE TABLE Salaries (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    employee_id BIGINT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    base_salary DECIMAL(18,2) NOT NULL,
    bonus DECIMAL(18,2) DEFAULT 0,
    deduction DECIMAL(18,2) DEFAULT 0,
    total_salary DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL, -- PENDING, PAID
    created_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (employee_id) REFERENCES Employees(id),
    UNIQUE(employee_id, month, year)
);
```

#### 11. SalaryUpdatedHistory Table
```sql
CREATE TABLE Salary_Updated_History (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    employee_id BIGINT NOT NULL,
    old_salary DECIMAL(18,2) NOT NULL,
    new_salary DECIMAL(18,2) NOT NULL,
    updated_by BIGINT NOT NULL,
    updated_at DATETIME2 DEFAULT GETDATE(),
    reason NVARCHAR(500),
    FOREIGN KEY (employee_id) REFERENCES Employees(id),
    FOREIGN KEY (updated_by) REFERENCES Users(id)
);
```

#### 12. DailyReports Table
```sql
CREATE TABLE Daily_Reports (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    report_date DATE UNIQUE NOT NULL,
    total_orders INT NOT NULL,
    total_revenue DECIMAL(18,2) NOT NULL,
    total_ingredient_cost DECIMAL(18,2) NOT NULL,
    total_working_hours DECIMAL(18,2) NOT NULL,
    notes NVARCHAR(1000),
    created_at DATETIME2 DEFAULT GETDATE()
);
```

---

## 🔌 API ENDPOINTS

### Base URL: `http://localhost:8080/api`

### 1. Authentication APIs

#### POST `/auth/login`
**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ROLE_ADMIN",
    "employee": {
      "id": 1,
      "fullName": "John Doe",
      "position": "Manager"
    }
  }
}
```

#### POST `/auth/logout`
**Headers:** `Authorization: Bearer {token}`

#### GET `/auth/me`
**Headers:** `Authorization: Bearer {token}`

#### POST `/auth/change-password`
**Request:**
```json
{
  "oldPassword": "old123",
  "newPassword": "new123"
}
```

---

### 2. Product APIs

#### GET `/products`
**Query Params:** `?category=Coffee&status=AVAILABLE`

#### GET `/products/{id}`

#### POST `/products`
**Request:**
```json
{
  "name": "Cappuccino",
  "category": "Coffee",
  "price": 45000,
  "status": "AVAILABLE",
  "description": "Classic Italian coffee",
  "ingredients": [
    {
      "ingredientId": 1,
      "quantityRequired": 30
    }
  ]
}
```

#### PUT `/products/{id}`

#### DELETE `/products/{id}`

---

### 3. Ingredient APIs

#### GET `/ingredients`

#### GET `/ingredients/{id}`

#### POST `/ingredients`
**Request:**
```json
{
  "name": "Coffee Beans",
  "unit": "g",
  "quantity": 5000,
  "reorderLevel": 1000,
  "supplier": "Vietnam Coffee Co."
}
```

#### PUT `/ingredients/{id}`

#### DELETE `/ingredients/{id}`

#### POST `/ingredients/transactions`
**Request:**
```json
{
  "ingredientId": 1,
  "type": "IMPORT",
  "quantity": 1000,
  "notes": "Monthly stock"
}
```

#### GET `/ingredients/transactions`
**Query Params:** `?startDate=2025-01-01&endDate=2025-01-31`

---

### 4. Order APIs

#### GET `/orders`
**Query Params:** `?status=PENDING&employeeId=1&startDate=2025-01-01&endDate=2025-01-31&sortBy=date&sortOrder=desc`

#### GET `/orders/{id}`

#### POST `/orders`
**Request:**
```json
{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 45000
    }
  ],
  "totalAmount": 90000
}
```

#### PUT `/orders/{id}/status`
**Request:**
```json
{
  "status": "COMPLETED"
}
```

---

### 5. Employee APIs

#### GET `/employees`

#### GET `/employees/{id}`

#### POST `/employees`
**Request:**
```json
{
  "fullName": "Jane Smith",
  "dob": "1995-05-15",
  "gender": "Female",
  "phone": "0123456789",
  "position": "Barista",
  "hireDate": "2025-01-01",
  "salary": 8000000,
  "status": "ACTIVE",
  "username": "jane.smith",
  "password": "password123",
  "role": "ROLE_STAFF"
}
```

#### PUT `/employees/{id}`

#### DELETE `/employees/{id}`

#### GET `/employees/{id}/attendance`
**Query Params:** `?month=1&year=2025`

#### POST `/employees/{id}/attendance`
**Request:**
```json
{
  "date": "2025-01-15",
  "checkInTime": "08:00:00",
  "checkOutTime": "17:00:00",
  "workingHours": 8.5,
  "status": "PRESENT"
}
```

#### GET `/employees/{id}/salary`
**Query Params:** `?year=2025`

#### POST `/employees/{id}/salary`
**Request:**
```json
{
  "month": 1,
  "year": 2025,
  "baseSalary": 8000000,
  "bonus": 500000,
  "deduction": 0,
  "totalSalary": 8500000
}
```

---

### 6. Report APIs

#### GET `/reports/daily`
**Query Params:** `?startDate=2025-01-01&endDate=2025-01-31`

#### GET `/reports/daily/{date}`

#### POST `/reports/daily`
**Request:**
```json
{
  "reportDate": "2025-01-15",
  "totalOrders": 45,
  "totalRevenue": 15000000,
  "totalIngredientCost": 5000000,
  "totalWorkingHours": 80,
  "notes": "Busy day with high sales"
}
```

#### GET `/reports/revenue`
**Query Params:** `?startDate=2025-01-01&endDate=2025-01-31&groupBy=day`

---

### 7. Dashboard APIs

#### GET `/dashboard/stats`
**Response:**
```json
{
  "todayOrders": 25,
  "todayRevenue": 8500000,
  "activeEmployees": 12,
  "lowStockIngredients": 3,
  "pendingOrders": 5
}
```

---

## 🔒 SECURITY & AUTHENTICATION

### JWT Configuration
```properties
jwt.secret=your-secret-key-here-min-256-bits
jwt.expiration=86400000  # 24 hours in milliseconds
```

### Security Rules
- **Public endpoints:** `/api/auth/login`, `/api/auth/register`
- **Protected endpoints:** All others require JWT token
- **Role-based access:**
  - `ROLE_ADMIN`: Full access
  - `ROLE_MANAGER`: Access to reports, employees, finance
  - `ROLE_STAFF`: Access to orders, products, menu

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

---

## 📦 DEPENDENCIES (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Spring Boot Starter Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Spring Boot Starter Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- SQL Server JDBC Driver -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Swagger/OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>

    <!-- Spring Boot DevTools -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Spring Boot Starter Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Spring Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## 🎯 IMPLEMENTATION PHASES

### Phase 1: Project Setup (Week 1)
- [x] Create Spring Boot project
- [ ] Setup SQL Server database
- [ ] Configure application.properties
- [ ] Setup project structure
- [ ] Add dependencies
- [ ] Create database schema
- [ ] Setup Git repository

### Phase 2: Core Entities & Repositories (Week 2)
- [ ] Create all entity classes
- [ ] Create all repository interfaces
- [ ] Add database migrations
- [ ] Create seed data
- [ ] Write repository tests

### Phase 3: Authentication & Security (Week 3)
- [ ] Implement JWT token provider
- [ ] Create authentication filter
- [ ] Implement UserDetailsService
- [ ] Create AuthController
- [ ] Create AuthService
- [ ] Add security configuration
- [ ] Test authentication flow

### Phase 4: Product & Ingredient Management (Week 4)
- [ ] Create ProductController
- [ ] Create ProductService
- [ ] Create IngredientController
- [ ] Create IngredientService
- [ ] Implement CRUD operations
- [ ] Add validation
- [ ] Write unit tests

### Phase 5: Order Management (Week 5)
- [ ] Create OrderController
- [ ] Create OrderService
- [ ] Implement order creation
- [ ] Implement order status updates
- [ ] Add order filtering
- [ ] Write integration tests

### Phase 6: Employee Management (Week 6)
- [ ] Create EmployeeController
- [ ] Create EmployeeService
- [ ] Implement attendance tracking
- [ ] Implement salary management
- [ ] Add employee CRUD
- [ ] Write tests

### Phase 7: Reporting & Analytics (Week 7)
- [ ] Create ReportController
- [ ] Create ReportService
- [ ] Implement daily reports
- [ ] Implement revenue reports
- [ ] Add report filtering
- [ ] Write tests

### Phase 8: Dashboard & Statistics (Week 8)
- [ ] Create DashboardController
- [ ] Implement statistics calculation
- [ ] Add caching for performance
- [ ] Optimize queries
- [ ] Write tests

### Phase 9: Testing & Bug Fixes (Week 9)
- [ ] Integration testing
- [ ] End-to-end testing
- [ ] Performance testing
- [ ] Bug fixes
- [ ] Code review

### Phase 10: Deployment & Documentation (Week 10)
- [ ] API documentation (Swagger)
- [ ] Deployment configuration
- [ ] Environment setup
- [ ] User manual
- [ ] Final testing

---

## 📝 APPLICATION.PROPERTIES

```properties
# Application
spring.application.name=CSMS-Backend
server.port=8080

# SQL Server Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CSMS_DB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# JWT Configuration
jwt.secret=your-secret-key-here-must-be-at-least-256-bits-long
jwt.expiration=86400000

# Logging
logging.level.com.csms.backend=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## 🧪 TESTING STRATEGY

### Unit Tests
- Service layer tests
- Repository tests
- Utility class tests

### Integration Tests
- Controller tests
- End-to-end API tests
- Database integration tests

### Test Coverage Goal
- Minimum 80% code coverage
- 100% critical path coverage

---

## 📊 PERFORMANCE CONSIDERATIONS

### Database Optimization
- Add indexes on frequently queried columns
- Use pagination for large datasets
- Implement caching for static data

### API Optimization
- Use DTOs to minimize data transfer
- Implement lazy loading
- Add response compression

### Caching Strategy
- Cache product catalog
- Cache employee list
- Cache daily reports

---

## 🚀 DEPLOYMENT

### Development Environment
- Local SQL Server
- Spring Boot DevTools
- H2 Console for testing

### Production Environment
- Azure SQL Database / AWS RDS
- Docker containerization
- CI/CD pipeline

---

## 📚 ADDITIONAL RESOURCES

### Documentation
- API Documentation: Swagger UI at `/swagger-ui.html`
- Database Schema: See `database-schema.sql`
- Postman Collection: See `CSMS-API.postman_collection.json`

### Tools
- **IDE:** IntelliJ IDEA / Eclipse
- **Database:** SQL Server Management Studio
- **API Testing:** Postman / Swagger UI
- **Version Control:** Git

---

**Status:** 📋 READY FOR IMPLEMENTATION  
**Next Step:** Setup Spring Boot project and database schema

---

**Created by:** Development Team  
**Last Updated:** November 6, 2025
