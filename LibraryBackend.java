import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import javax.swing.table.DefaultTableModel;

// LibraryBackend handles database connection and all SQL queries
public class LibraryBackend {

    // db connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // admin login credentials
    public static final String ADMIN_USER = "admin";
    public static final String ADMIN_PASS = "Amity@2004";

    // connect to mysql database
    public static Connection getConnection() throws SQLException {
        try {
            // load jdbc driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // checking if username and pass match
    public static boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return username.trim().equals(ADMIN_USER) && password.equals(ADMIN_PASS);
    }

    // get total books count for dashboard
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
            System.out.println("Error getting total books: " + e.getMessage());
        }
        return count;
    }

    // get total members count for dashboard
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
            System.out.println("Error getting total members: " + e.getMessage());
        }
        return count;
    }

    // get active loans count (where book is not returned yet)
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
            System.out.println("Error getting active loans: " + e.getMessage());
        }
        return count;
    }

    // get overdue books count
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
            System.out.println("Error getting overdue loans: " + e.getMessage());
        }
        return count;
    }

    // add a new book to the database
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
            System.out.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    // delete a book by id
    public static boolean deleteBook(int bookId) {
        String sql = "DELETE FROM Books WHERE book_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    // load all books into the table
    public static void loadBooksTable(DefaultTableModel model) {
        model.setRowCount(0); // clear table before loading
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
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    // search books by title
    public static void searchBooksTable(DefaultTableModel model, String keyword) {
        model.setRowCount(0); // clear old search results
        String sql = "SELECT book_id, title, author, publisher, copies FROM Books WHERE title LIKE ? ORDER BY book_id ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
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
            System.out.println("Error searching books: " + e.getMessage());
        }
    }

    // register a new member
    public static boolean addMember(String name, String contact) {
        String sql = "INSERT INTO Members (name, contact) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    // delete a member by id
    public static boolean deleteMember(int memberId) {
        String sql = "DELETE FROM Members WHERE member_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    // load all members into table
    public static void loadMembersTable(DefaultTableModel model) {
        model.setRowCount(0); // clear existing rows
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
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    // issue a book to a member with transaction handling
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

            // check if member exists
            String checkMemberSql = "SELECT name FROM Members WHERE member_id = ?";
            checkMemberStmt = conn.prepareStatement(checkMemberSql);
            checkMemberStmt.setInt(1, memberId);
            rsMember = checkMemberStmt.executeQuery();
            if (!rsMember.next()) {
                return "Error: Member with ID " + memberId + " does not exist!";
            }

            // check if book exists and has available copies
            String checkBookSql = "SELECT title, copies FROM Books WHERE book_id = ?";
            checkBookStmt = conn.prepareStatement(checkBookSql);
            checkBookStmt.setInt(1, bookId);
            rsBook = checkBookStmt.executeQuery();
            if (!rsBook.next()) {
                return "Error: Book with ID " + bookId + " does not exist!";
            }

            int availableCopies = rsBook.getInt("copies");
            String bookTitle = rsBook.getString("title");
            if (availableCopies <= 0) {
                return "Error: No copies of \"" + bookTitle + "\" are currently available!";
            }

            // start transaction
            conn.setAutoCommit(false);

            // decrement book copies
            String updateBookSql = "UPDATE Books SET copies = copies - 1 WHERE book_id = ? AND copies > 0";
            updateBookStmt = conn.prepareStatement(updateBookSql);
            updateBookStmt.setInt(1, bookId);
            int updatedRows = updateBookStmt.executeUpdate();
            if (updatedRows == 0) {
                conn.rollback(); // rollback if update failed
                return "Error: Failed to decrement book count. Issue cancelled.";
            }

            // insert into transactions table
            String insertTxSql = "INSERT INTO BookTransactions (book_id, member_id, borrow_date, due_date, return_date) "
                               + "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY), NULL)";
            insertTxStmt = conn.prepareStatement(insertTxSql);
            insertTxStmt.setInt(1, bookId);
            insertTxStmt.setInt(2, memberId);
            insertTxStmt.setInt(3, loanDays);
            insertTxStmt.executeUpdate();

            // commit transaction
            conn.commit();
            return "SUCCESS: \"" + bookTitle + "\" has been issued successfully for " + loanDays + " days!";

        } catch (SQLException e) {
            // transaction rollback if something fails
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Transaction rolled back: " + e.getMessage());
                } catch (SQLException ex) {
                    System.out.println("Rollback error: " + ex.getMessage());
                }
            }
            return "Database Error: " + e.getMessage();
        } finally {
            // close statements and reset auto commit
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
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // return a borrowed book with transaction handling
    public static String returnBook(int transactionId) {
        Connection conn = null;
        PreparedStatement checkTxStmt = null;
        PreparedStatement updateTxStmt = null;
        PreparedStatement updateBookStmt = null;
        ResultSet rsTx = null;

        try {
            conn = getConnection();

            // check if transaction exists and hasn't been returned yet
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
                return "Notice: This book was already returned on " + returnDate + "!";
            }

            int bookId = rsTx.getInt("book_id");
            String bookTitle = rsTx.getString("title");

            // start transaction
            conn.setAutoCommit(false);

            // set return date to today
            String updateTxSql = "UPDATE BookTransactions SET return_date = CURDATE() WHERE transaction_id = ?";
            updateTxStmt = conn.prepareStatement(updateTxSql);
            updateTxStmt.setInt(1, transactionId);
            updateTxStmt.executeUpdate();

            // increment book copies
            String updateBookSql = "UPDATE Books SET copies = copies + 1 WHERE book_id = ?";
            updateBookStmt = conn.prepareStatement(updateBookSql);
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();

            // commit transaction
            conn.commit();
            return "SUCCESS: \"" + bookTitle + "\" returned successfully!";

        } catch (SQLException e) {
            // transaction rollback if something fails
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Return transaction rolled back: " + e.getMessage());
                } catch (SQLException ex) {
                    System.out.println("Rollback error: " + ex.getMessage());
                }
            }
            return "Database Error: " + e.getMessage();
        } finally {
            // close statements and reset auto commit
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
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // load all transaction history into table
    public static void loadTransactionsTable(DefaultTableModel model) {
        model.setRowCount(0); // clear old rows
        String sql = "SELECT t.transaction_id, t.book_id, b.title, t.member_id, m.name, "
                   + "       t.borrow_date, t.due_date, t.return_date, "
                   + "       (t.return_date IS NULL AND t.due_date < CURDATE()) AS is_overdue "
                   + "FROM BookTransactions t "
                   + "JOIN Books b ON t.book_id = b.book_id "
                   + "JOIN Members m ON t.member_id = m.member_id "
                   + "ORDER BY t.transaction_id DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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

                // status text
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
        } catch (SQLException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }
}
