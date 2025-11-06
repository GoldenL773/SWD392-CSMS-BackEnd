# 🚀 CSMS Backend - Quick Start Guide

**Status:** Ready for Implementation  
**Last Updated:** November 6, 2025

---

## ✅ COMPLETED SETUP

### 1. Dependencies Added ✅
- JWT libraries (jjwt-api, jjwt-impl, jjwt-jackson) v0.11.5
- SpringDoc OpenAPI v2.2.0
- All Spring Boot starters configured

### 2. Project Structure ✅
- Maven project initialized
- pom.xml configured with all dependencies
- Git repository ready

---

## 🎯 IMMEDIATE NEXT STEPS

### Step 1: Create Main Application Class

**File:** `src/main/java/fu/se/swd392csms/Swd392CsmsApplication.java`

```java
package fu.se.swd392csms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Swd392CsmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(Swd392CsmsApplication.java, args);
    }
}
```

---

### Step 2: Configure application.properties

**File:** `src/main/resources/application.properties`

```properties
# Application Name
spring.application.name=CSMS-Backend
server.port=8080

# SQL Server Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CSMS_DB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR_PASSWORD_HERE
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# JWT Configuration
jwt.secret=csms-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm
jwt.expiration=86400000

# Logging
logging.level.fu.se.swd392csms=DEBUG
logging.level.org.hibernate.SQL=DEBUG

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Active Profile
spring.profiles.active=dev
```

---

### Step 3: Create Package Structure

Create these directories under `src/main/java/fu/se/swd392csms/`:

```
fu/se/swd392csms/
├── config/
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── entity/
├── repository/
├── service/
│   └── impl/
├── security/
├── exception/
└── util/
```

---

## 📋 ENTITY IMPLEMENTATION ORDER

### Priority 1: Core Security Entities

#### 1. Role.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String name; // ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF
}
```

#### 2. User.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password; // BCrypt hashed
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    @OneToOne(mappedBy = "user")
    private Employee employee;
}
```

#### 3. Employee.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    private LocalDate dob;
    
    private String gender;
    
    @Column(unique = true)
    private String phone;
    
    @Column(nullable = false)
    private String position;
    
    @Column(nullable = false)
    private LocalDate hireDate;
    
    @Column(nullable = false)
    private String status; // Active, Inactive
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
```

---

### Priority 2: Product & Inventory Entities

#### 4. Product.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String status; // Available, Unavailable
}
```

#### 5. Ingredient.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String unit;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal pricePerUnit;
}
```

#### 6. ProductIngredient.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal quantityRequired;
}
```

---

### Priority 3: Order Entities

#### 7. Order.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false)
    private String status; // Pending, Preparing, Completed, Cancelled
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
}
```

#### 8. OrderItem.java
```java
package fu.se.swd392csms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
}
```

---

### Priority 4: Employee Management Entities

#### 9. Attendance.java
#### 10. Salary.java
#### 11. SalaryUpdatedHistory.java

### Priority 5: Reporting Entities

#### 12. IngredientTransaction.java
#### 13. DailyReport.java

---

## 🔧 CONFIGURATION CLASSES

### 1. CorsConfig.java
```java
package fu.se.swd392csms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

---

## 📝 TESTING THE SETUP

### 1. Build the project:
```bash
mvn clean install
```

### 2. Run the application:
```bash
mvn spring-boot:run
```

### 3. Verify:
- Application starts on port 8080
- Swagger UI available at: `http://localhost:8080/swagger-ui.html`
- API docs at: `http://localhost:8080/api-docs`

---

## 🚨 IMPORTANT NOTES

1. **Database Setup Required:**
   - Create `CSMS_DB` database in SQL Server
   - Update password in application.properties

2. **Entity Relationships:**
   - All relationships follow ENTITIES.md specification
   - Use `@ManyToOne`, `@OneToMany`, `@OneToOne` as defined

3. **Lombok Usage:**
   - `@Data` for getters/setters
   - `@Builder` for builder pattern
   - `@NoArgsConstructor` & `@AllArgsConstructor` for constructors

4. **Naming Conventions:**
   - Package: `fu.se.swd392csms`
   - Tables: lowercase with underscores
   - Columns: camelCase in Java, snake_case in DB

---

## 📚 NEXT IMPLEMENTATION PHASES

### Phase 2: Repositories (Week 2)
- Create repository interfaces for all entities
- Add custom query methods
- Write repository tests

### Phase 3: Security (Week 3)
- Implement JWT token provider
- Create authentication filter
- Configure Spring Security
- Implement AuthController & AuthService

### Phase 4-8: Feature Modules
- Product & Ingredient management
- Order management
- Employee management
- Reporting & Dashboard

---

**Status:** 🟢 READY TO START IMPLEMENTATION  
**Next Action:** Create Main Application Class & application.properties

---

**Created by:** Development Team  
**Last Updated:** November 6, 2025
