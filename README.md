# Library Management System (LMS) - Viksit Bharat@2047
### BCA Academic Project (Core Java, Swing/AWT, JDBC & MySQL)

A desktop-based Library Management System engineered to strictly adhere to the **BCA Academic Java Syllabus**. It uses pure **Core Java (Swing & AWT)** for graphical user interface design and **JDBC with MySQL** for database persistence, with zero third-party UI libraries or complex frameworks.

---

## 🎓 BCA Syllabus Compliance Checklist

| Syllabus Topic | How It Is Implemented in This Project |
| :--- | :--- |
| **Core Java OOP** | Encapsulation, static utility methods, class separation (`LibraryBackend` and `LibraryFrontend`), constructors. |
| **Java Swing & AWT** | `JFrame`, `JPanel`, `JButton`, `JLabel`, `JTextField`, `JPasswordField`, `JTable`, `JScrollPane`, `JComboBox`, `JCheckBox`, `JOptionPane`. |
| **Layout Managers** | `BorderLayout`, `CardLayout`, `GridLayout`, `FlowLayout`, `BoxLayout` without external layout libraries. |
| **Event Handling** | `ActionListener` (buttons, combo box), `MouseAdapter` (table row clicks, hover effects), `KeyAdapter` (Enter key search), `ListSelectionListener` (table selection). |
| **Custom Renderers** | `DefaultTableCellRenderer` for table headers and dynamic color badges (Overdue: Red, Active: Amber, Returned: Green). |
| **JDBC & SQL Operations** | `Connection`, `PreparedStatement`, `Statement`, `ResultSet`, `DriverManager`. Parameterized queries to prevent SQL injection. |
| **Transaction Management** | ACID compliance via `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` in `issueBook()` and `returnBook()`. |
| **Exception Handling** | `try-catch-finally` and try-with-resources for clean resource closing and database error recovery. |
| **Validation & Dialogs** | Input validation for empty fields, non-numeric values, negative numbers, and interactive confirmation popups. |

---

## 📌 Architecture Overview

The system strictly adheres to a **Two-File Architecture** that is easy to explain in viva / practical exams:

1. **`LibraryBackend.java` (Data & Logic Layer)**:
   - Manages JDBC connection with MySQL (`library_db`).
   - Implements full CRUD for Books (`addBook`, `updateBook`, `deleteBook`, `searchBooksTable`).
   - Implements full CRUD for Members (`addMember`, `updateMember`, `deleteMember`, `searchMembersTable`).
   - Handles atomic book issuing and returns with rollback safety.
   - Calculates real-time aggregate statistics for the dashboard.
   - Filters transactions by status (`ACTIVE`, `OVERDUE`, `RETURNED`) and keyword.

2. **`LibraryFrontend.java` (Presentation & Event Layer)**:
   - Modern sidebar navigation with standard `CardLayout` view switching.
   - Overview dashboard featuring 4 database-connected metric cards and quick action shortcuts.
   - Form-to-table auto-population: clicking any table row fills input fields for quick updates.
   - Responsive transaction filter and search bar.
   - Secure login portal with a password reveal toggle.

---

## 🔐 Administrative Credentials

- **Username**: `admin`
- **Password**: `Amity@2004`

---

## 🗄️ Relational Database Schema (`library_db`)

```sql
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 1. Books Table
CREATE TABLE IF NOT EXISTS Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    copies INT NOT NULL DEFAULT 1
);

-- 2. Members Table
CREATE TABLE IF NOT EXISTS Members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL
);

-- 3. BookTransactions Table (ACID tracking with Foreign Keys)
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
```

---

## ⚡ Key Features

1. **📊 Overview Dashboard**:
   - 4 live database cards: Total Books, Registered Members, Active Loans, and Overdue Loans.
   - Direct shortcut buttons to quickly navigate to different modules.
   - Live database connectivity status banner.

2. **📚 Book Management (CRUD)**:
   - Add new books with input validation.
   - Search books by title or author.
   - Click any row to automatically load details into input fields.
   - Update book records directly.
   - Delete books with confirmation prompt (cascades safely).

3. **👥 Member Directory (CRUD)**:
   - Register members with name and contact details.
   - Search members by name or contact number.
   - Click to auto-fill for fast record updates.
   - Delete member accounts with confirmation.

4. **🔄 Issue & Return (Transaction Safety)**:
   - **Issue Book**: Verifies book availability, decrements available inventory, and creates loan record atomically.
   - **Return Book**: Restores available book copies and records return date.
   - **Interactive Auto-Fill**: Clicking any active loan in the table automatically populates the Transaction ID.
   - **Status Filtering**: Filter by All, Active Loans, Overdue Loans, or Returned Books.

---

## 🚀 Compilation & Execution

### Prerequisites
- **JDK 8 or higher**
- **MySQL / MariaDB Server** (Running on default port `3306`)
- **MySQL Connector/J JAR** (`mysql-connector-j-8.3.0.jar`, included in repo)

### 1. Compile the Source Code
```powershell
javac -d bin -cp ".;mysql-connector-j-8.3.0.jar" LibraryBackend.java LibraryFrontend.java
```

### 2. Launch the Application
```powershell
java -cp "bin;mysql-connector-j-8.3.0.jar" LibraryFrontend
```

*(On Windows, you can also double-click `run.bat` or run `run.ps1`)*

---

## 💡 Viva / Faculty Q&A Guide

- **Q: Why are PreparedStatements used instead of Statements?**
  *A:* PreparedStatements pre-compile SQL queries and use parameterized inputs (`?`), which completely prevents SQL injection attacks and improves execution performance.
- **Q: How is transaction safety guaranteed during Book Issue/Return?**
  *A:* By setting `conn.setAutoCommit(false)`, the multiple SQL statements (decrementing copies in `Books` and inserting into `BookTransactions`) execute as a single atomic unit. If any step fails, `conn.rollback()` restores the database state, preventing inconsistent records.
- **Q: How does the sidebar navigation switch screens without external libraries?**
  *A:* It uses Java Swing's built-in `CardLayout` manager. Each view is registered as a card (`"DASHBOARD"`, `"BOOKS"`, `"MEMBERS"`, `"TRANSACTIONS"`), and clicking a sidebar button triggers `cardLayout.show(cardsPanel, cardName)`.
