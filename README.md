# ☕ CSMS Backend - Coffee Shop Management System

**Technology Stack:** Spring Boot 3.x + SQL Server  
**Version:** 1.0.0  
**Status:** In Development

---

## 📋 OVERVIEW

CSMS Backend is a RESTful API service for managing coffee shop operations including:
- Product & Ingredient Management
- Order Processing
- Employee & Attendance Tracking
- Salary Management
- Daily Reports & Analytics
- User Authentication & Authorization

---

## 🚀 QUICK START

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- SQL Server 2019 or higher
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/your-org/CSMS-Backend.git
cd CSMS-Backend
```

2. **Setup SQL Server Database**
```sql
CREATE DATABASE CSMS_DB;
```

3. **Configure application.properties**
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1434;databaseName=CSMS_DB
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Build the project**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## 📚 API DOCUMENTATION

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user
- `POST /api/auth/change-password` - Change password

#### Products
- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

#### Ingredients
- `GET /api/ingredients` - Get all ingredients
- `POST /api/ingredients` - Create ingredient
- `POST /api/ingredients/transactions` - Record transaction
- `GET /api/ingredients/transactions` - Get transaction history

#### Orders
- `GET /api/orders` - Get all orders (with filters)
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}/status` - Update order status

#### Employees
- `GET /api/employees` - Get all employees
- `POST /api/employees` - Create employee
- `GET /api/employees/{id}/attendance` - Get attendance
- `POST /api/employees/{id}/attendance` - Record attendance
- `GET /api/employees/{id}/salary` - Get salary history

#### Reports
- `GET /api/reports/daily` - Get daily reports
- `POST /api/reports/daily` - Create daily report
- `GET /api/reports/revenue` - Get revenue report

---

## 🗄️ DATABASE SCHEMA

### Main Tables
- `Users` - User accounts and authentication
- `Employees` - Employee information
- `Products` - Product catalog
- `Ingredients` - Ingredient inventory
- `Product_Ingredients` - Product-ingredient mapping
- `Orders` - Customer orders
- `Order_Items` - Order line items
- `Ingredient_Transactions` - Ingredient stock movements
- `Attendance` - Employee attendance records
- `Salaries` - Employee salary records
- `Salary_Updated_History` - Salary change history
- `Daily_Reports` - Daily business reports

See `BACKEND_IMPLEMENTATION_PLAN.md` for detailed schema.

---

## 🔒 SECURITY

### Authentication
- JWT-based authentication
- Token expiration: 24 hours
- Secure password hashing (BCrypt)

### Authorization
- Role-based access control (RBAC)
- Roles: `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_STAFF`

### CORS
- Configured for frontend at `http://localhost:3000`
- Customizable in `CorsConfig.java`

---

## 🧪 TESTING

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=ProductServiceTest
```

### Generate test coverage report
```bash
mvn jacoco:report
```

---

## 📦 PROJECT STRUCTURE

```
src/
├── main/
│   ├── java/com/csms/backend/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic
│   │   ├── security/        # Security components
│   │   ├── exception/       # Exception handling
│   │   └── util/            # Utility classes
│   └── resources/
│       ├── application.properties
│       └── application-dev.properties
└── test/                    # Unit & integration tests
```

---

## 🛠️ DEVELOPMENT

### Code Style
- Follow Java naming conventions
- Use Lombok for boilerplate code
- Write JavaDoc for public methods
- Keep methods small and focused

### Git Workflow
1. Create feature branch: `git checkout -b feature/your-feature`
2. Make changes and commit: `git commit -m "Add feature"`
3. Push to remote: `git push origin feature/your-feature`
4. Create Pull Request

### Database Migrations
- Use Hibernate auto-update for development
- Use Flyway/Liquibase for production

---

## 🚀 DEPLOYMENT

### Build for production
```bash
mvn clean package -DskipTests
```

### Run JAR file
```bash
java -jar target/csms-backend-1.0.0.jar
```

### Docker (Optional)
```bash
docker build -t csms-backend .
docker run -p 8080:8080 csms-backend
```

---

## 📊 MONITORING

### Actuator Endpoints
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

---

## 🐛 TROUBLESHOOTING

### Common Issues

**Database connection failed**
- Check SQL Server is running
- Verify connection string in `application.properties`
- Ensure database exists

**Port 8080 already in use**
- Change port in `application.properties`: `server.port=8081`

**JWT token expired**
- Login again to get new token
- Adjust expiration time in properties

---

## 📝 CHANGELOG

### Version 1.0.0 (In Development)
- Initial project setup
- Core entity models
- Authentication & authorization
- CRUD operations for all entities
- Reporting functionality

---

## 👥 TEAM

- **Backend Lead:** [Name]
- **Database Admin:** [Name]
- **QA Engineer:** [Name]

---

## 📄 LICENSE

Copyright © 2025 CSMS Team. All rights reserved.

---

## 📞 SUPPORT

For issues and questions:
- Create an issue on GitHub
- Email: support@csms.com
- Documentation: See `BACKEND_IMPLEMENTATION_PLAN.md`

---

**Happy Coding! ☕**
