# Library Management System (LMS) - Viksit Bharat@2047

A desktop-based Library Management System developed using **Core Java (Swing & AWT)** and **MySQL via JDBC**. Built with clean, student-friendly code conventions, modular two-file separation of concerns, and ACID-compliant transaction safety.

---

## 📌 Architecture Overview

The application follows a streamlined **Two-File Architecture**:
1. **`LibraryBackend.java` (Data Layer & Business Logic)**:
   - Manages JDBC connection pooling with MySQL (`library_db`).
   - Implements parameterized SQL prepared statements to eliminate SQL injection.
   - Enforces manual transaction control using `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()` inside error-handling routines.
2. **`LibraryFrontend.java` (Presentation & Event Layer)**:
   - Modern Java Swing UI with a tabbed interface (`JTabbedPane`) and native system look-and-feel.
   - Dedicated authentication portal before unlocking dashboard operations.
   - Synchronous UI table refreshes on every data modification.

---

## 🔐 Authentication Credentials

A secure login screen guards the administrative workspace:
- **Username**: `admin`
- **Password**: `Amity@2004`

---

## 🗄️ Database Schema (`library_db`)

The system relies on three relational tables connected by foreign key cascades:

### 1. `Books` Table
| Column | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `book_id` | `INT` | `AUTO_INCREMENT, PRIMARY KEY` | Unique book identifier |
| `title` | `VARCHAR(255)` | `NOT NULL` | Title of the publication |
| `author` | `VARCHAR(255)` | `NOT NULL` | Author name |
| `publisher` | `VARCHAR(255)` | `NOT NULL` | Publisher name |
| `copies` | `INT` | `NOT NULL, DEFAULT 1` | Available inventory count |

### 2. `Members` Table
| Column | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `member_id` | `INT` | `AUTO_INCREMENT, PRIMARY KEY` | Unique member ID |
| `name` | `VARCHAR(255)` | `NOT NULL` | Full name of the member |
| `contact` | `VARCHAR(255)` | `NOT NULL` | Phone number or email |

### 3. `BookTransactions` Table
| Column | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `transaction_id` | `INT` | `AUTO_INCREMENT, PRIMARY KEY` | Unique loan tracking ID |
| `book_id` | `INT` | `FOREIGN KEY REFERENCES Books(book_id) ON DELETE CASCADE` | Borrowed book |
| `member_id` | `INT` | `FOREIGN KEY REFERENCES Members(member_id) ON DELETE CASCADE` | Borrowing member |
| `borrow_date` | `DATE` | `NOT NULL` | Issue date (`CURDATE()`) |
| `due_date` | `DATE` | `NOT NULL` | Expected return date |
| `return_date` | `DATE` | `DEFAULT NULL` | Actual return date |

---

## ⚡ Core Features

- **📊 Dashboard**: Real-time aggregation cards displaying Total Books, Registered Members, Active Loans, and Overdue Book counts.
- **📚 Book Management**: Add new books, search by title keyword, delete book records, and view live inventory status.
- **👥 Member Management**: Register new members, search membership records, and delete accounts.
- **🔄 Issue & Return (Transaction Safety)**:
  - **Issue Book**: Verifies book copy availability, reduces copies in `Books`, and logs record in `BookTransactions` atomically using JDBC transactions.
  - **Return Book**: Sets `return_date = CURDATE()` in `BookTransactions` and restores available copies in `Books`.
  - **Ergonomic Selection**: Clicking any active loan in the transaction table automatically auto-populates the Transaction ID for instant returns.

---

## 🚀 Setup & Execution Guide

### Prerequisites
- **Java SE Development Kit (JDK 8 or higher)**
- **MySQL Server** (Running locally on default port `3306`)
- **MySQL Connector/J JAR** (`mysql-connector-j-8.3.0.jar`, included in repository)

### 1. Database Initialization
Start your local MySQL service and import the initialization script:
```bash
# Log in to MySQL and run the schema script
mysql -u root < library_db.sql
```
*(By default, database credentials in `LibraryBackend.java` are set to `localhost:3306`, DB: `library_db`, user: `root`, password: `""` / empty).*

### 2. Compilation
Compile the source code and route all bytecode output into the clean `bin` directory:
```bash
javac -d bin -cp ".:mysql-connector-j-8.3.0.jar" LibraryBackend.java LibraryFrontend.java
```

### 3. Running the Application
Launch the graphical interface from the compiled `bin` folder:
```bash
java -cp "bin:mysql-connector-j-8.3.0.jar" LibraryFrontend
```

---

## 📁 Repository Structure

```text
Library-Management-System-Java/
├── bin/                          # Compiled bytecode classes (excluded in git)
├── .gitignore                    # Git exclusion rules
├── LibraryBackend.java           # JDBC database queries & transaction routines
├── LibraryFrontend.java          # Java Swing GUI and login window
├── library_db.sql                # MySQL schema and starter data
├── mysql-connector-j-8.3.0.jar   # MySQL JDBC Connector
└── README.md                     # Project documentation
```

---

## 📄 License & Credits
Developed as part of the **Viksit Bharat@2047** academic initiative. Educational and open for student collaboration.
