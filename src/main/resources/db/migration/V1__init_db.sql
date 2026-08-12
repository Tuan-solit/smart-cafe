-- SMART CAFE - DATABASE SCHEMA

CREATE DATABASE IF NOT EXISTS smart_cafe
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_cafe;

CREATE TABLE roles (
                       role_id   INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL
);

CREATE TABLE users (
                       user_id    INT AUTO_INCREMENT PRIMARY KEY,
                       role_id    INT,
                       full_name  VARCHAR(100) NOT NULL,
                       phone      VARCHAR(40),
                       email      VARCHAR(50),
                       password   VARCHAR(255) NOT NULL,
                       status ENUM('active', 'isActive') DEFAULT 'active',
                       CONSTRAINT fk_users_role
                           FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ;

CREATE TABLE categories (
                            category_id  INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL
);

CREATE TABLE sizes (
                       size_id  INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL
);

CREATE TABLE products (
                          product_id      INT AUTO_INCREMENT PRIMARY KEY,
                          category_id      INT,
                          size_id INT,
                          name     VARCHAR(100) NOT NULL,
                          price        DECIMAL(12,0) NOT NULL,
                          image   VARCHAR(255),
                          status ENUM('dang ban', 'tam het hang', 'ngung ban vinh vien') DEFAULT 'active',
                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id) REFERENCES categories(category_id),
                          CONSTRAINT fk_products_size
                              FOREIGN KEY (size_id) REFERENCES sizes(size_id)
);

CREATE TABLE tables (
                        table_id    INT AUTO_INCREMENT PRIMARY KEY,
                        table_number    VARCHAR(10) NOT NULL,
                        url_qr     VARCHAR(100),
                        status ENUM('trong', 'dang phuc vu') DEFAULT 'trong'
);

CREATE TABLE orders (
                        order_id              INT AUTO_INCREMENT PRIMARY KEY,
                        user_id              INT NULL,
                        table_id             INT NOT NULL,
                        created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
                        status ENUM('dang mo', 'da thanh toan'),
                        CONSTRAINT fk_orders_user
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                        CONSTRAINT fk_orders_table
                            FOREIGN KEY (table_id) REFERENCES tables(table_id)
);

CREATE TABLE order_detail (
                              od_id     INT AUTO_INCREMENT PRIMARY KEY,
                              order_id INT NOT NULL,
                              product_id     INT NOT NULL,
                              quantity  INT NOT NULL DEFAULT 1,
                              price   DECIMAL(12,0) NOT NULL,
                              note   VARCHAR(255),
                              CONSTRAINT fk_order_detail_order
                                  FOREIGN KEY (order_id) REFERENCES orders(order_id),
                              CONSTRAINT fk_order_detail_product
                                  FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE payments (
                          payment_id                 INT AUTO_INCREMENT PRIMARY KEY,
                          order_id                 INT NOT NULL,
                          user_id                 INT NULL,
                          total               DECIMAL(12,0) NOT NULL,
                          payment_method      ENUM('truc tuyen', 'tien mat') NOT NULL,
                          internal_transaction_code   VARCHAR(50),
                          gateway_transaction_code     VARCHAR(50),
                          status     ENUM('pending', 'success', 'failed') DEFAULT 'pending',
                          created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
                          confirmed_at    DATETIME NULL,
                          CONSTRAINT fk_payments_order
                              FOREIGN KEY (order_id) REFERENCES orders(order_id),
                          CONSTRAINT fk_payments_user
                              FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE payment_gateway_log (
                                     p_log_id            INT AUTO_INCREMENT PRIMARY KEY,
                                     payment_id             INT NOT NULL,
                                     type_callback     ENUM('return', 'ipn') NOT NULL,
                                     return_data TEXT,
                                     received_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
                                     CONSTRAINT fk_gateway_log_payment
                                         FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
);

CREATE TABLE activity_logs (
                               a_log_id      INT AUTO_INCREMENT PRIMARY KEY,
                               user_id       INT NOT NULL,
                               activity   VARCHAR(255) NOT NULL,
                               created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_activity_logs_user
                                   FOREIGN KEY (user_id) REFERENCES users(user_id)
);