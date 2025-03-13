CREATE DATABASE passwordmanagement;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
                            category_id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE password_entries (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  user_id INT NOT NULL,
                                  website VARCHAR(255) NOT NULL,
                                  username VARCHAR(255) NOT NULL,
                                  password VARCHAR(255) NOT NULL,
                                  category_id INT,
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                  FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
);
