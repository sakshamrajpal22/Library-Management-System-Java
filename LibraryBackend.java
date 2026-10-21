import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * LibraryBackend.java
 * Handles database connectivity, CRUD queries, and ACID-compliant transaction logic.
 * Developed in Core Java with JDBC for the BCA Academic Project syllabus.
 */
public class LibraryBackend {

    // Database connection credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Administrative login credentials
    public static final String ADMIN_USER = "admin";
    public static final String ADMIN_PASS = "Amity@2004";

    /**
     * Establishes and returns a connection to MySQL database.
     * Uses com.mysql.cj.jdbc.Driver provided by the connector jar.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Tests if the database connection can be established successfully.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates administrator authentication credentials.
     */
    public static boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return username.trim().equals(ADMIN_USER) && password.equals(ADMIN_PASS);
    }

    // =========================================================================
    // DASHBOARD AGGREGATE METRICS
    // =========================================================================

    /**
     * Counts the total number of distinct books in the catalog.
     */
    public static int getTotalBooksCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Books";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total books count: " + e.getMessage());
        }
        return count;
    }

    /**
     * Counts the total number of registered library members.
     */
    public static int getTotalMembersCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Members";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total members count: " + e.getMessage());
        }
        return count;
    }

    /**
     * Counts the active loans (books borrowed and not yet returned).
     */
    public static int getActiveLoansCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM BookTransactions WHERE return_date IS NULL";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active loans count: " + e.getMessage());
        }
        return count;
    }

    /**
     * Counts the overdue loans (unreturned loans where due_date < current date).
     */
    public static int getOverdueLoansCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM BookTransactions WHERE return_date IS NULL AND due_date < CURDATE()";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue loans count: " + e.getMessage());
        }
        return count;
    }

    // =========================================================================
    // BOOK MANAGEMENT (CRUD)
    // =========================================================================

    /**
     * Inserts a new book record into the Books table.
     */
    public static boolean addBook(String title, String author, String publisher, int copies) {
        String sql = "INSERT INTO Books (title, author, publisher, copies) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, copies);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing book record in the Books table.
     */
    public static boolean updateBook(int bookId, String title, String author, String publisher, int copies) {
        String sql = "UPDATE Books SET title = ?, author = ?, publisher = ?, copies = ? WHERE book_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, publisher);
            pstmt.setInt(4, copies);
            pstmt.setInt(5, bookId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a book record by its primary key (book_id).
     * Cascades to transaction records via database foreign key constraint.
     */
    public static boolean deleteBook(int bookId) {
        String sql = "DELETE FROM Books WHERE book_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads all book records into the Swing JTable model.
     */
    public static void loadBooksTable(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT book_id, title, author, publisher, copies FROM Books ORDER BY book_id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String publisher = rs.getString("publisher");
                int copies = rs.getInt("copies");

                model.addRow(new Object[]{id, title, author, publisher, copies});
            }
        } catch (SQLException e) {
            System.err.println("Error loading books table: " + e.getMessage());
        }
    }

    /**
     * Searches books by title or author containing the keyword.
     */
    public static void searchBooksTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        String sql = "SELECT book_id, title, author, publisher, copies FROM Books "
                   + "WHERE title LIKE ? OR author LIKE ? ORDER BY book_id ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("book_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String publisher = rs.getString("publisher");
                    int copies = rs.getInt("copies");

                    model.addRow(new Object[]{id, title, author, publisher, copies});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
    }

    // =========================================================================
    // MEMBER MANAGEMENT (CRUD)
    // =========================================================================

    /**
     * Registers a new member in the Members table and returns the auto-generated member_id.
     * Returns -1 if registration fails.
     */
    public static int registerMemberAndGetId(String name, String contact) {
        String sql = "INSERT INTO Members (name, contact) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering member and retrieving ID: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Registers a new member in the Members table.
     */
    public static boolean addMember(String name, String contact) {
        return registerMemberAndGetId(name, contact) > 0;
    }

    /**
     * Lightweight data model representing a member for combo box dropdowns.
     */
    public static class MemberItem {
        private final int id;
        private final String name;
        private final String contact;

        public MemberItem(int id, String name, String contact) {
            this.id = id;
            this.name = name;
            this.contact = contact;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getContact() { return contact; }

        @Override
        public String toString() {
            return "ID " + id + ": " + name + " (" + contact + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MemberItem other = (MemberItem) obj;
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    /**
     * Retrieves all members as a list of MemberItem objects for dropdown population.
     */
    public static List<MemberItem> getAllMembersList() {
        List<MemberItem> list = new ArrayList<>();
        String sql = "SELECT member_id, name, contact FROM Members ORDER BY member_id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MemberItem(
                    rs.getInt("member_id"),
                    rs.getString("name"),
                    rs.getString("contact")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading members list: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lightweight data model representing a book for combo box dropdowns.
     */
    public static class BookItem {
        private final int id;
        private final String title;
        private final String author;
        private final int copies;

        public BookItem(int id, String title, String author, int copies) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.copies = copies;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getCopies() { return copies; }

        @Override
        public String toString() {
            return "ID " + id + ": " + title + " (Avail: " + copies + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BookItem other = (BookItem) obj;
            return id == other.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    /**
     * Retrieves all books as a list of BookItem objects for dropdown population.
     */
    public static List<BookItem> getAllBooksList() {
        List<BookItem> list = new ArrayList<>();
        String sql = "SELECT book_id, title, author, copies FROM Books ORDER BY book_id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BookItem(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("copies")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading books list: " + e.getMessage());
        }
        return list;
    }

    /**
     * Updates an existing member's information.
     */
    public static boolean updateMember(int memberId, String name, String contact) {
        String sql = "UPDATE Members SET name = ?, contact = ? WHERE member_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setInt(3, memberId);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a member by member_id.
     */
    public static boolean deleteMember(int memberId) {
        String sql = "DELETE FROM Members WHERE member_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads all members into the Swing table model.
     */
    public static void loadMembersTable(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT member_id, name, contact FROM Members ORDER BY member_id ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("member_id");
                String name = rs.getString("name");
                String contact = rs.getString("contact");

                model.addRow(new Object[]{id, name, contact});
            }
        } catch (SQLException e) {
            System.err.println("Error loading members table: " + e.getMessage());
        }
    }

    /**
     * Searches members by name or contact number.
     */
    public static void searchMembersTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        String sql = "SELECT member_id, name, contact FROM Members "
                   + "WHERE name LIKE ? OR contact LIKE ? ORDER BY member_id ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("member_id");
                    String name = rs.getString("name");
                    String contact = rs.getString("contact");

                    model.addRow(new Object[]{id, name, contact});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
        }
    }

    // =========================================================================
    // ISSUE & RETURN OPERATIONS (ACID TRANSACTIONS)
    // =========================================================================

    /**
     * Issues a book to a member using atomic JDBC transaction management.
     * Decrements available book copies and inserts a record into BookTransactions.
     */
    public static String issueBook(int bookId, int memberId, int loanDays) {
        Connection conn = null;
        PreparedStatement checkMemberStmt = null;
        PreparedStatement checkBookStmt = null;
        PreparedStatement updateBookStmt = null;
        PreparedStatement insertTxStmt = null;
        ResultSet rsMember = null;
        ResultSet rsBook = null;

        try {
            conn = getConnection();

            // 1. Verify that the member exists
            String checkMemberSql = "SELECT name FROM Members WHERE member_id = ?";
            checkMemberStmt = conn.prepareStatement(checkMemberSql);
            checkMemberStmt.setInt(1, memberId);
            rsMember = checkMemberStmt.executeQuery();
            if (!rsMember.next()) {
                return "Error: Member ID " + memberId + " does not exist!";
            }
            String memberName = rsMember.getString("name");

            // 2. Verify book exists and has available copies
            String checkBookSql = "SELECT title, copies FROM Books WHERE book_id = ?";
            checkBookStmt = conn.prepareStatement(checkBookSql);
            checkBookStmt.setInt(1, bookId);
            rsBook = checkBookStmt.executeQuery();
            if (!rsBook.next()) {
                return "Error: Book ID " + bookId + " does not exist!";
            }

            int availableCopies = rsBook.getInt("copies");
            String bookTitle = rsBook.getString("title");
            if (availableCopies <= 0) {
                return "Error: No copies of \"" + bookTitle + "\" are currently available!";
            }

            // 3. Begin manual transaction
            conn.setAutoCommit(false);

            // 4. Decrement available copies in Books
            String updateBookSql = "UPDATE Books SET copies = copies - 1 WHERE book_id = ? AND copies > 0";
            updateBookStmt = conn.prepareStatement(updateBookSql);
            updateBookStmt.setInt(1, bookId);
            int updatedRows = updateBookStmt.executeUpdate();
            if (updatedRows == 0) {
                conn.rollback();
                return "Error: Failed to decrement book count. Issue operation cancelled.";
            }

            // 5. Insert new loan record into BookTransactions
            String insertTxSql = "INSERT INTO BookTransactions (book_id, member_id, borrow_date, due_date, return_date) "
                               + "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY), NULL)";
            insertTxStmt = conn.prepareStatement(insertTxSql);
            insertTxStmt.setInt(1, bookId);
            insertTxStmt.setInt(2, memberId);
            insertTxStmt.setInt(3, loanDays);
            insertTxStmt.executeUpdate();

            // 6. Commit transaction
            conn.commit();
            return "SUCCESS: \"" + bookTitle + "\" issued to " + memberName + " for " + loanDays + " days!";

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Issue transaction rolled back: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            return "Database Error: " + e.getMessage();
        } finally {
            try {
                if (rsMember != null) rsMember.close();
                if (rsBook != null) rsBook.close();
                if (checkMemberStmt != null) checkMemberStmt.close();
                if (checkBookStmt != null) checkBookStmt.close();
                if (updateBookStmt != null) updateBookStmt.close();
                if (insertTxStmt != null) insertTxStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Processes book return using atomic JDBC transaction management.
     * Records return_date = CURDATE() and increments available book copies.
     */
    public static String returnBook(int transactionId) {
        Connection conn = null;
        PreparedStatement checkTxStmt = null;
        PreparedStatement updateTxStmt = null;
        PreparedStatement updateBookStmt = null;
        ResultSet rsTx = null;

        try {
            conn = getConnection();

            // 1. Verify transaction exists and is still active
            String checkTxSql = "SELECT t.book_id, t.return_date, b.title "
                              + "FROM BookTransactions t "
                              + "JOIN Books b ON t.book_id = b.book_id "
                              + "WHERE t.transaction_id = ?";
            checkTxStmt = conn.prepareStatement(checkTxSql);
            checkTxStmt.setInt(1, transactionId);
            rsTx = checkTxStmt.executeQuery();

            if (!rsTx.next()) {
                return "Error: Transaction ID " + transactionId + " not found!";
            }

            Date returnDate = rsTx.getDate("return_date");
            if (returnDate != null) {
                return "Notice: This loan was already returned on " + returnDate + "!";
            }

            int bookId = rsTx.getInt("book_id");
            String bookTitle = rsTx.getString("title");

            // 2. Begin manual transaction
            conn.setAutoCommit(false);

            // 3. Mark return date
            String updateTxSql = "UPDATE BookTransactions SET return_date = CURDATE() WHERE transaction_id = ?";
            updateTxStmt = conn.prepareStatement(updateTxSql);
            updateTxStmt.setInt(1, transactionId);
            updateTxStmt.executeUpdate();

            // 4. Restore available copies
            String updateBookSql = "UPDATE Books SET copies = copies + 1 WHERE book_id = ?";
            updateBookStmt = conn.prepareStatement(updateBookSql);
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();

            // 5. Commit transaction
            conn.commit();
            return "SUCCESS: \"" + bookTitle + "\" returned successfully!";

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Return transaction rolled back: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Rollback error: " + ex.getMessage());
                }
            }
            return "Database Error: " + e.getMessage();
        } finally {
            try {
                if (rsTx != null) rsTx.close();
                if (checkTxStmt != null) checkTxStmt.close();
                if (updateTxStmt != null) updateTxStmt.close();
                if (updateBookStmt != null) updateBookStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // TRANSACTION HISTORY & FILTERING
    // =========================================================================

    /**
     * Default loader for all transactions.
     */
    public static void loadTransactionsTable(DefaultTableModel model) {
        loadTransactionsFiltered(model, "ALL", "");
    }

    /**
     * Loads transactions with optional status filter ("ALL", "ACTIVE", "OVERDUE", "RETURNED")
     * and optional keyword search (matches book title or member name).
     */
    public static void loadTransactionsFiltered(DefaultTableModel model, String filterType, String keyword) {
        model.setRowCount(0);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.transaction_id, t.book_id, b.title, t.member_id, m.name, ");
        sql.append("       t.borrow_date, t.due_date, t.return_date, ");
        sql.append("       (t.return_date IS NULL AND t.due_date < CURDATE()) AS is_overdue ");
        sql.append("FROM BookTransactions t ");
        sql.append("JOIN Books b ON t.book_id = b.book_id ");
        sql.append("JOIN Members m ON t.member_id = m.member_id ");
        sql.append("WHERE 1=1 ");

        if ("ACTIVE".equalsIgnoreCase(filterType)) {
            sql.append("AND t.return_date IS NULL ");
        } else if ("OVERDUE".equalsIgnoreCase(filterType)) {
            sql.append("AND t.return_date IS NULL AND t.due_date < CURDATE() ");
        } else if ("RETURNED".equalsIgnoreCase(filterType)) {
            sql.append("AND t.return_date IS NOT NULL ");
        }

        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());
        if (hasKeyword) {
            sql.append("AND (b.title LIKE ? OR m.name LIKE ?) ");
        }

        sql.append("ORDER BY t.transaction_id DESC");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int txId = rs.getInt("transaction_id");
                    int bookId = rs.getInt("book_id");
                    String bookTitle = rs.getString("title");
                    int memberId = rs.getInt("member_id");
                    String memberName = rs.getString("name");
                    Date borrowDate = rs.getDate("borrow_date");
                    Date dueDate = rs.getDate("due_date");
                    Date returnDate = rs.getDate("return_date");
                    boolean isOverdue = rs.getBoolean("is_overdue");

                    String status;
                    if (returnDate != null) {
                        status = "Returned (" + returnDate + ")";
                    } else if (isOverdue) {
                        status = "OVERDUE (Due: " + dueDate + ")";
                    } else {
                        status = "Active Loan (Due: " + dueDate + ")";
                    }

                    model.addRow(new Object[]{
                        txId, bookId, bookTitle, memberId, memberName, borrowDate, dueDate,
                        (returnDate == null ? "Not Returned" : returnDate.toString()), status
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading filtered transactions: " + e.getMessage());
        }
    }

    /**
     * Loads outstanding overdue loans specifically for the dashboard summary view.
     */
    public static void loadOverdueLoansSummary(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT t.transaction_id, b.title, m.name, t.due_date, "
                   + "       DATEDIFF(CURDATE(), t.due_date) AS days_overdue "
                   + "FROM BookTransactions t "
                   + "JOIN Books b ON t.book_id = b.book_id "
                   + "JOIN Members m ON t.member_id = m.member_id "
                   + "WHERE t.return_date IS NULL AND t.due_date < CURDATE() "
                   + "ORDER BY t.due_date ASC LIMIT 10";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int txId = rs.getInt("transaction_id");
                String title = rs.getString("title");
                String member = rs.getString("name");
                Date dueDate = rs.getDate("due_date");
                int days = rs.getInt("days_overdue");

                String overdueBadge = "+" + days + (days == 1 ? " day" : " days");
                model.addRow(new Object[]{txId, title, member, dueDate, overdueBadge});
            }
        } catch (SQLException e) {
            System.err.println("Error loading overdue loans summary: " + e.getMessage());
        }
    }

    /**
     * Loads recent actual circulation transactions for the dashboard summary view.
     */
    public static void loadRecentTransactionsSummary(DefaultTableModel model, int limit) {
        model.setRowCount(0);
        String sql = "SELECT t.transaction_id, b.title, m.name, t.borrow_date, t.due_date, t.return_date, "
                   + "       (t.return_date IS NULL AND t.due_date < CURDATE()) AS is_overdue "
                   + "FROM BookTransactions t "
                   + "JOIN Books b ON t.book_id = b.book_id "
                   + "JOIN Members m ON t.member_id = m.member_id "
                   + "ORDER BY t.transaction_id DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int txId = rs.getInt("transaction_id");
                    String title = rs.getString("title");
                    String member = rs.getString("name");
                    Date borrowDate = rs.getDate("borrow_date");
                    Date dueDate = rs.getDate("due_date");
                    Date returnDate = rs.getDate("return_date");
                    boolean isOverdue = rs.getBoolean("is_overdue");

                    String status;
                    if (returnDate != null) {
                        status = "Returned";
                    } else if (isOverdue) {
                        status = "OVERDUE";
                    } else {
                        status = "Active";
                    }

                    model.addRow(new Object[]{txId, title, member, borrowDate, status});
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading recent transactions summary: " + e.getMessage());
        }
    }
}

