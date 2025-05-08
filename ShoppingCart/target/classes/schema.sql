-- Drop existing tables if they exist (in correct order to respect foreign key constraints)
DROP TABLE IF EXISTS myorder_cart_items;
DROP TABLE IF EXISTS shopping_cart;
DROP TABLE IF EXISTS myorder;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS products;

-- Create products table
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    available_quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(100),
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create customer table
CREATE TABLE customer (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create myorder table
CREATE TABLE myorder (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_description TEXT,
    customer_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Create shopping_cart table
CREATE TABLE shopping_cart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    product_id INT,
    product_name VARCHAR(255),
    quantity INT NOT NULL DEFAULT 1,
    amount DECIMAL(10,2) NOT NULL,
    order_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (order_id) REFERENCES myorder(id)
);

-- Create myorder_cart_items table for the many-to-many relationship
CREATE TABLE myorder_cart_items (
    order_id INT,
    cart_items_id INT,
    quantity INT NOT NULL,
    price_at_time DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (order_id, cart_items_id),
    FOREIGN KEY (order_id) REFERENCES myorder(id),
    FOREIGN KEY (cart_items_id) REFERENCES shopping_cart(id)
);

-- Create indexes for better performance
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_order_customer ON myorder(customer_id);
CREATE INDEX idx_cart_customer ON shopping_cart(customer_id);
CREATE INDEX idx_cart_order ON shopping_cart(order_id); 