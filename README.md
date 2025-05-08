# Shopping Cart Application

A Spring Boot-based E-commerce Shopping Cart application with secure user authentication, product management, and order processing capabilities.

## 🚀 Features

- *User Management*
  - Secure authentication and authorization
  - User profile management
  - Shopping cart functionality

- *Product Management*
  - Product catalog
  - Product categories
  - Inventory management

- *Order Processing*
  - Shopping cart management
  - Order placement
  - Order history tracking

## 🛠 Tech Stack

- *Backend:* Spring Boot 3.2.4
- *Security:* Spring Security
- *Database:* MySQL
- *Build Tool:* Maven
- *Java Version:* 17

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## 🔧 Installation & Setup

1. *Clone the repository*
   ```bash
   git clone https://github.com/Kishan-jethloja/ShoppingCart.git
   cd ShoppingCart
   ```

2. *Configure MySQL*
   - Create a new database
   ```sql
   CREATE DATABASE shopping_cart;
   ```
   - Update application.properties with your database credentials

3. *Build and Run*
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. *Access the application*
   ```
   http://localhost:8080
   ```

## 🔑 API Endpoints

### Authentication
- POST /api/auth/register - Register new user
- POST /api/auth/login - User login

### Products
- GET /api/products - List all products
- POST /api/products - Add new product
- PUT /api/products/{id} - Update product
- DELETE /api/products/{id} - Delete product

### Cart
- GET /api/cart - View cart
- POST /api/cart/add - Add item to cart
- PUT /api/cart/update - Update cart item
- DELETE /api/cart/remove/{id} - Remove item from cart

### Orders
- GET /api/orders - View orders
- POST /api/orders - Place new order
- GET /api/orders/{id} - View order details

## 🔐 Security

- Authentication
- Password encryption using BCrypt
- Role-based access control

## 📝 Database Schema

The system uses the following main entities:
- Users
- Products
- Cart
- Orders
- OrderItems

## 🙏 Thank You

Thank you for exploring the Shopping Cart Application. Your feedback and contributions are always welcome! 
