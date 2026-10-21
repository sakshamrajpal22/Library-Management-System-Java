import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * LibraryFrontend.java
 * High-contrast, professionally styled desktop UI for the Library Management System.
 * Developed in pure Java Swing and AWT for BCA Academic Syllabus Compliance.
 *
 * Implements explicit foreground, background, border, and selection colors across
 * all buttons, inputs, tables, dropdowns, and dialogs to prevent Look-and-Feel visibility issues.
 */
public class LibraryFrontend extends JFrame {

    // =========================================================================
    // HIGH-CONTRAST COLOR PALETTE CONSTANTS
    // =========================================================================
    public static final Color COLOR_PRIMARY = new Color(66, 109, 181);        // #426DB5 Dark Blue / Navy
    public static final Color COLOR_SIDEBAR = new Color(32, 51, 79);          // #20334F Dark Navy Sidebar
    public static final Color COLOR_SIDEBAR_TEXT = new Color(245, 247, 251);  // #F5F7FB Readable Light Text
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(66, 109, 181); // #426DB5 Distinct Active Blue
    public static final Color COLOR_BG = new Color(248, 250, 252);            // #F8FAFC Off-White Background
    public static final Color COLOR_CARD_BG = Color.WHITE;                    // #FFFFFF Pure White
    public static final Color COLOR_BORDER = new Color(203, 213, 225);        // #CBD5E1 Light Gray Border
    public static final Color COLOR_TEXT_MAIN = new Color(32, 51, 79);        // #20334F Dark Navy Header Text
    public static final Color COLOR_INPUT_TEXT = new Color(36, 54, 77);       // #24364D Dark Gray/Navy Input Text
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);    // #64748B Slate Muted Text

    public static final Color COLOR_SUCCESS = new Color(22, 163, 74);         // #16A34A Forest Green
    public static final Color COLOR_DANGER = new Color(192, 68, 68);          // #C04444 Destructive Red
    public static final Color COLOR_WARNING = new Color(217, 119, 6);         // #D97706 Amber
    public static final Color COLOR_INFO = new Color(14, 165, 233);           // #0EA5E9 Sky Blue
    public static final Color COLOR_GOLD = new Color(233, 185, 91);           // #E9B95B Warm Gold Accent

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT_NUM = new Font("Segoe UI", Font.BOLD, 36);

    // =========================================================================
    // GUI COMPONENTS & STATE
    // =========================================================================
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private List<ModernButton> navButtons = new ArrayList<>();

    // Status bar label
    private JLabel statusBarLabel;

    // Dashboard metrics labels
    private JLabel totalBooksLabel;
    private JLabel totalMembersLabel;
    private JLabel activeLoansLabel;
    private JLabel overdueLoansLabel;

    // Dashboard summary tables
    private JTable overdueSummaryTable;
    private DefaultTableModel overdueSummaryModel;
    private JTable recentTxSummaryTable;
    private DefaultTableModel recentTxSummaryModel;

    // Books Module components
    private JTextField bookIdField;
    private JTextField bookTitleField;
    private JTextField bookAuthorField;
    private JTextField bookPublisherField;
    private JTextField bookCopiesField;
    private JTextField bookSearchField;
    private JTable booksTable;
    private DefaultTableModel booksModel;

    // Members Module components
    private JTextField memberIdField;
    private JTextField memberNameField;
    private JTextField memberContactField;
    private JTextField memberSearchField;
    private JTable membersTable;
    private DefaultTableModel membersModel;

    // Issue & Return Module components
    private JComboBox<LibraryBackend.BookItem> issueBookComboBox;
    private JComboBox<LibraryBackend.MemberItem> issueMemberComboBox;
    private JTextField issueDaysField;
    private JTextField returnTxIdField;
    private JComboBox<String> txFilterComboBox;
    private JTextField txSearchField;
    private JTable transactionsTable;
    private DefaultTableModel transactionsModel;

    /**
     * Constructs the main application window.
     */
    public LibraryFrontend() {
        setTitle("Library Management System (LMS) - Viksit Bharat@2047 Academic Edition");
        setSize(1120, 740);
        setMinimumSize(new Dimension(960, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Sidebar Navigation (West)
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Central Multi-View Panel with CardLayout
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(COLOR_BG);

        cardsPanel.add(createDashboardCard(), "DASHBOARD");
        cardsPanel.add(createBooksCard(), "BOOKS");
        cardsPanel.add(createMembersCard(), "MEMBERS");
        cardsPanel.add(createTransactionsCard(), "TRANSACTIONS");

        add(cardsPanel, BorderLayout.CENTER);

        // 3. Bottom Status Bar (South)
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);

        // Load initial data from MySQL database
        refreshAllData();
    }

    // =========================================================================
    // NAVIGATION & SIDEBAR COMPONENT
    // =========================================================================

    /**
     * Builds the left sidebar navigation panel.
     */
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(265, 0));
        sidebar.setBackground(COLOR_SIDEBAR);

        // Header / Brand section
        JPanel brandPanel = new JPanel(new BorderLayout(0, 8));
        brandPanel.setBackground(COLOR_SIDEBAR);
        brandPanel.setBorder(new EmptyBorder(22, 14, 18, 14));

        // Horizontal brand header: Custom Library Logo placed beside LMS PORTAL branding
        JPanel brandHeader = new JPanel(new BorderLayout(10, 0));
        brandHeader.setBackground(COLOR_SIDEBAR);

        // Custom Open Book Logo component (36x36)
        LibraryLogoPanel logoPanel = new LibraryLogoPanel(36);
        brandHeader.add(logoPanel, BorderLayout.WEST);

        // Text branding container (LMS PORTAL + Viksit Bharat@2047 Edition)
        JPanel brandTextPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        brandTextPanel.setBackground(COLOR_SIDEBAR);

        JLabel brandIcon = new JLabel("LMS PORTAL");
        brandIcon.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brandIcon.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("Viksit Bharat@2047 Edition");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        brandSub.setForeground(new Color(180, 195, 215));

        brandTextPanel.add(brandIcon);
        brandTextPanel.add(brandSub);

        brandHeader.add(brandTextPanel, BorderLayout.CENTER);

        JLabel userBadge = new JLabel("Logged in: " + LibraryBackend.ADMIN_USER);
        userBadge.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        userBadge.setForeground(new Color(210, 225, 245));
        userBadge.setBorder(new EmptyBorder(0, 2, 0, 0));

        brandPanel.add(brandHeader, BorderLayout.CENTER);
        brandPanel.add(userBadge, BorderLayout.SOUTH);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Buttons list in Center
        JPanel navMenuPanel = new JPanel();
        navMenuPanel.setLayout(new BoxLayout(navMenuPanel, BoxLayout.Y_AXIS));
        navMenuPanel.setBackground(COLOR_SIDEBAR);
        navMenuPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        ModernButton navDashboard = createNavButton("  Overview Dashboard", "DASHBOARD");
        ModernButton navBooks = createNavButton("  Book Catalog", "BOOKS");
        ModernButton navMembers = createNavButton("  Member Directory", "MEMBERS");
        ModernButton navTransactions = createNavButton("  Issue & Return Loans", "TRANSACTIONS");

        navButtons.add(navDashboard);
        navButtons.add(navBooks);
        navButtons.add(navMembers);
        navButtons.add(navTransactions);

        // Select initial view
        setButtonActive(navDashboard);

        navMenuPanel.add(navDashboard);
        navMenuPanel.add(Box.createVerticalStrut(8));
        navMenuPanel.add(navBooks);
        navMenuPanel.add(Box.createVerticalStrut(8));
        navMenuPanel.add(navMembers);
        navMenuPanel.add(Box.createVerticalStrut(8));
        navMenuPanel.add(navTransactions);

        sidebar.add(navMenuPanel, BorderLayout.CENTER);

        // Footer / Logout Section
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(COLOR_SIDEBAR);
        footerPanel.setBorder(new EmptyBorder(15, 15, 20, 15));

        ModernButton logoutBtn = createDangerButton("Sign Out");
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    LibraryFrontend.this,
                    "Are you sure you want to log out of the admin portal?",
                    "Confirm Sign Out",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });

        footerPanel.add(logoutBtn, BorderLayout.CENTER);
        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Creates a navigation button styled specifically for the dark sidebar.
     */
    private ModernButton createNavButton(String title, final String cardName) {
        final ModernButton button = new ModernButton(title, COLOR_SIDEBAR, COLOR_SIDEBAR_TEXT, new Color(51, 65, 85));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView(cardName, button);
            }
        });

        return button;
    }

    /**
     * Switches the CardLayout and highlights the active button.
     */
    private void switchView(String cardName, ModernButton activeButton) {
        for (ModernButton b : navButtons) {
            b.setButtonColors(COLOR_SIDEBAR, COLOR_SIDEBAR_TEXT, new Color(51, 65, 85));
        }
        setButtonActive(activeButton);
        cardLayout.show(cardsPanel, cardName);

        if ("DASHBOARD".equals(cardName)) {
            refreshDashboardMetrics();
        }
    }

    private void setButtonActive(ModernButton btn) {
        btn.setButtonColors(COLOR_SIDEBAR_ACTIVE, Color.WHITE, null);
    }

    // =========================================================================
    // 1. DASHBOARD VIEW (STATISTIC CARDS & QUICK ACTIONS)
    // =========================================================================

    /**
     * Builds the overview dashboard with horizontal 1x4 stat cards, compact quick
     * actions toolbar, overdue loans table, and recent circulation activity table.
     */
    private JPanel createDashboardCard() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(18, 22, 18, 22));

        // 1. Top Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(COLOR_BG);

        JLabel titleLbl = new JLabel("System Overview & Inventory Analytics");
        titleLbl.setFont(FONT_TITLE);
        titleLbl.setForeground(COLOR_TEXT_MAIN);

        JLabel subtitleLbl = new JLabel("Real-time database aggregated metrics, overdue alerts, and circulation activity");
        subtitleLbl.setFont(FONT_SUBTITLE);
        subtitleLbl.setForeground(COLOR_TEXT_MUTED);

        topHeader.add(titleLbl, BorderLayout.NORTH);
        topHeader.add(subtitleLbl, BorderLayout.SOUTH);

        mainPanel.add(topHeader, BorderLayout.NORTH);

        // Center Content Container
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setBackground(COLOR_BG);

        // 2. Row 1: Four Stat Cards in ONE horizontal row
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        cardsGrid.setBackground(COLOR_BG);
        cardsGrid.setPreferredSize(new Dimension(0, 112));
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));

        totalBooksLabel = new JLabel("0", SwingConstants.CENTER);
        totalMembersLabel = new JLabel("0", SwingConstants.CENTER);
        activeLoansLabel = new JLabel("0", SwingConstants.CENTER);
        overdueLoansLabel = new JLabel("0", SwingConstants.CENTER);

        cardsGrid.add(createStatCard("Total Books Cataloged", totalBooksLabel, "Unique book titles cataloged", COLOR_PRIMARY));
        cardsGrid.add(createStatCard("Registered Members", totalMembersLabel, "Total registered members", COLOR_SUCCESS));
        cardsGrid.add(createStatCard("Active Book Loans", activeLoansLabel, "Books currently on loan", COLOR_WARNING));
        cardsGrid.add(createStatCard("Overdue Book Loans", overdueLoansLabel, "Loans past due date", COLOR_DANGER));

        centerContent.add(cardsGrid);
        centerContent.add(Box.createVerticalStrut(14));

        // 3. Row 2: Compact Quick Management Shortcuts & Live DB Status
        JPanel shortcutsCard = new JPanel(new BorderLayout(12, 0));
        shortcutsCard.setBackground(COLOR_CARD_BG);
        shortcutsCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(10, 16, 10, 16)
        ));
        shortcutsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JPanel shortcutsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        shortcutsLeft.setBackground(COLOR_CARD_BG);

        JLabel shortcutsLbl = new JLabel("Quick Actions:");
        shortcutsLbl.setFont(FONT_BOLD);
        shortcutsLbl.setForeground(COLOR_TEXT_MAIN);
        shortcutsLeft.add(shortcutsLbl);

        ModernButton goBooksBtn = createPrimaryButton("Add New Book");
        ModernButton goMembersBtn = createSuccessButton("Register Member");
        ModernButton goIssueBtn = createPrimaryButton("Issue / Return Book");
        ModernButton refreshDataBtn = createSecondaryButton("Refresh All Metrics");

        shortcutsLeft.add(goBooksBtn);
        shortcutsLeft.add(goMembersBtn);
        shortcutsLeft.add(goIssueBtn);
        shortcutsLeft.add(refreshDataBtn);

        JPanel shortcutsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        shortcutsRight.setBackground(COLOR_CARD_BG);

        JLabel dbStatusIndicator = new JLabel("● MySQL (Port 3306) Connected | ACID Safe");
        dbStatusIndicator.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dbStatusIndicator.setForeground(new Color(22, 101, 52));
        shortcutsRight.add(dbStatusIndicator);

        shortcutsCard.add(shortcutsLeft, BorderLayout.WEST);
        shortcutsCard.add(shortcutsRight, BorderLayout.EAST);

        centerContent.add(shortcutsCard);
        centerContent.add(Box.createVerticalStrut(14));

        // Wire shortcut buttons
        goBooksBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("BOOKS", navButtons.get(1));
            }
        });
        goMembersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("MEMBERS", navButtons.get(2));
            }
        });
        goIssueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("TRANSACTIONS", navButtons.get(3));
            }
        });
        refreshDataBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllData();
                JOptionPane.showMessageDialog(LibraryFrontend.this,
                    "Database metrics, overdue loans, and tables successfully refreshed!",
                    "System Refreshed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 4. Row 3: Two Split Tables (Overdue Loans on Left, Recent Transactions on Right)
        JPanel tablesGrid = new JPanel(new GridLayout(1, 2, 14, 0));
        tablesGrid.setBackground(COLOR_BG);

        // --- Card A: Outstanding Overdue Loans ---
        JPanel overdueCard = new JPanel(new BorderLayout(8, 8));
        overdueCard.setBackground(COLOR_CARD_BG);
        overdueCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel overdueHeader = new JPanel(new BorderLayout());
        overdueHeader.setBackground(COLOR_CARD_BG);
        JLabel overdueTitle = new JLabel("⚠️ Outstanding Overdue Loans");
        overdueTitle.setFont(FONT_HEADER);
        overdueTitle.setForeground(COLOR_DANGER);

        JLabel overdueSub = new JLabel("Loans past due date requiring recovery action");
        overdueSub.setFont(FONT_SUBTITLE);
        overdueSub.setForeground(COLOR_TEXT_MUTED);

        overdueHeader.add(overdueTitle, BorderLayout.NORTH);
        overdueHeader.add(overdueSub, BorderLayout.SOUTH);

        String[] overdueCols = {"Tx ID", "Book Title", "Borrower", "Due Date", "Overdue"};
        overdueSummaryModel = new DefaultTableModel(overdueCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        overdueSummaryTable = new JTable(overdueSummaryModel);
        styleTable(overdueSummaryTable);
        overdueSummaryTable.setRowHeight(26);

        DefaultTableCellRenderer centerRender = createCenterTableCellRenderer();
        overdueSummaryTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        overdueSummaryTable.getColumnModel().getColumn(3).setCellRenderer(centerRender);

        // Column 4 (Overdue badge) renderer in bold red
        overdueSummaryTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                if (isSel) {
                    comp.setBackground(COLOR_PRIMARY);
                    comp.setForeground(new Color(254, 202, 202));
                } else {
                    comp.setBackground(Color.WHITE);
                    comp.setForeground(COLOR_DANGER);
                }
                comp.setFont(FONT_BOLD);
                setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            }
        });

        JScrollPane overdueScroll = new JScrollPane(overdueSummaryTable);
        overdueScroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        overdueScroll.setPreferredSize(new Dimension(0, 190));

        JPanel overdueBottom = new JPanel(new BorderLayout());
        overdueBottom.setBackground(COLOR_CARD_BG);
        ModernButton resolveOverdueBtn = createDangerButton("Resolve in Circulation");
        JLabel overdueTip = new JLabel("<html><i>Double-click row or click Resolve to return</i></html>");
        overdueTip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        overdueTip.setForeground(COLOR_TEXT_MUTED);

        overdueBottom.add(overdueTip, BorderLayout.WEST);
        overdueBottom.add(resolveOverdueBtn, BorderLayout.EAST);

        // Action when Resolve or row is clicked
        ActionListener resolveAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = overdueSummaryTable.getSelectedRow();
                if (selectedRow == -1) {
                    if (overdueSummaryTable.getRowCount() > 0) {
                        selectedRow = 0; // Auto-select first overdue record
                        overdueSummaryTable.setRowSelectionInterval(0, 0);
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "There are currently no overdue loans to resolve! All loans are on schedule.",
                            "No Overdue Loans", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
                Object txId = overdueSummaryModel.getValueAt(selectedRow, 0);
                returnTxIdField.setText(String.valueOf(txId));
                switchView("TRANSACTIONS", navButtons.get(3));
            }
        };

        resolveOverdueBtn.addActionListener(resolveAction);
        overdueSummaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    resolveAction.actionPerformed(null);
                }
            }
        });

        overdueCard.add(overdueHeader, BorderLayout.NORTH);
        overdueCard.add(overdueScroll, BorderLayout.CENTER);
        overdueCard.add(overdueBottom, BorderLayout.SOUTH);

        // --- Card B: Recent Circulation Activity ---
        JPanel recentCard = new JPanel(new BorderLayout(8, 8));
        recentCard.setBackground(COLOR_CARD_BG);
        recentCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setBackground(COLOR_CARD_BG);
        JLabel recentTitle = new JLabel("🕒 Recent Circulation Activity");
        recentTitle.setFont(FONT_HEADER);
        recentTitle.setForeground(COLOR_TEXT_MAIN);

        JLabel recentSub = new JLabel("Latest actual borrowing and return transactions from database");
        recentSub.setFont(FONT_SUBTITLE);
        recentSub.setForeground(COLOR_TEXT_MUTED);

        recentHeader.add(recentTitle, BorderLayout.NORTH);
        recentHeader.add(recentSub, BorderLayout.SOUTH);

        String[] recentCols = {"Tx ID", "Book Title", "Member", "Date", "Status"};
        recentTxSummaryModel = new DefaultTableModel(recentCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recentTxSummaryTable = new JTable(recentTxSummaryModel);
        styleTable(recentTxSummaryTable);
        recentTxSummaryTable.setRowHeight(26);

        recentTxSummaryTable.getColumnModel().getColumn(0).setCellRenderer(centerRender);
        recentTxSummaryTable.getColumnModel().getColumn(3).setCellRenderer(centerRender);

        // Status renderer for recent activity
        recentTxSummaryTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                if (isSel) {
                    comp.setBackground(COLOR_PRIMARY);
                    comp.setForeground(Color.WHITE);
                } else {
                    comp.setBackground(Color.WHITE);
                    if (val != null) {
                        String s = val.toString();
                        if (s.contains("OVERDUE")) {
                            comp.setForeground(COLOR_DANGER);
                            comp.setFont(FONT_BOLD);
                        } else if (s.contains("Active")) {
                            comp.setForeground(COLOR_WARNING);
                            comp.setFont(FONT_BOLD);
                        } else {
                            comp.setForeground(COLOR_SUCCESS);
                            comp.setFont(FONT_BODY);
                        }
                    }
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            }
        });

        JScrollPane recentScroll = new JScrollPane(recentTxSummaryTable);
        recentScroll.setBorder(new LineBorder(COLOR_BORDER, 1));
        recentScroll.setPreferredSize(new Dimension(0, 190));

        JPanel recentBottom = new JPanel(new BorderLayout());
        recentBottom.setBackground(COLOR_CARD_BG);
        ModernButton viewAllTxBtn = createSecondaryButton("View Full History in Circulation");
        JLabel recentTip = new JLabel("<html><i>Showing latest 5 activity records</i></html>");
        recentTip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        recentTip.setForeground(COLOR_TEXT_MUTED);

        recentBottom.add(recentTip, BorderLayout.WEST);
        recentBottom.add(viewAllTxBtn, BorderLayout.EAST);

        viewAllTxBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchView("TRANSACTIONS", navButtons.get(3));
            }
        });

        recentCard.add(recentHeader, BorderLayout.NORTH);
        recentCard.add(recentScroll, BorderLayout.CENTER);
        recentCard.add(recentBottom, BorderLayout.SOUTH);

        tablesGrid.add(overdueCard);
        tablesGrid.add(recentCard);

        centerContent.add(tablesGrid);

        // Wrap center content in a responsive scroll pane so it adapts gracefully to any window size
        JScrollPane mainScroll = new JScrollPane(centerContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(mainScroll, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Builds an individual statistic card.
     */
    private JPanel createStatCard(String title, JLabel valueLabel, String subtitle, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(16, 18, 16, 18)
            )
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_HEADER);
        titleLbl.setForeground(COLOR_TEXT_MAIN);

        valueLabel.setFont(FONT_STAT_NUM);
        valueLabel.setForeground(accentColor);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_SUBTITLE);
        subLbl.setForeground(COLOR_TEXT_MUTED);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);

        return card;
    }

    // =========================================================================
    // 2. BOOKS MANAGEMENT MODULE (FULL CRUD & SEARCH)
    // =========================================================================

    /**
     * Builds the Book Management card panel.
     */
    private JPanel createBooksCard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Form & Search Container
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(COLOR_BG);

        // Form Panel
        JPanel formCard = new JPanel(new GridLayout(2, 5, 12, 10));
        formCard.setBackground(COLOR_CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(15, 18, 15, 18)
        ));

        bookIdField = createStyledTextField();
        bookIdField.setEditable(false);
        bookIdField.setBackground(new Color(241, 245, 249));
        bookIdField.setForeground(new Color(71, 85, 105));

        bookTitleField = createStyledTextField();
        bookAuthorField = createStyledTextField();
        bookPublisherField = createStyledTextField();
        bookCopiesField = createStyledTextField();

        // Row 1
        formCard.add(createFieldGroup("Book ID (Auto):", bookIdField));
        formCard.add(createFieldGroup("Book Title:*", bookTitleField));
        formCard.add(createFieldGroup("Author Name:*", bookAuthorField));
        formCard.add(createFieldGroup("Publisher:*", bookPublisherField));
        formCard.add(createFieldGroup("Available Copies:*", bookCopiesField));

        // Form Buttons Row
        JPanel formBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        formBtnRow.setBackground(COLOR_CARD_BG);

        ModernButton addBookBtn = createPrimaryButton("Add Book");
        ModernButton updateBookBtn = createSecondaryButton("Update Selected");
        ModernButton clearBookBtn = createSecondaryButton("Clear Form");

        formBtnRow.add(addBookBtn);
        formBtnRow.add(updateBookBtn);
        formBtnRow.add(clearBookBtn);

        // Search Bar Panel
        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchBarPanel.setBackground(COLOR_CARD_BG);

        JLabel searchLbl = new JLabel("Search Title / Author:");
        searchLbl.setFont(FONT_BOLD);
        searchLbl.setForeground(COLOR_TEXT_MAIN);

        bookSearchField = createStyledTextField();
        bookSearchField.setPreferredSize(new Dimension(180, 34));

        ModernButton searchBtn = createPrimaryButton("Search");
        ModernButton showAllBtn = createSecondaryButton("Show All");

        searchBarPanel.add(searchLbl);
        searchBarPanel.add(bookSearchField);
        searchBarPanel.add(searchBtn);
        searchBarPanel.add(showAllBtn);

        // Combine form buttons and search in a neat toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(COLOR_CARD_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        toolbar.add(formBtnRow, BorderLayout.WEST);
        toolbar.add(searchBarPanel, BorderLayout.EAST);

        topContainer.add(formCard);
        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(toolbar);

        panel.add(topContainer, BorderLayout.NORTH);

        // Books Table in Center
        String[] bookColumns = {"Book ID", "Title", "Author", "Publisher", "Available Copies"};
        booksModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        booksTable = new JTable(booksModel);
        styleTable(booksTable);

        // Center align ID and Copies columns
        DefaultTableCellRenderer centerRenderer = createCenterTableCellRenderer();
        booksTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        booksTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // When a row is clicked, populate fields for fast editing
        booksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = booksTable.getSelectedRow();
                    if (selectedRow != -1) {
                        bookIdField.setText(String.valueOf(booksModel.getValueAt(selectedRow, 0)));
                        bookTitleField.setText(String.valueOf(booksModel.getValueAt(selectedRow, 1)));
                        bookAuthorField.setText(String.valueOf(booksModel.getValueAt(selectedRow, 2)));
                        bookPublisherField.setText(String.valueOf(booksModel.getValueAt(selectedRow, 3)));
                        bookCopiesField.setText(String.valueOf(booksModel.getValueAt(selectedRow, 4)));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions (Delete & Refresh)
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(COLOR_BG);

        ModernButton deleteBookBtn = createDangerButton("Delete Selected Book");
        ModernButton refreshBooksBtn = createSecondaryButton("Refresh Table");

        bottomBar.add(deleteBookBtn);
        bottomBar.add(refreshBooksBtn);
        panel.add(bottomBar, BorderLayout.SOUTH);

        // ======================== EVENT LISTENERS ============================

        // Add Book
        addBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = bookTitleField.getText().trim();
                String author = bookAuthorField.getText().trim();
                String publisher = bookPublisherField.getText().trim();
                String copiesText = bookCopiesField.getText().trim();

                if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || copiesText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please fill in all required fields (Title, Author, Publisher, Copies).",
                        "Input Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int copies;
                try {
                    copies = Integer.parseInt(copiesText);
                    if (copies < 1) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Copies count must be at least 1.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Available copies must be a valid whole number.",
                        "Number Format Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = LibraryBackend.addBook(title, author, publisher, copies);
                if (success) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Book \"" + title + "\" was added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearBookFields();
                    LibraryBackend.loadBooksTable(booksModel);
                    refreshBooksDropdown(-1);
                    refreshDashboardMetrics();
                } else {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Failed to add book into database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Update Book
        updateBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idText = bookIdField.getText().trim();
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please select a book from the table below to update.",
                        "Select Record", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String title = bookTitleField.getText().trim();
                String author = bookAuthorField.getText().trim();
                String publisher = bookPublisherField.getText().trim();
                String copiesText = bookCopiesField.getText().trim();

                if (title.isEmpty() || author.isEmpty() || publisher.isEmpty() || copiesText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Fields cannot be empty during update.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int bookId = Integer.parseInt(idText);
                    int copies = Integer.parseInt(copiesText);
                    if (copies < 0) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Copies cannot be negative.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    boolean updated = LibraryBackend.updateBook(bookId, title, author, publisher, copies);
                    if (updated) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Book details updated successfully!",
                            "Update Completed", JOptionPane.INFORMATION_MESSAGE);
                        clearBookFields();
                        LibraryBackend.loadBooksTable(booksModel);
                        refreshBooksDropdown(bookId);
                        refreshDashboardMetrics();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Could not update book details.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Invalid number format in Copies or ID.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Clear Fields
        clearBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearBookFields();
            }
        });

        // Delete Book
        deleteBookBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please select a book from the table to delete.",
                        "No Book Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int bookId = (int) booksModel.getValueAt(selectedRow, 0);
                String title = (String) booksModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(
                    LibraryFrontend.this,
                    "Are you sure you want to delete book \"" + title + "\" (ID: " + bookId + ")?\n"
                    + "Warning: This will also cascade delete all associated transaction records.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = LibraryBackend.deleteBook(bookId);
                    if (deleted) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Book deleted successfully!",
                            "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        clearBookFields();
                        LibraryBackend.loadBooksTable(booksModel);
                        LibraryBackend.loadTransactionsTable(transactionsModel);
                        refreshBooksDropdown(-1);
                        refreshDashboardMetrics();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Failed to delete book.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Search Books
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = bookSearchField.getText().trim();
                if (query.isEmpty()) {
                    LibraryBackend.loadBooksTable(booksModel);
                } else {
                    LibraryBackend.searchBooksTable(booksModel, query);
                }
            }
        });

        // Search on Enter key
        bookSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBtn.doClick();
                }
            }
        });

        // Show All Books
        showAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSearchField.setText("");
                LibraryBackend.loadBooksTable(booksModel);
            }
        });

        // Refresh Table
        refreshBooksBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibraryBackend.loadBooksTable(booksModel);
                refreshBooksDropdown(-1);
            }
        });

        return panel;
    }

    private void clearBookFields() {
        bookIdField.setText("");
        bookTitleField.setText("");
        bookAuthorField.setText("");
        bookPublisherField.setText("");
        bookCopiesField.setText("");
        booksTable.clearSelection();
    }

    // =========================================================================
    // 3. MEMBER MANAGEMENT MODULE (FULL CRUD & SEARCH)
    // =========================================================================

    /**
     * Builds the Member Management card panel.
     */
    private JPanel createMembersCard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Form & Search Container
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(COLOR_BG);

        // Form Panel
        JPanel formCard = new JPanel(new GridLayout(1, 3, 15, 10));
        formCard.setBackground(COLOR_CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(15, 18, 15, 18)
        ));

        memberIdField = createStyledTextField();
        memberIdField.setEditable(false);
        memberIdField.setBackground(new Color(241, 245, 249));
        memberIdField.setForeground(new Color(71, 85, 105));

        memberNameField = createStyledTextField();
        memberContactField = createStyledTextField();

        formCard.add(createFieldGroup("Member ID (Auto):", memberIdField));
        formCard.add(createFieldGroup("Full Name:*", memberNameField));
        formCard.add(createFieldGroup("Contact / Phone Number:*", memberContactField));

        // Toolbar: Buttons & Search
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(COLOR_CARD_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel formBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        formBtnRow.setBackground(COLOR_CARD_BG);

        ModernButton addMemberBtn = createPrimaryButton("Register Member");
        ModernButton updateMemberBtn = createSecondaryButton("Update Selected");
        ModernButton clearMemberBtn = createSecondaryButton("Clear Form");

        formBtnRow.add(addMemberBtn);
        formBtnRow.add(updateMemberBtn);
        formBtnRow.add(clearMemberBtn);

        JPanel searchBarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchBarPanel.setBackground(COLOR_CARD_BG);

        JLabel searchLbl = new JLabel("Search Name / Contact:");
        searchLbl.setFont(FONT_BOLD);
        searchLbl.setForeground(COLOR_TEXT_MAIN);

        memberSearchField = createStyledTextField();
        memberSearchField.setPreferredSize(new Dimension(180, 34));

        ModernButton searchMemberBtn = createPrimaryButton("Search");
        ModernButton showAllMembersBtn = createSecondaryButton("Show All");

        searchBarPanel.add(searchLbl);
        searchBarPanel.add(memberSearchField);
        searchBarPanel.add(searchMemberBtn);
        searchBarPanel.add(showAllMembersBtn);

        toolbar.add(formBtnRow, BorderLayout.WEST);
        toolbar.add(searchBarPanel, BorderLayout.EAST);

        topContainer.add(formCard);
        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(toolbar);

        panel.add(topContainer, BorderLayout.NORTH);

        // Members Table in Center
        String[] memberColumns = {"Member ID", "Full Name", "Contact Number"};
        membersModel = new DefaultTableModel(memberColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        membersTable = new JTable(membersModel);
        styleTable(membersTable);

        DefaultTableCellRenderer centerRenderer = createCenterTableCellRenderer();
        membersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Row Selection updates input fields
        membersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = membersTable.getSelectedRow();
                    if (selectedRow != -1) {
                        memberIdField.setText(String.valueOf(membersModel.getValueAt(selectedRow, 0)));
                        memberNameField.setText(String.valueOf(membersModel.getValueAt(selectedRow, 1)));
                        memberContactField.setText(String.valueOf(membersModel.getValueAt(selectedRow, 2)));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(membersTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions (Delete & Refresh)
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(COLOR_BG);

        ModernButton deleteMemberBtn = createDangerButton("Delete Selected Member");
        ModernButton refreshMembersBtn = createSecondaryButton("Refresh Table");

        bottomBar.add(deleteMemberBtn);
        bottomBar.add(refreshMembersBtn);
        panel.add(bottomBar, BorderLayout.SOUTH);

        // ======================== EVENT LISTENERS ============================

        // Add Member
        addMemberBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = memberNameField.getText().trim();
                String contact = memberContactField.getText().trim();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please provide member name and contact information.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int newMemberId = LibraryBackend.registerMemberAndGetId(name, contact);
                if (newMemberId > 0) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Member \"" + name + "\" (ID: " + newMemberId + ") registered successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearMemberFields();
                    LibraryBackend.loadMembersTable(membersModel);
                    refreshMembersDropdown(newMemberId);
                    refreshDashboardMetrics();
                } else {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Failed to register member in database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Update Member
        updateMemberBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idText = memberIdField.getText().trim();
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please select a member from the table to update.",
                        "Select Record", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String name = memberNameField.getText().trim();
                String contact = memberContactField.getText().trim();

                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Name and Contact fields cannot be empty.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int memberId = Integer.parseInt(idText);
                    boolean updated = LibraryBackend.updateMember(memberId, name, contact);
                    if (updated) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Member information updated successfully!",
                            "Update Completed", JOptionPane.INFORMATION_MESSAGE);
                        clearMemberFields();
                        LibraryBackend.loadMembersTable(membersModel);
                        refreshMembersDropdown(memberId);
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Failed to update member.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Invalid Member ID.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Clear Member Fields
        clearMemberBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMemberFields();
            }
        });

        // Delete Member
        deleteMemberBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = membersTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please select a member from the table to delete.",
                        "Notice", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int memberId = (int) membersModel.getValueAt(selectedRow, 0);
                String name = (String) membersModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(
                    LibraryFrontend.this,
                    "Are you sure you want to delete member \"" + name + "\" (ID: " + memberId + ")?\n"
                    + "Warning: This will also cascade delete all loan transactions for this member.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = LibraryBackend.deleteMember(memberId);
                    if (deleted) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Member deleted successfully!",
                            "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        clearMemberFields();
                        LibraryBackend.loadMembersTable(membersModel);
                        LibraryBackend.loadTransactionsTable(transactionsModel);
                        refreshMembersDropdown(-1);
                        refreshDashboardMetrics();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Failed to delete member.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Search Members
        searchMemberBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = memberSearchField.getText().trim();
                if (keyword.isEmpty()) {
                    LibraryBackend.loadMembersTable(membersModel);
                } else {
                    LibraryBackend.searchMembersTable(membersModel, keyword);
                }
            }
        });

        memberSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchMemberBtn.doClick();
                }
            }
        });

        // Show All Members
        showAllMembersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                memberSearchField.setText("");
                LibraryBackend.loadMembersTable(membersModel);
            }
        });

        // Refresh Table
        refreshMembersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibraryBackend.loadMembersTable(membersModel);
                refreshMembersDropdown(-1);
            }
        });

        return panel;
    }

    private void clearMemberFields() {
        memberIdField.setText("");
        memberNameField.setText("");
        memberContactField.setText("");
        membersTable.clearSelection();
    }

    // =========================================================================
    // 4. ISSUE & RETURN TRANSACTIONS (ATOMIC TRANSACTION OPERATIONS)
    // =========================================================================

    /**
     * Builds the Issue & Return transactions panel with loan history table.
     */
    private JPanel createTransactionsCard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Split: Issue Book Form (Left) & Return Book Form (Right)
        JPanel formsContainer = new JPanel(new GridLayout(1, 2, 18, 0));
        formsContainer.setBackground(COLOR_BG);

        // --- Card 1: Issue Book Form ---
        JPanel issueCard = new JPanel(new BorderLayout(10, 10));
        issueCard.setBackground(COLOR_CARD_BG);
        issueCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel issueHeader = new JLabel("Issue Book to Member");
        issueHeader.setFont(FONT_HEADER);
        issueHeader.setForeground(COLOR_TEXT_MAIN);

        JPanel issueInputs = new JPanel(new GridBagLayout());
        issueInputs.setBackground(COLOR_CARD_BG);

        issueBookComboBox = new JComboBox<>();
        styleComboBox(issueBookComboBox);
        issueBookComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                if (value instanceof LibraryBackend.BookItem) {
                    LibraryBackend.BookItem b = (LibraryBackend.BookItem) value;
                    lbl.setText(b.getTitle() + " - " + b.getAuthor() + " (ID: " + b.getId() + ", Available: " + b.getCopies() + ")");
                }
                if (isSelected) {
                    lbl.setBackground(COLOR_PRIMARY);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(COLOR_INPUT_TEXT);
                }
                return lbl;
            }
        });

        issueMemberComboBox = new JComboBox<>();
        styleComboBox(issueMemberComboBox);
        issueMemberComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                if (value instanceof LibraryBackend.MemberItem) {
                    LibraryBackend.MemberItem m = (LibraryBackend.MemberItem) value;
                    lbl.setText("ID " + m.getId() + ": " + m.getName() + " (" + m.getContact() + ")");
                }
                if (isSelected) {
                    lbl.setBackground(COLOR_PRIMARY);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(COLOR_INPUT_TEXT);
                }
                return lbl;
            }
        });

        ModernButton registerMemberQuickBtn = createSuccessButton("+ Register New Member");
        registerMemberQuickBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerMemberQuickBtn.setPreferredSize(new Dimension(170, 34));
        registerMemberQuickBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterMemberDialog dlg = new RegisterMemberDialog(LibraryFrontend.this);
                dlg.setVisible(true);
            }
        });

        issueDaysField = createStyledTextField();
        issueDaysField.setText("14"); // Standard 14-day academic loan

        JLabel bLbl = new JLabel("Select Book:*");
        bLbl.setFont(FONT_BOLD);
        bLbl.setForeground(COLOR_TEXT_MAIN);

        JLabel mLbl = new JLabel("Select Member:*");
        mLbl.setFont(FONT_BOLD);
        mLbl.setForeground(COLOR_TEXT_MAIN);

        JLabel daysLbl = new JLabel("Loan Duration (Days):*");
        daysLbl.setFont(FONT_BOLD);
        daysLbl.setForeground(COLOR_TEXT_MAIN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Select Book
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        issueInputs.add(bLbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        issueInputs.add(issueBookComboBox, gbc);

        // Row 1: Select Member
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        issueInputs.add(mLbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        issueInputs.add(issueMemberComboBox, gbc);

        // Row 2: + Register New Member button below member dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        issueInputs.add(new JLabel(""), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        issueInputs.add(registerMemberQuickBtn, gbc);

        // Row 3: Loan Duration
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        issueInputs.add(daysLbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        issueInputs.add(issueDaysField, gbc);

        JPanel issueBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        issueBtnRow.setBackground(COLOR_CARD_BG);
        ModernButton issueBtn = createPrimaryButton("Process Loan (Issue)");
        issueBtnRow.add(issueBtn);

        issueCard.add(issueHeader, BorderLayout.NORTH);
        issueCard.add(issueInputs, BorderLayout.CENTER);
        issueCard.add(issueBtnRow, BorderLayout.SOUTH);

        // --- Card 2: Return Book Form ---
        JPanel returnCard = new JPanel(new BorderLayout(10, 10));
        returnCard.setBackground(COLOR_CARD_BG);
        returnCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel returnHeader = new JLabel("Return Borrowed Book");
        returnHeader.setFont(FONT_HEADER);
        returnHeader.setForeground(COLOR_TEXT_MAIN);

        JPanel returnInputs = new JPanel(new GridLayout(2, 2, 10, 10));
        returnInputs.setBackground(COLOR_CARD_BG);

        returnTxIdField = createStyledTextField();

        JLabel txIdLbl = new JLabel("Transaction ID:*");
        txIdLbl.setFont(FONT_BOLD);
        txIdLbl.setForeground(COLOR_TEXT_MAIN);

        JLabel tipTitleLbl = new JLabel("Quick Action:");
        tipTitleLbl.setFont(FONT_BOLD);
        tipTitleLbl.setForeground(COLOR_TEXT_MAIN);

        JLabel tipLabel = new JLabel("<html><i>Click any active loan row in the table below to auto-fill ID.</i></html>");
        tipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tipLabel.setForeground(COLOR_TEXT_MUTED);

        returnInputs.add(txIdLbl);
        returnInputs.add(returnTxIdField);
        returnInputs.add(tipTitleLbl);
        returnInputs.add(tipLabel);

        JPanel returnBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        returnBtnRow.setBackground(COLOR_CARD_BG);
        ModernButton returnBtn = createSuccessButton("Process Return");
        returnBtnRow.add(returnBtn);

        returnCard.add(returnHeader, BorderLayout.NORTH);
        returnCard.add(returnInputs, BorderLayout.CENTER);
        returnCard.add(returnBtnRow, BorderLayout.SOUTH);

        formsContainer.add(issueCard);
        formsContainer.add(returnCard);

        // Filter & Search Toolbar for Circulation History
        JPanel filterToolbar = new JPanel(new BorderLayout());
        filterToolbar.setBackground(COLOR_CARD_BG);
        filterToolbar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel filterLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterLeft.setBackground(COLOR_CARD_BG);

        JLabel filterLbl = new JLabel("Filter History:");
        filterLbl.setFont(FONT_BOLD);
        filterLbl.setForeground(COLOR_TEXT_MAIN);

        String[] filters = {"All Transactions", "Active Loans Only", "Overdue Loans Only", "Returned Books Only"};
        txFilterComboBox = new JComboBox<>(filters);
        txFilterComboBox.setFont(FONT_BODY);
        txFilterComboBox.setBackground(Color.WHITE);
        txFilterComboBox.setForeground(COLOR_INPUT_TEXT);
        txFilterComboBox.setPreferredSize(new Dimension(190, 34));
        txFilterComboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(2, 6, 2, 6)
        ));

        // Custom list cell renderer to ensure high contrast dropdown items
        txFilterComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBorder(new EmptyBorder(6, 10, 6, 10));
                if (isSelected) {
                    lbl.setBackground(COLOR_PRIMARY);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(COLOR_INPUT_TEXT);
                }
                return lbl;
            }
        });

        filterLeft.add(filterLbl);
        filterLeft.add(txFilterComboBox);

        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        filterRight.setBackground(COLOR_CARD_BG);

        JLabel searchTxLbl = new JLabel("Search Title / Member:");
        searchTxLbl.setFont(FONT_BOLD);
        searchTxLbl.setForeground(COLOR_TEXT_MAIN);

        txSearchField = createStyledTextField();
        txSearchField.setPreferredSize(new Dimension(170, 34));

        ModernButton searchTxBtn = createPrimaryButton("Filter");
        ModernButton resetTxBtn = createSecondaryButton("Reset");

        filterRight.add(searchTxLbl);
        filterRight.add(txSearchField);
        filterRight.add(searchTxBtn);
        filterRight.add(resetTxBtn);

        filterToolbar.add(filterLeft, BorderLayout.WEST);
        filterToolbar.add(filterRight, BorderLayout.EAST);

        // North combined container
        JPanel northCombined = new JPanel();
        northCombined.setLayout(new BoxLayout(northCombined, BoxLayout.Y_AXIS));
        northCombined.setBackground(COLOR_BG);
        northCombined.add(formsContainer);
        northCombined.add(Box.createVerticalStrut(12));
        northCombined.add(filterToolbar);

        panel.add(northCombined, BorderLayout.NORTH);

        // Table for Transaction History
        String[] txColumns = {
            "Tx ID", "Book ID", "Book Title", "Member ID", "Member Name",
            "Borrow Date", "Due Date", "Return Date", "Loan Status"
        };
        transactionsModel = new DefaultTableModel(txColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionsTable = new JTable(transactionsModel);
        styleTable(transactionsTable);

        // Center align IDs and Dates
        DefaultTableCellRenderer centerRenderer = createCenterTableCellRenderer();
        transactionsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        // Custom Status Badge Renderer for Loan Status
        transactionsTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (isSelected) {
                    c.setBackground(COLOR_PRIMARY);
                    if (value != null) {
                        String str = value.toString();
                        if (str.contains("OVERDUE")) {
                            c.setForeground(new Color(254, 202, 202)); // Soft readable light red
                            c.setFont(FONT_BOLD);
                        } else if (str.contains("Active")) {
                            c.setForeground(new Color(253, 230, 138)); // Soft readable light gold
                            c.setFont(FONT_BOLD);
                        } else {
                            c.setForeground(new Color(187, 247, 208)); // Soft readable mint green
                            c.setFont(FONT_BODY);
                        }
                    }
                } else {
                    c.setBackground(Color.WHITE);
                    if (value != null) {
                        String str = value.toString();
                        if (str.contains("OVERDUE")) {
                            c.setForeground(COLOR_DANGER); // #C04444
                            c.setFont(FONT_BOLD);
                        } else if (str.contains("Active")) {
                            c.setForeground(COLOR_WARNING); // #D97706
                            c.setFont(FONT_BOLD);
                        } else {
                            c.setForeground(COLOR_SUCCESS); // #16A34A
                            c.setFont(FONT_BODY);
                        }
                    }
                }
                return c;
            }
        });

        // Clicking a row in transactions table auto-populates the Return Tx ID
        transactionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = transactionsTable.getSelectedRow();
                if (selectedRow != -1) {
                    Object txId = transactionsModel.getValueAt(selectedRow, 0);
                    returnTxIdField.setText(String.valueOf(txId));
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomBar.setBackground(COLOR_BG);
        ModernButton refreshTxBtn = createSecondaryButton("Refresh Transaction History");
        bottomBar.add(refreshTxBtn);
        panel.add(bottomBar, BorderLayout.SOUTH);

        // ======================== EVENT LISTENERS ============================

        // Process Issue
        issueBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LibraryBackend.BookItem selectedBook = (LibraryBackend.BookItem) issueBookComboBox.getSelectedItem();
                LibraryBackend.MemberItem selectedMember = (LibraryBackend.MemberItem) issueMemberComboBox.getSelectedItem();
                String daysText = issueDaysField.getText().trim();

                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please select an available book from the dropdown.",
                        "No Book Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (selectedMember == null) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please select a member or click \"+ Register New Member\" to register one.",
                        "No Member Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (daysText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please enter the loan duration in days.",
                        "Incomplete Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int loanDays = Integer.parseInt(daysText);
                    if (loanDays < 1) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            "Loan duration must be at least 1 day.",
                            "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int bookId = selectedBook.getId();
                    int memberId = selectedMember.getId();

                    // Atomic Issue transaction
                    String result = LibraryBackend.issueBook(bookId, memberId, loanDays);

                    if (result.startsWith("SUCCESS")) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            result, "Loan Issued", JOptionPane.INFORMATION_MESSAGE);
                        issueDaysField.setText("14");

                        refreshAllData();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            result, "Issue Operation Failed", JOptionPane.WARNING_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Loan Days must be a valid whole number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Process Return
        returnBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String txText = returnTxIdField.getText().trim();
                if (txText.isEmpty()) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Please specify or select a Transaction ID to return.",
                        "Missing Transaction ID", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int txId = Integer.parseInt(txText);
                    String result = LibraryBackend.returnBook(txId);

                    if (result.startsWith("SUCCESS")) {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            result, "Book Returned", JOptionPane.INFORMATION_MESSAGE);
                        returnTxIdField.setText("");
                        refreshAllData();
                    } else {
                        JOptionPane.showMessageDialog(LibraryFrontend.this,
                            result, "Return Notice", JOptionPane.WARNING_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LibraryFrontend.this,
                        "Transaction ID must be a numeric value.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Filter / Search Action
        ActionListener filterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = txFilterComboBox.getSelectedIndex();
                String filterType = "ALL";
                if (selectedIndex == 1) filterType = "ACTIVE";
                else if (selectedIndex == 2) filterType = "OVERDUE";
                else if (selectedIndex == 3) filterType = "RETURNED";

                String query = txSearchField.getText().trim();
                LibraryBackend.loadTransactionsFiltered(transactionsModel, filterType, query);
            }
        };

        searchTxBtn.addActionListener(filterAction);
        txFilterComboBox.addActionListener(filterAction);
        txSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchTxBtn.doClick();
                }
            }
        });

        resetTxBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txFilterComboBox.setSelectedIndex(0);
                txSearchField.setText("");
                LibraryBackend.loadTransactionsFiltered(transactionsModel, "ALL", "");
            }
        });

        refreshTxBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterAction.actionPerformed(e);
            }
        });

        return panel;
    }

    // =========================================================================
    // STATUS BAR & DATA REFRESH HELPERS
    // =========================================================================

    /**
     * Builds the bottom status bar displaying operational status.
     */
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(241, 245, 249));
        bar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1),
            new EmptyBorder(6, 16, 6, 16)
        ));

        statusBarLabel = new JLabel("● Database: Connected to MySQL (library_db) | Host: localhost:3306 | System Ready");
        statusBarLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusBarLabel.setForeground(new Color(22, 101, 52)); // Dark forest green

        JLabel creditsLabel = new JLabel("BCA Academic Project | Viksit Bharat@2047");
        creditsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        creditsLabel.setForeground(COLOR_TEXT_MUTED);

        bar.add(statusBarLabel, BorderLayout.WEST);
        bar.add(creditsLabel, BorderLayout.EAST);
        return bar;
    }

    /**
     * Refreshes all tables, dropdowns, and dashboard counts from MySQL.
     */
    public void refreshAllData() {
        refreshDashboardMetrics();
        if (booksModel != null) LibraryBackend.loadBooksTable(booksModel);
        if (membersModel != null) LibraryBackend.loadMembersTable(membersModel);
        if (transactionsModel != null) LibraryBackend.loadTransactionsTable(transactionsModel);
        refreshMembersDropdown(-1);
        refreshBooksDropdown(-1);
    }

    /**
     * Refreshes the Member dropdown list on the Circulation panel from MySQL Members table.
     * If selectMemberId > 0, that member is selected; otherwise preserves current selection if possible.
     */
    public void refreshMembersDropdown(int selectMemberId) {
        if (issueMemberComboBox == null) return;

        int targetId = selectMemberId;
        if (targetId <= 0) {
            LibraryBackend.MemberItem current = (LibraryBackend.MemberItem) issueMemberComboBox.getSelectedItem();
            if (current != null) {
                targetId = current.getId();
            }
        }

        issueMemberComboBox.removeAllItems();
        List<LibraryBackend.MemberItem> members = LibraryBackend.getAllMembersList();
        LibraryBackend.MemberItem toSelect = null;
        for (LibraryBackend.MemberItem m : members) {
            issueMemberComboBox.addItem(m);
            if (m.getId() == targetId) {
                toSelect = m;
            }
        }

        if (toSelect != null) {
            issueMemberComboBox.setSelectedItem(toSelect);
        } else if (issueMemberComboBox.getItemCount() > 0) {
            issueMemberComboBox.setSelectedIndex(0);
        }
    }

    /**
     * Refreshes the Book dropdown list on the Circulation panel from MySQL Books table.
     * Preserves current selection if available.
     */
    public void refreshBooksDropdown(int selectBookId) {
        if (issueBookComboBox == null) return;

        int targetId = selectBookId;
        if (targetId <= 0) {
            LibraryBackend.BookItem current = (LibraryBackend.BookItem) issueBookComboBox.getSelectedItem();
            if (current != null) {
                targetId = current.getId();
            }
        }

        issueBookComboBox.removeAllItems();
        List<LibraryBackend.BookItem> books = LibraryBackend.getAllBooksList();
        LibraryBackend.BookItem toSelect = null;
        for (LibraryBackend.BookItem b : books) {
            issueBookComboBox.addItem(b);
            if (b.getId() == targetId) {
                toSelect = b;
            }
        }

        if (toSelect != null) {
            issueBookComboBox.setSelectedItem(toSelect);
        } else if (issueBookComboBox.getItemCount() > 0) {
            issueBookComboBox.setSelectedIndex(0);
        }
    }

    /**
     * Updates the aggregate statistics on the dashboard.
     */
    private void refreshDashboardMetrics() {
        if (totalBooksLabel != null) {
            totalBooksLabel.setText(String.valueOf(LibraryBackend.getTotalBooksCount()));
            totalMembersLabel.setText(String.valueOf(LibraryBackend.getTotalMembersCount()));
            activeLoansLabel.setText(String.valueOf(LibraryBackend.getActiveLoansCount()));
            overdueLoansLabel.setText(String.valueOf(LibraryBackend.getOverdueLoansCount()));
        }
        if (overdueSummaryModel != null) {
            LibraryBackend.loadOverdueLoansSummary(overdueSummaryModel);
        }
        if (recentTxSummaryModel != null) {
            LibraryBackend.loadRecentTransactionsSummary(recentTxSummaryModel, 5);
        }
    }

    // =========================================================================
    // MODERN HIGH-CONTRAST BUTTON & INPUT BUILDERS
    // =========================================================================

    /**
     * ModernButton extends JButton and bypasses native Look-and-Feel button
     * painting issues by rendering its own anti-aliased background, border,
     * and high-contrast text states.
     */
    public static class ModernButton extends JButton {
        private Color normalBg;
        private Color hoverBg;
        private Color pressedBg;
        private Color normalFg;
        private Color borderColor;
        private int cornerRadius = 6;

        public ModernButton(String text, Color bg, Color fg) {
            this(text, bg, fg, null);
        }

        public ModernButton(String text, Color bg, Color fg, Color border) {
            super(text);
            this.normalBg = bg;
            this.normalFg = fg;
            this.borderColor = border;
            this.hoverBg = computeHoverColor(bg);
            this.pressedBg = computePressedColor(bg);

            setFont(FONT_BOLD);
            setForeground(fg);
            setBackground(bg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 16, 8, 16));
        }

        public void setButtonColors(Color bg, Color fg, Color border) {
            this.normalBg = bg;
            this.normalFg = fg;
            this.borderColor = border;
            this.hoverBg = computeHoverColor(bg);
            this.pressedBg = computePressedColor(bg);
            setForeground(fg);
            setBackground(bg);
            repaint();
        }

        private Color computeHoverColor(Color c) {
            if (c == null) return null;
            // For dark backgrounds (like sidebar #20334F)
            if (c.getRed() < 60 && c.getGreen() < 80 && c.getBlue() < 120) {
                return new Color(
                    Math.min(255, c.getRed() + 20),
                    Math.min(255, c.getGreen() + 25),
                    Math.min(255, c.getBlue() + 35)
                );
            }
            // For white or very light backgrounds
            if (c.getRed() > 220 && c.getGreen() > 220 && c.getBlue() > 220) {
                return new Color(241, 245, 249);
            }
            // For standard colors: slightly darker shade
            return new Color(
                Math.max(0, (int)(c.getRed() * 0.88)),
                Math.max(0, (int)(c.getGreen() * 0.88)),
                Math.max(0, (int)(c.getBlue() * 0.88))
            );
        }

        private Color computePressedColor(Color c) {
            if (c == null) return null;
            return new Color(
                Math.max(0, (int)(c.getRed() * 0.78)),
                Math.max(0, (int)(c.getGreen() * 0.78)),
                Math.max(0, (int)(c.getBlue() * 0.78))
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (!isEnabled()) {
                g2.setColor(new Color(241, 245, 249));
                g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
                g2.setColor(new Color(203, 213, 225));
                g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
                setForeground(new Color(148, 163, 184));
            } else {
                Color currentBg = normalBg;
                if (getModel().isPressed()) {
                    currentBg = pressedBg;
                } else if (getModel().isRollover()) {
                    currentBg = hoverBg;
                }

                if (currentBg != null) {
                    g2.setColor(currentBg);
                    g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
                }

                if (borderColor != null) {
                    g2.setColor(borderColor);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
                }

                setForeground(normalFg);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Primary button: Dark Navy / Blue background (#426DB5) with white text.
     */
    public static ModernButton createPrimaryButton(String text) {
        return new ModernButton(text, COLOR_PRIMARY, Color.WHITE);
    }

    /**
     * Secondary button: White background with dark navy text (#20334F) and visible border (#CBD5E1).
     */
    public static ModernButton createSecondaryButton(String text) {
        return new ModernButton(text, Color.WHITE, COLOR_SIDEBAR, COLOR_BORDER);
    }

    /**
     * Destructive button: Red background (#C04444) with white text.
     */
    public static ModernButton createDangerButton(String text) {
        return new ModernButton(text, COLOR_DANGER, Color.WHITE);
    }

    /**
     * Success button: Green background (#16A34A) with white text.
     */
    public static ModernButton createSuccessButton(String text) {
        return new ModernButton(text, COLOR_SUCCESS, Color.WHITE);
    }

    /**
     * Creates a standardized text field with explicit high-contrast colors,
     * visible border, and blue accent focus indicator.
     */
    public static JTextField createStyledTextField() {
        final JTextField tf = new JTextField();
        applyInputStyle(tf);
        return tf;
    }

    private static void applyInputStyle(final javax.swing.text.JTextComponent tc) {
        tc.setFont(FONT_BODY);
        tc.setBackground(Color.WHITE);
        tc.setForeground(COLOR_INPUT_TEXT);
        tc.setCaretColor(COLOR_INPUT_TEXT);
        tc.setSelectedTextColor(Color.WHITE);
        tc.setSelectionColor(COLOR_PRIMARY);

        final Border normalBorder = BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        );
        final Border focusBorder = BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_PRIMARY, 2, true),
            new EmptyBorder(5, 9, 5, 9)
        );

        tc.setBorder(normalBorder);

        tc.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tc.isEditable()) {
                    tc.setBorder(focusBorder);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                tc.setBorder(normalBorder);
            }
        });
    }

    /**
     * Helper to wrap a label and an input component into a compact vertical group.
     */
    private static JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setBackground(COLOR_CARD_BG);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MAIN);

        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group;
    }

    /**
     * Styles a standard JComboBox for high contrast, clean typography, and visible border.
     */
    public static void styleComboBox(JComboBox<?> cb) {
        cb.setFont(FONT_BODY);
        cb.setBackground(Color.WHITE);
        cb.setForeground(COLOR_INPUT_TEXT);
        cb.setPreferredSize(new Dimension(240, 34));
        cb.setMinimumSize(new Dimension(160, 34));
        cb.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(2, 6, 2, 6)
        ));
    }

    /**
     * Styles standard JTable for clean corporate / academic presentation.
     */
    private static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BODY);
        table.setBackground(Color.WHITE);
        table.setForeground(COLOR_INPUT_TEXT);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(COLOR_SIDEBAR);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Explicit Header Renderer to guarantee background and foreground across all Look and Feels
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setBackground(new Color(241, 245, 249));
                lbl.setForeground(COLOR_SIDEBAR);
                lbl.setFont(FONT_BOLD);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 1, COLOR_BORDER),
                    new EmptyBorder(6, 10, 6, 10)
                ));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    /**
     * Reusable centered table cell renderer that maintains proper selection contrast.
     */
    private static DefaultTableCellRenderer createCenterTableCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (isSelected) {
                    c.setBackground(COLOR_PRIMARY);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(COLOR_INPUT_TEXT);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };
    }

    // =========================================================================
    // LOGIN PORTAL WINDOW (STUDENT/EXAMINER FRIENDLY)
    // =========================================================================

    /**
     * LoginFrame provides an organized authentication dialog with password toggle
     * and high-contrast inputs and buttons.
     */
    public static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JCheckBox showPasswordCheck;

        public LoginFrame() {
            setTitle("LMS Administrative Login - Viksit Bharat@2047");
            setSize(480, 350);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setLayout(new BorderLayout());

            // Header Banner with Logo
            JPanel header = new JPanel(new BorderLayout(14, 0));
            header.setBackground(COLOR_SIDEBAR);
            header.setBorder(new EmptyBorder(16, 22, 16, 22));

            LibraryLogoPanel loginLogo = new LibraryLogoPanel(42);
            header.add(loginLogo, BorderLayout.WEST);

            JPanel titleCol = new JPanel(new GridLayout(2, 1, 0, 2));
            titleCol.setOpaque(false);

            JLabel title = new JLabel("Library Management System");
            title.setFont(new Font("Segoe UI", Font.BOLD, 17));
            title.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel("Administrative Sign-In Portal");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(203, 213, 225));

            titleCol.add(title);
            titleCol.add(subtitle);

            header.add(titleCol, BorderLayout.CENTER);
            add(header, BorderLayout.NORTH);

            // Form Fields Center Panel
            JPanel center = new JPanel(new GridLayout(3, 2, 12, 14));
            center.setBackground(COLOR_CARD_BG);
            center.setBorder(new EmptyBorder(24, 30, 12, 30));

            JLabel userLbl = new JLabel("Username:");
            userLbl.setFont(FONT_BOLD);
            userLbl.setForeground(COLOR_TEXT_MAIN);

            usernameField = createStyledTextField();
            usernameField.setText("admin"); // Default convenience

            JLabel passLbl = new JLabel("Password:");
            passLbl.setFont(FONT_BOLD);
            passLbl.setForeground(COLOR_TEXT_MAIN);

            passwordField = new JPasswordField("Amity@2004");
            applyInputStyle(passwordField);

            showPasswordCheck = new JCheckBox("Show Password");
            showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            showPasswordCheck.setBackground(COLOR_CARD_BG);
            showPasswordCheck.setForeground(COLOR_TEXT_MAIN);

            JLabel hintLbl = new JLabel("Default: admin / Amity@2004");
            hintLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            hintLbl.setForeground(COLOR_TEXT_MUTED);

            center.add(userLbl);
            center.add(usernameField);
            center.add(passLbl);
            center.add(passwordField);
            center.add(showPasswordCheck);
            center.add(hintLbl);

            add(center, BorderLayout.CENTER);

            // Toggle show password
            showPasswordCheck.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (showPasswordCheck.isSelected()) {
                        passwordField.setEchoChar((char) 0);
                    } else {
                        passwordField.setEchoChar('•');
                    }
                }
            });

            // Action Buttons
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
            bottom.setBackground(new Color(248, 250, 252));
            bottom.setBorder(new LineBorder(COLOR_BORDER, 1, false));

            ModernButton exitBtn = createSecondaryButton("Cancel");
            ModernButton loginBtn = createPrimaryButton("Sign In to LMS");

            bottom.add(exitBtn);
            bottom.add(loginBtn);
            add(bottom, BorderLayout.SOUTH);

            // Pressing enter triggers login
            getRootPane().setDefaultButton(loginBtn);

            loginBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String user = usernameField.getText().trim();
                    String pass = new String(passwordField.getPassword());

                    if (LibraryBackend.validateLogin(user, pass)) {
                        dispose(); // Close login window
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                new LibraryFrontend().setVisible(true);
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Invalid Username or Password!\nPlease verify your administrative credentials.",
                            "Access Denied",
                            JOptionPane.ERROR_MESSAGE
                        );
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                }
            });

            exitBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    /**
     * Modal dialog for quickly registering a new member directly from the Circulation panel.
     * Adheres strictly to BCA syllabus Java Swing & JDBC standards.
     */
    public static class RegisterMemberDialog extends JDialog {
        private final JTextField nameField;
        private final JTextField contactField;
        private final ModernButton registerBtn;
        private final ModernButton cancelBtn;
        private final LibraryFrontend parent;

        public RegisterMemberDialog(LibraryFrontend parent) {
            super(parent, "Register New Member", true);
            this.parent = parent;

            setSize(460, 270);
            setMinimumSize(new Dimension(420, 250));
            setLocationRelativeTo(parent);
            setResizable(false);
            setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBackground(COLOR_CARD_BG);
            mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

            // Dialog Header
            JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
            headerPanel.setBackground(COLOR_CARD_BG);

            JLabel titleLabel = new JLabel("Register New Member");
            titleLabel.setFont(FONT_HEADER);
            titleLabel.setForeground(COLOR_TEXT_MAIN);

            JLabel subtitleLabel = new JLabel("Add a new library member record directly to MySQL database.");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitleLabel.setForeground(COLOR_TEXT_MUTED);

            headerPanel.add(titleLabel, BorderLayout.NORTH);
            headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Form inputs
            JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 14));
            formPanel.setBackground(COLOR_CARD_BG);

            JLabel nameLbl = new JLabel("Full Name:*");
            nameLbl.setFont(FONT_BOLD);
            nameLbl.setForeground(COLOR_TEXT_MAIN);

            nameField = createStyledTextField();

            JLabel contactLbl = new JLabel("Contact / Phone Number:*");
            contactLbl.setFont(FONT_BOLD);
            contactLbl.setForeground(COLOR_TEXT_MAIN);

            contactField = createStyledTextField();

            formPanel.add(nameLbl);
            formPanel.add(nameField);
            formPanel.add(contactLbl);
            formPanel.add(contactField);

            mainPanel.add(formPanel, BorderLayout.CENTER);

            // Buttons row
            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            btnRow.setBackground(COLOR_CARD_BG);

            cancelBtn = createSecondaryButton("Cancel");
            registerBtn = createPrimaryButton("Register Member");

            btnRow.add(cancelBtn);
            btnRow.add(registerBtn);
            mainPanel.add(btnRow, BorderLayout.SOUTH);

            add(mainPanel, BorderLayout.CENTER);

            // Actions
            cancelBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            registerBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleRegistration();
                }
            });

            KeyAdapter enterKeyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        handleRegistration();
                    }
                }
            };
            nameField.addKeyListener(enterKeyAdapter);
            contactField.addKeyListener(enterKeyAdapter);
        }

        private void handleRegistration() {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            // Field validation matching Member Directory form
            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please provide both Full Name and Contact / Phone Number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                if (name.isEmpty()) {
                    nameField.requestFocusInWindow();
                } else {
                    contactField.requestFocusInWindow();
                }
                return;
            }

            // Prevent duplicate submissions from double clicks
            if (!registerBtn.isEnabled()) {
                return;
            }
            registerBtn.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            try {
                // Save directly to MySQL via LibraryBackend
                int newMemberId = LibraryBackend.registerMemberAndGetId(name, contact);

                if (newMemberId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Member \"" + name + "\" (ID: " + newMemberId + ") registered successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                    // 1. Reload members from MySQL and auto-select this new member in the Circulation dropdown
                    parent.refreshMembersDropdown(newMemberId);

                    // 2. Also refresh the Member Directory table and Dashboard metrics
                    if (parent.membersModel != null) {
                        LibraryBackend.loadMembersTable(parent.membersModel);
                    }
                    parent.refreshDashboardMetrics();

                    // Close dialog
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to register member in MySQL database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    registerBtn.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Database operation error: " + ex.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
                registerBtn.setEnabled(true);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    // =========================================================================
    // MAIN APPLICATION ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        // Set native system look-and-feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Set global high-contrast option pane colors
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT_MAIN);

        // Launch login portal on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
