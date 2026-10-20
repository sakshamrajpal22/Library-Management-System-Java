import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

// main gui window for the library system
public class LibraryFrontend extends JFrame {

    // tab pane for navigation
    private JTabbedPane tabbedPane;

    // dashboard labels for displaying counts
    private JLabel totalBooksLabel;
    private JLabel totalMembersLabel;
    private JLabel activeLoansLabel;
    private JLabel overdueLoansLabel;

    // books tab components
    private JTextField bookTitleField;
    private JTextField bookAuthorField;
    private JTextField bookPublisherField;
    private JTextField bookCopiesField;
    private JTextField bookSearchField;
    private JTable booksTable;
    private DefaultTableModel booksModel;

    // members tab components
    private JTextField memberNameField;
    private JTextField memberContactField;
    private JTable membersTable;
    private DefaultTableModel membersModel;

    // issue & return tab components
    private JTextField issueBookIdField;
    private JTextField issueMemberIdField;
    private JTextField issueDaysField;
    private JTextField returnTxIdField;
    private JTable transactionsTable;
    private DefaultTableModel transactionsModel;

    // constructor to build the main frame
    public LibraryFrontend() {
        setTitle("Library Management System (LMS) - Admin Workspace");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null); // center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // top banner
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        // adding tabs
        tabbedPane.addTab("  Dashboard  ", createDashboardTab());
        tabbedPane.addTab("  Book Management  ", createBooksTab());
        tabbedPane.addTab("  Member Management  ", createMembersTab());
        tabbedPane.addTab("  Issue & Return  ", createTransactionsTab());

        add(tabbedPane, BorderLayout.CENTER);

        // bottom status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        JLabel statusLabel = new JLabel("Status: Connected to local MySQL (library_db) | Ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // load initial data into tables and dashboard
        refreshDashboardData();
        LibraryBackend.loadBooksTable(booksModel);
        LibraryBackend.loadMembersTable(membersModel);
        LibraryBackend.loadTransactionsTable(transactionsModel);
    }

    // header bar with title and logout button
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 50, 77)); // navy blue background
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Logged in as: admin");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userLabel.setForeground(new Color(220, 220, 220));

        // logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    LibraryFrontend.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose(); // close current window
                    new LoginFrame().setVisible(true); // show login screen again
                }
            }
        });

        rightPanel.add(userLabel);
        rightPanel.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // dashboard tab with summary cards
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel welcomeLabel = new JLabel("Overview & Key Library Statistics");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(40, 40, 40));

        // grid for the 4 stat cards
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));

        totalBooksLabel = new JLabel("0", SwingConstants.CENTER);
        totalMembersLabel = new JLabel("0", SwingConstants.CENTER);
        activeLoansLabel = new JLabel("0", SwingConstants.CENTER);
        overdueLoansLabel = new JLabel("0", SwingConstants.CENTER);

        cardsGrid.add(createStatCard("Total Books Registered", totalBooksLabel, new Color(52, 152, 219)));
        cardsGrid.add(createStatCard("Total Library Members", totalMembersLabel, new Color(46, 204, 113)));
        cardsGrid.add(createStatCard("Active Book Loans", activeLoansLabel, new Color(241, 196, 15)));
        cardsGrid.add(createStatCard("Overdue Books", overdueLoansLabel, new Color(231, 76, 60)));

        // refresh button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Dashboard Counts");
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 13));
        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshDashboardData();
                JOptionPane.showMessageDialog(LibraryFrontend.this, "Dashboard metrics updated!", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        bottomPanel.add(refreshBtn);

        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(cardsGrid, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // helper to make nice card panels for dashboard numbers
    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(accentColor, 2, true),
            new EmptyBorder(20, 15, 20, 15)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 15));
        titleLbl.setForeground(new Color(70, 70, 70));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 42));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // refresh numbers on dashboard
    private void refreshDashboardData() {
        totalBooksLabel.setText(String.valueOf(LibraryBackend.getTotalBooksCount()));
        totalMembersLabel.setText(String.valueOf(LibraryBackend.getTotalMembersCount()));
        activeLoansLabel.setText(String.valueOf(LibraryBackend.getActiveLoansCount()));
        overdueLoansLabel.setText(String.valueOf(LibraryBackend.getOverdueLoansCount()));
    }

    // book management tab
    private JPanel createBooksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel northPanel = new JPanel(new BorderLayout(10, 10));

        // add book input form
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 8));
        formPanel.setBorder(new TitledBorder("Add New Book"));

        bookTitleField = new JTextField();
        bookAuthorField = new JTextField();
        bookPublisherField = new JTextField();
        bookCopiesField = new JTextField();

        formPanel.add(new JLabel("Title:"));
        formPanel.add(bookTitleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(bookAuthorField);
        formPanel.add(new JLabel("Publisher:"));
        formPanel.add(bookPublisherField);
        formPanel.add(new JLabel("Copies:"));
        formPanel.add(bookCopiesField);

        // buttons and search box
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBookBtn = new JButton("Add Book");
        JButton clearBookBtn = new JButton("Clear Fields");

        JLabel searchLbl = new JLabel(" | Search by Title:");
        searchLbl.setFont(new Font("Arial", Font.BOLD, 12));
        bookSearchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");

        actionsPanel.add(addBookBtn);
        actionsPanel.add(clearBookBtn);
        actionsPanel.add(searchLbl);
        actionsPanel.add(bookSearchField);
        actionsPanel.add(searchBtn);
        actionsPanel.add(showAllBtn);

        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(actionsPanel, BorderLayout.SOUTH);

        // books table
        String[] bookColumns = {"Book ID", "Title", "Author", "Publisher", "Available Copies"};
        booksModel = new DefaultTableModel(bookColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // prevent direct editing in table cells
            }
        };
        booksTable = new JTable(booksModel);
        booksTable.setRowHeight(24);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(booksTable);

        // bottom buttons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton deleteBookBtn = new JButton("Delete Selected Book");
        deleteBookBtn.setBackground(new Color(220, 53, 69));
        JButton refreshBooksBtn = new JButton("Refresh Table");

        southPanel.add(deleteBookBtn);
        southPanel.add(refreshBooksBtn);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        // add book button click
        addBookBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = bookTitleField.getText().trim();
                String author = bookAuthorField.getText().trim();
                String publisher = bookPublisherField.getText().trim();
                String copiesText = bookCopiesField.getText().trim();

                if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || copiesText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Please fill in all book fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int copies;
                try {
                    copies = Integer.parseInt(copiesText);
                    if (copies < 1) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this, "Copies must be at least 1!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Copies must be a valid number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = LibraryBackend.addBook(title, author, publisher, copies);
                if (success) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // clear the text fields
                    bookTitleField.setText("");
                    bookAuthorField.setText("");
                    bookPublisherField.setText("");
                    bookCopiesField.setText("");
                    LibraryBackend.loadBooksTable(booksModel);
                    refreshDashboardData();
                } else {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Failed to add book.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // clear button
        clearBookBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookTitleField.setText("");
                bookAuthorField.setText("");
                bookPublisherField.setText("");
                bookCopiesField.setText("");
            }
        });

        // search button
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String query = bookSearchField.getText().trim();
                if (query.isEmpty()) {
                    LibraryBackend.loadBooksTable(booksModel);
                } else {
                    LibraryBackend.searchBooksTable(booksModel, query);
                }
            }
        });

        // show all button
        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookSearchField.setText("");
                LibraryBackend.loadBooksTable(booksModel);
            }
        });

        // delete book button click
        deleteBookBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Please select a book from the table to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int bookId = (int) booksModel.getValueAt(selectedRow, 0);
                String title = (String) booksModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(
                    LibraryFrontend.this,
                    "Are you sure you want to delete book \"" + title + "\" (ID: " + bookId + ")?\n"
                    + "Note: This will also delete related transaction history.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = LibraryBackend.deleteBook(bookId);
                    if (deleted) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this, "Book deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        LibraryBackend.loadBooksTable(booksModel);
                        LibraryBackend.loadTransactionsTable(transactionsModel);
                        refreshDashboardData();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this, "Failed to delete book.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // refresh table button
        refreshBooksBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LibraryBackend.loadBooksTable(booksModel);
            }
        });

        return panel;
    }

    // member management tab
    private JPanel createMembersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // member input form
        JPanel formPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        formPanel.setBorder(new TitledBorder("Register New Member"));

        memberNameField = new JTextField();
        memberContactField = new JTextField();

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(memberNameField);
        formPanel.add(new JLabel("Contact / Phone:"));
        formPanel.add(memberContactField);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addMemberBtn = new JButton("Add Member");
        JButton clearMemberBtn = new JButton("Clear Fields");
        actionsPanel.add(addMemberBtn);
        actionsPanel.add(clearMemberBtn);

        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(actionsPanel, BorderLayout.SOUTH);

        // members table
        String[] memberColumns = {"Member ID", "Full Name", "Contact Number"};
        membersModel = new DefaultTableModel(memberColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        membersTable = new JTable(membersModel);
        membersTable.setRowHeight(24);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(membersTable);

        // bottom buttons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton deleteMemberBtn = new JButton("Delete Selected Member");
        deleteMemberBtn.setBackground(new Color(220, 53, 69));
        JButton refreshMembersBtn = new JButton("Refresh Table");

        southPanel.add(deleteMemberBtn);
        southPanel.add(refreshMembersBtn);

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        // add member button click
        addMemberBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = memberNameField.getText().trim();
                String contact = memberContactField.getText().trim();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Please fill in member name and contact!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean success = LibraryBackend.addMember(name, contact);
                if (success) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Member registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // clear the text fields
                    memberNameField.setText("");
                    memberContactField.setText("");
                    LibraryBackend.loadMembersTable(membersModel);
                    refreshDashboardData();
                } else {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Failed to register member.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // clear member fields
        clearMemberBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                memberNameField.setText("");
                memberContactField.setText("");
            }
        });

        // delete member button click
        deleteMemberBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = membersTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Please select a member from the table to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int memberId = (int) membersModel.getValueAt(selectedRow, 0);
                String memberName = (String) membersModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(
                    LibraryFrontend.this,
                    "Are you sure you want to delete member \"" + memberName + "\" (ID: " + memberId + ")?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = LibraryBackend.deleteMember(memberId);
                    if (deleted) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this, "Member deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        LibraryBackend.loadMembersTable(membersModel);
                        LibraryBackend.loadTransactionsTable(transactionsModel);
                        refreshDashboardData();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this, "Failed to delete member.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // refresh members table
        refreshMembersBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LibraryBackend.loadMembersTable(membersModel);
            }
        });

        return panel;
    }

    // issue and return tab
    private JPanel createTransactionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // issue book and return book forms side by side
        JPanel formsContainer = new JPanel(new GridLayout(1, 2, 15, 10));

        // issue book form
        JPanel issuePanel = new JPanel(new GridLayout(4, 2, 8, 8));
        issuePanel.setBorder(new TitledBorder("Issue Book to Member"));

        issueBookIdField = new JTextField();
        issueMemberIdField = new JTextField();
        issueDaysField = new JTextField("14"); // default loan is 14 days
        JButton issueBtn = new JButton("Issue Book");

        issuePanel.add(new JLabel("Book ID:"));
        issuePanel.add(issueBookIdField);
        issuePanel.add(new JLabel("Member ID:"));
        issuePanel.add(issueMemberIdField);
        issuePanel.add(new JLabel("Loan Days:"));
        issuePanel.add(issueDaysField);
        issuePanel.add(new JLabel("")); // empty spacer
        issuePanel.add(issueBtn);

        // return book form
        JPanel returnPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        returnPanel.setBorder(new TitledBorder("Return Borrowed Book"));

        returnTxIdField = new JTextField();
        JButton returnBtn = new JButton("Return Book");

        returnPanel.add(new JLabel("Transaction ID:"));
        returnPanel.add(returnTxIdField);
        returnPanel.add(new JLabel("Tip:"));
        JLabel tipLabel = new JLabel("Click table row below to auto-fill ID");
        tipLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        returnPanel.add(tipLabel);
        returnPanel.add(new JLabel(""));
        returnPanel.add(new JLabel(""));
        returnPanel.add(new JLabel(""));
        returnPanel.add(returnBtn);

        formsContainer.add(issuePanel);
        formsContainer.add(returnPanel);

        // transactions history table
        String[] txColumns = {
            "Tx ID", "Book ID", "Book Title", "Member ID", "Member Name",
            "Borrow Date", "Due Date", "Return Date", "Loan Status"
        };
        transactionsModel = new DefaultTableModel(txColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionsTable = new JTable(transactionsModel);
        transactionsTable.setRowHeight(24);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);

        // clicking a row auto fills the transaction id
        transactionsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = transactionsTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object txId = transactionsModel.getValueAt(selectedRow, 0);
                    returnTxIdField.setText(String.valueOf(txId));
                }
            }
        });

        // bottom refresh button
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshTxBtn = new JButton("Refresh Transactions Table");
        southPanel.add(refreshTxBtn);

        panel.add(formsContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        // issue book button click
        issueBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bIdText = issueBookIdField.getText().trim();
                String mIdText = issueMemberIdField.getText().trim();
                String daysText = issueDaysField.getText().trim();

                if (bIdText.isEmpty() || mIdText.isEmpty() || daysText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Please enter Book ID, Member ID, and Loan Days!", "Missing Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int bookId, memberId, loanDays;
                try {
                    bookId = Integer.parseInt(bIdText);
                    memberId = Integer.parseInt(mIdText);
                    loanDays = Integer.parseInt(daysText);
                    if (loanDays < 1) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this, "Loan days must be at least 1!", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Book ID, Member ID, and Loan Days must be numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // call backend issue method
                String resultMessage = LibraryBackend.issueBook(bookId, memberId, loanDays);

                if (resultMessage.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, resultMessage, "Transaction Succeeded", JOptionPane.INFORMATION_MESSAGE);
                    // clear fields
                    issueBookIdField.setText("");
                    issueMemberIdField.setText("");
                    issueDaysField.setText("14");

                    // refresh tables and counts
                    LibraryBackend.loadTransactionsTable(transactionsModel);
                    LibraryBackend.loadBooksTable(booksModel);
                    refreshDashboardData();
                } else {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, resultMessage, "Issue Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // return book button click
        returnBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String txText = returnTxIdField.getText().trim();
                if (txText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Please enter or select a Transaction ID to return!", "Missing Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int txId;
                try {
                    txId = Integer.parseInt(txText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, "Transaction ID must be a number!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // call backend return method
                String resultMessage = LibraryBackend.returnBook(txId);

                if (resultMessage.startsWith("SUCCESS")) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, resultMessage, "Return Complete", JOptionPane.INFORMATION_MESSAGE);
                    returnTxIdField.setText("");

                    // refresh tables and counts
                    LibraryBackend.loadTransactionsTable(transactionsModel);
                    LibraryBackend.loadBooksTable(booksModel);
                    refreshDashboardData();
                } else {
                    JOptionPane.showMessageDialog(LibraryFrontend.this, resultMessage, "Return Notice", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // refresh transactions
        refreshTxBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LibraryBackend.loadTransactionsTable(transactionsModel);
            }
        });

        return panel;
    }

    // login screen before main window opens
    public static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;

        public LoginFrame() {
            setTitle("LMS Login Portal");
            setSize(420, 300);
            setLocationRelativeTo(null); // center on screen
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setLayout(new BorderLayout());

            // top header banner
            JPanel topPanel = new JPanel();
            topPanel.setBackground(new Color(33, 50, 77));
            topPanel.setBorder(new EmptyBorder(15, 10, 15, 10));
            JLabel titleLabel = new JLabel("Library Management System");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            topPanel.add(titleLabel);

            // login form fields
            JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 15));
            centerPanel.setBorder(new EmptyBorder(25, 30, 15, 30));

            JLabel userLbl = new JLabel("Username:");
            userLbl.setFont(new Font("Arial", Font.BOLD, 13));
            usernameField = new JTextField();

            JLabel passLbl = new JLabel("Password:");
            passLbl.setFont(new Font("Arial", Font.BOLD, 13));
            passwordField = new JPasswordField();

            centerPanel.add(userLbl);
            centerPanel.add(usernameField);
            centerPanel.add(passLbl);
            centerPanel.add(passwordField);

            // credentials hint
            JLabel hintLbl = new JLabel("Credentials:");
            JLabel hintValLbl = new JLabel("admin / Amity@2004");
            hintValLbl.setForeground(Color.GRAY);
            hintValLbl.setFont(new Font("Arial", Font.ITALIC, 11));
            centerPanel.add(hintLbl);
            centerPanel.add(hintValLbl);

            // buttons
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
            JButton loginBtn = new JButton("Login");
            loginBtn.setFont(new Font("Arial", Font.BOLD, 13));
            loginBtn.setBackground(new Color(40, 167, 69));
            loginBtn.setForeground(Color.WHITE);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(new Font("Arial", Font.PLAIN, 13));

            bottomPanel.add(loginBtn);
            bottomPanel.add(cancelBtn);

            add(topPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            // pressing enter submits the login form
            getRootPane().setDefaultButton(loginBtn);

            // checking if username and pass match
            loginBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    if (LibraryBackend.validateLogin(username, password)) {
                        JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Login Successful! Welcome, Administrator.",
                            "Authentication Passed",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        dispose(); // close login window
                        new LibraryFrontend().setVisible(true); // open main dashboard
                    } else {
                        JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Invalid Username or Password!\nPlease check your credentials and try again.",
                            "Access Denied",
                            JOptionPane.ERROR_MESSAGE
                        );
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                }
            });

            // cancel button exits app
            cancelBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    // application main method
    public static void main(String[] args) {
        // use system theme for clean look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        // start login frame on the event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
