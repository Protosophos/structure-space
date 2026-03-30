import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

public class Funktionsplotter extends JFrame {

    private final java.util.List<MathFunction> functions = new ArrayList<>();
    private double xMin = -10, xMax = 10;
    private double yMin = -10, yMax = 10;
    private PlotPanel plotPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Funktionsplotter().setVisible(true));
    }

    public Funktionsplotter() {
        super("Function Plotter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 30, 46));

        initFunctions();

        plotPanel = new PlotPanel();

        // --- Sidebar with checkboxes ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 45, 68));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Functions");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(10));

        for (MathFunction mf : functions) {
            JCheckBox cb = new JCheckBox(mf.name, mf.visible);
            cb.setForeground(mf.color);
            cb.setBackground(new Color(45, 45, 68));
            cb.setFocusPainted(false);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            cb.addActionListener(e -> {
                mf.visible = cb.isSelected();
                plotPanel.repaint();
            });
            sidebar.add(cb);
            sidebar.add(Box.createVerticalStrut(4));
        }

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setPreferredSize(new Dimension(260, 0));
        sidebarScroll.setBorder(null);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);

        // --- Zoom slider ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(30, 30, 46));
        JLabel zoomLabel = new JLabel("Zoom:");
        zoomLabel.setForeground(Color.WHITE);
        bottomPanel.add(zoomLabel);

        JSlider zoomSlider = new JSlider(1, 50, 10);
        zoomSlider.setBackground(new Color(30, 30, 46));
        zoomSlider.setForeground(Color.WHITE);
        zoomSlider.setMajorTickSpacing(10);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPreferredSize(new Dimension(500, 40));
        zoomSlider.addChangeListener(e -> {
            int val = zoomSlider.getValue();
            xMin = -val;
            xMax = val;
            yMin = -val;
            yMax = val;
            plotPanel.repaint();
        });
        bottomPanel.add(zoomSlider);

        // --- Layout ---
        setLayout(new BorderLayout());
        add(sidebarScroll, BorderLayout.WEST);
        add(plotPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initFunctions() {
        functions.add(new MathFunction("Linear: f(x) = 2x + 1", x -> 2 * x + 1, new Color(231, 76, 60), true));
        functions.add(new MathFunction("Quadratic: f(x) = x^2", x -> x * x, new Color(52, 152, 219), true));
        functions.add(new MathFunction("Cubic: f(x) = x^3", x -> x * x * x, new Color(46, 204, 113), true));
        functions.add(new MathFunction("Exponential: f(x) = e^x", Math::exp, new Color(243, 156, 18), false));
        functions.add(new MathFunction("Exponential: f(x) = 2^x", x -> Math.pow(2, x), new Color(155, 89, 182), false));
        functions.add(new MathFunction("Logarithmic: f(x) = ln(x)", Math::log, new Color(26, 188, 156), false));
        functions.add(new MathFunction("Square root: f(x) = sqrt(x)", Math::sqrt, new Color(230, 126, 34), false));
        functions.add(new MathFunction("Sine: f(x) = sin(x)", Math::sin, new Color(41, 128, 185), false));
        functions.add(new MathFunction("Cosine: f(x) = cos(x)", Math::cos, new Color(39, 174, 96), false));
        functions.add(new MathFunction("Tangent: f(x) = tan(x)", Math::tan, new Color(142, 68, 173), false));
        functions.add(new MathFunction("Absolute: f(x) = |x|", Math::abs, new Color(211, 84, 0), false));
        functions.add(new MathFunction("1/x (Hyperbola)", x -> 1.0 / x, new Color(22, 160, 133), false));
    }

    // ======================================================================
    // Plot panel - draws the coordinate system and functions
    // ======================================================================
    class PlotPanel extends JPanel {

        PlotPanel() {
            setBackground(new Color(30, 30, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margin = 40;
            int plotW = w - 2 * margin;
            int plotH = h - 2 * margin;

            // --- Grid and axes ---
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(1));

            // Vertical grid lines
            for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                g2.drawLine(px, margin, px, margin + plotH);
            }
            // Horizontal grid lines
            for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                g2.drawLine(margin, py, margin + plotW, py);
            }

            // X and Y axis through origin
            g2.setColor(new Color(150, 150, 150));
            g2.setStroke(new BasicStroke(1.5f));
            int originX = margin + (int) ((0 - xMin) / (xMax - xMin) * plotW);
            int originY = margin + (int) ((yMax - 0) / (yMax - yMin) * plotH);
            if (originX >= margin && originX <= margin + plotW) {
                g2.drawLine(originX, margin, originX, margin + plotH);
            }
            if (originY >= margin && originY <= margin + plotH) {
                g2.drawLine(margin, originY, margin + plotW, originY);
            }

            // Axis labels
            g2.setColor(new Color(200, 200, 200));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                if (i == 0) continue;
                int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                if (originY >= margin && originY <= margin + plotH) {
                    g2.drawString(String.valueOf(i), px - 5, originY + 15);
                }
            }
            for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                if (i == 0) continue;
                int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                if (originX >= margin && originX <= margin + plotW) {
                    g2.drawString(String.valueOf(i), originX + 5, py + 4);
                }
            }

            // --- Plot functions ---
            g2.setStroke(new BasicStroke(2.5f));
            int steps = plotW * 2;
            double dx = (xMax - xMin) / steps;

            for (MathFunction mf : functions) {
                if (!mf.visible) continue;
                g2.setColor(mf.color);

                GeneralPath path = new GeneralPath();
                boolean drawing = false;

                for (int s = 0; s <= steps; s++) {
                    double x = xMin + s * dx;
                    double y;
                    try {
                        y = mf.func.applyAsDouble(x);
                    } catch (Exception e) {
                        drawing = false;
                        continue;
                    }

                    if (Double.isNaN(y) || Double.isInfinite(y) || Math.abs(y) > 1e6) {
                        drawing = false;
                        continue;
                    }

                    float px = (float) (margin + (x - xMin) / (xMax - xMin) * plotW);
                    float py = (float) (margin + (yMax - y) / (yMax - yMin) * plotH);

                    if (!drawing) {
                        path.moveTo(px, py);
                        drawing = true;
                    } else {
                        path.lineTo(px, py);
                    }
                }
                // Clip to plot area
                Shape oldClip = g2.getClip();
                g2.setClip(margin, margin, plotW, plotH);
                g2.draw(path);
                g2.setClip(oldClip);
            }

            // Border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(margin, margin, plotW, plotH);

            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Function Plotter", margin, margin - 10);
        }
    }

    // ======================================================================
    // Data class for a mathematical function
    // ======================================================================
    static class MathFunction {
        String name;
        DoubleUnaryOperator func;
        Color color;
        boolean visible;

        MathFunction(String name, DoubleUnaryOperator func, Color color, boolean visible) {
            this.name = name;
            this.func = func;
            this.color = color;
            this.visible = visible;
        }
    }
}
