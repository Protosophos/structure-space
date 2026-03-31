package plotter;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.function.DoubleBinaryOperator;

public class Funktionsplotter extends JFrame {

    private final java.util.List<MathFunction> functions = new ArrayList<>();
    private double xMin = -10, xMax = 10;
    private double yMin = -10, yMax = 10;
    private double paramA = 1.0, paramB = 1.0, paramC = 0.0;
    private PlotPanel plotPanel;
    private JLabel aValueLabel, bValueLabel, cValueLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Funktionsplotter().setVisible(true));
    }

    public Funktionsplotter() {
        super("Function Plotter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
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
            cb.setFont(new Font("SansSerif", Font.PLAIN, 11));
            cb.setAlignmentX(Component.LEFT_ALIGNMENT);
            cb.addActionListener(e -> {
                mf.visible = cb.isSelected();
                plotPanel.repaint();
            });
            sidebar.add(cb);
            sidebar.add(Box.createVerticalStrut(2));
        }

        // --- Parameter sliders ---
        sidebar.add(Box.createVerticalStrut(15));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(240, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(10));

        JLabel paramTitle = new JLabel("Parameters");
        paramTitle.setForeground(Color.WHITE);
        paramTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        paramTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(paramTitle);
        sidebar.add(Box.createVerticalStrut(10));

        // Slider a: -5.0 to 5.0 (stored as int * 10)
        aValueLabel = new JLabel("a = 1.0");
        aValueLabel.setForeground(new Color(243, 156, 18));
        aValueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        aValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(aValueLabel);

        JSlider sliderA = new JSlider(-50, 50, 10);
        styleSlider(sliderA);
        sliderA.addChangeListener(e -> {
            paramA = sliderA.getValue() / 10.0;
            aValueLabel.setText("a = " + paramA);
            plotPanel.repaint();
        });
        sidebar.add(sliderA);
        sidebar.add(Box.createVerticalStrut(8));

        // Slider b: -5.0 to 5.0
        bValueLabel = new JLabel("b = 1.0");
        bValueLabel.setForeground(new Color(52, 152, 219));
        bValueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        bValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(bValueLabel);

        JSlider sliderB = new JSlider(-50, 50, 10);
        styleSlider(sliderB);
        sliderB.addChangeListener(e -> {
            paramB = sliderB.getValue() / 10.0;
            bValueLabel.setText("b = " + paramB);
            plotPanel.repaint();
        });
        sidebar.add(sliderB);
        sidebar.add(Box.createVerticalStrut(8));

        // Slider c: -10.0 to 10.0
        cValueLabel = new JLabel("c = 0.0");
        cValueLabel.setForeground(new Color(46, 204, 113));
        cValueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        cValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(cValueLabel);

        JSlider sliderC = new JSlider(-100, 100, 0);
        styleSlider(sliderC);
        sliderC.addChangeListener(e -> {
            paramC = sliderC.getValue() / 10.0;
            cValueLabel.setText("c = " + paramC);
            plotPanel.repaint();
        });
        sidebar.add(sliderC);

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setPreferredSize(new Dimension(280, 0));
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

    private void styleSlider(JSlider slider) {
        slider.setBackground(new Color(45, 45, 68));
        slider.setForeground(Color.WHITE);
        slider.setMaximumSize(new Dimension(240, 30));
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void initFunctions() {
        // Functions using parameters a, b, c
        functions.add(new MathFunction("f(x) = a*x + c",
            (x, p) -> p[0] * x + p[2],
            new Color(231, 76, 60), true));

        functions.add(new MathFunction("f(x) = a*x^2 + c",
            (x, p) -> p[0] * x * x + p[2],
            new Color(52, 152, 219), true));

        functions.add(new MathFunction("f(x) = a*x^3",
            (x, p) -> p[0] * x * x * x,
            new Color(46, 204, 113), false));

        functions.add(new MathFunction("f(x) = a * e^(b*x)",
            (x, p) -> p[0] * Math.exp(p[1] * x),
            new Color(243, 156, 18), false));

        functions.add(new MathFunction("f(x) = a * 2^(b*x)",
            (x, p) -> p[0] * Math.pow(2, p[1] * x),
            new Color(155, 89, 182), false));

        functions.add(new MathFunction("f(x) = a * ln(b*x)",
            (x, p) -> p[0] * Math.log(p[1] * x),
            new Color(26, 188, 156), false));

        functions.add(new MathFunction("f(x) = a * sqrt(b*x)",
            (x, p) -> p[0] * Math.sqrt(p[1] * x),
            new Color(230, 126, 34), false));

        functions.add(new MathFunction("f(x) = a * sin(b*x + c)",
            (x, p) -> p[0] * Math.sin(p[1] * x + p[2]),
            new Color(41, 128, 185), false));

        functions.add(new MathFunction("f(x) = a * cos(b*x + c)",
            (x, p) -> p[0] * Math.cos(p[1] * x + p[2]),
            new Color(39, 174, 96), false));

        functions.add(new MathFunction("f(x) = a * tan(b*x)",
            (x, p) -> p[0] * Math.tan(p[1] * x),
            new Color(142, 68, 173), false));

        functions.add(new MathFunction("f(x) = a * |b*x + c|",
            (x, p) -> p[0] * Math.abs(p[1] * x + p[2]),
            new Color(211, 84, 0), false));

        functions.add(new MathFunction("f(x) = a / (b*x)",
            (x, p) -> p[0] / (p[1] * x),
            new Color(22, 160, 133), false));
    }

    // ======================================================================
    // Plot panel
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

            // --- Grid ---
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new BasicStroke(1));

            for (int i = (int) Math.ceil(xMin); i <= (int) Math.floor(xMax); i++) {
                int px = margin + (int) ((i - xMin) / (xMax - xMin) * plotW);
                g2.drawLine(px, margin, px, margin + plotH);
            }
            for (int i = (int) Math.ceil(yMin); i <= (int) Math.floor(yMax); i++) {
                int py = margin + (int) ((yMax - i) / (yMax - yMin) * plotH);
                g2.drawLine(margin, py, margin + plotW, py);
            }

            // Axes through origin
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

            // Axis numbers
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
            double[] params = {paramA, paramB, paramC};
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
                        y = mf.func.apply(x, params);
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

            // Parameter display
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.setColor(new Color(200, 200, 200));
            String paramStr = String.format("a = %.1f    b = %.1f    c = %.1f", paramA, paramB, paramC);
            g2.drawString(paramStr, margin + plotW - 220, margin - 10);
        }
    }

    // ======================================================================
    // Function with parameters
    // ======================================================================
    @FunctionalInterface
    interface ParamFunction {
        double apply(double x, double[] params);
    }

    static class MathFunction {
        String name;
        ParamFunction func;
        Color color;
        boolean visible;

        MathFunction(String name, ParamFunction func, Color color, boolean visible) {
            this.name = name;
            this.func = func;
            this.color = color;
            this.visible = visible;
        }
    }
}
