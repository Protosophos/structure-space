import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Animated Taylor Series approximation of e^x.
 * Designed for students to understand how polynomial approximations
 * converge to the exponential function step by step.
 */
public class TaylorExponential extends JFrame {

    private int maxTerms = 1;
    private final int totalTerms = 12;
    private double time = 0;
    private double xMin = -5, xMax = 5;
    private double yMin = -3, yMax = 15;
    private boolean animating = false;
    private int animFrame = 0;
    private TaylorPanel plotPanel;
    private JSlider termsSlider;
    private JTextArea infoArea;
    private JLabel formulaLabel;

    /**
     * Entry point of the application.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaylorExponential().setVisible(true));
    }

    /**
     * Constructs the main window with plot, sidebar, and animation timer.
     */
    public TaylorExponential() {
        super("Taylor Series - e^x");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        plotPanel = new TaylorPanel();

        // --- Right sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(25, 25, 45));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        sidebar.setPreferredSize(new Dimension(300, 0));

        // Title
        JLabel title = new JLabel("Taylor Series");
        title.setForeground(new Color(100, 200, 255));
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(5));

        // Subtitle
        JLabel subtitle = new JLabel("Approximation of e^x");
        subtitle.setForeground(new Color(180, 180, 200));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(15));

        // Explanation
        JTextArea explanation = new JTextArea(
            "The Taylor series breaks e^x into\n" +
            "a sum of simple polynomials.\n\n" +
            "With each new term, the colored\n" +
            "curve gets closer to the white\n" +
            "e^x curve.\n\n" +
            "The white line = exact e^x\n" +
            "Colored lines = approximations"
        );
        explanation.setEditable(false);
        explanation.setBackground(new Color(35, 35, 60));
        explanation.setForeground(new Color(200, 200, 220));
        explanation.setFont(new Font("SansSerif", Font.PLAIN, 13));
        explanation.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setMaximumSize(new Dimension(270, 200));
        explanation.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(explanation);
        sidebar.add(Box.createVerticalStrut(20));

        // Terms slider
        JLabel termsTitle = new JLabel("Number of terms");
        termsTitle.setForeground(Color.WHITE);
        termsTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        termsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(termsTitle);
        sidebar.add(Box.createVerticalStrut(5));

        termsSlider = new JSlider(0, totalTerms, 1);
        termsSlider.setBackground(new Color(25, 25, 45));
        termsSlider.setForeground(Color.WHITE);
        termsSlider.setMajorTickSpacing(1);
        termsSlider.setPaintTicks(true);
        termsSlider.setPaintLabels(true);
        termsSlider.setMaximumSize(new Dimension(270, 50));
        termsSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        termsSlider.addChangeListener(e -> {
            maxTerms = termsSlider.getValue();
            // Only stop animation if user drags the slider manually
            if (termsSlider.getValueIsAdjusting()) {
                animating = false;
            }
            updateInfo();
            plotPanel.repaint();
        });
        sidebar.add(termsSlider);
        sidebar.add(Box.createVerticalStrut(15));

        // Current formula
        formulaLabel = new JLabel("<html><b>T(x) = 1</b></html>");
        formulaLabel.setForeground(new Color(255, 200, 100));
        formulaLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        formulaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(formulaLabel);
        sidebar.add(Box.createVerticalStrut(10));

        // Info area for error display
        infoArea = new JTextArea(4, 20);
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(35, 35, 60));
        infoArea.setForeground(new Color(150, 255, 150));
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        infoArea.setMaximumSize(new Dimension(270, 100));
        infoArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(infoArea);
        sidebar.add(Box.createVerticalStrut(15));

        // Zoom slider
        JLabel zoomTitle = new JLabel("Zoom");
        zoomTitle.setForeground(Color.WHITE);
        zoomTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        zoomTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(zoomTitle);
        sidebar.add(Box.createVerticalStrut(5));

        JSlider zoomSlider = new JSlider(2, 20, 5);
        zoomSlider.setBackground(new Color(25, 25, 45));
        zoomSlider.setForeground(Color.WHITE);
        zoomSlider.setMaximumSize(new Dimension(270, 30));
        zoomSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        zoomSlider.addChangeListener(e -> {
            int val = zoomSlider.getValue();
            xMin = -val;
            xMax = val;
            yMin = -val * 0.6;
            yMax = val * 3;
            plotPanel.repaint();
        });
        sidebar.add(zoomSlider);
        sidebar.add(Box.createVerticalStrut(20));

        // Buttons
        JButton animBtn = new JButton("Animate");
        animBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        animBtn.setMaximumSize(new Dimension(270, 35));
        animBtn.setBackground(new Color(52, 152, 219));
        animBtn.setForeground(Color.WHITE);
        animBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        animBtn.setFocusPainted(false);
        animBtn.addActionListener(e -> {
            maxTerms = 0;
            animFrame = 0;
            animating = true;
            termsSlider.setValue(0);
            updateInfo();
        });
        sidebar.add(animBtn);

        sidebar.add(Box.createVerticalGlue());

        // Layout
        setLayout(new BorderLayout());
        add(plotPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);

        updateInfo();

        // Animation timer
        javax.swing.Timer timer = new javax.swing.Timer(16, e -> {
            time += 0.02;
            if (animating) {
                animFrame++;
                // One new term every 60 frames (~1 second at 60fps)
                if (animFrame >= 60) {
                    animFrame = 0;
                    maxTerms++;
                    if (maxTerms <= totalTerms) {
                        termsSlider.setValue(maxTerms);
                        updateInfo();
                    } else {
                        maxTerms = totalTerms;
                        animating = false;
                    }
                }
            }
            plotPanel.repaint();
        });
        timer.start();
    }

    /**
     * Updates the formula label and error info area.
     */
    private void updateInfo() {
        formulaLabel.setText("<html><b>" + buildFormula(maxTerms) + "</b></html>");

        double approx = taylorExp(1.0, maxTerms);
        double exact = Math.E;
        double error = Math.abs(exact - approx);
        String accuracy;
        if (error < 1e-10) accuracy = "almost perfect!";
        else if (error < 0.01) accuracy = "very good";
        else if (error < 0.1) accuracy = "good";
        else if (error < 1.0) accuracy = "rough";
        else accuracy = "bad";

        infoArea.setText(String.format(
            "At x = 1:\n" +
            "  Exact e  = %.8f\n" +
            "  Approx   = %.8f\n" +
            "  Error    = %.8f (%s)", exact, approx, error, accuracy));
    }

    /**
     * Builds a human-readable Taylor polynomial formula.
     *
     * @param n number of terms
     * @return the formula string
     */
    private String buildFormula(int n) {
        StringBuilder sb = new StringBuilder("T(x) = ");
        for (int i = 0; i < n && i < 8; i++) {
            if (i > 0) sb.append(" + ");
            if (i == 0) sb.append("1");
            else if (i == 1) sb.append("x");
            else sb.append("x^").append(i).append("/").append(i).append("!");
        }
        if (n > 8) sb.append(" + ...");
        return sb.toString();
    }

    /**
     * Computes the factorial of n.
     *
     * @param n a non-negative integer
     * @return n!
     */
    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }

    /**
     * Evaluates the Taylor polynomial of e^x with a given number of terms.
     *
     * @param x     the evaluation point
     * @param terms number of terms
     * @return the polynomial value
     */
    private double taylorExp(double x, int terms) {
        double sum = 0;
        double power = 1;
        double fact = 1;
        for (int i = 0; i < terms; i++) {
            if (i > 0) {
                power *= x;
                fact *= i;
            }
            sum += power / fact;
        }
        return sum;
    }

    /**
     * Returns a rainbow color for the given term index.
     *
     * @param index term index
     * @param total total number of terms
     * @return a distinct color
     */
    private Color getColor(int index, int total) {
        float hue = (float) index / total;
        return Color.getHSBColor(hue, 0.9f, 1.0f);
    }

    // ======================================================================
    // Plot panel - renders the coordinate system and function curves
    // ======================================================================
    class TaylorPanel extends JPanel {

        /** Creates the panel with dark background. */
        TaylorPanel() {
            setBackground(new Color(15, 15, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 50;
            int plotW = w - 2 * margin;
            int plotH = h - 2 * margin;

            // Background glow at origin
            int glowCx = margin + (int) ((0 - xMin) / (xMax - xMin) * plotW);
            int glowCy = margin + (int) ((yMax - 0) / (yMax - yMin) * plotH);
            for (int r = 250; r > 0; r -= 5) {
                float hue = (float) ((time * 0.02 + r * 0.001) % 1.0);
                Color c = Color.getHSBColor(hue, 0.3f, 0.12f);
                int alpha = Math.max(0, Math.min(255, (int) (25 * (1.0 - r / 250.0))));
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                g2.fillOval(glowCx - r, glowCy - r, r * 2, r * 2);
            }

            // Grid
            g2.setColor(new Color(255, 255, 255, 20));
            g2.setStroke(new BasicStroke(1));
            for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                g2.drawLine(px, margin, px, margin + plotH);
            }
            for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                g2.drawLine(margin, py, margin + plotW, py);
            }

            // Axes
            g2.setColor(new Color(150, 150, 150));
            g2.setStroke(new BasicStroke(1.5f));
            int originX = margin + (int) ((0 - xMin) / (xMax - xMin) * plotW);
            int originY = margin + (int) ((yMax - 0) / (yMax - yMin) * plotH);
            if (originX >= margin && originX <= margin + plotW)
                g2.drawLine(originX, margin, originX, margin + plotH);
            if (originY >= margin && originY <= margin + plotH)
                g2.drawLine(margin, originY, margin + plotW, originY);

            // Axis numbers
            g2.setColor(new Color(180, 180, 180));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                if (i == 0) continue;
                int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                if (originY >= margin && originY <= margin + plotH)
                    g2.drawString(String.valueOf(i), px - 5, originY + 16);
            }
            for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                if (i == 0) continue;
                int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                if (originX >= margin && originX <= margin + plotW)
                    g2.drawString(String.valueOf(i), originX + 6, py + 4);
            }

            int steps = plotW * 2;
            double dx = (xMax - xMin) / steps;
            Shape clipRect = new java.awt.Rectangle(margin, margin, plotW, plotH);

            // --- Draw the real e^x (thick white line with glow) ---
            GeneralPath realPath = new GeneralPath();
            boolean started = false;
            for (int s = 0; s <= steps; s++) {
                double x = xMin + s * dx;
                double y = Math.exp(x);
                if (y < yMin - 10 || y > yMax + 10) { started = false; continue; }
                float px = (float) (margin + (x - xMin) / (xMax - xMin) * plotW);
                float py = (float) (margin + (yMax - y) / (yMax - yMin) * plotH);
                if (!started) { realPath.moveTo(px, py); started = true; }
                else realPath.lineTo(px, py);
            }

            Shape oldClip = g2.getClip();
            g2.setClip(clipRect);
            // Glow
            g2.setColor(new Color(255, 255, 255, 25));
            g2.setStroke(new BasicStroke(10f));
            g2.draw(realPath);
            // Main white line
            g2.setColor(new Color(255, 255, 255, 200));
            g2.setStroke(new BasicStroke(3f));
            g2.draw(realPath);
            g2.setClip(oldClip);

            // --- Draw Taylor approximations ---
            for (int n = 1; n <= maxTerms; n++) {
                Color color = getColor(n - 1, totalTerms);
                boolean isLatest = (n == maxTerms);
                int alpha = isLatest ? 255 : 50 + (int) (100.0 * n / maxTerms);

                GeneralPath path = new GeneralPath();
                started = false;
                double prevY = 0;
                for (int s = 0; s <= steps; s++) {
                    double x = xMin + s * dx;
                    double y = taylorExp(x, n);
                    if (Double.isNaN(y) || Double.isInfinite(y) || Math.abs(y) > 1e6) {
                        started = false;
                        continue;
                    }
                    if (started && Math.abs(y - prevY) > (yMax - yMin) * 2) {
                        started = false;
                    }
                    prevY = y;
                    float px = (float) (margin + (x - xMin) / (xMax - xMin) * plotW);
                    float py = (float) (margin + (yMax - y) / (yMax - yMin) * plotH);
                    if (!started) { path.moveTo(px, py); started = true; }
                    else path.lineTo(px, py);
                }

                oldClip = g2.getClip();
                g2.setClip(clipRect);

                if (isLatest) {
                    // Glow for the active term
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
                    g2.setStroke(new BasicStroke(10f));
                    g2.draw(path);
                }

                float thickness = isLatest ? 3.5f : 1.5f;
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                g2.setStroke(new BasicStroke(thickness));
                g2.draw(path);
                g2.setClip(oldClip);
            }

            // --- Legend (top left, compact) ---
            int legendX = margin + 12;
            int legendY = margin + 22;
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));

            // Background box for legend
            int legendH = 20 + Math.min(maxTerms, 8) * 16 + 5;
            g2.setColor(new Color(15, 15, 30, 180));
            g2.fillRoundRect(legendX - 8, legendY - 18, 170, legendH, 8, 8);

            // e^x label
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillRect(legendX - 3, legendY - 12, 10, 10);
            g2.drawString("e^x (exact)", legendX + 12, legendY - 3);
            legendY += 18;

            // Show at most 8 entries to keep it tidy
            int showCount = Math.min(maxTerms, 8);
            for (int n = maxTerms - showCount + 1; n <= maxTerms; n++) {
                if (n < 1) continue;
                Color c = getColor(n - 1, totalTerms);
                boolean isLatest = (n == maxTerms);
                int alpha = isLatest ? 255 : 140;
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                g2.fillRect(legendX - 3, legendY - 9, 10, 10);
                g2.drawString("Degree " + (n - 1), legendX + 12, legendY);
                legendY += 16;
            }

            // --- Title ---
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.setColor(new Color(255, 255, 255, 230));
            g2.drawString("Taylor Series: e^x", margin, margin - 15);

            // --- General formula (top right) ---
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            float formulaHue = (float) ((time * 0.04) % 1.0);
            g2.setColor(Color.getHSBColor(formulaHue, 0.5f, 1.0f));
            String formula = "e^x = 1 + x + x^2/2! + x^3/3! + ...";
            int fw = g2.getFontMetrics().stringWidth(formula);
            g2.drawString(formula, margin + plotW - fw, margin - 15);

            // Border
            g2.setColor(new Color(80, 80, 100));
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(margin, margin, plotW, plotH);
        }
    }
}
