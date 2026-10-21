import java.awt.*;
import javax.swing.*;

/**
 * LibraryLogoPanel.java
 * Reusable custom JPanel component displaying a clean, academic open book logo
 * using pure Java AWT Graphics2D primitives (polygons, lines, and rounded rectangles).
 *
 * Designed for BCA Academic Syllabus Compliance:
 * - Uses Graphics2D with Anti-Aliasing for smooth vector rendering.
 * - Symmetrically calculated coordinates ensure clean centering when resized.
 * - Uses the project's high-contrast color palette:
 *     Navy (#20334F), Blue (#426DB5), White (#FFFFFF), and Gold Accent (#E9B95B).
 */
public class LibraryLogoPanel extends JPanel {

    // Prescribed application color palette
    public static final Color COLOR_NAVY = new Color(32, 51, 79);   // #20334F
    public static final Color COLOR_BLUE = new Color(66, 109, 181);  // #426DB5
    public static final Color COLOR_WHITE = Color.WHITE;             // #FFFFFF
    public static final Color COLOR_GOLD = new Color(233, 185, 91);  // #E9B95B

    private int logoSize;

    /**
     * Default constructor creates a 40x40 logo panel.
     */
    public LibraryLogoPanel() {
        this(40);
    }

    /**
     * Parameterized constructor creates a square logo panel with specified size.
     * @param size Width and height in pixels.
     */
    public LibraryLogoPanel(int size) {
        this.logoSize = size;
        Dimension dim = new Dimension(size, size);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
        setOpaque(false); // Transparent background so parent container background shines through
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Graphics2D provides advanced 2D drawing capabilities in AWT
        Graphics2D g2 = (Graphics2D) g.create();

        // Enable Anti-Aliasing for smooth vector rendering without jagged edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();

        // Maintain square aspect ratio and keep drawing centered
        int size = Math.min(w, h);
        int x = (w - size) / 2;
        int y = (h - size) / 2;

        // 1. Badge Tile / Emblem (Primary Blue #426DB5 with rounded corners)
        int pad = Math.max(2, size / 16);
        int bx = x + pad;
        int by = y + pad;
        int bw = size - 2 * pad;
        int bh = size - 2 * pad;
        int arc = bw / 4; // Corner radius

        g2.setColor(COLOR_BLUE);
        g2.fillRoundRect(bx, by, bw, bh, arc, arc);

        // Subtle Gold Accent Border (#E9B95B)
        g2.setColor(COLOR_GOLD);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bx, by, bw, bh, arc, arc);

        // 2. Open Book Geometry (Centering coordinates)
        int cx = bx + bw / 2;
        int cy = by + bh / 2;

        int bookW = (int) (bw * 0.70);
        int bookH = (int) (bh * 0.48);
        int pageW = bookW / 2;

        int spineTop = cy - bookH / 2;
        int spineBottom = cy + bookH / 2 - 2;
        int dip = (int) (bookH * 0.22); // Angle / elevation of outer page edges

        // Book Cover / Shadow Base (Navy #20334F gives depth underneath pages)
        int coverYOffset = Math.max(2, size / 16);
        int[] xCover = {
            cx - pageW - 2, cx, cx + pageW + 2,
            cx + pageW + 2, cx, cx - pageW - 2
        };
        int[] yCover = {
            spineTop + dip + coverYOffset, spineTop + coverYOffset, spineTop + dip + coverYOffset,
            spineBottom + dip + coverYOffset, spineBottom + coverYOffset, spineBottom + dip + coverYOffset
        };
        g2.setColor(COLOR_NAVY);
        g2.fillPolygon(xCover, yCover, 6);

        // 3. Left Page (Pure White #FFFFFF)
        int[] xLeft = { cx - pageW, cx - 1, cx - 1, cx - pageW };
        int[] yLeft = { spineTop + dip, spineTop, spineBottom, spineBottom + dip };
        g2.setColor(COLOR_WHITE);
        g2.fillPolygon(xLeft, yLeft, 4);

        // 4. Right Page (Pure White #FFFFFF)
        int[] xRight = { cx + 1, cx + pageW, cx + pageW, cx + 1 };
        int[] yRight = { spineTop, spineTop + dip, spineBottom + dip, spineBottom };
        g2.fillPolygon(xRight, yRight, 4);

        // Page Outlines and Central Spine (Navy #20334F for clean contrast)
        g2.setColor(COLOR_NAVY);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawPolygon(xLeft, yLeft, 4);
        g2.drawPolygon(xRight, yRight, 4);

        // 5. Stylized Text Lines on Pages
        g2.setColor(new Color(203, 213, 225)); // Slate / light gray text lines
        g2.setStroke(new BasicStroke(1.0f));
        int lineLeftX1 = cx - pageW + 3;
        int lineLeftX2 = cx - 3;
        int lineRightX1 = cx + 3;
        int lineRightX2 = cx + pageW - 3;

        int lineY1 = spineTop + (spineBottom - spineTop) * 1 / 3;
        int lineY2 = spineTop + (spineBottom - spineTop) * 2 / 3;

        g2.drawLine(lineLeftX1, lineY1 + dip / 2, lineLeftX2, lineY1);
        g2.drawLine(lineLeftX1, lineY2 + dip / 2, lineLeftX2, lineY2);
        g2.drawLine(lineRightX1, lineY1, lineRightX2, lineY1 + dip / 2);
        g2.drawLine(lineRightX1, lineY2, lineRightX2, lineY2 + dip / 2);

        // 6. Center Gold Bookmark Ribbon (#E9B95B)
        g2.setColor(COLOR_GOLD);
        int ribbonW = Math.max(2, size / 14);
        int ribbonTop = spineTop + 2;
        int ribbonBottom = spineBottom + coverYOffset + 4;
        int[] xRibbon = { cx - ribbonW / 2, cx + ribbonW / 2, cx + ribbonW / 2, cx, cx - ribbonW / 2 };
        int[] yRibbon = { ribbonTop, ribbonTop, ribbonBottom, ribbonBottom - 2, ribbonBottom };
        g2.fillPolygon(xRibbon, yRibbon, 5);

        // Dispose temporary graphics context to release system resources
        g2.dispose();
    }

    /**
     * Standalone main method to preview the component in an isolated test window.
     * Convenient for BCA project viva demonstrations.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Logo Preview - BCA Project Viva");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(340, 200);
            frame.setLocationRelativeTo(null);

            JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 35));
            container.setBackground(COLOR_NAVY);

            // Display logo at standard sizes
            container.add(new LibraryLogoPanel(32));
            container.add(new LibraryLogoPanel(40));
            container.add(new LibraryLogoPanel(56));

            frame.add(container);
            frame.setVisible(true);
        });
    }
}
