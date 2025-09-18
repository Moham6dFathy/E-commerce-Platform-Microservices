# 🛒 E-Commerce Platform (Microservices Architecture)

## 🎯 Project Overview

This system is an **e-commerce platform** where:

- Users register/login with JWT.
- Products can be browsed, added to a **cart**.
- **Cart items expire after 24 hours**, extended to **1 week** when ordered, and set **indefinitely** when payment is completed.
- **RabbitMQ** ensures async communication between services (e.g., sending confirmation emails, updating stock, payment notifications).
- **Swagger/OpenAPI** documents all APIs.

---

## 🏗 System Components

### **Java Spring Boot Microservices**
1. **Product Service** (MySQL)
   - Manage products, categories, stock.
   - Publishes stock reservation events via RabbitMQ when orders are created.

2. **Order Service** (PostgreSQL)
   - Manages order lifecycle.
   - Validates JWT before placing an order.
   - Consumes stock reservation events from RabbitMQ.
   - Emits order status events (Created, Confirmed, Cancelled).

3. **Auth Service** (PostgreSQL, JWT)
   - User registration/login.
   - JWT token management.
   - User roles: `customer`, `admin`.

### **C# ASP.NET Core Microservices**
4. **Cart Service** (Redis)
   - User carts with TTL rules:
     - Default: 24 hours.
     - On order placed: extend TTL = 1 week.
     - On payment confirmed: TTL removed (indefinite).

5. **Payment Service** (SQL Server)
   - Validates JWT before payments.
   - Processes payments for orders.
   - Publishes payment success/failure events to RabbitMQ.

### **Infrastructure**
- **API Gateway (Spring Boot)**
  - Routes traffic.
  - Validates JWT tokens before Order & Payment APIs.
- **Service Discovery (Spring Boot Eureka)**
  - Registry of all microservices.
- **RabbitMQ**
  - Asynchronous event bus for:
    - Order → Product (stock reservation).
    - Order → Payment.
    - Payment → Order (update status).
    - Payment → Notification (email).

### **Frontend (Angular + PrimeNG)**
 **Authentication Flow:** Secure user registration and login with JWT token management.
- **Product Browsing:** View products by categories with search and filtering options.
- **Cart Management:**
  - Add/remove products from cart.
  - Real-time cart updates with item quantity management.
- **Checkout & Payments:**
  - Guided checkout process with order summary.
  - JWT validation ensures secure payments.
- **Responsive Design:** Optimized for desktop and mobile devices.
- **API Exploration:** Integrated **Swagger UI** for testing APIs directly from the browser.


## 🚀 Functionalities

- **Stock Reservation** via RabbitMQ ensures no overselling.
- **Asynchronous Payment Processing**.
- **Email/Notification Service** (consuming RabbitMQ events).
- **Swagger/OpenAPI** documentation for each service.

---

## ✅ Non-Functional Requirements

- **Security:** JWT validated at Gateway + Service-level filters.
- **Async Communication:** RabbitMQ ensures services are loosely coupled.
- **Resilience:** Orders remain valid even if Payment service is down temporarily.
- **Docs:** Swagger/OpenAPI auto-generated for all REST APIs.
- **Cart Persistence:** Redis TTL with business logic overrides.

---

## 📋 Prerequisites

Make sure you have installed:

- **Docker & Docker Compose**
- **Java 21+**
- **.NET 8 SDK**
- **Node.js 18+ & Angular CLI**
- **MySQL, PostgreSQL, Redis**
- **RabbitMQ**

---

## 🏃‍♂️ How to Run the Project

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/ecommerce-microservices.git
   cd ecommerce-microservices
   ```

2. **Start infrastructure (databases + RabbitMQ):**
   ```bash
   docker-compose up -d
   ```

3. **Run backend services:**
   - Java Spring Boot services:
     ```bash
     cd services/java-service
     mvn spring-boot:run
     ```
   - ASP.NET Core services:
     ```bash
     cd services/dotnet-service
     dotnet run
     ```

4. **Run frontend (Angular):**
   ```bash
   cd frontend
   npm install
   ng serve -o
   ```

5. **Access the system:**
   - Frontend: `http://localhost:4200`
   - API Gateway: `http://localhost:9000/api`
   - RabbitMQ Management: `http://localhost:15672`
   - Swagger Docs: `http://localhost:9000/swagger-ui.html`

---

## 📦 Deliverables

- 2 Java Spring Boot services (Product, Order).
- 1 Java Auth service with JWT.
- 2 ASP.NET Core services (Cart, Payment).
- API Gateway + Discovery Service.
- RabbitMQ integration.
- Angular + PrimeNG frontend.
- Swagger docs.
