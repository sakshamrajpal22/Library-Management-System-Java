-- ============================================================================
-- SQL SCRIPT: Library Management System Database Setup
-- Database: library_db
-- Description: Creates the database, tables, foreign key constraints,
--              and inserts beginner-friendly initial sample data.
-- ============================================================================

-- Step 1: Create and select the database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Step 2: Create the Books table
-- Stores book details and available copies
CREATE TABLE IF NOT EXISTS Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    copies INT NOT NULL DEFAULT 1
);

-- Step 3: Create the Members table
-- Stores student/member details
CREATE TABLE IF NOT EXISTS Members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL
);

-- Step 4: Create the BookTransactions table
-- Stores book borrowing and return history with foreign keys
CREATE TABLE IF NOT EXISTS BookTransactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE DEFAULT NULL,
    FOREIGN KEY (book_id) REFERENCES Books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES Members(member_id) ON DELETE CASCADE
);

-- Step 5: Insert sample records for testing
-- Sample Books
INSERT INTO Books (title, author, publisher, copies) VALUES
('Introduction to Java Programming', 'Y. Daniel Liang', 'Pearson', 5),
('Clean Code', 'Robert C. Martin', 'Prentice Hall', 3),
('Data Structures and Algorithms in Java', 'Robert Lafore', 'Sams Publishing', 4),
('Database System Concepts', 'Abraham Silberschatz', 'McGraw-Hill', 2);

-- Sample Members
INSERT INTO Members (name, contact) VALUES
('Rahul Sharma', '9876543210'),
('Priya Patel', '9123456780'),
('Aman Verma', '9988776655');

-- Sample Transactions:
-- Transaction 1: Borrowed 20 days ago, due 6 days ago, NOT returned (Overdue Loan)
-- Transaction 2: Borrowed today, due in 14 days, NOT returned (Active Loan)
-- Transaction 3: Borrowed 30 days ago, returned 15 days ago (Completed Loan)
INSERT INTO BookTransactions (book_id, member_id, borrow_date, due_date, return_date) VALUES
(1, 1, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL),
(2, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL),
(3, 3, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY));
