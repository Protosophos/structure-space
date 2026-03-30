import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * MathExplorer - An educational math visualization tool for students.
 * Features 8 interactive tabs exploring Fibonacci numbers, matrices,
 * eigenvalues, the golden ratio, Taylor series, dimensions, pi, and more.
 * Uses a dark theme with colorful animations and glow effects.
 */
public class MathExplorer extends JFrame {

    /** Dark background color for the main window. */
    private static final Color BG_DARK = new Color(15, 15, 30);
    /** Dark background color for panels. */
    private static final Color PANEL_BG = new Color(25, 25, 45);
    /** Accent color for text. */
    private static final Color TEXT_COLOR = new Color(220, 220, 255);
    /** Accent color for highlights. */
    private static final Color ACCENT = new Color(100, 180, 255);
    /** Golden ratio constant. */
    private static final double PHI = (1.0 + Math.sqrt(5.0)) / 2.0;
    /** Conjugate golden ratio. */
    private static final double PSI = (1.0 - Math.sqrt(5.0)) / 2.0;

    /** The tabbed pane holding all 8 tabs. */
    private JTabbedPane tabbedPane;

    /**
     * Constructs the MathExplorer application window with all tabs.
     */
    public MathExplorer() {
        super("MathExplorer - Educational Math Visualization");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        UIManager.put("TabbedPane.selected", new Color(50, 50, 80));
        UIManager.put("TabbedPane.contentAreaColor", PANEL_BG);
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
        UIManager.put("TabbedPane.unselectedForeground", new Color(180, 180, 200));
        UIManager.put("TabbedPane.foreground", Color.WHITE);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));

        tabbedPane.addTab("Fib Matrix", new FibonacciMatrixPanel());
        tabbedPane.addTab("Vectors", new VectorTransformPanel());
        tabbedPane.addTab("Eigenval", new EigenvaluePanel());
        tabbedPane.addTab("Matrix Exp", new MatrixExpPanel());
        tabbedPane.addTab("Taylor", new TaylorExpPanel());
        tabbedPane.addTab("e^x Props", new ExpPropertiesPanel());
        tabbedPane.addTab("Euler", new EulerFormulaPanel());
        tabbedPane.addTab("DiffEq", new DiffEqLaplacePanel());
        tabbedPane.addTab("Spiral", new FibSpiralPanel());
        tabbedPane.addTab("Dimensions", new DimensionsPanel());
        tabbedPane.addTab("Circle/Pi", new CirclePiPanel());
        tabbedPane.addTab("Norms", new NormsPanel());
        tabbedPane.addTab("Photon", new PhotonPanel());
        tabbedPane.addTab("Spacetime", new SpacetimePanel());
        tabbedPane.addTab("Redshift", new RedshiftPanel());
        tabbedPane.addTab("Overview", new OverviewPanel(tabbedPane));

        add(tabbedPane);
    }

    /**
     * Clamps an integer value to the valid alpha range 0-255.
     * @param value the value to clamp
     * @return clamped value between 0 and 255
     */
    public static int clampAlpha(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * Creates a color with clamped alpha.
     * @param r red component
     * @param g green component
     * @param b blue component
     * @param a alpha component (will be clamped to 0-255)
     * @return new Color with safe alpha
     */
    public static Color colorWithAlpha(int r, int g, int b, int a) {
        return new Color(
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b)),
            clampAlpha(a)
        );
    }

    /**
     * Creates a color with clamped alpha from an existing color.
     * @param c the base color
     * @param alpha the desired alpha (will be clamped)
     * @return new Color with safe alpha
     */
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), clampAlpha(alpha));
    }

    /**
     * Draws a glowing string at the specified position.
     * @param g2 the graphics context
     * @param text the string to draw
     * @param x x-coordinate
     * @param y y-coordinate
     * @param color the glow color
     */
    public static void drawGlowString(Graphics2D g2, String text, int x, int y, Color color) {
        g2.setColor(withAlpha(color, 40));
        g2.drawString(text, x - 1, y - 1);
        g2.drawString(text, x + 1, y + 1);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    /**
     * Draws a glowing circle at the specified position.
     * @param g2 the graphics context
     * @param cx center x
     * @param cy center y
     * @param r radius
     * @param color the glow color
     */
    public static void drawGlowCircle(Graphics2D g2, int cx, int cy, int r, Color color) {
        for (int i = 3; i >= 0; i--) {
            int glowR = r + i * 3;
            int alpha = clampAlpha(30 - i * 7);
            g2.setColor(withAlpha(color, alpha));
            g2.fillOval(cx - glowR, cy - glowR, glowR * 2, glowR * 2);
        }
        g2.setColor(color);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
    }

    /**
     * Configures antialiasing and rendering quality on a Graphics2D context.
     * @param g2 the graphics context
     */
    public static void setupRendering(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Multiplies two 2x2 matrices.
     * @param a first matrix
     * @param b second matrix
     * @return product matrix
     */
    public static double[][] matMul2(double[][] a, double[][] b) {
        double[][] r = new double[2][2];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                for (int k = 0; k < 2; k++)
                    r[i][j] += a[i][k] * b[k][j];
        return r;
    }

    /**
     * Raises a 2x2 matrix to the given power.
     * @param m the matrix
     * @param n the exponent
     * @return m raised to power n
     */
    public static double[][] matPow2(double[][] m, int n) {
        double[][] result = {{1, 0}, {0, 1}};
        for (int i = 0; i < n; i++) {
            result = matMul2(result, m);
        }
        return result;
    }

    /**
     * Returns the nth Fibonacci number using the matrix method.
     * @param n the index
     * @return F(n)
     */
    public static long fibonacci(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long tmp = a + b;
            a = b;
            b = tmp;
        }
        return b;
    }

    /**
     * Creates a styled dark slider.
     * @param min minimum value
     * @param max maximum value
     * @param initial initial value
     * @return styled JSlider
     */
    public static JSlider createDarkSlider(int min, int max, int initial) {
        JSlider slider = new JSlider(min, max, initial);
        slider.setBackground(PANEL_BG);
        slider.setForeground(TEXT_COLOR);
        slider.setMajorTickSpacing(Math.max(1, (max - min) / 5));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setFont(new Font("SansSerif", Font.PLAIN, 10));
        return slider;
    }

    /**
     * Creates a styled dark button.
     * @param text button label
     * @return styled JButton
     */
    public static JButton createDarkButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(50, 50, 80));
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120), 1));
        return btn;
    }

    /**
     * Creates a styled dark label.
     * @param text label text
     * @return styled JLabel
     */
    public static JLabel createDarkLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return lbl;
    }

    // =====================================================================
    // TAB 1: Fibonacci Matrix
    // =====================================================================

    /**
     * Panel displaying the Fibonacci matrix raised to power n,
     * showing step-by-step matrix exponentiation and convergence
     * of F(n+1)/F(n) to the golden ratio.
     */
    static class FibonacciMatrixPanel extends JPanel {
        private int power = 1;
        private int animFrame = 0;
        private int animTarget = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;
        private JSlider slider;
        private JLabel infoLabel;

        /**
         * Constructs the Fibonacci Matrix panel with controls and animation.
         */
        public FibonacciMatrixPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            controls.setBackground(PANEL_BG);

            slider = createDarkSlider(0, 30, 1);
            slider.addChangeListener(e -> {
                if (!animating) {
                    power = slider.getValue();
                    repaint();
                }
            });

            JButton animBtn = createDarkButton("Animate");
            animBtn.addActionListener(e -> startAnimation());

            JButton resetBtn = createDarkButton("Reset");
            resetBtn.addActionListener(e -> {
                stopAnimation();
                power = 0;
                slider.setValue(0);
                repaint();
            });

            infoLabel = createDarkLabel("n = 1");

            controls.add(createDarkLabel("Power n:"));
            controls.add(slider);
            controls.add(animBtn);
            controls.add(resetBtn);
            controls.add(infoLabel);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                if (animFrame % 30 == 0 && power < animTarget) {
                    power++;
                    slider.setValue(power);
                }
                if (power >= animTarget) {
                    stopAnimation();
                }
                repaint();
            });
        }

        /**
         * Starts the step-by-step animation of matrix exponentiation.
         */
        private void startAnimation() {
            animating = true;
            animTarget = 30;
            power = 0;
            slider.setValue(0);
            animFrame = 0;
            animTimer.start();
        }

        /**
         * Stops the running animation.
         */
        private void stopAnimation() {
            animating = false;
            animTimer.stop();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            drawGlowString(g2, "Fibonacci Matrix Exponentiation", w / 2 - 200, 35, ACCENT);

            infoLabel.setText("n = " + power);

            // Draw base matrix
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawGlowString(g2, "Base Matrix F:", 40, 80, new Color(255, 200, 100));

            double[][] fibM = {{1, 1}, {1, 0}};
            drawMatrix(g2, fibM, 40, 90, 80, new Color(100, 200, 255));

            // Draw F^n
            double[][] result = matPow2(fibM, power);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            String powLabel = "F^" + power + " =";
            drawGlowString(g2, powLabel, w / 2 - 120, 80, new Color(255, 150, 100));

            drawMatrixLarge(g2, result, w / 2 - 120, 95, 140, power);

            // Show Fibonacci numbers
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            int infoX = w / 2 + 200;
            drawGlowString(g2, "Fibonacci Numbers:", infoX, 80, new Color(100, 255, 150));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            if (power > 0) {
                long fn1 = (long) result[0][0];
                long fn = (long) result[0][1];
                long fnm1 = (long) result[1][1];
                g2.setColor(TEXT_COLOR);
                g2.drawString("F(" + (power + 1) + ") = " + fn1, infoX, 110);
                g2.drawString("F(" + power + ") = " + fn, infoX, 135);
                g2.drawString("F(" + (power - 1) + ") = " + fnm1, infoX, 160);

                if (fn > 0) {
                    double ratio = (double) fn1 / fn;
                    g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                    drawGlowString(g2, String.format("F(%d)/F(%d) = %.10f", power + 1, power, ratio),
                            infoX, 195, new Color(255, 220, 100));
                    drawGlowString(g2, String.format("Golden Ratio phi = %.10f", PHI),
                            infoX, 220, new Color(255, 180, 50));
                    double err = Math.abs(ratio - PHI);
                    drawGlowString(g2, String.format("Error = %.2e", err),
                            infoX, 245, new Color(255, 100, 100));
                }
            } else {
                g2.setColor(TEXT_COLOR);
                g2.drawString("F^0 = Identity Matrix", infoX, 110);
            }

            // Draw convergence graph
            drawConvergenceGraph(g2, 40, h - 340, w - 80, 250, power);

            // Explanation
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g2.setColor(withAlpha(TEXT_COLOR, 180));
            g2.drawString("The matrix [[1,1],[1,0]]^n contains Fibonacci numbers. " +
                    "The ratio F(n+1)/F(n) converges to the golden ratio phi.", 40, h - 60);
        }

        /**
         * Draws a small 2x2 matrix at the given position.
         */
        private void drawMatrix(Graphics2D g2, double[][] m, int x, int y, int size, Color color) {
            g2.setColor(withAlpha(color, 40));
            g2.drawRoundRect(x, y, size * 2 + 20, size + 20, 10, 10);

            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    float hue = (i * 2 + j) / 4.0f;
                    Color c = Color.getHSBColor(hue, 0.7f, 1.0f);
                    String val = String.valueOf((long) m[i][j]);
                    g2.setColor(c);
                    g2.drawString(val, x + 15 + j * size, y + 25 + i * (size / 2));
                }
            }
        }

        /**
         * Draws a large 2x2 matrix with rainbow-colored entries and glow effects.
         */
        private void drawMatrixLarge(Graphics2D g2, double[][] m, int x, int y, int cellSize, int n) {
            // Draw bracket glow
            g2.setColor(withAlpha(ACCENT, 50));
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(x - 10, y - 5, cellSize * 2 + 30, cellSize * 2 + 20, 15, 15);

            g2.setFont(new Font("Monospaced", Font.BOLD, 22));
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    float hue = ((i * 2 + j) * 0.25f + n * 0.02f) % 1.0f;
                    Color c = Color.getHSBColor(hue, 0.8f, 1.0f);
                    String val = String.valueOf((long) m[i][j]);

                    // Glow behind text
                    g2.setColor(withAlpha(c, 60));
                    int tx = x + 10 + j * cellSize;
                    int ty = y + 35 + i * cellSize;
                    g2.fillRoundRect(tx - 8, ty - 22, cellSize - 10, 30, 8, 8);

                    g2.setColor(c);
                    g2.drawString(val, tx, ty);
                }
            }
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws the convergence graph showing F(n+1)/F(n) approaching phi.
         */
        private void drawConvergenceGraph(Graphics2D g2, int x, int y, int w, int h, int currentN) {
            // Background
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 80));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Convergence: F(n+1)/F(n) -> phi", x + 10, y + 20, ACCENT);

            int gx = x + 50, gy = y + 35;
            int gw = w - 70, gh = h - 55;

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 100));
            g2.drawLine(gx, gy, gx, gy + gh);
            g2.drawLine(gx, gy + gh, gx + gw, gy + gh);

            // phi line
            double minVal = 0.5, maxVal = 2.5;
            int phiY = gy + gh - (int) ((PHI - minVal) / (maxVal - minVal) * gh);
            g2.setColor(withAlpha(new Color(255, 215, 0), 120));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0));
            g2.drawLine(gx, phiY, gx + gw, phiY);
            g2.setStroke(new BasicStroke(1));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(new Color(255, 215, 0));
            g2.drawString("phi = 1.618...", gx + gw - 85, phiY - 5);

            // Plot points
            int maxPoints = Math.min(currentN, 30);
            for (int i = 1; i <= maxPoints; i++) {
                long fi = fibonacci(i);
                long fi1 = fibonacci(i + 1);
                if (fi > 0) {
                    double ratio = (double) fi1 / fi;
                    int px = gx + (int) ((double) i / 30 * gw);
                    int py = gy + gh - (int) ((ratio - minVal) / (maxVal - minVal) * gh);
                    py = Math.max(gy, Math.min(gy + gh, py));

                    float hue = i / 30.0f;
                    Color dotColor = Color.getHSBColor(hue, 0.9f, 1.0f);
                    drawGlowCircle(g2, px, py, 4, dotColor);

                    if (i > 1) {
                        long fPrev = fibonacci(i - 1);
                        if (fPrev > 0) {
                            double prevRatio = (double) fi / fPrev;
                            int ppx = gx + (int) ((double) (i - 1) / 30 * gw);
                            int ppy = gy + gh - (int) ((prevRatio - minVal) / (maxVal - minVal) * gh);
                            ppy = Math.max(gy, Math.min(gy + gh, ppy));
                            g2.setColor(withAlpha(dotColor, 80));
                            g2.drawLine(ppx, ppy, px, py);
                        }
                    }
                }
            }

            // Y axis labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            for (double v = 1.0; v <= 2.5; v += 0.5) {
                int ly = gy + gh - (int) ((v - minVal) / (maxVal - minVal) * gh);
                g2.drawString(String.format("%.1f", v), gx - 30, ly + 4);
            }
        }
    }

    // =====================================================================
    // TAB 2: Vector Transformation
    // =====================================================================

    /**
     * Panel showing 2D vectors being transformed by the Fibonacci matrix,
     * with grid deformation and eigenvector direction highlights.
     */
    static class VectorTransformPanel extends JPanel {
        private int steps = 1;
        private int animFrame = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;
        private JSlider slider;
        private JSlider zoomSlider;
        private double zoom = 40.0;
        private double[][] matrix = {{1, 1}, {1, 0}};
        private JTextField mA, mB, mC, mD;
        private DrawPanel drawPanel;

        /**
         * Constructs the general Vector Transformation panel with matrix input,
         * presets, zoom, and animation controls.
         */
        public VectorTransformPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            // --- Right sidebar ---
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(30, 30, 55));
            sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            sidebar.setPreferredSize(new Dimension(220, 0));

            JLabel title = new JLabel("Matrix [a b; c d]");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 14));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(8));

            // Matrix input fields
            JPanel matPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            matPanel.setBackground(new Color(30, 30, 55));
            matPanel.setMaximumSize(new Dimension(200, 60));
            matPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            mA = createMatField("1");
            mB = createMatField("1");
            mC = createMatField("1");
            mD = createMatField("0");
            matPanel.add(mA);
            matPanel.add(mB);
            matPanel.add(mC);
            matPanel.add(mD);
            sidebar.add(matPanel);
            sidebar.add(Box.createVerticalStrut(5));

            JButton applyBtn = createDarkButton("Apply");
            applyBtn.setMaximumSize(new Dimension(200, 28));
            applyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            applyBtn.addActionListener(e -> applyMatrix());
            sidebar.add(applyBtn);
            sidebar.add(Box.createVerticalStrut(12));

            // Presets
            JLabel presetLabel = new JLabel("Presets");
            presetLabel.setForeground(TEXT_COLOR);
            presetLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            presetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(presetLabel);
            sidebar.add(Box.createVerticalStrut(5));

            addPreset(sidebar, "Fibonacci", 1, 1, 1, 0);
            addPreset(sidebar, "Rotation 45", 0.707, -0.707, 0.707, 0.707);
            addPreset(sidebar, "Rotation 90", 0, -1, 1, 0);
            addPreset(sidebar, "Scale 1.5x", 1.5, 0, 0, 1.5);
            addPreset(sidebar, "Shear X", 1, 1, 0, 1);
            addPreset(sidebar, "Shear Y", 1, 0, 1, 1);
            addPreset(sidebar, "Reflect X", -1, 0, 0, 1);
            addPreset(sidebar, "Reflect Y", 1, 0, 0, -1);
            sidebar.add(Box.createVerticalStrut(12));

            // Info area
            JLabel infoTitle = new JLabel("Info");
            infoTitle.setForeground(TEXT_COLOR);
            infoTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(infoTitle);
            sidebar.add(Box.createVerticalStrut(3));

            JTextArea infoArea = new JTextArea(
                "Change the 2x2 matrix or\n" +
                "pick a preset to see how\n" +
                "vectors get transformed.\n\n" +
                "Each colored arrow shows\n" +
                "the vector after n steps\n" +
                "of multiplication.\n\n" +
                "The green grid shows how\n" +
                "the matrix warps space."
            );
            infoArea.setEditable(false);
            infoArea.setBackground(new Color(25, 25, 50));
            infoArea.setForeground(new Color(180, 180, 200));
            infoArea.setFont(new Font("SansSerif", Font.PLAIN, 11));
            infoArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            infoArea.setMaximumSize(new Dimension(200, 160));
            infoArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(infoArea);

            sidebar.add(Box.createVerticalGlue());

            // --- Bottom controls ---
            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
            controls.setBackground(PANEL_BG);

            controls.add(createDarkLabel("Steps:"));
            slider = createDarkSlider(0, 10, 1);
            slider.addChangeListener(e -> {
                if (!animating) {
                    steps = slider.getValue();
                    drawPanel.repaint();
                }
            });
            controls.add(slider);

            controls.add(createDarkLabel("Zoom:"));
            zoomSlider = createDarkSlider(5, 150, 40);
            zoomSlider.addChangeListener(e -> {
                zoom = zoomSlider.getValue();
                drawPanel.repaint();
            });
            controls.add(zoomSlider);

            JButton animBtn = createDarkButton("Animate");
            animBtn.addActionListener(e -> {
                animating = true;
                steps = 0;
                slider.setValue(0);
                animFrame = 0;
                animTimer.start();
            });
            controls.add(animBtn);

            JButton resetBtn = createDarkButton("Reset");
            resetBtn.addActionListener(e -> {
                animating = false;
                animTimer.stop();
                steps = 0;
                slider.setValue(0);
                drawPanel.repaint();
            });
            controls.add(resetBtn);

            drawPanel = new DrawPanel();
            add(sidebar, BorderLayout.EAST);
            add(drawPanel, BorderLayout.CENTER);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                if (animFrame >= 50) {
                    animFrame = 0;
                    if (steps < 10) {
                        steps++;
                        slider.setValue(steps);
                    } else {
                        animating = false;
                        animTimer.stop();
                    }
                }
                drawPanel.repaint();
            });
        }

        private JTextField createMatField(String value) {
            JTextField field = new JTextField(value);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setBackground(new Color(40, 40, 70));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
            field.setFont(new Font("Monospaced", Font.BOLD, 14));
            field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 120)));
            return field;
        }

        private void applyMatrix() {
            try {
                matrix[0][0] = Double.parseDouble(mA.getText().trim());
                matrix[0][1] = Double.parseDouble(mB.getText().trim());
                matrix[1][0] = Double.parseDouble(mC.getText().trim());
                matrix[1][1] = Double.parseDouble(mD.getText().trim());
                drawPanel.repaint();
            } catch (NumberFormatException ex) {
                // ignore invalid input
            }
        }

        private void setPreset(double a, double b, double c, double d) {
            matrix[0][0] = a; matrix[0][1] = b;
            matrix[1][0] = c; matrix[1][1] = d;
            mA.setText(String.valueOf(a));
            mB.setText(String.valueOf(b));
            mC.setText(String.valueOf(c));
            mD.setText(String.valueOf(d));
            drawPanel.repaint();
        }

        private void addPreset(JPanel parent, String name, double a, double b, double c, double d) {
            JButton btn = new JButton(name);
            btn.setBackground(new Color(50, 50, 80));
            btn.setForeground(new Color(200, 200, 220));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setMaximumSize(new Dimension(200, 24));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> setPreset(a, b, c, d));
            parent.add(btn);
            parent.add(Box.createVerticalStrut(2));
        }

        /**
         * Computes eigenvalues of the current 2x2 matrix.
         *
         * @return array of two eigenvalues (may be NaN if complex)
         */
        private double[] eigenvalues() {
            double a = matrix[0][0], b = matrix[0][1];
            double c = matrix[1][0], d = matrix[1][1];
            double trace = a + d;
            double det = a * d - b * c;
            double disc = trace * trace - 4 * det;
            if (disc < 0) return new double[]{Double.NaN, Double.NaN};
            return new double[]{(trace + Math.sqrt(disc)) / 2, (trace - Math.sqrt(disc)) / 2};
        }

        /** Inner panel that draws the coordinate system and vectors. */
        class DrawPanel extends JPanel {
            DrawPanel() { setBackground(PANEL_BG); }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                setupRendering(g2);

                int w = getWidth(), h = getHeight();
                int cx = w / 2;
                int cy = h / 2;

                // Title
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                drawGlowString(g2, "2D Vector Transformation", 20, 28, ACCENT);

                // Grid
                g2.setColor(withAlpha(TEXT_COLOR, 20));
                int gridRange = (int) (Math.max(w, h) / zoom) + 2;
                for (int i = -gridRange; i <= gridRange; i++) {
                    int px = cx + (int) (i * zoom);
                    g2.drawLine(px, 0, px, h);
                    int py = cy - (int) (i * zoom);
                    g2.drawLine(0, py, w, py);
                }

                // Axes
                g2.setColor(withAlpha(TEXT_COLOR, 80));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, cy, w, cy);
                g2.drawLine(cx, 0, cx, h);

                // Axis numbers
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(withAlpha(TEXT_COLOR, 120));
                for (int i = -gridRange; i <= gridRange; i++) {
                    if (i == 0) continue;
                    g2.drawString(String.valueOf(i), cx + (int)(i * zoom) - 4, cy + 14);
                    g2.drawString(String.valueOf(i), cx + 5, cy - (int)(i * zoom) + 4);
                }
                g2.setStroke(new BasicStroke(1));

                // Deformed grid
                if (steps > 0) {
                    double[][] transform = matPow2(matrix, steps);
                    g2.setColor(withAlpha(new Color(100, 255, 100), 25));
                    for (int i = -gridRange; i <= gridRange; i++) {
                        // Horizontal lines
                        double x1 = -gridRange * zoom, y1 = i * zoom;
                        double x2 = gridRange * zoom, y2 = i * zoom;
                        double tx1 = transform[0][0] * x1 + transform[0][1] * y1;
                        double ty1 = transform[1][0] * x1 + transform[1][1] * y1;
                        double tx2 = transform[0][0] * x2 + transform[0][1] * y2;
                        double ty2 = transform[1][0] * x2 + transform[1][1] * y2;
                        g2.drawLine(cx + (int)tx1, cy - (int)ty1, cx + (int)tx2, cy - (int)ty2);
                        // Vertical lines
                        x1 = i * zoom; y1 = -gridRange * zoom;
                        x2 = i * zoom; y2 = gridRange * zoom;
                        tx1 = transform[0][0] * x1 + transform[0][1] * y1;
                        ty1 = transform[1][0] * x1 + transform[1][1] * y1;
                        tx2 = transform[0][0] * x2 + transform[0][1] * y2;
                        ty2 = transform[1][0] * x2 + transform[1][1] * y2;
                        g2.drawLine(cx + (int)tx1, cy - (int)ty1, cx + (int)tx2, cy - (int)ty2);
                    }
                }

                // Eigenvector directions
                double[] evals = eigenvalues();
                if (!Double.isNaN(evals[0])) {
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8, 4}, 0));
                    double b = matrix[0][1];
                    for (int ev = 0; ev < 2; ev++) {
                        double lambda = evals[ev];
                        double evx, evy;
                        if (Math.abs(b) > 1e-9) {
                            evx = b;
                            evy = lambda - matrix[0][0];
                        } else {
                            evx = (ev == 0) ? 1 : 0;
                            evy = (ev == 0) ? 0 : 1;
                        }
                        double norm = Math.sqrt(evx * evx + evy * evy);
                        if (norm < 1e-9) continue;
                        evx /= norm;
                        evy /= norm;
                        Color evColor = (ev == 0) ? new Color(255, 215, 0) : new Color(150, 100, 255);
                        g2.setColor(withAlpha(evColor, 100));
                        int len = Math.max(w, h);
                        g2.drawLine(cx - (int)(evx * len), cy + (int)(evy * len),
                                    cx + (int)(evx * len), cy - (int)(evy * len));
                    }
                    g2.setStroke(new BasicStroke(1));

                    // Eigenvalue labels
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g2.setColor(new Color(255, 215, 0));
                    g2.drawString(String.format("EV1 = %.3f", evals[0]), 15, h - 35);
                    g2.setColor(new Color(150, 100, 255));
                    g2.drawString(String.format("EV2 = %.3f", evals[1]), 15, h - 20);
                }

                // Draw vectors
                double vx = 1.0, vy = 0.0;
                for (int i = 0; i <= steps; i++) {
                    float hue = i / 11.0f;
                    Color arrowColor = Color.getHSBColor(hue, 0.9f, 1.0f);

                    int endX = cx + (int)(vx * zoom);
                    int endY = cy - (int)(vy * zoom);

                    // Glow
                    g2.setColor(withAlpha(arrowColor, 40));
                    g2.setStroke(new BasicStroke(6));
                    g2.drawLine(cx, cy, endX, endY);

                    // Arrow
                    g2.setColor(arrowColor);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(cx, cy, endX, endY);

                    // Arrowhead
                    double angle = Math.atan2(-(vy * zoom), vx * zoom);
                    int ahLen = 12;
                    int ax1 = endX - (int)(ahLen * Math.cos(angle - 0.4));
                    int ay1 = endY + (int)(ahLen * Math.sin(angle - 0.4));
                    int ax2 = endX - (int)(ahLen * Math.cos(angle + 0.4));
                    int ay2 = endY + (int)(ahLen * Math.sin(angle + 0.4));
                    g2.fillPolygon(new int[]{endX, ax1, ax2}, new int[]{endY, ay1, ay2}, 3);

                    // Label with offset to avoid overlap
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2.setColor(arrowColor);
                    int labelOffsetY = (i % 2 == 0) ? -8 : 16;
                    g2.drawString(String.format("n=%d [%.1f, %.1f]", i, vx, vy), endX + 8, endY + labelOffsetY);

                    // Dot at tip
                    g2.fillOval(endX - 4, endY - 4, 8, 8);

                    // Next step
                    double newVx = matrix[0][0] * vx + matrix[0][1] * vy;
                    double newVy = matrix[1][0] * vx + matrix[1][1] * vy;
                    vx = newVx;
                    vy = newVy;
                }
                g2.setStroke(new BasicStroke(1));

                // Determinant info
                double det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                g2.setColor(new Color(255, 180, 100));
                g2.drawString(String.format("det = %.3f", det), 15, 50);
                if (Math.abs(det) > 1e-9) {
                    g2.setColor(withAlpha(TEXT_COLOR, 150));
                    g2.drawString(String.format("Area scale: %.3fx", Math.abs(det)), 15, 66);
                }
            }
        }
    }

    // =====================================================================
    // TAB 3: Eigenvalues & Golden Ratio
    // =====================================================================

    /**
     * Panel showing eigenvalue decomposition of the Fibonacci matrix,
     * Binet's formula, convergence to phi, and the golden rectangle.
     */
    static class EigenvaluePanel extends JPanel {
        private int animFrame = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;

        /**
         * Constructs the Eigenvalue panel.
         */
        public EigenvaluePanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            controls.setBackground(PANEL_BG);

            JButton animBtn = createDarkButton("Animate");
            animBtn.addActionListener(e -> {
                animating = true;
                animFrame = 0;
                animTimer.start();
            });

            JButton resetBtn = createDarkButton("Reset");
            resetBtn.addActionListener(e -> {
                animating = false;
                animTimer.stop();
                animFrame = 0;
                repaint();
            });

            controls.add(animBtn);
            controls.add(resetBtn);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawGlowString(g2, "Eigenvalues & the Golden Ratio", w / 2 - 190, 30, ACCENT);

            // Left section: eigenvalue decomposition
            int lx = 30;
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            drawGlowString(g2, "Eigenvalue Decomposition", lx, 65, new Color(255, 200, 100));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Fibonacci Matrix F = [[1,1],[1,0]]", lx, 90);
            g2.drawString("Characteristic equation: t^2 - t - 1 = 0", lx, 115);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, String.format("Eigenvalue 1: phi = (1+sqrt(5))/2 = %.8f", PHI),
                    lx, 150, new Color(255, 215, 0));
            drawGlowString(g2, String.format("Eigenvalue 2: psi = (1-sqrt(5))/2 = %.8f", PSI),
                    lx, 175, new Color(150, 100, 255));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Eigenvector for phi: [phi, 1]", lx, 205);
            g2.drawString("Eigenvector for psi: [psi, 1]", lx, 225);

            // Binet's formula
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            drawGlowString(g2, "Binet's Formula:", lx, 260, new Color(100, 255, 200));
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawGlowString(g2, "F(n) = (phi^n - psi^n) / sqrt(5)", lx + 20, 290, new Color(100, 255, 200));

            // Verify Binet's formula for several n
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(withAlpha(TEXT_COLOR, 200));
            int maxShow = animating ? Math.min(animFrame / 15, 10) : 10;
            for (int n = 1; n <= maxShow; n++) {
                double binet = (Math.pow(PHI, n) - Math.pow(PSI, n)) / Math.sqrt(5);
                long actual = fibonacci(n);
                g2.drawString(String.format("n=%2d: Binet=%.4f, Actual=%d", n, binet, actual), lx, 310 + (n - 1) * 18);
            }

            // Right section: eigenvector visualization
            int rx = w / 2 + 30;
            int rcy = 220;
            int rSize = 160;

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "Eigenvectors in 2D", rx + rSize - 80, 65, ACCENT);

            // Draw coordinate system
            g2.setColor(withAlpha(TEXT_COLOR, 50));
            g2.drawLine(rx, rcy, rx + rSize * 2, rcy);
            g2.drawLine(rx + rSize, rcy - rSize, rx + rSize, rcy + rSize);

            // Draw eigenvectors
            double evScale = 80;
            double norm1 = Math.sqrt(PHI * PHI + 1);
            double norm2 = Math.sqrt(PSI * PSI + 1);

            // Eigenvector 1
            int ex1 = rx + rSize + (int) (PHI / norm1 * evScale);
            int ey1 = rcy - (int) (1.0 / norm1 * evScale);
            g2.setColor(withAlpha(new Color(255, 215, 0), 60));
            g2.setStroke(new BasicStroke(8));
            g2.drawLine(rx + rSize, rcy, ex1, ey1);
            g2.setColor(new Color(255, 215, 0));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(rx + rSize, rcy, ex1, ey1);
            drawGlowCircle(g2, ex1, ey1, 6, new Color(255, 215, 0));

            // Eigenvector 2
            int ex2 = rx + rSize + (int) (PSI / norm2 * evScale);
            int ey2 = rcy - (int) (1.0 / norm2 * evScale);
            g2.setColor(withAlpha(new Color(150, 100, 255), 60));
            g2.setStroke(new BasicStroke(8));
            g2.drawLine(rx + rSize, rcy, ex2, ey2);
            g2.setColor(new Color(150, 100, 255));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(rx + rSize, rcy, ex2, ey2);
            drawGlowCircle(g2, ex2, ey2, 6, new Color(150, 100, 255));
            g2.setStroke(new BasicStroke(1));

            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(255, 215, 0));
            g2.drawString("[phi, 1]", ex1 + 8, ey1 - 8);
            g2.setColor(new Color(150, 100, 255));
            g2.drawString("[psi, 1]", ex2 + 8, ey2 + 15);

            // Golden rectangle
            int grx = rx + 20, gry = rcy + rSize - 30;
            int grw = 240, grh = (int) (grw / PHI);
            drawGoldenRectangle(g2, grx, gry, grw, grh, 6, animFrame);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Golden Rectangle (ratio = phi)", grx, gry - 10, new Color(255, 200, 100));

            // Convergence graph at bottom
            drawPhiConvergenceGraph(g2, 30, h - 200, w - 60, 150, animFrame);
        }

        /**
         * Draws a golden rectangle subdividing into squares.
         */
        private void drawGoldenRectangle(Graphics2D g2, int x, int y, int totalW, int totalH, int depth, int frame) {
            if (depth <= 0 || totalW < 3 || totalH < 3) return;

            int showDepth = animating ? Math.min(frame / 20, depth) : depth;

            int curX = x, curY = y, curW = totalW, curH = totalH;
            boolean horizontal = true;

            for (int i = 0; i < showDepth; i++) {
                float hue = i / (float) depth;
                Color c = Color.getHSBColor(hue, 0.7f, 0.9f);

                if (horizontal) {
                    int sq = curH;
                    if (sq > curW) sq = curW;
                    g2.setColor(withAlpha(c, 50));
                    g2.fillRect(curX, curY, sq, sq);
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(curX, curY, sq, sq);
                    curX += sq;
                    curW -= sq;
                } else {
                    int sq = curW;
                    if (sq > curH) sq = curH;
                    g2.setColor(withAlpha(c, 50));
                    g2.fillRect(curX, curY, sq, sq);
                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(curX, curY, sq, sq);
                    curY += sq;
                    curH -= sq;
                }
                horizontal = !horizontal;
            }
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws a convergence graph of F(n+1)/F(n) toward phi with animated points.
         */
        private void drawPhiConvergenceGraph(Graphics2D g2, int x, int y, int w, int h, int frame) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 60));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            drawGlowString(g2, "F(n+1)/F(n) Convergence", x + 10, y + 18, ACCENT);

            int gx = x + 45, gy = y + 25;
            int gw = w - 60, gh = h - 40;

            double minV = 1.0, maxV = 2.2;
            int phiY = gy + gh - (int) ((PHI - minV) / (maxV - minV) * gh);
            g2.setColor(withAlpha(new Color(255, 215, 0), 80));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            g2.drawLine(gx, phiY, gx + gw, phiY);
            g2.setStroke(new BasicStroke(1));

            int maxPts = animating ? Math.min(frame / 8, 20) : 20;
            for (int n = 1; n <= maxPts; n++) {
                long fn = fibonacci(n);
                long fn1 = fibonacci(n + 1);
                if (fn > 0) {
                    double ratio = (double) fn1 / fn;
                    int px = gx + n * gw / 21;
                    int py = gy + gh - (int) ((ratio - minV) / (maxV - minV) * gh);
                    py = Math.max(gy, Math.min(gy + gh, py));

                    float hue = n / 21.0f;
                    drawGlowCircle(g2, px, py, 4, Color.getHSBColor(hue, 0.9f, 1.0f));
                }
            }
        }
    }

    // =====================================================================
    // TAB 4: Matrix Exponential (Taylor)
    // =====================================================================

    /**
     * Panel showing the Taylor series expansion of the matrix exponential
     * e^A for A = Fibonacci matrix, with animated term-by-term addition.
     */
    static class MatrixExpPanel extends JPanel {
        private int terms = 1;
        private int animFrame = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;
        private JSlider slider;

        /**
         * Constructs the Matrix Exponential panel.
         */
        public MatrixExpPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            controls.setBackground(PANEL_BG);

            slider = createDarkSlider(0, 15, 1);
            slider.addChangeListener(e -> {
                if (!animating) {
                    terms = slider.getValue();
                    repaint();
                }
            });

            JButton animBtn = createDarkButton("Animate");
            animBtn.addActionListener(e -> {
                animating = true;
                terms = 0;
                slider.setValue(0);
                animFrame = 0;
                animTimer.start();
            });

            JButton resetBtn = createDarkButton("Reset");
            resetBtn.addActionListener(e -> {
                animating = false;
                animTimer.stop();
                terms = 0;
                slider.setValue(0);
                repaint();
            });

            controls.add(createDarkLabel("Terms:"));
            controls.add(slider);
            controls.add(animBtn);
            controls.add(resetBtn);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                if (animFrame % 40 == 0 && terms < 15) {
                    terms++;
                    slider.setValue(terms);
                }
                if (terms >= 15) {
                    animating = false;
                    animTimer.stop();
                }
                repaint();
            });
        }

        /**
         * Computes factorial of n.
         * @param n input
         * @return n!
         */
        private double factorial(int n) {
            double r = 1;
            for (int i = 2; i <= n; i++) r *= i;
            return r;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawGlowString(g2, "Matrix Exponential: e^A (Taylor Series)", w / 2 - 230, 30, ACCENT);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "e^A = I + A + A^2/2! + A^3/3! + ... + A^n/n!", 30, 60, new Color(255, 200, 100));

            double[][] fibM = {{1, 1}, {1, 0}};
            double[][] sum = {{0, 0}, {0, 0}};

            // Display individual terms
            int termY = 85;
            int termX = 30;
            int colW = 260;
            int rowH = 120;

            for (int k = 0; k <= Math.min(terms, 15); k++) {
                double[][] aPow = matPow2(fibM, k);
                double fact = factorial(k);
                double[][] term = new double[2][2];
                for (int i = 0; i < 2; i++)
                    for (int j = 0; j < 2; j++)
                        term[i][j] = aPow[i][j] / fact;

                // Add to sum
                for (int i = 0; i < 2; i++)
                    for (int j = 0; j < 2; j++)
                        sum[i][j] += term[i][j];

                // Position: grid layout
                int col = k % 5;
                int row = k / 5;
                int tx = termX + col * colW;
                int ty = termY + row * rowH;

                if (ty + rowH > h - 200) continue; // avoid overflow

                float hue = k / 16.0f;
                Color termColor = Color.getHSBColor(hue, 0.7f, 1.0f);

                // Term box
                g2.setColor(withAlpha(termColor, 25));
                g2.fillRoundRect(tx, ty, colW - 15, rowH - 10, 8, 8);
                g2.setColor(withAlpha(termColor, 80));
                g2.drawRoundRect(tx, ty, colW - 15, rowH - 10, 8, 8);

                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                String label = (k == 0) ? "I (Identity)" : String.format("A^%d / %d!", k, k);
                drawGlowString(g2, label, tx + 5, ty + 16, termColor);

                g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
                g2.setColor(TEXT_COLOR);
                g2.drawString(String.format("[%8.4f %8.4f]", term[0][0], term[0][1]), tx + 5, ty + 38);
                g2.drawString(String.format("[%8.4f %8.4f]", term[1][0], term[1][1]), tx + 5, ty + 54);

                // Show factorial
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(withAlpha(TEXT_COLOR, 140));
                if (k > 0) {
                    g2.drawString(String.format("%d! = %.0f", k, fact), tx + 5, ty + 72);
                }
            }

            // Running sum at bottom
            int sumY = h - 180;
            g2.setColor(colorWithAlpha(20, 20, 50, 220));
            g2.fillRoundRect(30, sumY, w - 60, 130, 10, 10);
            g2.setColor(withAlpha(ACCENT, 100));
            g2.drawRoundRect(30, sumY, w - 60, 130, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawGlowString(g2, String.format("Running Sum (e^A with %d terms):", terms + 1), 50, sumY + 25, ACCENT);

            g2.setFont(new Font("Monospaced", Font.BOLD, 20));
            g2.setColor(new Color(100, 255, 150));
            g2.drawString(String.format("[%12.6f  %12.6f]", sum[0][0], sum[0][1]), 80, sumY + 60);
            g2.drawString(String.format("[%12.6f  %12.6f]", sum[1][0], sum[1][1]), 80, sumY + 85);

            // Show actual e^A values for reference
            // e^A for Fibonacci matrix can be computed analytically but we show the numerical convergence
            g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
            g2.setColor(withAlpha(TEXT_COLOR, 160));
            g2.drawString("As more terms are added, the matrix exponential converges.", 80, sumY + 115);
        }
    }

    // =====================================================================
    // TAB 5: Taylor Series e^x
    // =====================================================================

    /**
     * Panel showing the Taylor series approximation of e^x with animated
     * polynomial terms converging to the exponential function.
     */
    static class TaylorExpPanel extends JPanel {
        private int maxTerms = 1;
        private final int totalTerms = 12;
        private double time = 0;
        private double xMin = -5, xMax = 5;
        private double yMin = -3, yMax = 15;
        private boolean animating = false;
        private int animFrame = 0;
        private JSlider termsSlider;
        private JSlider zoomSlider;
        private JTextArea infoArea;
        private JLabel formulaLabel;
        private DrawPanel drawPanel;

        /**
         * Constructs the Taylor Series panel with controls and plot area.
         */
        public TaylorExpPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            drawPanel = new DrawPanel();

            // --- Right sidebar ---
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(25, 25, 45));
            sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            sidebar.setPreferredSize(new Dimension(280, 0));

            JLabel title = new JLabel("Taylor Series");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(5));

            JLabel subtitle = new JLabel("Approximation of e^x");
            subtitle.setForeground(new Color(180, 180, 200));
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
            subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(subtitle);
            sidebar.add(Box.createVerticalStrut(12));

            JTextArea explanation = new JTextArea(
                "The Taylor series breaks e^x\n" +
                "into a sum of polynomials.\n\n" +
                "With each new term, the\n" +
                "colored curve gets closer\n" +
                "to the white e^x curve.\n\n" +
                "White line = exact e^x\n" +
                "Colored lines = approximations"
            );
            explanation.setEditable(false);
            explanation.setBackground(new Color(35, 35, 60));
            explanation.setForeground(new Color(200, 200, 220));
            explanation.setFont(new Font("SansSerif", Font.PLAIN, 12));
            explanation.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            explanation.setMaximumSize(new Dimension(250, 170));
            explanation.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(explanation);
            sidebar.add(Box.createVerticalStrut(15));

            // Terms slider
            JLabel termsTitle = new JLabel("Number of terms");
            termsTitle.setForeground(Color.WHITE);
            termsTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            termsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(termsTitle);
            sidebar.add(Box.createVerticalStrut(3));

            termsSlider = new JSlider(0, totalTerms, 1);
            termsSlider.setBackground(new Color(25, 25, 45));
            termsSlider.setForeground(Color.WHITE);
            termsSlider.setMajorTickSpacing(1);
            termsSlider.setPaintTicks(true);
            termsSlider.setPaintLabels(true);
            termsSlider.setMaximumSize(new Dimension(250, 45));
            termsSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            termsSlider.addChangeListener(e -> {
                maxTerms = termsSlider.getValue();
                if (termsSlider.getValueIsAdjusting()) animating = false;
                updateTaylorInfo();
                drawPanel.repaint();
            });
            sidebar.add(termsSlider);
            sidebar.add(Box.createVerticalStrut(10));

            // Formula
            formulaLabel = new JLabel("<html><b>T(x) = 1</b></html>");
            formulaLabel.setForeground(new Color(255, 200, 100));
            formulaLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            formulaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(formulaLabel);
            sidebar.add(Box.createVerticalStrut(8));

            // Info area
            infoArea = new JTextArea(4, 20);
            infoArea.setEditable(false);
            infoArea.setBackground(new Color(35, 35, 60));
            infoArea.setForeground(new Color(150, 255, 150));
            infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            infoArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            infoArea.setMaximumSize(new Dimension(250, 90));
            infoArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(infoArea);
            sidebar.add(Box.createVerticalStrut(12));

            // Zoom
            JLabel zoomTitle = new JLabel("Zoom");
            zoomTitle.setForeground(Color.WHITE);
            zoomTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            zoomTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(zoomTitle);
            sidebar.add(Box.createVerticalStrut(3));

            zoomSlider = new JSlider(2, 20, 5);
            zoomSlider.setBackground(new Color(25, 25, 45));
            zoomSlider.setForeground(Color.WHITE);
            zoomSlider.setMaximumSize(new Dimension(250, 30));
            zoomSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            zoomSlider.addChangeListener(e -> {
                int val = zoomSlider.getValue();
                xMin = -val; xMax = val;
                yMin = -val * 0.6; yMax = val * 3;
                drawPanel.repaint();
            });
            sidebar.add(zoomSlider);
            sidebar.add(Box.createVerticalStrut(15));

            // Animate button
            JButton animBtn = createDarkButton("Animate");
            animBtn.setBackground(new Color(52, 152, 219));
            animBtn.setMaximumSize(new Dimension(250, 35));
            animBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            animBtn.addActionListener(e -> {
                maxTerms = 0;
                animFrame = 0;
                animating = true;
                termsSlider.setValue(0);
                updateTaylorInfo();
            });
            sidebar.add(animBtn);
            sidebar.add(Box.createVerticalGlue());

            add(drawPanel, BorderLayout.CENTER);
            add(sidebar, BorderLayout.EAST);

            updateTaylorInfo();

            javax.swing.Timer timer = new javax.swing.Timer(16, e -> {
                time += 0.02;
                if (animating) {
                    animFrame++;
                    if (animFrame >= 60) {
                        animFrame = 0;
                        maxTerms++;
                        if (maxTerms <= totalTerms) {
                            termsSlider.setValue(maxTerms);
                            updateTaylorInfo();
                        } else {
                            maxTerms = totalTerms;
                            animating = false;
                        }
                    }
                }
                drawPanel.repaint();
            });
            timer.start();
        }

        private void updateTaylorInfo() {
            formulaLabel.setText("<html><b>" + buildTaylorFormula(maxTerms) + "</b></html>");
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
                "At x = 1:\n  Exact e  = %.8f\n  Approx   = %.8f\n  Error    = %.8f (%s)",
                exact, approx, error, accuracy));
        }

        private String buildTaylorFormula(int n) {
            StringBuilder sb = new StringBuilder("T(x) = ");
            for (int i = 0; i < n && i < 8; i++) {
                if (i > 0) sb.append(" + ");
                if (i == 0) sb.append("1");
                else if (i == 1) sb.append("x");
                else sb.append("x^").append(i).append("/").append(i).append("!");
            }
            if (n > 8) sb.append(" + ...");
            if (n == 0) sb.append("0");
            return sb.toString();
        }

        private double taylorExp(double x, int terms) {
            double sum = 0, power = 1, fact = 1;
            for (int i = 0; i < terms; i++) {
                if (i > 0) { power *= x; fact *= i; }
                sum += power / fact;
            }
            return sum;
        }

        private Color getTaylorColor(int index, int total) {
            float hue = (float) index / total;
            return Color.getHSBColor(hue, 0.9f, 1.0f);
        }

        /** Inner panel for drawing the Taylor series plot. */
        class DrawPanel extends JPanel {
            DrawPanel() { setBackground(new Color(15, 15, 30)); }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                setupRendering(g2);

                int w = getWidth(), h = getHeight();
                int margin = 50;
                int plotW = w - 2 * margin;
                int plotH = h - 2 * margin;

                // Background glow
                int glowCx = margin + (int) ((0 - xMin) / (xMax - xMin) * plotW);
                int glowCy = margin + (int) ((yMax - 0) / (yMax - yMin) * plotH);
                for (int r = 250; r > 0; r -= 5) {
                    float hue = (float) ((time * 0.02 + r * 0.001) % 1.0);
                    Color c = Color.getHSBColor(hue, 0.3f, 0.12f);
                    int alpha = clampAlpha((int) (25 * (1.0 - r / 250.0)));
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                    g2.fillOval(glowCx - r, glowCy - r, r * 2, r * 2);
                }

                // Grid
                g2.setColor(withAlpha(TEXT_COLOR, 20));
                for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                    int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                    g2.drawLine(px, margin, px, margin + plotH);
                }
                for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                    int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                    g2.drawLine(margin, py, margin + plotW, py);
                }

                // Axes
                g2.setColor(withAlpha(TEXT_COLOR, 80));
                g2.setStroke(new BasicStroke(1.5f));
                int originX = margin + (int) ((0 - xMin) / (xMax - xMin) * plotW);
                int originY = margin + (int) ((yMax - 0) / (yMax - yMin) * plotH);
                if (originX >= margin && originX <= margin + plotW)
                    g2.drawLine(originX, margin, originX, margin + plotH);
                if (originY >= margin && originY <= margin + plotH)
                    g2.drawLine(margin, originY, margin + plotW, originY);

                // Axis numbers
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(withAlpha(TEXT_COLOR, 150));
                for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                    if (i == 0) continue;
                    int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                    if (originY >= margin && originY <= margin + plotH)
                        g2.drawString(String.valueOf(i), px - 4, originY + 15);
                }
                for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                    if (i == 0) continue;
                    int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                    if (originX >= margin && originX <= margin + plotW)
                        g2.drawString(String.valueOf(i), originX + 5, py + 4);
                }

                int steps = plotW * 2;
                double dx = (xMax - xMin) / steps;
                Shape clipRect = new java.awt.Rectangle(margin, margin, plotW, plotH);

                // Real e^x (white glow line)
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
                g2.setColor(withAlpha(TEXT_COLOR, 25));
                g2.setStroke(new BasicStroke(10f));
                g2.draw(realPath);
                g2.setColor(withAlpha(TEXT_COLOR, 200));
                g2.setStroke(new BasicStroke(3f));
                g2.draw(realPath);
                g2.setClip(oldClip);

                // Taylor approximations
                for (int n = 1; n <= maxTerms; n++) {
                    Color color = getTaylorColor(n - 1, totalTerms);
                    boolean isLatest = (n == maxTerms);
                    int alpha = isLatest ? 255 : 50 + (int) (100.0 * n / maxTerms);

                    GeneralPath path = new GeneralPath();
                    started = false;
                    double prevY = 0;
                    for (int s = 0; s <= steps; s++) {
                        double x = xMin + s * dx;
                        double y = taylorExp(x, n);
                        if (Double.isNaN(y) || Double.isInfinite(y) || Math.abs(y) > 1e6) {
                            started = false; continue;
                        }
                        if (started && Math.abs(y - prevY) > (yMax - yMin) * 2) started = false;
                        prevY = y;
                        float px = (float) (margin + (x - xMin) / (xMax - xMin) * plotW);
                        float py = (float) (margin + (yMax - y) / (yMax - yMin) * plotH);
                        if (!started) { path.moveTo(px, py); started = true; }
                        else path.lineTo(px, py);
                    }
                    oldClip = g2.getClip();
                    g2.setClip(clipRect);
                    if (isLatest) {
                        g2.setColor(withAlpha(color, 35));
                        g2.setStroke(new BasicStroke(10f));
                        g2.draw(path);
                    }
                    g2.setColor(withAlpha(color, alpha));
                    g2.setStroke(new BasicStroke(isLatest ? 3.5f : 1.5f));
                    g2.draw(path);
                    g2.setClip(oldClip);
                }

                // Legend
                int lx = margin + 12, ly = margin + 22;
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                int legendH = 20 + Math.min(maxTerms, 8) * 16 + 5;
                g2.setColor(colorWithAlpha(15, 15, 30, 180));
                g2.fillRoundRect(lx - 8, ly - 18, 150, legendH, 8, 8);
                g2.setColor(withAlpha(TEXT_COLOR, 220));
                g2.fillRect(lx - 3, ly - 12, 10, 10);
                g2.drawString("e^x (exact)", lx + 12, ly - 3);
                ly += 18;
                int showCount = Math.min(maxTerms, 8);
                for (int n = maxTerms - showCount + 1; n <= maxTerms; n++) {
                    if (n < 1) continue;
                    Color c = getTaylorColor(n - 1, totalTerms);
                    g2.setColor(withAlpha(c, (n == maxTerms) ? 255 : 140));
                    g2.fillRect(lx - 3, ly - 9, 10, 10);
                    g2.drawString("Degree " + (n - 1), lx + 12, ly);
                    ly += 16;
                }

                // Title
                g2.setFont(new Font("SansSerif", Font.BOLD, 18));
                drawGlowString(g2, "Taylor Series: e^x", margin, margin - 15, ACCENT);

                // Formula top right
                g2.setFont(new Font("Monospaced", Font.BOLD, 13));
                float fHue = (float) ((time * 0.04) % 1.0);
                g2.setColor(Color.getHSBColor(fHue, 0.5f, 1.0f));
                String formula = "e^x = 1 + x + x^2/2! + x^3/3! + ...";
                int fw = g2.getFontMetrics().stringWidth(formula);
                g2.drawString(formula, margin + plotW - fw, margin - 15);

                // Border
                g2.setColor(withAlpha(TEXT_COLOR, 40));
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(margin, margin, plotW, plotH);
            }
        }
    }

    // =====================================================================
    // TAB 6: e^x Properties (derivative, growth/decay, ln, limit definition)
    // =====================================================================

    /**
     * Panel showing key properties of e^x: derivative equals itself,
     * growth/decay with parameters, ln(x) as inverse, and the limit definition.
     */
    static class ExpPropertiesPanel extends JPanel {
        private double paramA = 1.0, paramB = 1.0;
        private int limitN = 1;
        private int animFrame = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;
        private JSlider aSlider, bSlider, nSlider;

        /**
         * Constructs the e^x properties panel.
         */
        public ExpPropertiesPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            // Sidebar
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(25, 25, 45));
            sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            sidebar.setPreferredSize(new Dimension(240, 0));

            JLabel title = new JLabel("e^x Properties");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(10));

            JTextArea info = new JTextArea(
                "Key properties of e^x:\n\n" +
                "1. (e^x)' = e^x\n" +
                "   Its own derivative!\n\n" +
                "2. a*e^(bx)\n" +
                "   b>0: growth\n" +
                "   b<0: decay\n\n" +
                "3. ln(x) is the inverse\n" +
                "   of e^x\n\n" +
                "4. e = lim (1+1/n)^n"
            );
            info.setEditable(false);
            info.setBackground(new Color(35, 35, 60));
            info.setForeground(new Color(200, 200, 220));
            info.setFont(new Font("Monospaced", Font.PLAIN, 11));
            info.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            info.setMaximumSize(new Dimension(220, 200));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(info);
            sidebar.add(Box.createVerticalStrut(12));

            // Parameter a
            sidebar.add(makeLabel("a (amplitude)"));
            aSlider = new JSlider(-30, 30, 10); // /10
            styleSmallSlider(aSlider);
            aSlider.addChangeListener(e -> { paramA = aSlider.getValue() / 10.0; repaint(); });
            sidebar.add(aSlider);
            sidebar.add(Box.createVerticalStrut(5));

            // Parameter b
            sidebar.add(makeLabel("b (rate)"));
            bSlider = new JSlider(-30, 30, 10); // /10
            styleSmallSlider(bSlider);
            bSlider.addChangeListener(e -> { paramB = bSlider.getValue() / 10.0; repaint(); });
            sidebar.add(bSlider);
            sidebar.add(Box.createVerticalStrut(10));

            // Limit n
            sidebar.add(makeLabel("n for (1+1/n)^n"));
            nSlider = new JSlider(1, 200, 1);
            styleSmallSlider(nSlider);
            nSlider.addChangeListener(e -> { limitN = nSlider.getValue(); repaint(); });
            sidebar.add(nSlider);
            sidebar.add(Box.createVerticalStrut(10));

            JButton animBtn = createDarkButton("Animate n");
            animBtn.setMaximumSize(new Dimension(220, 30));
            animBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            animBtn.addActionListener(e -> { animating = true; animFrame = 0; limitN = 1; nSlider.setValue(1); });
            sidebar.add(animBtn);
            sidebar.add(Box.createVerticalGlue());

            add(sidebar, BorderLayout.EAST);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                if (animating) {
                    if (animFrame % 5 == 0 && limitN < 200) {
                        limitN++;
                        nSlider.setValue(limitN);
                    }
                    if (limitN >= 200) animating = false;
                }
                repaint();
            });
            animTimer.start();
        }

        private JLabel makeLabel(String text) {
            JLabel l = new JLabel(text);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("SansSerif", Font.BOLD, 11));
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        private void styleSmallSlider(JSlider s) {
            s.setBackground(new Color(25, 25, 45));
            s.setForeground(Color.WHITE);
            s.setMaximumSize(new Dimension(220, 28));
            s.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth() - 240;
            int h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            int halfW = w / 2;
            int halfH = h / 2;

            // --- Top left: e^x and its derivative ---
            drawPlot(g2, 10, 10, halfW - 20, halfH - 20,
                "f(x) = e^x and f'(x) = e^x", -4, 4, -1, 8,
                new PlotFunc[]{
                    new PlotFunc("e^x", x -> Math.exp(x), new Color(100, 200, 255)),
                    new PlotFunc("(e^x)' = e^x", x -> Math.exp(x), new Color(255, 100, 100)),
                },
                "The derivative of e^x is itself!");

            // --- Top right: a * e^(bx) growth/decay ---
            String growLabel = String.format("f(x) = %.1f * e^(%.1fx)", paramA, paramB);
            String growInfo = paramB > 0 ? "Growth (b > 0)" : paramB < 0 ? "Decay (b < 0)" : "Constant (b = 0)";
            drawPlot(g2, halfW + 10, 10, halfW - 20, halfH - 20,
                growLabel, -4, 4, -5, 10,
                new PlotFunc[]{
                    new PlotFunc("a*e^(bx)", x -> paramA * Math.exp(paramB * x), new Color(255, 200, 50)),
                },
                growInfo);

            // --- Bottom left: e^x and ln(x) as inverses ---
            drawPlot(g2, 10, halfH + 10, halfW - 20, halfH - 30,
                "e^x and ln(x) are inverses", -4, 6, -4, 6,
                new PlotFunc[]{
                    new PlotFunc("e^x", x -> Math.exp(x), new Color(100, 200, 255)),
                    new PlotFunc("ln(x)", x -> x > 0 ? Math.log(x) : Double.NaN, new Color(100, 255, 100)),
                    new PlotFunc("y = x", x -> x, withAlpha(TEXT_COLOR, 100)),
                },
                "They mirror along y = x");

            // --- Bottom right: limit definition ---
            int lx = halfW + 10, ly = halfH + 10;
            int lw = halfW - 20, lh = halfH - 30;
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(lx, ly, lw, lh, 8, 8);

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "Limit Definition of e", lx + 10, ly + 25, ACCENT);

            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(new Color(255, 200, 100));
            g2.drawString("e = lim (1 + 1/n)^n", lx + 20, ly + 55);

            double limitVal = Math.pow(1.0 + 1.0 / limitN, limitN);
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.setColor(new Color(100, 255, 100));
            g2.drawString(String.format("n = %d", limitN), lx + 20, ly + 90);
            g2.drawString(String.format("(1 + 1/%d)^%d = %.10f", limitN, limitN, limitVal), lx + 20, ly + 115);

            g2.setColor(new Color(255, 150, 150));
            g2.drawString(String.format("e             = %.10f", Math.E), lx + 20, ly + 145);

            double error = Math.abs(Math.E - limitVal);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.drawString(String.format("Error: %.10f", error), lx + 20, ly + 175);

            // Convergence bar
            int barX = lx + 20, barY = ly + 195, barW = lw - 40, barH = 20;
            g2.setColor(withAlpha(TEXT_COLOR, 40));
            g2.fillRoundRect(barX, barY, barW, barH, 5, 5);
            double progress = Math.min(1.0, 1.0 - error / 0.5);
            g2.setColor(new Color(100, 255, 100));
            g2.fillRoundRect(barX, barY, (int)(barW * progress), barH, 5, 5);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString("Convergence to e", barX + barW / 2 - 55, barY + 14);
        }

        /**
         * Draws a small plot with multiple functions.
         */
        private void drawPlot(Graphics2D g2, int px, int py, int pw, int ph,
                              String title, double xMin, double xMax, double yMin, double yMax,
                              PlotFunc[] funcs, String note) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(px, py, pw, ph, 8, 8);

            int margin = 30;
            int plotW = pw - 2 * margin;
            int plotH = ph - 2 * margin - 15;
            int ox = px + margin;
            int oy = py + margin + 15;

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            drawGlowString(g2, title, px + 10, py + 18, ACCENT);

            // Grid
            g2.setColor(withAlpha(TEXT_COLOR, 15));
            for (int i = (int)Math.ceil(xMin); i <= (int)Math.floor(xMax); i++) {
                int gx = ox + (int)((i - xMin) / (xMax - xMin) * plotW);
                g2.drawLine(gx, oy, gx, oy + plotH);
            }
            for (int i = (int)Math.ceil(yMin); i <= (int)Math.floor(yMax); i++) {
                int gy = oy + (int)((yMax - i) / (yMax - yMin) * plotH);
                g2.drawLine(ox, gy, ox + plotW, gy);
            }

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 60));
            g2.setStroke(new BasicStroke(1));
            int axisX = ox + (int)((0 - xMin) / (xMax - xMin) * plotW);
            int axisY = oy + (int)((yMax - 0) / (yMax - yMin) * plotH);
            if (axisX >= ox && axisX <= ox + plotW) g2.drawLine(axisX, oy, axisX, oy + plotH);
            if (axisY >= oy && axisY <= oy + plotH) g2.drawLine(ox, axisY, ox + plotW, axisY);

            // Plot functions
            Shape oldClip = g2.getClip();
            g2.setClip(ox, oy, plotW, plotH);
            int steps = plotW * 2;
            double dx = (xMax - xMin) / steps;
            for (PlotFunc f : funcs) {
                GeneralPath path = new GeneralPath();
                boolean started = false;
                for (int s = 0; s <= steps; s++) {
                    double x = xMin + s * dx;
                    double y = f.func.applyAsDouble(x);
                    if (Double.isNaN(y) || Double.isInfinite(y) || Math.abs(y) > 1e6) { started = false; continue; }
                    float fx = (float)(ox + (x - xMin) / (xMax - xMin) * plotW);
                    float fy = (float)(oy + (yMax - y) / (yMax - yMin) * plotH);
                    if (!started) { path.moveTo(fx, fy); started = true; }
                    else path.lineTo(fx, fy);
                }
                g2.setColor(withAlpha(f.color, 30));
                g2.setStroke(new BasicStroke(6f));
                g2.draw(path);
                g2.setColor(f.color);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(path);
            }
            g2.setClip(oldClip);

            // Note
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            g2.drawString(note, px + 10, py + ph - 8);
            g2.setStroke(new BasicStroke(1));
        }

        /** Helper class for plot functions. */
        static class PlotFunc {
            String name;
            java.util.function.DoubleUnaryOperator func;
            Color color;
            PlotFunc(String n, java.util.function.DoubleUnaryOperator f, Color c) {
                this.name = n; this.func = f; this.color = c;
            }
        }
    }

    // =====================================================================
    // TAB 7: Euler Formula (e^(ix) = cos(x) + i*sin(x))
    // =====================================================================

    /**
     * Panel visualizing Euler's formula on the complex plane,
     * showing e^(ix) as rotation, and Euler's identity e^(i*pi)+1=0.
     */
    static class EulerFormulaPanel extends JPanel {
        private double angle = 0;
        private double realPart = 0;
        private int animFrame = 0;
        private javax.swing.Timer animTimer;
        private JSlider angleSlider, realSlider;

        /**
         * Constructs the Euler Formula panel.
         */
        public EulerFormulaPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(25, 25, 45));
            sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            sidebar.setPreferredSize(new Dimension(260, 0));

            JLabel title = new JLabel("Euler's Formula");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(8));

            JTextArea info = new JTextArea(
                "Euler's formula:\n\n" +
                "e^(ix) = cos(x) + i*sin(x)\n\n" +
                "This connects:\n" +
                "- Exponentials\n" +
                "- Trigonometry\n" +
                "- Complex numbers\n" +
                "- Rotation\n\n" +
                "e^(ix) traces the unit\n" +
                "circle in the complex plane.\n\n" +
                "Adding a real part (a):\n" +
                "e^((a+ix)) = e^a * e^(ix)\n" +
                "= growth/decay + rotation"
            );
            info.setEditable(false);
            info.setBackground(new Color(35, 35, 60));
            info.setForeground(new Color(200, 200, 220));
            info.setFont(new Font("SansSerif", Font.PLAIN, 11));
            info.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            info.setMaximumSize(new Dimension(240, 260));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(info);
            sidebar.add(Box.createVerticalStrut(12));

            // Angle slider (x in e^(ix))
            JLabel aLabel = new JLabel("x (angle in radians)");
            aLabel.setForeground(Color.WHITE);
            aLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            aLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(aLabel);
            angleSlider = new JSlider(0, 628, 0); // 0 to 2*pi * 100
            angleSlider.setBackground(new Color(25, 25, 45));
            angleSlider.setForeground(Color.WHITE);
            angleSlider.setMaximumSize(new Dimension(240, 28));
            angleSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            angleSlider.addChangeListener(e -> { angle = angleSlider.getValue() / 100.0; repaint(); });
            sidebar.add(angleSlider);
            sidebar.add(Box.createVerticalStrut(8));

            // Real part slider (a in e^((a+ix)))
            JLabel rLabel = new JLabel("a (real part, growth/decay)");
            rLabel.setForeground(Color.WHITE);
            rLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            rLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(rLabel);
            realSlider = new JSlider(-20, 20, 0); // /10
            realSlider.setBackground(new Color(25, 25, 45));
            realSlider.setForeground(Color.WHITE);
            realSlider.setMaximumSize(new Dimension(240, 28));
            realSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            realSlider.addChangeListener(e -> { realPart = realSlider.getValue() / 10.0; repaint(); });
            sidebar.add(realSlider);
            sidebar.add(Box.createVerticalStrut(15));

            // Euler identity box
            JTextArea identity = new JTextArea(
                "Euler's Identity (x = pi):\n\n" +
                "  e^(i*pi) + 1 = 0\n\n" +
                "Connects: e, i, pi, 1, 0"
            );
            identity.setEditable(false);
            identity.setBackground(new Color(50, 30, 30));
            identity.setForeground(new Color(255, 200, 150));
            identity.setFont(new Font("Monospaced", Font.BOLD, 12));
            identity.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            identity.setMaximumSize(new Dimension(240, 100));
            identity.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(identity);

            sidebar.add(Box.createVerticalGlue());
            add(sidebar, BorderLayout.EAST);

            animTimer = new javax.swing.Timer(16, e -> { animFrame++; repaint(); });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth() - 260;
            int h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            int cx = w / 2;
            int cy = h / 2;
            double scale = Math.min(w, h) * 0.25;

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            drawGlowString(g2, "Euler's Formula: e^(ix) = cos(x) + i*sin(x)", 20, 28, ACCENT);

            // Grid
            g2.setColor(withAlpha(TEXT_COLOR, 15));
            for (int i = -5; i <= 5; i++) {
                g2.drawLine(cx + (int)(i * scale), 40, cx + (int)(i * scale), h - 10);
                g2.drawLine(10, cy - (int)(i * scale), w - 10, cy - (int)(i * scale));
            }

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(10, cy, w - 10, cy);
            g2.drawLine(cx, 40, cx, h - 10);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Real", w - 45, cy - 8);
            g2.drawString("Imaginary", cx + 8, 55);

            // Unit circle
            g2.setColor(withAlpha(TEXT_COLOR, 40));
            g2.setStroke(new BasicStroke(1.5f));
            int ucr = (int) scale;
            g2.drawOval(cx - ucr, cy - ucr, ucr * 2, ucr * 2);

            // Draw spiral trace for e^((a+ix)) for x from 0 to current angle
            double r0 = Math.exp(realPart * 0);
            GeneralPath spiral = new GeneralPath();
            boolean started = false;
            int traceSteps = (int)(angle * 50) + 1;
            for (int s = 0; s <= traceSteps; s++) {
                double t = angle * s / traceSteps;
                double radius = Math.exp(realPart * t / (2 * Math.PI)) * scale;
                double px = cx + radius * Math.cos(t);
                double py = cy - radius * Math.sin(t);
                if (!started) { spiral.moveTo((float)px, (float)py); started = true; }
                else spiral.lineTo((float)px, (float)py);
            }
            float traceHue = (float)((animFrame * 0.005) % 1.0);
            Color traceColor = Color.getHSBColor(traceHue, 0.8f, 1.0f);
            g2.setColor(withAlpha(traceColor, 30));
            g2.setStroke(new BasicStroke(6));
            g2.draw(spiral);
            g2.setColor(traceColor);
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(spiral);

            // Current point
            double radius = Math.exp(realPart * angle / (2 * Math.PI)) * scale;
            double pointX = cx + radius * Math.cos(angle);
            double pointY = cy - radius * Math.sin(angle);

            // Projections
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 3}, 0));
            g2.setColor(withAlpha(new Color(100, 200, 255), 120));
            g2.drawLine((int)pointX, (int)pointY, (int)pointX, cy); // to real axis
            g2.setColor(withAlpha(new Color(100, 255, 100), 120));
            g2.drawLine((int)pointX, (int)pointY, cx, (int)pointY); // to imaginary axis
            g2.setStroke(new BasicStroke(1));

            // Radius line
            g2.setColor(withAlpha(Color.WHITE, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx, cy, (int)pointX, (int)pointY);

            // Point
            drawGlowCircle(g2, (int)pointX, (int)pointY, 8, traceColor);

            // Angle arc
            if (angle > 0.05) {
                g2.setColor(withAlpha(new Color(255, 200, 100), 100));
                g2.setStroke(new BasicStroke(2));
                int arcR = 40;
                g2.drawArc(cx - arcR, cy - arcR, arcR * 2, arcR * 2, 0, (int)Math.toDegrees(angle));
            }
            g2.setStroke(new BasicStroke(1));

            // Labels
            double cosVal = Math.cos(angle) * Math.exp(realPart * angle / (2 * Math.PI));
            double sinVal = Math.sin(angle) * Math.exp(realPart * angle / (2 * Math.PI));

            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.setColor(new Color(100, 200, 255));
            g2.drawString(String.format("cos(%.2f) = %.4f", angle, Math.cos(angle)), 20, h - 80);
            g2.setColor(new Color(100, 255, 100));
            g2.drawString(String.format("sin(%.2f) = %.4f", angle, Math.sin(angle)), 20, h - 60);
            g2.setColor(new Color(255, 200, 100));
            g2.drawString(String.format("x = %.2f rad = %.1f deg", angle, Math.toDegrees(angle)), 20, h - 40);

            if (Math.abs(realPart) > 0.01) {
                g2.setColor(new Color(255, 150, 255));
                g2.drawString(String.format("e^(%.1f) * e^(i*%.2f) = %.4f + %.4fi",
                    realPart * angle / (2 * Math.PI), angle, cosVal, sinVal), 20, h - 20);
            } else {
                g2.setColor(traceColor);
                g2.drawString(String.format("e^(i*%.2f) = %.4f + %.4fi", angle, Math.cos(angle), Math.sin(angle)), 20, h - 20);
            }

            // cos/sin formulas from e
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            g2.drawString("cos(x) = (e^(ix) + e^(-ix)) / 2", 20, 55);
            g2.drawString("sin(x) = (e^(ix) - e^(-ix)) / (2i)", 20, 70);
        }
    }

    // =====================================================================
    // TAB 8: Differential Equations & Laplace Transform
    // =====================================================================

    /**
     * Panel visualizing the differential equation y' = ky and
     * the Laplace transform with e^(-st) as kernel.
     */
    static class DiffEqLaplacePanel extends JPanel {
        private double kValue = 1.0;
        private double sValue = 1.0;
        private int animFrame = 0;
        private javax.swing.Timer animTimer;

        /**
         * Constructs the DiffEq and Laplace panel.
         */
        public DiffEqLaplacePanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(25, 25, 45));
            sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            sidebar.setPreferredSize(new Dimension(260, 0));

            JLabel title = new JLabel("DiffEq & Laplace");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(8));

            JTextArea info = new JTextArea(
                "Differential equation:\n" +
                "  y' = k*y\n" +
                "  Solution: y = C*e^(kx)\n\n" +
                "k > 0: exponential growth\n" +
                "k < 0: exponential decay\n" +
                "k = 0: constant\n\n" +
                "Laplace Transform:\n" +
                "  L{f(t)} = integral\n" +
                "    f(t)*e^(-st) dt\n\n" +
                "The kernel e^(-st) maps\n" +
                "time functions to the\n" +
                "s-domain.\n\n" +
                "Key transforms:\n" +
                "  L{1} = 1/s\n" +
                "  L{e^(at)} = 1/(s-a)\n" +
                "  L{cos(wt)} = s/(s^2+w^2)\n" +
                "  L{sin(wt)} = w/(s^2+w^2)"
            );
            info.setEditable(false);
            info.setBackground(new Color(35, 35, 60));
            info.setForeground(new Color(200, 200, 220));
            info.setFont(new Font("Monospaced", Font.PLAIN, 10));
            info.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            info.setMaximumSize(new Dimension(240, 350));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(info);
            sidebar.add(Box.createVerticalStrut(10));

            // k slider
            JLabel kLabel = new JLabel("k (growth/decay rate)");
            kLabel.setForeground(Color.WHITE);
            kLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            kLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(kLabel);
            JSlider kSlider = new JSlider(-30, 30, 10);
            kSlider.setBackground(new Color(25, 25, 45));
            kSlider.setForeground(Color.WHITE);
            kSlider.setMaximumSize(new Dimension(240, 28));
            kSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            kSlider.addChangeListener(e -> { kValue = kSlider.getValue() / 10.0; repaint(); });
            sidebar.add(kSlider);
            sidebar.add(Box.createVerticalStrut(8));

            // s slider
            JLabel sLabel = new JLabel("s (Laplace parameter)");
            sLabel.setForeground(Color.WHITE);
            sLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            sLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(sLabel);
            JSlider sSlider = new JSlider(1, 50, 10);
            sSlider.setBackground(new Color(25, 25, 45));
            sSlider.setForeground(Color.WHITE);
            sSlider.setMaximumSize(new Dimension(240, 28));
            sSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            sSlider.addChangeListener(e -> { sValue = sSlider.getValue() / 10.0; repaint(); });
            sidebar.add(sSlider);

            sidebar.add(Box.createVerticalGlue());
            add(sidebar, BorderLayout.EAST);

            animTimer = new javax.swing.Timer(16, e -> { animFrame++; repaint(); });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth() - 260;
            int h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            int halfW = w / 2;
            int halfH = h / 2;
            int margin = 40;

            // --- Top: y' = ky solutions ---
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawGlowString(g2, String.format("y' = %.1fy  =>  y(t) = e^(%.1ft)", kValue, kValue), 20, 28, ACCENT);

            int plotW = w - 2 * margin;
            int plotH = halfH - margin - 30;
            int ox = margin, oy = 40;

            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(ox - 5, oy - 5, plotW + 10, plotH + 10, 8, 8);

            // Grid and axes
            double tMin = 0, tMax = 5, yMin2 = -2, yMax2 = 5;
            g2.setColor(withAlpha(TEXT_COLOR, 15));
            for (int i = (int)tMin; i <= (int)tMax; i++) {
                int px = ox + (int)((i - tMin) / (tMax - tMin) * plotW);
                g2.drawLine(px, oy, px, oy + plotH);
            }
            g2.setColor(withAlpha(TEXT_COLOR, 60));
            int zeroY = oy + (int)((yMax2) / (yMax2 - yMin2) * plotH);
            g2.drawLine(ox, zeroY, ox + plotW, zeroY);
            g2.drawLine(ox, oy, ox, oy + plotH);

            // Axis labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(withAlpha(TEXT_COLOR, 120));
            g2.drawString("t", ox + plotW - 10, zeroY + 15);

            // Plot multiple k values for context
            Shape oldClip = g2.getClip();
            g2.setClip(ox, oy, plotW, plotH);
            double[] kValues = {-2, -1, -0.5, 0, 0.5, 1, 2};
            for (double k : kValues) {
                boolean isCurrent = Math.abs(k - kValue) < 0.05;
                float hue = (float)((k + 3) / 6.0);
                Color c = Color.getHSBColor(hue, isCurrent ? 0.9f : 0.4f, isCurrent ? 1.0f : 0.5f);
                g2.setColor(isCurrent ? c : withAlpha(c, 60));
                g2.setStroke(new BasicStroke(isCurrent ? 3f : 1f));
                GeneralPath path = new GeneralPath();
                boolean started = false;
                for (int s = 0; s <= plotW; s++) {
                    double t = tMin + (tMax - tMin) * s / plotW;
                    double y = Math.exp(k * t);
                    if (Math.abs(y) > 50) { started = false; continue; }
                    float fx = ox + s;
                    float fy = (float)(oy + (yMax2 - y) / (yMax2 - yMin2) * plotH);
                    if (!started) { path.moveTo(fx, fy); started = true; }
                    else path.lineTo(fx, fy);
                }
                g2.draw(path);
                if (isCurrent) {
                    g2.setColor(withAlpha(c, 30));
                    g2.setStroke(new BasicStroke(8f));
                    g2.draw(path);
                }
            }

            // Current k value curve
            float curHue = (float)((kValue + 3) / 6.0);
            Color curColor = Color.getHSBColor(curHue, 0.9f, 1.0f);
            g2.setClip(oldClip);
            g2.setStroke(new BasicStroke(1));

            // Labels
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(curColor);
            String behavior = kValue > 0 ? "Growth" : kValue < 0 ? "Decay" : "Constant";
            g2.drawString(String.format("k = %.1f (%s)", kValue, behavior), ox + 10, oy + plotH + 20);

            // --- Bottom: Laplace kernel e^(-st) ---
            int ly = halfH + 20;
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawGlowString(g2, String.format("Laplace kernel: e^(-%.1ft)", sValue), 20, ly, ACCENT);

            int lPlotY = ly + 15;
            int lPlotH = halfH - 60;

            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(ox - 5, lPlotY - 5, plotW + 10, lPlotH + 10, 8, 8);

            // Grid
            g2.setColor(withAlpha(TEXT_COLOR, 15));
            for (int i = 0; i <= 5; i++) {
                int px = ox + (int)(i / 5.0 * plotW);
                g2.drawLine(px, lPlotY, px, lPlotY + lPlotH);
            }
            int lZeroY = lPlotY + (int)(1.0 / 1.5 * lPlotH);
            g2.setColor(withAlpha(TEXT_COLOR, 60));
            g2.drawLine(ox, lZeroY, ox + plotW, lZeroY);
            g2.drawLine(ox, lPlotY, ox, lPlotY + lPlotH);

            // Plot e^(-st) for current s
            oldClip = g2.getClip();
            g2.setClip(ox, lPlotY, plotW, lPlotH);
            GeneralPath kernel = new GeneralPath();
            boolean started = false;
            for (int px = 0; px <= plotW; px++) {
                double t = 5.0 * px / plotW;
                double y = Math.exp(-sValue * t);
                float fx = ox + px;
                float fy = (float)(lPlotY + (1.0 - y) / 1.5 * lPlotH);
                if (!started) { kernel.moveTo(fx, fy); started = true; }
                else kernel.lineTo(fx, fy);
            }
            Color kernelColor = new Color(255, 150, 50);
            g2.setColor(withAlpha(kernelColor, 30));
            g2.setStroke(new BasicStroke(8f));
            g2.draw(kernel);
            g2.setColor(kernelColor);
            g2.setStroke(new BasicStroke(3f));
            g2.draw(kernel);

            // Also show f(t)=1 * e^(-st) shaded area
            GeneralPath area = new GeneralPath();
            area.moveTo(ox, lZeroY);
            for (int px = 0; px <= plotW; px++) {
                double t = 5.0 * px / plotW;
                double y = Math.exp(-sValue * t);
                area.lineTo(ox + px, (float)(lPlotY + (1.0 - y) / 1.5 * lPlotH));
            }
            area.lineTo(ox + plotW, lZeroY);
            area.closePath();
            g2.setColor(withAlpha(kernelColor, 15));
            g2.fill(area);
            g2.setClip(oldClip);
            g2.setStroke(new BasicStroke(1));

            // Info
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.setColor(kernelColor);
            g2.drawString(String.format("s = %.1f  =>  L{1} = 1/s = %.4f", sValue, 1.0 / sValue),
                ox + 10, lPlotY + lPlotH + 20);
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString("Shaded area = integral = Laplace transform value", ox + 10, lPlotY + lPlotH + 38);
        }
    }

    // =====================================================================
    // TAB 9: Fibonacci Spiral & Convergence
    // =====================================================================

    /**
     * Panel drawing Fibonacci squares with golden spiral arcs,
     * and a convergence graph of F(n+1)/F(n) toward phi.
     */
    static class FibSpiralPanel extends JPanel {
        private int animFrame = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;
        private int showSquares = 10;

        /**
         * Constructs the Fibonacci Spiral panel.
         */
        public FibSpiralPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            controls.setBackground(PANEL_BG);

            JButton animBtn = createDarkButton("Animate Growth");
            animBtn.addActionListener(e -> {
                animating = true;
                showSquares = 0;
                animFrame = 0;
                animTimer.start();
            });

            JButton resetBtn = createDarkButton("Reset");
            resetBtn.addActionListener(e -> {
                animating = false;
                animTimer.stop();
                showSquares = 10;
                animFrame = 0;
                repaint();
            });

            controls.add(animBtn);
            controls.add(resetBtn);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                if (animFrame % 25 == 0 && showSquares < 12) {
                    showSquares++;
                }
                if (showSquares >= 12) {
                    animating = false;
                    animTimer.stop();
                }
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawGlowString(g2, "Fibonacci Spiral & Convergence", w / 2 - 190, 30, ACCENT);

            // Draw Fibonacci squares on the left
            int spiralCX = w / 2 - 140;
            int spiralCY = h / 2 - 10;
            drawFibonacciSpiral(g2, spiralCX, spiralCY, showSquares);

            // Draw convergence graph on the right
            int gx = w - 350, gy = 50, gw = 320, gh = h - 130;
            drawSpiralConvergence(g2, gx, gy, gw, gh);
        }

        /**
         * Draws the Fibonacci squares and golden spiral arcs.
         */
        private void drawFibonacciSpiral(Graphics2D g2, int cx, int cy, int maxSq) {
            long[] fibs = new long[maxSq + 2];
            fibs[0] = 1;
            if (maxSq > 0) fibs[1] = 1;
            for (int i = 2; i <= maxSq; i++) fibs[i] = fibs[i - 1] + fibs[i - 2];

            double scale = 3.5;
            // Reduce scale for large Fibonacci numbers
            if (maxSq > 8) scale = 1.5;
            if (maxSq > 10) scale = 0.8;

            double x = 0, y = 0;
            int dir = 0; // 0=right, 1=up, 2=left, 3=down

            for (int i = 0; i < maxSq; i++) {
                double side = fibs[i] * scale;
                float hue = i / (float) Math.max(maxSq, 1);
                Color sqColor = Color.getHSBColor(hue, 0.6f, 0.9f);

                int sx = cx + (int) x;
                int sy = cy - (int) y;

                double drawX = x, drawY = y;

                // Position adjustment based on direction
                switch (dir) {
                    case 0: break;
                    case 1: drawY = y + side; break;
                    case 2: drawX = x - side; drawY = y + side; break;
                    case 3: drawX = x - side; break;
                }

                int px = cx + (int) drawX;
                int py = cy - (int) drawY;

                // Fill square
                g2.setColor(withAlpha(sqColor, 40));
                g2.fillRect(px, py, (int) side, (int) side);

                // Draw square border
                g2.setColor(sqColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(px, py, (int) side, (int) side);

                // Label
                if (side > 15) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, Math.min(14, (int) (side / 4)))));
                    g2.setColor(withAlpha(sqColor, 200));
                    g2.drawString(String.valueOf(fibs[i]), px + 4, py + (int) side - 4);
                }

                // Draw arc (quarter circle)
                g2.setColor(withAlpha(new Color(255, 255, 255), 180));
                g2.setStroke(new BasicStroke(2.5f));
                int arcX, arcY;
                int startAngle;
                switch (dir) {
                    case 0:
                        arcX = px + (int) side - (int) (2 * side);
                        arcY = py;
                        startAngle = 0;
                        break;
                    case 1:
                        arcX = px;
                        arcY = py + (int) side - (int) (2 * side);
                        startAngle = 270;
                        break;
                    case 2:
                        arcX = px;
                        arcY = py;
                        startAngle = 180;
                        break;
                    default:
                        arcX = px + (int) side - (int) (2 * side);
                        arcY = py;
                        startAngle = 90;
                        break;
                }
                g2.drawArc(arcX, arcY, (int) (2 * side), (int) (2 * side), startAngle, 90);

                // Move position for next square
                switch (dir) {
                    case 0: x += side; break;
                    case 1: y += side; break;
                    case 2: x -= side; break;
                    case 3: y -= side; break;
                }
                dir = (dir + 1) % 4;
            }
            g2.setStroke(new BasicStroke(1));
        }

        /**
         * Draws the convergence graph for the spiral panel.
         */
        private void drawSpiralConvergence(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 60));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Convergence to phi", x + 10, y + 22, ACCENT);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(TEXT_COLOR);
            g2.drawString(String.format("phi = %.10f...", PHI), x + 10, y + 42);

            int gx = x + 40, gy = y + 55;
            int gw = w - 55, gh = h - 80;

            // Phi line
            double minV = 1.0, maxV = 2.2;
            int phiY = gy + gh - (int) ((PHI - minV) / (maxV - minV) * gh);
            g2.setColor(withAlpha(new Color(255, 215, 0), 80));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            g2.drawLine(gx, phiY, gx + gw, phiY);
            g2.setStroke(new BasicStroke(1));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(255, 215, 0));
            g2.drawString("phi", gx + gw + 3, phiY + 4);

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 60));
            g2.drawLine(gx, gy, gx, gy + gh);
            g2.drawLine(gx, gy + gh, gx + gw, gy + gh);

            for (int n = 1; n <= 20; n++) {
                long fn = fibonacci(n);
                long fn1 = fibonacci(n + 1);
                if (fn > 0) {
                    double ratio = (double) fn1 / fn;
                    int px = gx + n * gw / 21;
                    int py = gy + gh - (int) ((ratio - minV) / (maxV - minV) * gh);
                    py = Math.max(gy, Math.min(gy + gh, py));

                    float hue = n / 21.0f;
                    Color c = Color.getHSBColor(hue, 0.8f, 1.0f);
                    drawGlowCircle(g2, px, py, 4, c);

                    if (n <= 10) {
                        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                        g2.setColor(withAlpha(c, 180));
                        g2.drawString(String.format("%.3f", ratio), px - 12, py - 8);
                    }
                }
            }

            // Show Fibonacci sequence
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            drawGlowString(g2, "Fibonacci Sequence:", x + 10, y + h - 50, new Color(100, 200, 255));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(TEXT_COLOR);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= 12; i++) {
                if (i > 0) sb.append(", ");
                sb.append(fibonacci(i));
            }
            sb.append(", ...");
            g2.drawString(sb.toString(), x + 10, y + h - 30);
        }
    }

    // =====================================================================
    // TAB 6: Dimensions (0D to 4D)
    // =====================================================================

    /**
     * Panel showing Fibonacci-related concepts across dimensions 0D through 4D,
     * including Tribonacci (3D) and Tetranacci (4D) matrices with wireframe projections.
     */
    static class DimensionsPanel extends JPanel {
        private int dimension = 0;
        private int animFrame = 0;
        private javax.swing.Timer animTimer;
        private JSlider dimSlider;

        /**
         * Constructs the Dimensions panel.
         */
        public DimensionsPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            controls.setBackground(PANEL_BG);

            dimSlider = createDarkSlider(0, 5, 0);
            dimSlider.setMajorTickSpacing(1);
            dimSlider.setSnapToTicks(true);
            dimSlider.addChangeListener(e -> {
                dimension = dimSlider.getValue();
                repaint();
            });

            controls.add(createDarkLabel("Dimension:"));
            controls.add(dimSlider);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                repaint();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            String[] dimNames = {"0D - Point", "1D - Line", "2D - Plane", "3D - Space", "4D - Hyperspace", "5D - Penteract"};
            Color[] dimColors = {
                new Color(255, 100, 100),
                new Color(100, 255, 100),
                new Color(100, 100, 255),
                new Color(255, 200, 50),
                new Color(200, 100, 255),
                new Color(255, 100, 200)
            };

            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            drawGlowString(g2, "Dimensions: " + dimNames[dimension], w / 2 - 150, 35, dimColors[dimension]);

            switch (dimension) {
                case 0: draw0D(g2, w, h); break;
                case 1: draw1D(g2, w, h); break;
                case 2: draw2D(g2, w, h); break;
                case 3: draw3D(g2, w, h); break;
                case 4: draw4D(g2, w, h); break;
                case 5: draw5D(g2, w, h); break;
            }
        }

        /**
         * Draws the 0D (point) visualization.
         */
        private void draw0D(Graphics2D g2, int w, int h) {
            int cx = w / 2, cy = h / 2 - 20;

            // Pulsing glow
            int pulseR = 30 + (int) (10 * Math.sin(animFrame * 0.05));
            for (int i = 5; i >= 0; i--) {
                int alpha = clampAlpha(30 - i * 5);
                g2.setColor(colorWithAlpha(255, 100, 100, alpha));
                g2.fillOval(cx - pulseR - i * 10, cy - pulseR - i * 10,
                        (pulseR + i * 10) * 2, (pulseR + i * 10) * 2);
            }
            g2.setColor(new Color(255, 100, 100));
            g2.fillOval(cx - 15, cy - 15, 30, 30);

            // Display a Fibonacci number
            int fibIdx = (animFrame / 60) % 15;
            long fibVal = fibonacci(fibIdx);
            g2.setFont(new Font("SansSerif", Font.BOLD, 60));
            String fibStr = String.valueOf(fibVal);
            int strW = g2.getFontMetrics().stringWidth(fibStr);
            drawGlowString(g2, fibStr, cx - strW / 2, cy + 100, new Color(255, 150, 150));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g2.setColor(TEXT_COLOR);
            g2.drawString("A 0D point: a single Fibonacci number F(" + fibIdx + ")", cx - 180, cy + 140);
            g2.drawString("In zero dimensions, we have a single value - no direction, no extent.", cx - 260, cy + 170);
        }

        /**
         * Draws the 1D (number line) visualization.
         */
        private void draw1D(Graphics2D g2, int w, int h) {
            int cy = h / 2;
            int lineX = 50, lineW = w - 100;

            // Number line
            g2.setColor(withAlpha(TEXT_COLOR, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(lineX, cy, lineX + lineW, cy);

            // Tick marks and Fibonacci numbers
            int maxFib = 12;
            int showCount = Math.min(maxFib, animFrame / 15 + 1);
            long maxVal = fibonacci(maxFib);

            for (int i = 0; i <= showCount; i++) {
                long fib = fibonacci(i);
                int px = lineX + (int) ((double) fib / maxVal * (lineW - 40));
                float hue = i / (float) maxFib;
                Color c = Color.getHSBColor(hue, 0.8f, 1.0f);

                // Tick
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(px, cy - 15, px, cy + 15);

                // Glow dot
                drawGlowCircle(g2, px, cy, 6, c);

                // Label
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.setColor(c);
                g2.drawString(String.valueOf(fib), px - 5, cy - 25);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(withAlpha(c, 150));
                g2.drawString("F(" + i + ")", px - 8, cy + 35);
            }
            g2.setStroke(new BasicStroke(1));

            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.setColor(TEXT_COLOR);
            g2.drawString("1D: Fibonacci numbers placed on a number line.", w / 2 - 180, cy + 80);
            g2.drawString("Each number is the sum of the previous two: F(n) = F(n-1) + F(n-2).", w / 2 - 250, cy + 105);
        }

        /**
         * Draws the 2D (plane) visualization with Fibonacci spiral.
         */
        private void draw2D(Graphics2D g2, int w, int h) {
            int cx = w / 2 - 50, cy = h / 2;

            // Grid
            g2.setColor(withAlpha(TEXT_COLOR, 20));
            int gridSize = 30;
            for (int i = -20; i <= 20; i++) {
                g2.drawLine(cx + i * gridSize, cy - 400, cx + i * gridSize, cy + 400);
                g2.drawLine(cx - 600, cy + i * gridSize, cx + 600, cy + i * gridSize);
            }

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx - 400, cy, cx + 400, cy);
            g2.drawLine(cx, cy - 300, cx, cy + 300);
            g2.setStroke(new BasicStroke(1));

            // Draw a simple golden spiral
            g2.setStroke(new BasicStroke(3));
            double angle = 0;
            double r = 2;
            int prevX = cx + (int) r, prevY = cy;
            int spiralSteps = Math.min(animFrame, 500);

            for (int i = 0; i < spiralSteps; i++) {
                angle += 0.05;
                r = 2 * Math.exp(0.0053 * angle / 0.05);
                if (r > 280) break;
                int nx = cx + (int) (r * Math.cos(angle));
                int ny = cy - (int) (r * Math.sin(angle));
                float hue = (float) (angle / (2 * Math.PI)) % 1.0f;
                g2.setColor(Color.getHSBColor(hue, 0.8f, 1.0f));
                g2.drawLine(prevX, prevY, nx, ny);
                prevX = nx;
                prevY = ny;
            }
            g2.setStroke(new BasicStroke(1));

            // Info
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.setColor(TEXT_COLOR);
            g2.drawString("2D: The Fibonacci matrix [[1,1],[1,0]] transforms the 2D plane.", 40, h - 70);
            g2.drawString("The golden spiral emerges from Fibonacci square subdivisions.", 40, h - 50);

            // Matrix display
            int mx = w - 200, my = 60;
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "2x2 Fibonacci Matrix:", mx, my, new Color(100, 100, 255));
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.setColor(new Color(150, 150, 255));
            g2.drawString("|1  1|", mx + 10, my + 30);
            g2.drawString("|1  0|", mx + 10, my + 50);
        }

        /**
         * Draws the 3D visualization with Tribonacci matrix and rotating wireframe.
         */
        private void draw3D(Graphics2D g2, int w, int h) {
            int cx = w / 2, cy = h / 2 - 20;

            // Show Tribonacci matrix
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            drawGlowString(g2, "3x3 Tribonacci Matrix:", 40, 70, new Color(255, 200, 50));
            g2.setFont(new Font("Monospaced", Font.BOLD, 16));
            g2.setColor(new Color(255, 220, 100));
            g2.drawString("|1  1  1|", 50, 100);
            g2.drawString("|1  0  0|", 50, 120);
            g2.drawString("|0  1  0|", 50, 140);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Tribonacci: T(n) = T(n-1) + T(n-2) + T(n-3)", 40, 170);
            g2.drawString("1, 1, 2, 4, 7, 13, 24, 44, 81, ...", 40, 190);

            // Draw rotating 3D wireframe cube
            double rotAngle = animFrame * 0.02;
            int cubeSize = 120;
            double[][] vertices = {
                {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
                {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1}
            };
            int[][] edges = {
                {0,1},{1,2},{2,3},{3,0},{4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
            };

            int[] px = new int[8], py = new int[8];
            for (int i = 0; i < 8; i++) {
                double x = vertices[i][0], y = vertices[i][1], z = vertices[i][2];
                // Rotate around Y
                double x2 = x * Math.cos(rotAngle) - z * Math.sin(rotAngle);
                double z2 = x * Math.sin(rotAngle) + z * Math.cos(rotAngle);
                // Rotate around X
                double y2 = y * Math.cos(rotAngle * 0.7) - z2 * Math.sin(rotAngle * 0.7);
                double z3 = y * Math.sin(rotAngle * 0.7) + z2 * Math.cos(rotAngle * 0.7);
                // Project
                double scale = 1.5 / (3 + z3);
                px[i] = cx + (int) (x2 * cubeSize * scale);
                py[i] = cy + (int) (y2 * cubeSize * scale);
            }

            // Draw edges
            for (int[] edge : edges) {
                float hue = (edge[0] + edge[1]) / 14.0f;
                Color c = Color.getHSBColor(hue, 0.7f, 1.0f);
                g2.setColor(withAlpha(c, 40));
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(px[edge[0]], py[edge[0]], px[edge[1]], py[edge[1]]);
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(px[edge[0]], py[edge[0]], px[edge[1]], py[edge[1]]);
            }

            // Draw vertices
            for (int i = 0; i < 8; i++) {
                float hue = i / 8.0f;
                drawGlowCircle(g2, px[i], py[i], 5, Color.getHSBColor(hue, 0.8f, 1.0f));
            }
            g2.setStroke(new BasicStroke(1));

            // 3D vectors
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "3D Vectors from Tribonacci Matrix", cx - 130, cy + cubeSize + 50, new Color(255, 200, 50));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Rotating wireframe cube in projected 3D space.", cx - 150, cy + cubeSize + 75);
        }

        /**
         * Draws the 4D visualization with Tetranacci matrix and rotating tesseract.
         */
        private void draw4D(Graphics2D g2, int w, int h) {
            int cx = w / 2, cy = h / 2 - 20;

            // Show Tetranacci matrix
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            drawGlowString(g2, "4x4 Tetranacci Matrix:", 40, 70, new Color(200, 100, 255));
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(new Color(220, 150, 255));
            g2.drawString("|1  1  1  1|", 50, 100);
            g2.drawString("|1  0  0  0|", 50, 120);
            g2.drawString("|0  1  0  0|", 50, 140);
            g2.drawString("|0  0  1  0|", 50, 160);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Tetranacci: T(n) = T(n-1) + T(n-2) + T(n-3) + T(n-4)", 40, 190);
            g2.drawString("1, 1, 2, 4, 8, 15, 29, 56, 108, ...", 40, 210);

            // Draw rotating tesseract (4D hypercube projection)
            double a = animFrame * 0.015;
            double b = animFrame * 0.01;

            // 4D vertices of a tesseract
            double[][] v4 = new double[16][4];
            for (int i = 0; i < 16; i++) {
                v4[i][0] = ((i & 1) == 0) ? -1 : 1;
                v4[i][1] = ((i & 2) == 0) ? -1 : 1;
                v4[i][2] = ((i & 4) == 0) ? -1 : 1;
                v4[i][3] = ((i & 8) == 0) ? -1 : 1;
            }

            // 4D edges
            List<int[]> edges4 = new ArrayList<>();
            for (int i = 0; i < 16; i++) {
                for (int j = i + 1; j < 16; j++) {
                    int diff = 0;
                    for (int k = 0; k < 4; k++) {
                        if (v4[i][k] != v4[j][k]) diff++;
                    }
                    if (diff == 1) edges4.add(new int[]{i, j});
                }
            }

            int[] px = new int[16], py = new int[16];
            int cubeSize = 100;
            for (int i = 0; i < 16; i++) {
                double x = v4[i][0], y = v4[i][1], z = v4[i][2], ww = v4[i][3];

                // Rotate in XW plane
                double x2 = x * Math.cos(a) - ww * Math.sin(a);
                double w2 = x * Math.sin(a) + ww * Math.cos(a);

                // Rotate in YZ plane
                double y2 = y * Math.cos(b) - z * Math.sin(b);
                double z2 = y * Math.sin(b) + z * Math.cos(b);

                // Rotate in XY plane
                double x3 = x2 * Math.cos(a * 0.5) - y2 * Math.sin(a * 0.5);
                double y3 = x2 * Math.sin(a * 0.5) + y2 * Math.cos(a * 0.5);

                // Perspective project from 4D to 2D
                double dist4 = 3.0 / (4.0 + w2);
                double dist3 = 1.5 / (3.0 + z2 * dist4);
                px[i] = cx + (int) (x3 * dist4 * cubeSize * dist3);
                py[i] = cy + (int) (y3 * dist4 * cubeSize * dist3);
            }

            // Draw edges
            for (int[] edge : edges4) {
                float hue = (edge[0] + edge[1]) / 32.0f;
                Color c = Color.getHSBColor(hue, 0.7f, 1.0f);
                g2.setColor(withAlpha(c, 30));
                g2.setStroke(new BasicStroke(4));
                g2.drawLine(px[edge[0]], py[edge[0]], px[edge[1]], py[edge[1]]);
                g2.setColor(withAlpha(c, 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(px[edge[0]], py[edge[0]], px[edge[1]], py[edge[1]]);
            }

            // Draw vertices
            for (int i = 0; i < 16; i++) {
                float hue = i / 16.0f;
                drawGlowCircle(g2, px[i], py[i], 4, Color.getHSBColor(hue, 0.8f, 1.0f));
            }
            g2.setStroke(new BasicStroke(1));

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "4D Tesseract (Hypercube) Projection", cx - 150, cy + cubeSize + 80, new Color(200, 100, 255));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("16 vertices, 32 edges", cx - 70, cy + cubeSize + 105);
        }

        /**
         * Draws the 5D visualization with Pentanacci matrix and rotating penteract.
         */
        private void draw5D(Graphics2D g2, int w, int h) {
            int cx = w / 2, cy = h / 2 - 20;

            // Pentanacci matrix
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            drawGlowString(g2, "5x5 Pentanacci Matrix:", 40, 70, new Color(255, 100, 200));
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(new Color(255, 150, 220));
            g2.drawString("|1  1  1  1  1|", 50, 100);
            g2.drawString("|1  0  0  0  0|", 50, 120);
            g2.drawString("|0  1  0  0  0|", 50, 140);
            g2.drawString("|0  0  1  0  0|", 50, 160);
            g2.drawString("|0  0  0  1  0|", 50, 180);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Pentanacci: P(n) = P(n-1) + P(n-2) + P(n-3) + P(n-4) + P(n-5)", 40, 210);
            g2.drawString("1, 1, 2, 4, 8, 16, 31, 61, 120, 236, ...", 40, 230);

            // Draw rotating 5D penteract (32 vertices, 80 edges)
            double a = animFrame * 0.012;
            double b = animFrame * 0.008;
            double c = animFrame * 0.006;

            // 5D vertices: 2^5 = 32
            int numVerts = 32;
            double[][] v5 = new double[numVerts][5];
            for (int i = 0; i < numVerts; i++) {
                v5[i][0] = ((i & 1) == 0) ? -1 : 1;
                v5[i][1] = ((i & 2) == 0) ? -1 : 1;
                v5[i][2] = ((i & 4) == 0) ? -1 : 1;
                v5[i][3] = ((i & 8) == 0) ? -1 : 1;
                v5[i][4] = ((i & 16) == 0) ? -1 : 1;
            }

            // 5D edges: connect vertices that differ in exactly one coordinate
            List<int[]> edges5 = new ArrayList<>();
            for (int i = 0; i < numVerts; i++) {
                for (int j = i + 1; j < numVerts; j++) {
                    int diff = 0;
                    for (int k = 0; k < 5; k++) {
                        if (v5[i][k] != v5[j][k]) diff++;
                    }
                    if (diff == 1) edges5.add(new int[]{i, j});
                }
            }

            int[] px = new int[numVerts], py = new int[numVerts];
            int cubeSize = 110;
            for (int i = 0; i < numVerts; i++) {
                double x = v5[i][0], y = v5[i][1], z = v5[i][2];
                double w4 = v5[i][3], w5 = v5[i][4];

                // Rotate in XW5 plane
                double x2 = x * Math.cos(a) - w5 * Math.sin(a);
                double w5r = x * Math.sin(a) + w5 * Math.cos(a);

                // Rotate in YW4 plane
                double y2 = y * Math.cos(b) - w4 * Math.sin(b);
                double w4r = y * Math.sin(b) + w4 * Math.cos(b);

                // Rotate in ZW5 plane
                double z2 = z * Math.cos(c) - w5r * Math.sin(c);
                double w5r2 = z * Math.sin(c) + w5r * Math.cos(c);

                // Rotate in XY plane
                double x3 = x2 * Math.cos(a * 0.3) - y2 * Math.sin(a * 0.3);
                double y3 = x2 * Math.sin(a * 0.3) + y2 * Math.cos(a * 0.3);

                // Perspective project 5D -> 2D
                double dist5 = 2.5 / (4.0 + w5r2);
                double dist4 = 2.5 / (4.0 + w4r * dist5);
                double dist3 = 1.5 / (3.0 + z2 * dist4 * dist5);
                px[i] = cx + (int) (x3 * dist5 * dist4 * cubeSize * dist3);
                py[i] = cy + (int) (y3 * dist5 * dist4 * cubeSize * dist3);
            }

            // Draw edges
            for (int[] edge : edges5) {
                float hue = (float) ((edge[0] + edge[1]) / 64.0 + animFrame * 0.001) % 1.0f;
                Color col = Color.getHSBColor(hue, 0.7f, 1.0f);
                g2.setColor(withAlpha(col, 20));
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(px[edge[0]], py[edge[0]], px[edge[1]], py[edge[1]]);
                g2.setColor(withAlpha(col, 140));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(px[edge[0]], py[edge[0]], px[edge[1]], py[edge[1]]);
            }

            // Draw vertices
            for (int i = 0; i < numVerts; i++) {
                float hue = (float) ((i / (float) numVerts + animFrame * 0.002) % 1.0);
                drawGlowCircle(g2, px[i], py[i], 3, Color.getHSBColor(hue, 0.9f, 1.0f));
            }
            g2.setStroke(new BasicStroke(1));

            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "5D Penteract (Hypercube) Projection", cx - 160, cy + cubeSize + 80, new Color(255, 100, 200));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("32 vertices, 80 edges", cx - 70, cy + cubeSize + 105);
        }
    }

    // =====================================================================
    // TAB 7: Circle & Pi
    // =====================================================================

    /**
     * Panel combining Monte Carlo pi estimation, Leibniz series for pi,
     * unit circle visualization, and area comparison of circle vs. square.
     */
    static class CirclePiPanel extends JPanel {
        private int animFrame = 0;
        private boolean animating = false;
        private javax.swing.Timer animTimer;
        private List<double[]> monteCarloDots = new ArrayList<>();
        private int insideCount = 0;
        private int totalDots = 0;
        private Random rng = new Random(42);

        /**
         * Constructs the Circle and Pi panel.
         */
        public CirclePiPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            controls.setBackground(PANEL_BG);

            JButton animBtn = createDarkButton("Animate");
            animBtn.addActionListener(e -> {
                animating = true;
                animFrame = 0;
                monteCarloDots.clear();
                insideCount = 0;
                totalDots = 0;
                rng = new Random(42);
                animTimer.start();
            });

            JButton resetBtn = createDarkButton("Reset");
            resetBtn.addActionListener(e -> {
                animating = false;
                animTimer.stop();
                animFrame = 0;
                monteCarloDots.clear();
                insideCount = 0;
                totalDots = 0;
                repaint();
            });

            controls.add(animBtn);
            controls.add(resetBtn);
            add(controls, BorderLayout.SOUTH);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                // Add Monte Carlo dots
                if (animFrame % 2 == 0 && totalDots < 3000) {
                    double dx = rng.nextDouble() * 2 - 1;
                    double dy = rng.nextDouble() * 2 - 1;
                    boolean inside = (dx * dx + dy * dy) <= 1.0;
                    monteCarloDots.add(new double[]{dx, dy, inside ? 1 : 0});
                    totalDots++;
                    if (inside) insideCount++;
                }
                repaint();
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawGlowString(g2, "Circle, Pi, and Estimation Methods", w / 2 - 200, 28, ACCENT);

            // Layout: 2x2 grid
            int halfW = w / 2 - 15;
            int halfH = (h - 80) / 2;

            // Top-left: Monte Carlo
            drawMonteCarlo(g2, 10, 40, halfW, halfH);

            // Top-right: Leibniz series
            drawLeibniz(g2, w / 2 + 5, 40, halfW, halfH);

            // Bottom-left: Unit circle
            drawUnitCircle(g2, 10, 40 + halfH + 5, halfW, halfH - 30);

            // Bottom-right: Area comparison
            drawAreaComparison(g2, w / 2 + 5, 40 + halfH + 5, halfW, halfH - 30);
        }

        /**
         * Draws the Monte Carlo pi estimation visualization.
         */
        private void drawMonteCarlo(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 60));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Monte Carlo Pi Estimation", x + 10, y + 20, new Color(100, 200, 255));

            int cx = x + w / 2;
            int cy = y + h / 2 + 15;
            int r = Math.min(w, h) / 2 - 40;

            // Draw square
            g2.setColor(withAlpha(TEXT_COLOR, 40));
            g2.drawRect(cx - r, cy - r, r * 2, r * 2);

            // Draw circle
            g2.setColor(withAlpha(new Color(100, 200, 255), 60));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            // Draw dots
            for (double[] dot : monteCarloDots) {
                int dx = cx + (int) (dot[0] * r);
                int dy = cy + (int) (dot[1] * r);
                if (dot[2] > 0.5) {
                    g2.setColor(colorWithAlpha(100, 255, 100, 150));
                } else {
                    g2.setColor(colorWithAlpha(255, 100, 100, 150));
                }
                g2.fillRect(dx, dy, 2, 2);
            }

            // Pi estimate
            if (totalDots > 0) {
                double piEst = 4.0 * insideCount / totalDots;
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                drawGlowString(g2, String.format("Pi ~ %.6f  (dots: %d)", piEst, totalDots),
                        x + 10, y + h - 10, new Color(100, 255, 100));
            }
        }

        /**
         * Draws the Leibniz series for pi visualization.
         */
        private void drawLeibniz(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 60));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Leibniz Series: pi/4 = 1 - 1/3 + 1/5 - ...", x + 10, y + 20, new Color(255, 200, 100));

            int terms = animating ? Math.min(animFrame / 3, 200) : 200;
            double sum = 0;

            int gx = x + 40, gy = y + 35;
            int gw = w - 55, gh = h - 70;

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 50));
            g2.drawLine(gx, gy, gx, gy + gh);
            g2.drawLine(gx, gy + gh, gx + gw, gy + gh);

            // Pi line
            double minV = 2.5, maxV = 4.0;
            int piY = gy + gh - (int) ((Math.PI - minV) / (maxV - minV) * gh);
            g2.setColor(withAlpha(new Color(255, 100, 100), 80));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            g2.drawLine(gx, piY, gx + gw, piY);
            g2.setStroke(new BasicStroke(1));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(new Color(255, 100, 100));
            g2.drawString("pi", gx + gw + 3, piY + 4);

            int prevPx = -1, prevPy = -1;
            for (int n = 0; n < terms; n++) {
                sum += (n % 2 == 0 ? 1.0 : -1.0) / (2 * n + 1);
                double piEst = sum * 4;

                if (n % Math.max(1, terms / 100) == 0 || n == terms - 1) {
                    int px = gx + (int) ((double) n / 200 * gw);
                    int py = gy + gh - (int) ((piEst - minV) / (maxV - minV) * gh);
                    py = Math.max(gy, Math.min(gy + gh, py));

                    float hue = (n / 200.0f) * 0.7f;
                    Color c = Color.getHSBColor(hue, 0.8f, 1.0f);

                    if (prevPx >= 0) {
                        g2.setColor(withAlpha(c, 100));
                        g2.drawLine(prevPx, prevPy, px, py);
                    }
                    prevPx = px;
                    prevPy = py;
                }
            }

            // Current value
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            drawGlowString(g2, String.format("Sum * 4 = %.8f  (terms: %d)", sum * 4, terms),
                    x + 10, y + h - 10, new Color(255, 200, 100));
        }

        /**
         * Draws the unit circle with sin/cos visualization.
         */
        private void drawUnitCircle(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 60));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Unit Circle - sin & cos", x + 10, y + 20, new Color(100, 255, 200));

            int cx = x + w / 2;
            int cy = y + h / 2 + 10;
            int r = Math.min(w, h) / 2 - 35;

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 50));
            g2.drawLine(cx - r - 20, cy, cx + r + 20, cy);
            g2.drawLine(cx, cy - r - 20, cx, cy + r + 20);

            // Circle
            g2.setColor(withAlpha(ACCENT, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);

            // Moving point
            double angle = animFrame * 0.03;
            int px = cx + (int) (r * Math.cos(angle));
            int py = cy - (int) (r * Math.sin(angle));

            // cos projection
            g2.setColor(withAlpha(new Color(255, 100, 100), 100));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0));
            g2.drawLine(px, py, px, cy);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 100, 100));
            g2.drawLine(cx, cy, px, cy);

            // sin projection
            g2.setColor(withAlpha(new Color(100, 100, 255), 100));
            g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0));
            g2.drawLine(px, py, cx, py);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(100, 100, 255));
            g2.drawLine(cx, cy, cx, py);

            // Radius
            g2.setColor(new Color(100, 255, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx, cy, px, py);

            // Point
            drawGlowCircle(g2, px, py, 6, new Color(255, 255, 100));
            g2.setStroke(new BasicStroke(1));

            // Labels
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(new Color(255, 100, 100));
            g2.drawString(String.format("cos = %.3f", Math.cos(angle)), cx + r + 5, cy + 15);
            g2.setColor(new Color(100, 100, 255));
            g2.drawString(String.format("sin = %.3f", Math.sin(angle)), cx - r - 60, cy - r - 5);
        }

        /**
         * Draws the area comparison of circle inscribed in square.
         */
        private void drawAreaComparison(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(colorWithAlpha(20, 20, 40, 200));
            g2.fillRoundRect(x, y, w, h, 10, 10);
            g2.setColor(withAlpha(ACCENT, 60));
            g2.drawRoundRect(x, y, w, h, 10, 10);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Area: Circle in Square, ratio = pi/4", x + 10, y + 20, new Color(200, 150, 255));

            int cx = x + w / 2 - 60;
            int cy = y + h / 2 + 10;
            int r = Math.min(w, h) / 2 - 40;

            // Square
            g2.setColor(withAlpha(new Color(200, 150, 255), 30));
            g2.fillRect(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(200, 150, 255));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(cx - r, cy - r, r * 2, r * 2);

            // Circle
            g2.setColor(withAlpha(new Color(100, 255, 200), 40));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(100, 255, 200));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1));

            // Info text
            int tx = cx + r + 20;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Square area = (2r)^2 = 4r^2", tx, cy - 40);
            g2.drawString("Circle area = pi * r^2", tx, cy - 20);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Ratio = pi/4 = 0.7854...", tx, cy + 5, new Color(255, 215, 0));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("This is why Monte Carlo", tx, cy + 30);
            g2.drawString("works: random dots in the", tx, cy + 48);
            g2.drawString("square land in the circle", tx, cy + 66);
            g2.drawString("with probability pi/4.", tx, cy + 84);
        }
    }

    // =====================================================================
    // TAB 8: Overview
    // =====================================================================
    // TAB 8: Norms (L1, L2, Lp, L-infinity)
    // =====================================================================

    /**
     * Panel visualizing different vector norms and their unit circles.
     * Shows L1 (Manhattan), L2 (Euclidean), Lp, and L-infinity norms.
     */
    static class NormsPanel extends JPanel {
        private double pValue = 2.0;
        private int animFrame = 0;
        private javax.swing.Timer animTimer;
        private JSlider pSlider;
        private JLabel pLabel;

        /**
         * Constructs the Norms visualization panel.
         */
        public NormsPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            // --- Right sidebar ---
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(30, 30, 55));
            sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            sidebar.setPreferredSize(new Dimension(260, 0));

            JLabel title = new JLabel("Vector Norms");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(15));

            // Explanation
            JTextArea explanation = new JTextArea(
                "A norm measures the 'length'\n" +
                "of a vector. Different norms\n" +
                "define different shapes for\n" +
                "the unit circle (all points\n" +
                "with norm = 1).\n\n" +
                "Lp norm:\n" +
                "||v||p = (|x|^p + |y|^p)^(1/p)\n\n" +
                "p=1: Manhattan (diamond)\n" +
                "p=2: Euclidean (circle)\n" +
                "p->inf: Chebyshev (square)"
            );
            explanation.setEditable(false);
            explanation.setBackground(new Color(35, 35, 60));
            explanation.setForeground(new Color(200, 200, 220));
            explanation.setFont(new Font("Monospaced", Font.PLAIN, 12));
            explanation.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            explanation.setMaximumSize(new Dimension(230, 240));
            explanation.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(explanation);
            sidebar.add(Box.createVerticalStrut(15));

            // P-value slider
            JLabel pTitle = new JLabel("p value (Lp norm)");
            pTitle.setForeground(Color.WHITE);
            pTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
            pTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(pTitle);
            sidebar.add(Box.createVerticalStrut(5));

            pLabel = new JLabel("p = 2.0 (Euclidean)");
            pLabel.setForeground(new Color(100, 200, 255));
            pLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
            pLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(pLabel);
            sidebar.add(Box.createVerticalStrut(3));

            // Slider: 1 to 200 maps to p = 0.1 to 20.0
            pSlider = new JSlider(1, 200, 20);
            pSlider.setBackground(new Color(30, 30, 55));
            pSlider.setForeground(Color.WHITE);
            pSlider.setMaximumSize(new Dimension(230, 30));
            pSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            pSlider.addChangeListener(e -> {
                pValue = pSlider.getValue() / 10.0;
                updatePLabel();
                repaint();
            });
            sidebar.add(pSlider);
            sidebar.add(Box.createVerticalStrut(15));

            // Preset buttons
            JLabel presetLabel = new JLabel("Presets");
            presetLabel.setForeground(Color.WHITE);
            presetLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            presetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(presetLabel);
            sidebar.add(Box.createVerticalStrut(5));

            addNormPreset(sidebar, "L1 - Manhattan (p=1)", 10);
            addNormPreset(sidebar, "L1.5 (p=1.5)", 15);
            addNormPreset(sidebar, "L2 - Euclidean (p=2)", 20);
            addNormPreset(sidebar, "L3 (p=3)", 30);
            addNormPreset(sidebar, "L5 (p=5)", 50);
            addNormPreset(sidebar, "L10 (p=10)", 100);
            addNormPreset(sidebar, "L-inf - Chebyshev (p=20)", 200);

            sidebar.add(Box.createVerticalGlue());

            add(sidebar, BorderLayout.EAST);

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                repaint();
            });
            animTimer.start();
        }

        private void addNormPreset(JPanel parent, String name, int sliderVal) {
            JButton btn = new JButton(name);
            btn.setBackground(new Color(50, 50, 80));
            btn.setForeground(new Color(200, 200, 220));
            btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setMaximumSize(new Dimension(230, 24));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(e -> {
                pSlider.setValue(sliderVal);
                pValue = sliderVal / 10.0;
                updatePLabel();
                repaint();
            });
            parent.add(btn);
            parent.add(Box.createVerticalStrut(2));
        }

        private void updatePLabel() {
            String name;
            if (pValue <= 1.05) name = "Manhattan";
            else if (Math.abs(pValue - 2.0) < 0.05) name = "Euclidean";
            else if (pValue >= 19.0) name = "Chebyshev (inf)";
            else name = "Lp";
            pLabel.setText(String.format("p = %.1f (%s)", pValue, name));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            int cx = (w - 260) / 2;
            int cy = h / 2;
            double scale = Math.min(w - 260, h) * 0.3;

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawGlowString(g2, "Lp Norm Unit Circles", cx - 100, 30, ACCENT);

            // Grid
            g2.setColor(withAlpha(TEXT_COLOR, 20));
            for (int i = -10; i <= 10; i++) {
                int px = cx + (int)(i * scale / 3);
                g2.drawLine(px, 50, px, h - 20);
                int py = cy + (int)(i * scale / 3);
                g2.drawLine(20, py, w - 280, py);
            }

            // Axes
            g2.setColor(withAlpha(TEXT_COLOR, 80));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(20, cy, w - 280, cy);
            g2.drawLine(cx, 50, cx, h - 20);

            // Axis labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            for (int i = -3; i <= 3; i++) {
                if (i == 0) continue;
                g2.drawString(String.valueOf(i), cx + (int)(i * scale / 3) - 4, cy + 16);
                g2.drawString(String.valueOf(i), cx + 6, cy - (int)(i * scale / 3) + 4);
            }
            g2.setStroke(new BasicStroke(1));

            // Draw reference norms (L1, L2, L-inf) as faded lines
            // L1 (diamond)
            drawLpCircle(g2, cx, cy, scale / 3, 1.0, withAlpha(new Color(255, 100, 100), 60), 1.5f);
            // L2 (circle)
            drawLpCircle(g2, cx, cy, scale / 3, 2.0, withAlpha(new Color(100, 255, 100), 60), 1.5f);
            // L-inf (square)
            drawLpCircle(g2, cx, cy, scale / 3, 20.0, withAlpha(new Color(100, 100, 255), 60), 1.5f);

            // Draw current Lp norm (bright, with glow)
            float hue = (float)((pValue - 1.0) / 19.0);
            Color currentColor = Color.getHSBColor(hue, 0.9f, 1.0f);
            drawLpCircle(g2, cx, cy, scale / 3, pValue, withAlpha(currentColor, 40), 8f);
            drawLpCircle(g2, cx, cy, scale / 3, pValue, currentColor, 3f);

            // Animated point on the current unit circle
            double t = animFrame * 0.02;
            double angle = t % (2 * Math.PI);
            double px = Math.cos(angle);
            double py = Math.sin(angle);
            // Normalize to Lp unit circle
            double lpNorm = Math.pow(Math.pow(Math.abs(px), pValue) + Math.pow(Math.abs(py), pValue), 1.0 / pValue);
            if (lpNorm > 1e-9) {
                px /= lpNorm;
                py /= lpNorm;
            }
            int dotX = cx + (int)(px * scale / 3);
            int dotY = cy - (int)(py * scale / 3);
            drawGlowCircle(g2, dotX, dotY, 6, currentColor);

            // Draw vector from origin to point
            g2.setColor(withAlpha(currentColor, 150));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(cx, cy, dotX, dotY);
            g2.setStroke(new BasicStroke(1));

            // Labels
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(withAlpha(currentColor, 230));
            g2.drawString(String.format("Point: (%.2f, %.2f)", px, py), dotX + 10, dotY - 10);
            g2.drawString(String.format("||v||p = 1.00"), dotX + 10, dotY + 5);

            // Legend
            int lx = 30, ly = 60;
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));

            g2.setColor(new Color(255, 100, 100, 180));
            g2.fillRect(lx, ly, 12, 12);
            g2.drawString("L1 (Manhattan) - diamond", lx + 18, ly + 11);
            ly += 20;

            g2.setColor(new Color(100, 255, 100, 180));
            g2.fillRect(lx, ly, 12, 12);
            g2.drawString("L2 (Euclidean) - circle", lx + 18, ly + 11);
            ly += 20;

            g2.setColor(new Color(100, 100, 255, 180));
            g2.fillRect(lx, ly, 12, 12);
            g2.drawString("L-inf (Chebyshev) - square", lx + 18, ly + 11);
            ly += 20;

            g2.setColor(currentColor);
            g2.fillRect(lx, ly, 12, 12);
            g2.drawString(String.format("Current: p = %.1f", pValue), lx + 18, ly + 11);

            // Bottom info
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(TEXT_COLOR);
            g2.drawString("The unit circle changes shape depending on which norm you use.", 30, h - 40);
            g2.drawString("As p grows, the diamond becomes a circle, then a square.", 30, h - 20);
        }

        /**
         * Draws a unit circle for the given Lp norm.
         *
         * @param g2    the graphics context
         * @param cx    center x
         * @param cy    center y
         * @param scale pixels per unit
         * @param p     the p-value of the Lp norm
         * @param color the line color
         * @param stroke the line thickness
         */
        private void drawLpCircle(Graphics2D g2, int cx, int cy, double scale, double p, Color color, float stroke) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke));
            GeneralPath path = new GeneralPath();
            int steps = 500;
            boolean started = false;
            for (int i = 0; i <= steps; i++) {
                double angle = 2 * Math.PI * i / steps;
                double rawX = Math.cos(angle);
                double rawY = Math.sin(angle);
                double norm = Math.pow(Math.pow(Math.abs(rawX), p) + Math.pow(Math.abs(rawY), p), 1.0 / p);
                if (norm < 1e-9) continue;
                double x = rawX / norm;
                double y = rawY / norm;
                float px = (float)(cx + x * scale);
                float py = (float)(cy - y * scale);
                if (!started) { path.moveTo(px, py); started = true; }
                else path.lineTo(px, py);
            }
            path.closePath();
            g2.draw(path);
            g2.setStroke(new BasicStroke(1));
        }
    }

    // =====================================================================
    // TAB 9: Photon Emission
    // =====================================================================

    /**
     * Panel visualizing photon emission from atomic energy level transitions.
     * Shows an atom with energy levels, electron transitions, electromagnetic
     * wave emission, and the relationship E = h * f.
     */
    static class PhotonPanel extends JPanel {
        private int animFrame = 0;
        private int selectedLevel = 3;
        private int targetLevel = 1;
        private boolean emitting = false;
        private int emitFrame = 0;
        private double photonX = 0;
        private javax.swing.Timer animTimer;
        private JSlider fromSlider, toSlider;
        private JLabel infoLabel;
        private boolean adjusting = false;

        /** Energy levels in eV (simplified hydrogen-like) */
        private static final double[] ENERGY_LEVELS = {
            -13.6, -3.4, -1.51, -0.85, -0.54, -0.38
        };

        /**
         * Constructs the Photon emission visualization panel.
         */
        public PhotonPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            // --- Right sidebar ---
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(30, 30, 55));
            sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            sidebar.setPreferredSize(new Dimension(280, 0));

            JLabel title = new JLabel("Photon Emission");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(10));

            JTextArea explanation = new JTextArea(
                "When an electron drops from a\n" +
                "higher to a lower energy level,\n" +
                "it emits a photon.\n\n" +
                "The photon's energy equals the\n" +
                "difference between the levels:\n\n" +
                "E = E_high - E_low = h * f\n\n" +
                "Higher energy = shorter wavelength\n" +
                "= bluer light.\n" +
                "Lower energy = longer wavelength\n" +
                "= redder light."
            );
            explanation.setEditable(false);
            explanation.setBackground(new Color(35, 35, 60));
            explanation.setForeground(new Color(200, 200, 220));
            explanation.setFont(new Font("SansSerif", Font.PLAIN, 12));
            explanation.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            explanation.setMaximumSize(new Dimension(250, 220));
            explanation.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(explanation);
            sidebar.add(Box.createVerticalStrut(15));

            // From level
            JLabel fromLabel = new JLabel("From level (n):");
            fromLabel.setForeground(Color.WHITE);
            fromLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            fromLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(fromLabel);
            sidebar.add(Box.createVerticalStrut(3));

            fromSlider = new JSlider(2, 6, 3);
            fromSlider.setBackground(new Color(30, 30, 55));
            fromSlider.setForeground(Color.WHITE);
            fromSlider.setMajorTickSpacing(1);
            fromSlider.setPaintTicks(true);
            fromSlider.setPaintLabels(true);
            fromSlider.setSnapToTicks(true);
            fromSlider.setMaximumSize(new Dimension(250, 45));
            fromSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            fromSlider.addChangeListener(e -> {
                int val = fromSlider.getValue();
                if (val <= targetLevel) val = targetLevel + 1;
                if (val > 6) val = 6;
                selectedLevel = val;
                updateInfo();
                repaint();
            });
            sidebar.add(fromSlider);
            sidebar.add(Box.createVerticalStrut(10));

            // To level
            JLabel toLabel = new JLabel("To level (n):");
            toLabel.setForeground(Color.WHITE);
            toLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
            toLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(toLabel);
            sidebar.add(Box.createVerticalStrut(3));

            toSlider = new JSlider(1, 5, 1);
            toSlider.setBackground(new Color(30, 30, 55));
            toSlider.setForeground(Color.WHITE);
            toSlider.setMajorTickSpacing(1);
            toSlider.setPaintTicks(true);
            toSlider.setPaintLabels(true);
            toSlider.setSnapToTicks(true);
            toSlider.setMaximumSize(new Dimension(250, 45));
            toSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            toSlider.addChangeListener(e -> {
                int val = toSlider.getValue();
                if (val >= selectedLevel) val = selectedLevel - 1;
                if (val < 1) val = 1;
                targetLevel = val;
                updateInfo();
                repaint();
            });
            sidebar.add(toSlider);
            sidebar.add(Box.createVerticalStrut(15));

            // Emit button
            JButton emitBtn = createDarkButton("Emit Photon!");
            emitBtn.setBackground(new Color(220, 50, 50));
            emitBtn.setMaximumSize(new Dimension(250, 35));
            emitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            emitBtn.addActionListener(e -> {
                emitting = true;
                emitFrame = 0;
                photonX = 0;
            });
            sidebar.add(emitBtn);
            sidebar.add(Box.createVerticalStrut(15));

            // Info label
            infoLabel = new JLabel();
            infoLabel.setForeground(new Color(150, 255, 150));
            infoLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
            infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(infoLabel);

            sidebar.add(Box.createVerticalGlue());
            add(sidebar, BorderLayout.EAST);

            updateInfo();

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                if (emitting) {
                    emitFrame++;
                    photonX += 4;
                    if (emitFrame > 300) {
                        emitting = false;
                    }
                }
                repaint();
            });
            animTimer.start();
        }

        /**
         * Updates the info label with current energy, wavelength, and frequency.
         */
        private void updateInfo() {
            double eHigh = ENERGY_LEVELS[selectedLevel - 1];
            double eLow = ENERGY_LEVELS[targetLevel - 1];
            double deltaE = eHigh - eLow;
            double deltaEJoules = deltaE * 1.602e-19;
            double h = 6.626e-34;
            double c = 3e8;
            double freq = Math.abs(deltaEJoules) / h;
            double wavelength = c / freq * 1e9;

            infoLabel.setText(String.format(
                "<html>" +
                "E_high = %.2f eV (n=%d)<br>" +
                "E_low = %.2f eV (n=%d)<br>" +
                "Delta E = %.2f eV<br><br>" +
                "Wavelength = %.1f nm<br>" +
                "Frequency = %.2e Hz<br><br>" +
                "E = h * f" +
                "</html>",
                eHigh, selectedLevel, eLow, targetLevel, deltaE, wavelength, freq
            ));
        }

        /**
         * Converts a photon wavelength in nm to a visible color.
         *
         * @param wavelength the wavelength in nanometers
         * @return the approximate visible color
         */
        private Color wavelengthToColor(double wavelength) {
            double r = 0, g = 0, b = 0;
            if (wavelength >= 380 && wavelength < 440) {
                r = -(wavelength - 440) / (440 - 380);
                b = 1.0;
            } else if (wavelength >= 440 && wavelength < 490) {
                g = (wavelength - 440) / (490 - 440);
                b = 1.0;
            } else if (wavelength >= 490 && wavelength < 510) {
                g = 1.0;
                b = -(wavelength - 510) / (510 - 490);
            } else if (wavelength >= 510 && wavelength < 580) {
                r = (wavelength - 510) / (580 - 510);
                g = 1.0;
            } else if (wavelength >= 580 && wavelength < 645) {
                r = 1.0;
                g = -(wavelength - 645) / (645 - 580);
            } else if (wavelength >= 645 && wavelength <= 780) {
                r = 1.0;
            } else if (wavelength < 380) {
                r = 0.6; b = 1.0;
            } else {
                r = 0.8;
            }
            return new Color(
                Math.max(0, Math.min(255, (int)(r * 255))),
                Math.max(0, Math.min(255, (int)(g * 255))),
                Math.max(0, Math.min(255, (int)(b * 255)))
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth() - 280;
            int h = getHeight();

            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            drawGlowString(g2, "Photon Emission from Atomic Energy Levels", 20, 30, ACCENT);

            // --- Left: Atom with energy levels ---
            int atomX = 180;
            int atomY = h / 2 - 30;
            int maxRadius = 160;

            // Draw nucleus
            drawGlowCircle(g2, atomX, atomY, 12, new Color(255, 150, 50));
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.setColor(Color.WHITE);
            g2.drawString("+", atomX - 4, atomY + 4);

            // Draw energy levels as orbits
            for (int n = 1; n <= 6; n++) {
                int radius = 25 + n * 22;
                float hue = (n - 1) / 6.0f;
                Color levelColor = Color.getHSBColor(hue, 0.6f, 0.8f);

                boolean isFrom = (n == selectedLevel);
                boolean isTo = (n == targetLevel);

                if (isFrom || isTo) {
                    g2.setColor(withAlpha(levelColor, 80));
                    g2.setStroke(new BasicStroke(3));
                } else {
                    g2.setColor(withAlpha(levelColor, 40));
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawOval(atomX - radius, atomY - radius, radius * 2, radius * 2);

                // Level label - placed at top of each orbit to avoid overlap
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.setColor(withAlpha(levelColor, 200));
                g2.drawString(String.format("n=%d", n),
                    atomX - 8, atomY - radius - 4);

                // Draw electron on selected level
                if (isFrom && !emitting) {
                    double eAngle = animFrame * 0.03;
                    int ex = atomX + (int)(radius * Math.cos(eAngle));
                    int ey = atomY + (int)(radius * Math.sin(eAngle));
                    drawGlowCircle(g2, ex, ey, 6, new Color(100, 150, 255));
                }

                // Draw electron on target level after emission
                if (isTo && emitting && emitFrame > 20) {
                    double eAngle = animFrame * 0.04;
                    int ex = atomX + (int)(radius * Math.cos(eAngle));
                    int ey = atomY + (int)(radius * Math.sin(eAngle));
                    drawGlowCircle(g2, ex, ey, 6, new Color(100, 150, 255));
                }
            }
            g2.setStroke(new BasicStroke(1));

            // Transition arrow
            if (!emitting) {
                int fromR = 25 + selectedLevel * 22;
                int toR = 25 + targetLevel * 22;
                g2.setColor(new Color(255, 255, 100, 150));
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 3}, 0));
                g2.drawLine(atomX + fromR, atomY, atomX + toR, atomY);
                // Arrowhead
                g2.fillPolygon(
                    new int[]{atomX + toR + 2, atomX + toR + 10, atomX + toR + 10},
                    new int[]{atomY, atomY - 5, atomY + 5}, 3);
                g2.setStroke(new BasicStroke(1));
            }

            // --- Right: Electromagnetic wave / Photon ---
            double eHigh = ENERGY_LEVELS[selectedLevel - 1];
            double eLow = ENERGY_LEVELS[targetLevel - 1];
            double deltaE = eHigh - eLow;
            double deltaEJ = deltaE * 1.602e-19;
            double freq = Math.abs(deltaEJ) / 6.626e-34;
            double wavelengthNm = 3e8 / freq * 1e9;
            Color photonColor = wavelengthToColor(wavelengthNm);

            int waveStartX = atomX + maxRadius + 60;
            int waveY = atomY;
            int waveLen = w - waveStartX - 30;

            if (emitting && emitFrame > 10) {
                // Wave frequency scales with energy
                double waveFreq = 0.05 + deltaE * 0.02;
                double amplitude = 50;

                // Photon wave packet
                double packetCenter = photonX;
                double packetWidth = 100;

                // E-field (vertical)
                g2.setStroke(new BasicStroke(2.5f));
                GeneralPath ePath = new GeneralPath();
                boolean started = false;
                for (int px = 0; px < waveLen; px++) {
                    double dist = px - packetCenter;
                    double envelope = Math.exp(-(dist * dist) / (packetWidth * packetWidth));
                    double yOff = amplitude * envelope * Math.sin(px * waveFreq);
                    float drawX = waveStartX + px;
                    float drawY = (float)(waveY - yOff);
                    if (!started) { ePath.moveTo(drawX, drawY); started = true; }
                    else ePath.lineTo(drawX, drawY);
                }
                // Glow
                g2.setColor(withAlpha(photonColor, 30));
                g2.setStroke(new BasicStroke(8));
                g2.draw(ePath);
                // Main line
                g2.setColor(photonColor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.draw(ePath);

                // B-field (horizontal, phase shifted)
                GeneralPath bPath = new GeneralPath();
                started = false;
                g2.setColor(withAlpha(photonColor, 100));
                for (int px = 0; px < waveLen; px++) {
                    double dist = px - packetCenter;
                    double envelope = Math.exp(-(dist * dist) / (packetWidth * packetWidth));
                    double xOff = amplitude * 0.6 * envelope * Math.cos(px * waveFreq);
                    float drawX = (float)(waveStartX + px);
                    float drawY = (float)(waveY + xOff * 0.3);
                    if (!started) { bPath.moveTo(drawX, drawY); started = true; }
                    else bPath.lineTo(drawX, drawY);
                }
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(bPath);

                // Photon particle (bright dot at center of packet)
                int dotX = waveStartX + (int)packetCenter;
                if (dotX > waveStartX && dotX < waveStartX + waveLen) {
                    drawGlowCircle(g2, dotX, waveY, 8, photonColor);
                }

                // Labels
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.setColor(photonColor);
                g2.drawString("E-field", waveStartX, waveY - 60);
                g2.setColor(withAlpha(photonColor, 150));
                g2.drawString("B-field", waveStartX, waveY + 40);
            }

            // Propagation axis
            g2.setColor(withAlpha(TEXT_COLOR, 40));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(waveStartX, waveY, waveStartX + waveLen, waveY);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(withAlpha(TEXT_COLOR, 100));
            g2.drawString("direction of propagation ->", waveStartX + waveLen / 2 - 80, waveY + 80);

            // --- Bottom: Energy level diagram ---
            int diagY = h - 160;
            int diagH = 120;
            int diagX = 30;
            int diagW = w - 60;

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            drawGlowString(g2, "Energy Level Diagram", diagX, diagY - 10, ACCENT);

            for (int n = 1; n <= 6; n++) {
                // Use linear spacing by level number to avoid overlap
                int ly = diagY + diagH - (n - 1) * (diagH / 5);

                float hue = (n - 1) / 6.0f;
                Color c = Color.getHSBColor(hue, 0.7f, 0.9f);
                boolean isFrom = (n == selectedLevel);
                boolean isTo = (n == targetLevel);

                g2.setColor(isFrom || isTo ? c : withAlpha(c, 100));
                g2.setStroke(new BasicStroke(isFrom || isTo ? 3 : 1.5f));
                int lineX = diagX + 80;
                int lineW = 150;
                g2.drawLine(lineX, ly, lineX + lineW, ly);

                g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
                g2.setColor(c);
                g2.drawString(String.format("n=%d  %.2f eV", n, ENERGY_LEVELS[n - 1]), lineX + lineW + 10, ly + 4);
            }
            g2.setStroke(new BasicStroke(1));

            // Wavelength and color bar
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.setColor(photonColor);
            String wlStr = String.format("Wavelength: %.0f nm", wavelengthNm);
            g2.drawString(wlStr, diagX + 350, diagY + 20);

            // Color swatch
            g2.setColor(photonColor);
            g2.fillRoundRect(diagX + 350, diagY + 30, 100, 20, 5, 5);
            g2.setColor(withAlpha(photonColor, 40));
            g2.fillRoundRect(diagX + 345, diagY + 25, 110, 30, 8, 8);

            // Visible / invisible indicator
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.setColor(TEXT_COLOR);
            String visStr;
            if (wavelengthNm < 380) visStr = "(Ultraviolet - invisible)";
            else if (wavelengthNm > 780) visStr = "(Infrared - invisible)";
            else visStr = "(Visible light)";
            g2.drawString(visStr, diagX + 350, diagY + 70);

            // Spectrum bar at very bottom
            int specY = h - 18;
            for (int px = 0; px < diagW; px++) {
                double wl = 380 + (px / (double)diagW) * 400;
                Color sc = wavelengthToColor(wl);
                g2.setColor(sc);
                g2.drawLine(diagX + px, specY, diagX + px, specY + 12);
            }
            // Marker for current wavelength
            if (wavelengthNm >= 380 && wavelengthNm <= 780) {
                int markerX = diagX + (int)((wavelengthNm - 380) / 400 * diagW);
                g2.setColor(Color.WHITE);
                g2.fillPolygon(
                    new int[]{markerX - 4, markerX + 4, markerX},
                    new int[]{specY - 2, specY - 2, specY + 3}, 3);
            }
        }
    }

    // =====================================================================
    // TAB: Spacetime (Minkowski diagram, Lorentz transformation)
    // =====================================================================

    /**
     * Panel visualizing special relativity: Minkowski spacetime diagram,
     * Lorentz boost as matrix transformation, time dilation, length contraction,
     * and light cones. Shows the underlying math (hyperbolic functions, matrices).
     */
    static class SpacetimePanel extends JPanel {
        private double velocity = 0.0; // v/c, range -0.99 to 0.99
        private int animFrame = 0;
        private javax.swing.Timer animTimer;

        /**
         * Constructs the Spacetime panel.
         */
        public SpacetimePanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(25, 25, 45));
            sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            sidebar.setPreferredSize(new Dimension(270, 0));

            JLabel title = new JLabel("Spacetime");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(8));

            JTextArea info = new JTextArea(
                "Minkowski Spacetime:\n\n" +
                "The Lorentz boost is a\n" +
                "matrix transformation:\n\n" +
                "| ct' |   | g  -gb | | ct |\n" +
                "| x'  | = | -gb  g | | x  |\n\n" +
                "where g = 1/sqrt(1-b^2)\n" +
                "      b = v/c\n\n" +
                "This uses cosh and sinh:\n" +
                "  g = cosh(rapidity)\n" +
                "  gb = sinh(rapidity)\n\n" +
                "cosh/sinh relate to e^x:\n" +
                "  cosh(r) = (e^r+e^-r)/2\n" +
                "  sinh(r) = (e^r-e^-r)/2\n\n" +
                "Light cone: x = +/- ct\n" +
                "(45 degree lines)"
            );
            info.setEditable(false);
            info.setBackground(new Color(35, 35, 60));
            info.setForeground(new Color(200, 200, 220));
            info.setFont(new Font("Monospaced", Font.PLAIN, 10));
            info.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            info.setMaximumSize(new Dimension(250, 320));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(info);
            sidebar.add(Box.createVerticalStrut(12));

            JLabel vLabel = new JLabel("Velocity v/c");
            vLabel.setForeground(Color.WHITE);
            vLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            vLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(vLabel);
            sidebar.add(Box.createVerticalStrut(3));

            JSlider vSlider = new JSlider(-99, 99, 0);
            vSlider.setBackground(new Color(25, 25, 45));
            vSlider.setForeground(Color.WHITE);
            vSlider.setMaximumSize(new Dimension(250, 30));
            vSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            vSlider.addChangeListener(e -> { velocity = vSlider.getValue() / 100.0; repaint(); });
            sidebar.add(vSlider);

            sidebar.add(Box.createVerticalGlue());
            add(sidebar, BorderLayout.EAST);

            animTimer = new javax.swing.Timer(16, e -> { animFrame++; repaint(); });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth() - 270;
            int h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            double beta = velocity;
            double gamma = 1.0 / Math.sqrt(1.0 - beta * beta);
            double rapidity = 0.5 * Math.log((1 + Math.abs(beta)) / (1 - Math.abs(beta)));
            if (beta < 0) rapidity = -rapidity;

            // --- Left: Minkowski Diagram ---
            int cx = w / 3;
            int cy = h / 2;
            double scale = Math.min(w / 3.0, h / 2.0) * 0.7;

            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            drawGlowString(g2, "Minkowski Diagram", 20, 28, ACCENT);

            // Grid
            g2.setColor(withAlpha(TEXT_COLOR, 15));
            for (int i = -5; i <= 5; i++) {
                g2.drawLine(cx + (int)(i * scale / 3), 50, cx + (int)(i * scale / 3), h - 10);
                g2.drawLine(20, cy - (int)(i * scale / 3), cx * 2, cy - (int)(i * scale / 3));
            }

            // Rest frame axes
            g2.setColor(withAlpha(TEXT_COLOR, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(20, cy, cx * 2, cy); // x axis
            g2.drawLine(cx, 50, cx, h - 10); // ct axis
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString("x", cx * 2 - 15, cy - 8);
            g2.drawString("ct", cx + 8, 65);

            // Light cone (45 degree lines)
            g2.setColor(new Color(255, 255, 100, 80));
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{6, 4}, 0));
            int lcLen = (int)(scale * 1.5);
            g2.drawLine(cx - lcLen, cy + lcLen, cx + lcLen, cy - lcLen); // future right
            g2.drawLine(cx + lcLen, cy + lcLen, cx - lcLen, cy - lcLen); // future left

            // Light cone fill
            g2.setColor(new Color(255, 255, 100, 10));
            int[] xPts = {cx, cx + lcLen, cx, cx - lcLen};
            int[] yPts = {cy, cy - lcLen, cy - 2 * lcLen, cy - lcLen};
            // Future cone
            g2.fillPolygon(new int[]{cx, cx + lcLen, cx - lcLen}, new int[]{cy, cy - lcLen, cy - lcLen}, 3);
            // Past cone
            g2.fillPolygon(new int[]{cx, cx + lcLen, cx - lcLen}, new int[]{cy, cy + lcLen, cy + lcLen}, 3);

            g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
            g2.setColor(new Color(255, 255, 100, 150));
            g2.drawString("Future", cx - 18, cy - lcLen + 15);
            g2.drawString("Past", cx - 12, cy + lcLen - 8);

            // Boosted axes (ct' and x')
            if (Math.abs(beta) > 0.01) {
                g2.setStroke(new BasicStroke(2.5f));
                // ct' axis tilts toward light cone
                double ctAngle = Math.atan(beta); // angle from vertical
                int axLen = (int)(scale * 1.3);
                // ct' axis
                g2.setColor(new Color(100, 200, 255, 180));
                int ct1x = cx + (int)(axLen * Math.sin(ctAngle));
                int ct1y = cy - (int)(axLen * Math.cos(ctAngle));
                int ct2x = cx - (int)(axLen * Math.sin(ctAngle));
                int ct2y = cy + (int)(axLen * Math.cos(ctAngle));
                g2.drawLine(ct2x, ct2y, ct1x, ct1y);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("ct'", ct1x + 5, ct1y + 5);

                // x' axis
                g2.setColor(new Color(255, 100, 100, 180));
                int x1x = cx + (int)(axLen * Math.cos(ctAngle));
                int x1y = cy - (int)(axLen * Math.sin(ctAngle));
                int x2x = cx - (int)(axLen * Math.cos(ctAngle));
                int x2y = cy + (int)(axLen * Math.sin(ctAngle));
                g2.drawLine(x2x, x2y, x1x, x1y);
                g2.drawString("x'", x1x + 5, x1y - 5);
            }
            g2.setStroke(new BasicStroke(1));

            // Worldline of moving observer
            g2.setColor(new Color(100, 255, 100, 200));
            g2.setStroke(new BasicStroke(3));
            int wlLen = (int)(scale * 1.2);
            int wlx1 = cx - (int)(beta * wlLen);
            int wly1 = cy + wlLen;
            int wlx2 = cx + (int)(beta * wlLen);
            int wly2 = cy - wlLen;
            g2.drawLine(wlx1, wly1, wlx2, wly2);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
            g2.drawString("worldline", wlx2 + 5, wly2 + 15);
            g2.setStroke(new BasicStroke(1));

            // --- Right: Math formulas and values ---
            int rx = w * 2 / 3 - 20;
            int ry = 50;
            int rw = w / 3 + 10;

            // Lorentz matrix
            g2.setColor(colorWithAlpha(25, 25, 50, 220));
            g2.fillRoundRect(rx, ry, rw, 180, 8, 8);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "Lorentz Boost Matrix", rx + 10, ry + 22, ACCENT);

            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.setColor(new Color(100, 200, 255));
            g2.drawString(String.format("| %6.3f  %7.3f |", gamma, -gamma * beta), rx + 15, ry + 50);
            g2.drawString(String.format("| %6.3f  %7.3f |", -gamma * beta, gamma), rx + 15, ry + 70);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.setColor(new Color(255, 200, 100));
            g2.drawString(String.format("v/c = %.2f", beta), rx + 15, ry + 100);
            g2.drawString(String.format("gamma = %.4f", gamma), rx + 15, ry + 118);
            g2.drawString(String.format("rapidity = %.4f", rapidity), rx + 15, ry + 136);

            g2.setColor(new Color(200, 200, 220));
            g2.drawString(String.format("cosh(r) = %.4f", Math.cosh(rapidity)), rx + 15, ry + 158);
            g2.drawString(String.format("sinh(r) = %.4f", Math.sinh(rapidity)), rx + 15, ry + 176);

            // Time dilation and length contraction
            int dy = ry + 200;
            g2.setColor(colorWithAlpha(25, 25, 50, 220));
            g2.fillRoundRect(rx, dy, rw, 160, 8, 8);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "Relativistic Effects", rx + 10, dy + 22, ACCENT);

            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.setColor(new Color(100, 255, 100));
            g2.drawString("Time dilation:", rx + 15, dy + 48);
            g2.setColor(TEXT_COLOR);
            g2.drawString(String.format("  dt' = g*dt = %.3f * dt", gamma), rx + 15, dy + 66);
            g2.drawString("  1 sec -> " + String.format("%.3f sec", gamma), rx + 15, dy + 84);

            g2.setColor(new Color(255, 100, 100));
            g2.drawString("Length contraction:", rx + 15, dy + 108);
            g2.setColor(TEXT_COLOR);
            g2.drawString(String.format("  L' = L/g = L * %.3f", 1.0 / gamma), rx + 15, dy + 126);
            g2.drawString("  1 m -> " + String.format("%.3f m", 1.0 / gamma), rx + 15, dy + 144);

            // Hyperbolic connection to e^x
            int ey = dy + 180;
            g2.setColor(colorWithAlpha(25, 25, 50, 220));
            g2.fillRoundRect(rx, ey, rw, 120, 8, 8);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "Connection to e^x", rx + 10, ey + 22, ACCENT);

            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.setColor(new Color(200, 200, 220));
            g2.drawString("cosh(r) = (e^r + e^-r) / 2", rx + 15, ey + 48);
            g2.drawString("sinh(r) = (e^r - e^-r) / 2", rx + 15, ey + 66);
            g2.drawString(String.format("e^r  = %.4f", Math.exp(rapidity)), rx + 15, ey + 90);
            g2.drawString(String.format("e^-r = %.4f", Math.exp(-rapidity)), rx + 15, ey + 108);

            // Bottom info
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            g2.drawString("The Lorentz boost is a hyperbolic rotation - just like e^x connects to cosh and sinh.", 20, h - 15);
        }
    }

    // =====================================================================
    // TAB: Redshift (Doppler effect, wavelength shift)
    // =====================================================================

    /**
     * Panel visualizing relativistic Doppler effect / redshift.
     * Shows how velocity affects photon wavelength and color,
     * with the underlying math (Doppler formula, z parameter).
     */
    static class RedshiftPanel extends JPanel {
        private double velocity = 0.0; // v/c
        private double emitWavelength = 500; // nm (green)
        private int animFrame = 0;
        private javax.swing.Timer animTimer;

        /**
         * Constructs the Redshift panel.
         */
        public RedshiftPanel() {
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(new Color(25, 25, 45));
            sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            sidebar.setPreferredSize(new Dimension(260, 0));

            JLabel title = new JLabel("Redshift");
            title.setForeground(ACCENT);
            title.setFont(new Font("SansSerif", Font.BOLD, 16));
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(title);
            sidebar.add(Box.createVerticalStrut(8));

            JTextArea info = new JTextArea(
                "Relativistic Doppler effect:\n\n" +
                "When a light source moves\n" +
                "away, its wavelength gets\n" +
                "stretched (redshift).\n" +
                "Moving toward: blueshift.\n\n" +
                "Formula:\n" +
                "l_obs = l_emit *\n" +
                "  sqrt((1+v/c) / (1-v/c))\n\n" +
                "Redshift parameter:\n" +
                "z = (l_obs - l_emit) / l_emit\n" +
                "z = sqrt((1+b)/(1-b)) - 1\n\n" +
                "v > 0: moving away (red)\n" +
                "v < 0: moving toward (blue)"
            );
            info.setEditable(false);
            info.setBackground(new Color(35, 35, 60));
            info.setForeground(new Color(200, 200, 220));
            info.setFont(new Font("Monospaced", Font.PLAIN, 10));
            info.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            info.setMaximumSize(new Dimension(240, 280));
            info.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(info);
            sidebar.add(Box.createVerticalStrut(12));

            // Velocity slider
            JLabel vLabel = new JLabel("Velocity v/c");
            vLabel.setForeground(Color.WHITE);
            vLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            vLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(vLabel);
            JSlider vSlider = new JSlider(-95, 95, 0);
            vSlider.setBackground(new Color(25, 25, 45));
            vSlider.setForeground(Color.WHITE);
            vSlider.setMaximumSize(new Dimension(240, 30));
            vSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            vSlider.addChangeListener(e -> { velocity = vSlider.getValue() / 100.0; repaint(); });
            sidebar.add(vSlider);
            sidebar.add(Box.createVerticalStrut(10));

            // Emitted wavelength slider
            JLabel wLabel = new JLabel("Emitted wavelength (nm)");
            wLabel.setForeground(Color.WHITE);
            wLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            wLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(wLabel);
            JSlider wSlider = new JSlider(380, 700, 500);
            wSlider.setBackground(new Color(25, 25, 45));
            wSlider.setForeground(Color.WHITE);
            wSlider.setMaximumSize(new Dimension(240, 30));
            wSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
            wSlider.addChangeListener(e -> { emitWavelength = wSlider.getValue(); repaint(); });
            sidebar.add(wSlider);

            sidebar.add(Box.createVerticalGlue());
            add(sidebar, BorderLayout.EAST);

            animTimer = new javax.swing.Timer(16, e -> { animFrame++; repaint(); });
            animTimer.start();
        }

        /**
         * Converts wavelength in nm to approximate RGB color.
         *
         * @param wl wavelength in nanometers
         * @return the visible color
         */
        private Color wlToColor(double wl) {
            double r = 0, g = 0, b = 0;
            if (wl >= 380 && wl < 440) { r = -(wl - 440) / 60.0; b = 1; }
            else if (wl >= 440 && wl < 490) { g = (wl - 440) / 50.0; b = 1; }
            else if (wl >= 490 && wl < 510) { g = 1; b = -(wl - 510) / 20.0; }
            else if (wl >= 510 && wl < 580) { r = (wl - 510) / 70.0; g = 1; }
            else if (wl >= 580 && wl < 645) { r = 1; g = -(wl - 645) / 65.0; }
            else if (wl >= 645 && wl <= 780) { r = 1; }
            else if (wl < 380) { r = 0.4; b = 1; }
            else { r = 0.7; }
            return new Color(clampAlpha((int)(r * 255)), clampAlpha((int)(g * 255)), clampAlpha((int)(b * 255)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth() - 260;
            int h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            double beta = velocity;
            double dopplerFactor = Math.sqrt((1 + beta) / (1 - beta));
            double obsWavelength = emitWavelength * dopplerFactor;
            double z = dopplerFactor - 1;

            Color emitColor = wlToColor(emitWavelength);
            Color obsColor = wlToColor(obsWavelength);

            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            drawGlowString(g2, "Relativistic Doppler Effect / Redshift", 20, 28, ACCENT);

            // --- Top: Source and Observer with wave ---
            int waveY = 120;
            int srcX = 80;
            int obsX = w - 80;
            int waveW = obsX - srcX;

            // Source
            g2.setColor(emitColor);
            drawGlowCircle(g2, srcX, waveY, 20, emitColor);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(emitColor);
            g2.drawString("Source", srcX - 20, waveY - 30);

            // Velocity arrow
            if (Math.abs(beta) > 0.01) {
                int arrowLen = (int)(beta * 60);
                g2.setColor(new Color(255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(srcX, waveY + 30, srcX + arrowLen, waveY + 30);
                g2.fillPolygon(
                    new int[]{srcX + arrowLen + (arrowLen > 0 ? 8 : -8), srcX + arrowLen, srcX + arrowLen},
                    new int[]{waveY + 30, waveY + 25, waveY + 35}, 3);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2.drawString(String.format("v = %.2fc", beta), srcX + arrowLen + (arrowLen > 0 ? 12 : -50), waveY + 34);
                g2.setStroke(new BasicStroke(1));
            }

            // Observer
            g2.setColor(obsColor);
            drawGlowCircle(g2, obsX, waveY, 15, obsColor);
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.drawString("Observer", obsX - 25, waveY - 25);

            // Wave between source and observer
            double emitFreq = 0.03 + (700 - emitWavelength) * 0.0001;
            double obsFreq = emitFreq / dopplerFactor;
            g2.setStroke(new BasicStroke(2.5f));
            // Emitted wave (left half)
            GeneralPath emitWave = new GeneralPath();
            for (int px = 0; px < waveW / 2; px++) {
                double x = px + animFrame * 2;
                double y = 25 * Math.sin(x * emitFreq);
                float fx = srcX + 25 + px;
                float fy = (float)(waveY + y);
                if (px == 0) emitWave.moveTo(fx, fy);
                else emitWave.lineTo(fx, fy);
            }
            g2.setColor(withAlpha(emitColor, 30));
            g2.setStroke(new BasicStroke(8));
            g2.draw(emitWave);
            g2.setColor(emitColor);
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(emitWave);

            // Observed wave (right half)
            GeneralPath obsWave = new GeneralPath();
            for (int px = 0; px < waveW / 2; px++) {
                double x = px + animFrame * 2;
                double y = 25 * Math.sin(x * obsFreq);
                float fx = srcX + 25 + waveW / 2 + px;
                float fy = (float)(waveY + y);
                if (px == 0) obsWave.moveTo(fx, fy);
                else obsWave.lineTo(fx, fy);
            }
            g2.setColor(withAlpha(obsColor, 30));
            g2.setStroke(new BasicStroke(8));
            g2.draw(obsWave);
            g2.setColor(obsColor);
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(obsWave);
            g2.setStroke(new BasicStroke(1));

            // --- Middle: Color comparison and values ---
            int midY = 200;
            // Emitted color bar
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(TEXT_COLOR);
            g2.drawString("Emitted:", 30, midY + 15);
            g2.setColor(emitColor);
            g2.fillRoundRect(120, midY, 200, 25, 5, 5);
            g2.setColor(Color.WHITE);
            g2.drawString(String.format("%.0f nm", emitWavelength), 140, midY + 17);

            // Observed color bar
            g2.setColor(TEXT_COLOR);
            g2.drawString("Observed:", 30, midY + 55);
            g2.setColor(obsColor);
            g2.fillRoundRect(120, midY + 40, 200, 25, 5, 5);
            g2.setColor(Color.WHITE);
            g2.drawString(String.format("%.0f nm", obsWavelength), 140, midY + 57);

            // Math values
            int mx = 360;
            g2.setFont(new Font("Monospaced", Font.BOLD, 13));
            g2.setColor(new Color(255, 200, 100));
            g2.drawString(String.format("Doppler factor = %.4f", dopplerFactor), mx, midY + 15);
            g2.setColor(z > 0 ? new Color(255, 100, 100) : new Color(100, 100, 255));
            g2.drawString(String.format("z = %.4f  (%s)", z, z > 0 ? "redshift" : z < 0 ? "blueshift" : "none"), mx, midY + 38);
            g2.setColor(TEXT_COLOR);
            g2.drawString(String.format("v/c = %.3f", beta), mx, midY + 58);

            // --- Formula display ---
            int fy = midY + 90;
            g2.setColor(colorWithAlpha(25, 25, 50, 220));
            g2.fillRoundRect(30, fy, w - 60, 80, 8, 8);
            g2.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2.setColor(new Color(100, 200, 255));
            g2.drawString("Relativistic Doppler Formula:", 50, fy + 22);
            g2.setColor(new Color(255, 200, 100));
            g2.drawString("l_obs = l_emit * sqrt( (1 + v/c) / (1 - v/c) )", 50, fy + 48);
            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g2.setColor(TEXT_COLOR);
            g2.drawString(String.format("      = %.0f   * sqrt( (1 + %.2f) / (1 - %.2f) ) = %.1f nm",
                emitWavelength, beta, beta, obsWavelength), 50, fy + 68);

            // --- Bottom: Full spectrum with shift visualization ---
            int specY = fy + 100;
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            drawGlowString(g2, "Visible Spectrum", 30, specY, ACCENT);

            int specX = 30, specW = w - 60, specH = 40;
            specY += 10;
            // Draw spectrum
            for (int px = 0; px < specW; px++) {
                double wl = 380 + (px / (double) specW) * 400;
                g2.setColor(wlToColor(wl));
                g2.drawLine(specX + px, specY, specX + px, specY + specH);
            }

            // Emitted marker
            if (emitWavelength >= 380 && emitWavelength <= 780) {
                int emitPx = specX + (int)((emitWavelength - 380) / 400 * specW);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(emitPx, specY - 5, emitPx, specY + specH + 5);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("emit", emitPx - 12, specY - 8);
            }

            // Observed marker
            if (obsWavelength >= 380 && obsWavelength <= 780) {
                int obsPx = specX + (int)((obsWavelength - 380) / 400 * specW);
                g2.setColor(obsColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(obsPx, specY - 5, obsPx, specY + specH + 5);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2.drawString("obs", obsPx - 10, specY + specH + 18);
            }

            // Arrow between markers
            if (Math.abs(z) > 0.01) {
                int fromPx = specX + (int)(Math.max(0, Math.min(1, (emitWavelength - 380) / 400.0)) * specW);
                int toPx = specX + (int)(Math.max(0, Math.min(1, (obsWavelength - 380) / 400.0)) * specW);
                g2.setColor(new Color(255, 255, 255, 150));
                g2.setStroke(new BasicStroke(1.5f));
                int arrowY = specY + specH + 25;
                g2.drawLine(fromPx, arrowY, toPx, arrowY);
                int dir = toPx > fromPx ? -1 : 1;
                g2.fillPolygon(
                    new int[]{toPx, toPx + dir * 8, toPx + dir * 8},
                    new int[]{arrowY, arrowY - 4, arrowY + 4}, 3);
                g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
                g2.drawString(z > 0 ? "redshift" : "blueshift", (fromPx + toPx) / 2 - 20, arrowY - 5);
            }
            g2.setStroke(new BasicStroke(1));

            // Bottom note
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            g2.drawString("Redshift is how we measure the expansion speed of the universe.", 30, h - 15);
        }
    }

    // =====================================================================
    // TAB: Overview
    // =====================================================================

    /**
     * Panel showing a visual connection graph linking all mathematical concepts,
     * with clickable nodes that navigate to the corresponding tabs.
     */
    static class OverviewPanel extends JPanel {
        private JTabbedPane parentTabs;
        private int animFrame = 0;
        private javax.swing.Timer animTimer;
        private int hoveredNode = -1;

        /** Node definitions: name, x-fraction, y-fraction, connected tab index. */
        private static final String[] NODE_NAMES = {
            "Fibonacci", "Matrix", "Eigenvalues", "Matrix Exp",
            "Taylor", "e^x Props", "Euler", "DiffEq/Laplace",
            "Spiral", "Dimensions", "Circle/Pi", "Norms",
            "Photon", "Spacetime", "Redshift"
        };
        private static final double[][] NODE_POS = {
            {0.12, 0.15}, {0.32, 0.15}, {0.52, 0.15}, {0.72, 0.15},
            {0.12, 0.40}, {0.32, 0.40}, {0.52, 0.40}, {0.72, 0.40},
            {0.12, 0.65}, {0.32, 0.65}, {0.52, 0.65}, {0.72, 0.65},
            {0.92, 0.15}, {0.92, 0.40}, {0.92, 0.65}
        };
        private static final int[] NODE_TABS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        private static final int[][] EDGES = {
            {0, 1}, {1, 2}, {1, 3}, {3, 4}, {4, 5}, {5, 6},
            {6, 7}, {0, 8}, {2, 5}, {0, 9}, {6, 10}, {4, 3},
            {5, 7}, {10, 11}, {7, 12}, {2, 0}, {8, 10}, {6, 12},
            {12, 14}, {13, 14}, {1, 13}, {5, 13}, {12, 13}
        };
        private static final String[] EDGE_LABELS = {
            "generates", "decomposition", "e^A series", "Taylor approx",
            "properties", "complex plane", "y'=ky solution",
            "golden spiral", "phi eigenvalue", "0D-5D",
            "e^(ix) on circle", "matrix series", "decay/growth",
            "Lp unit shapes", "E=hf photon", "phi ratio",
            "Fib squares", "wave equation",
            "Doppler shift", "wavelength change", "Lorentz matrix",
            "cosh/sinh from e^x", "E=hf energy"
        };

        /**
         * Constructs the Overview panel.
         * @param tabs the parent tabbed pane for navigation
         */
        public OverviewPanel(JTabbedPane tabs) {
            this.parentTabs = tabs;
            setLayout(new BorderLayout());
            setBackground(PANEL_BG);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int node = hitTest(e.getX(), e.getY());
                    if (node >= 0) {
                        parentTabs.setSelectedIndex(NODE_TABS[node]);
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int newHover = hitTest(e.getX(), e.getY());
                    if (newHover != hoveredNode) {
                        hoveredNode = newHover;
                        setCursor(newHover >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                : Cursor.getDefaultCursor());
                        repaint();
                    }
                }
            });

            animTimer = new javax.swing.Timer(16, e -> {
                animFrame++;
                repaint();
            });
            animTimer.start();
        }

        /**
         * Tests if a point hits any node.
         * @param mx mouse x
         * @param my mouse y
         * @return node index or -1
         */
        private int hitTest(int mx, int my) {
            int w = getWidth(), h = getHeight();
            for (int i = 0; i < NODE_NAMES.length; i++) {
                int nx = (int) (NODE_POS[i][0] * w);
                int ny = (int) (NODE_POS[i][1] * h);
                double dist = Math.sqrt((mx - nx) * (mx - nx) + (my - ny) * (my - ny));
                if (dist < 35) return i;
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            setupRendering(g2);

            int w = getWidth(), h = getHeight();
            g2.setColor(PANEL_BG);
            g2.fillRect(0, 0, w, h);

            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            drawGlowString(g2, "Concept Map - How Everything Connects", w / 2 - 220, 35, ACCENT);

            // Compute node positions
            int[] nx = new int[NODE_NAMES.length];
            int[] ny = new int[NODE_NAMES.length];
            for (int i = 0; i < NODE_NAMES.length; i++) {
                nx[i] = (int) (NODE_POS[i][0] * w);
                ny[i] = (int) (NODE_POS[i][1] * h);
            }

            // Draw edges
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int e = 0; e < EDGES.length; e++) {
                int a = EDGES[e][0], b = EDGES[e][1];
                float hue = e / (float) EDGES.length;
                Color edgeColor = Color.getHSBColor(hue, 0.5f, 0.8f);

                // Animated pulse
                int pulseAlpha = clampAlpha(40 + (int) (20 * Math.sin(animFrame * 0.03 + e)));
                g2.setColor(withAlpha(edgeColor, pulseAlpha));
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(nx[a], ny[a], nx[b], ny[b]);

                g2.setColor(withAlpha(edgeColor, pulseAlpha + 40));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(nx[a], ny[a], nx[b], ny[b]);

                // Edge label
                int lx = (nx[a] + nx[b]) / 2;
                int ly = (ny[a] + ny[b]) / 2;
                g2.setColor(withAlpha(edgeColor, 120));
                if (e < EDGE_LABELS.length) {
                    g2.drawString(EDGE_LABELS[e], lx - 30, ly - 5);
                }
            }
            g2.setStroke(new BasicStroke(1));

            // Draw nodes
            for (int i = 0; i < NODE_NAMES.length; i++) {
                float hue = i / (float) NODE_NAMES.length;
                Color nodeColor = Color.getHSBColor(hue, 0.7f, 1.0f);

                int nodeR = 28;
                boolean hovered = (i == hoveredNode);
                if (hovered) nodeR = 34;

                // Glow
                int glowPulse = clampAlpha(40 + (int) (20 * Math.sin(animFrame * 0.05 + i)));
                for (int gl = 4; gl >= 0; gl--) {
                    int alpha = clampAlpha(glowPulse - gl * 8);
                    g2.setColor(withAlpha(nodeColor, alpha));
                    g2.fillOval(nx[i] - nodeR - gl * 4, ny[i] - nodeR - gl * 4,
                            (nodeR + gl * 4) * 2, (nodeR + gl * 4) * 2);
                }

                // Node fill
                g2.setColor(withAlpha(nodeColor, 180));
                g2.fillOval(nx[i] - nodeR, ny[i] - nodeR, nodeR * 2, nodeR * 2);

                // Border
                g2.setColor(hovered ? Color.WHITE : nodeColor);
                g2.setStroke(new BasicStroke(hovered ? 3 : 2));
                g2.drawOval(nx[i] - nodeR, ny[i] - nodeR, nodeR * 2, nodeR * 2);

                // Label
                g2.setFont(new Font("SansSerif", Font.BOLD, hovered ? 13 : 11));
                FontMetrics fm = g2.getFontMetrics();
                int sw = fm.stringWidth(NODE_NAMES[i]);
                g2.setColor(Color.WHITE);
                g2.drawString(NODE_NAMES[i], nx[i] - sw / 2, ny[i] + 4);
            }
            g2.setStroke(new BasicStroke(1));

            // Instruction text
            g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
            g2.setColor(withAlpha(TEXT_COLOR, 150));
            g2.drawString("Click any node to jump to the related tab.", w / 2 - 160, h - 20);
        }
    }

    /**
     * Main entry point. Launches the MathExplorer application.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            MathExplorer app = new MathExplorer();
            app.setVisible(true);
        });
    }
}
