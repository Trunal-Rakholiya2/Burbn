-- Database Schema for Burbn - Social Media Feed Simulator
-- Run this script in MySQL Client or phpMyAdmin

CREATE DATABASE IF NOT EXISTS socialmedia;
USE socialmedia;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile_no VARCHAR(15) DEFAULT '0000000000',
    aura_points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Followers Mapping Table
CREATE TABLE IF NOT EXISTS followers (
    follower_id INT NOT NULL,
    following_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Posts & Ephemeral Moments Table
CREATE TABLE IF NOT EXISTS posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    is_moment TINYINT(1) DEFAULT 0,
    expires_at DATETIME NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 4. Likes Table
CREATE TABLE IF NOT EXISTS likes (
    like_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_like (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(user_id) ON DELETE CASCADE
);

-- 5. Comments Table
CREATE TABLE IF NOT EXISTS comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
);

-- 6. Direct Messages & Reactions Table
CREATE TABLE IF NOT EXISTS messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Sample Data Insertion
INSERT IGNORE INTO users (user_id, username, password, email, mobile_no, aura_points) VALUES
(1, 'alex', 'password123', 'alex@burbn.com', '9876543210', 120),
(2, 'sarah', 'password123', 'sarah@burbn.com', '9876543211', 350),
(3, 'tech_guru', 'password123', 'guru@burbn.com', '9876543212', 550);

INSERT IGNORE INTO followers (follower_id, following_id) VALUES
(1, 2), (1, 3), (2, 3);

INSERT IGNORE INTO posts (post_id, user_id, content) VALUES
(1, 2, 'Excited to build custom data structures in Java! 🚀'),
(2, 3, 'Just updated the recommendation algorithm for Burbn feed.');
