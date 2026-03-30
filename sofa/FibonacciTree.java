import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * Animated Fibonacci Fractal Tree - branches split following Fibonacci ratios.
 * Grows from a seed, with colorful leaves and wind animation.
 */
public class FibonacciTree extends JFrame {

    private double growProgress = 0;
    private double time = 0;
    private final java.util.List<Leaf> leaves = new ArrayList<>();
    private final Random rng = new Random(42);
    private boolean grown = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FibonacciTree().setVisible(true));
    }

    public FibonacciTree() {
        super("Fibonacci Tree");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 900);
        setLocationRelativeTo(null);

        TreePanel panel = new TreePanel();
        add(panel);

        javax.swing.Timer timer = new javax.swing.Timer(16, e -> {
            if (growProgress < 1.0) {
                growProgress += 0.003;
            } else if (!grown) {
                grown = true;
            }
            time += 0.02;
            panel.repaint();
        });
        timer.start();
    }

    class TreePanel extends JPanel {

        TreePanel() {
            setBackground(new Color(5, 10, 25));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Gradient sky
            for (int y = 0; y < h; y++) {
                float ratio = (float) y / h;
                int r = (int) (5 + ratio * 15);
                int gr = (int) (10 + ratio * 20);
                int b = (int) (25 + ratio * 30);
                g2.setColor(new Color(r, gr, b));
                g2.drawLine(0, y, w, y);
            }

            // Ground
            g2.setColor(new Color(20, 35, 20));
            g2.fillRect(0, h - 80, w, 80);
            for (int i = 0; i < 50; i++) {
                float hue = 0.25f + rng.nextFloat() * 0.1f;
                g2.setColor(Color.getHSBColor(hue, 0.6f, 0.2f + rng.nextFloat() * 0.1f));
                int gx = (int) (rng.nextDouble() * w);
                int gy = h - 80 + (int) (rng.nextDouble() * 80);
                g2.fillOval(gx, gy, 30 + rng.nextInt(40), 10 + rng.nextInt(15));
            }

            // Stars
            rng.setSeed(123);
            for (int i = 0; i < 100; i++) {
                double twinkle = 0.5 + 0.5 * Math.abs(Math.sin(time * 2 + i));
                g2.setColor(new Color(255, 255, 255, Math.max(0, Math.min(255, (int) (twinkle * 200)))));
                int sx = (int) (rng.nextDouble() * w);
                int sy = (int) (rng.nextDouble() * (h - 100));
                g2.fillOval(sx, sy, 2, 2);
            }

            // Draw the tree
            leaves.clear();
            rng.setSeed(42);
            drawBranch(g2, w / 2.0, h - 80, -Math.PI / 2, 150 * growProgress, 12, 0, 0);

            // Draw leaves
            for (Leaf leaf : leaves) {
                double wind = Math.sin(time * 1.5 + leaf.x * 0.01 + leaf.y * 0.01) * 3;
                double sway = Math.sin(time * 2 + leaf.phase) * 2;
                double lx = leaf.x + wind;
                double ly = leaf.y + sway;

                // Leaf glow
                float hue = (float) ((leaf.hue + time * 0.01) % 1.0);
                Color c = Color.getHSBColor(hue, 0.9f, 1.0f);
                int glowSize = (int) (leaf.size * 4);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 30));
                g2.fillOval((int) lx - glowSize / 2, (int) ly - glowSize / 2, glowSize, glowSize);

                // Leaf body
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 220));
                g2.fillOval((int) (lx - leaf.size / 2), (int) (ly - leaf.size / 2),
                    (int) leaf.size, (int) leaf.size);

                // Bright center
                g2.setColor(new Color(255, 255, 255, 80));
                int cs = (int) (leaf.size * 0.3);
                g2.fillOval((int) lx - cs / 2, (int) ly - cs / 2, cs, cs);
            }

            // Fibonacci numbers on trunk
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            int a = 1, b = 1;
            for (int i = 0; i < 10; i++) {
                if (i * 0.1 > growProgress) break;
                double t = i * 0.1;
                int fx = w / 2 + 20;
                int fy = (int) (h - 80 - t * 150 * growProgress);
                float hue = (float) ((i * 0.1 + time * 0.05) % 1.0);
                g2.setColor(Color.getHSBColor(hue, 0.7f, 1.0f));
                g2.drawString(String.valueOf(a), fx, fy);
                int next = a + b;
                a = b;
                b = next;
            }

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("Fibonacci Tree", 20, 35);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(200, 200, 200, 150));
            String info = String.format("Growth: %.0f%% | Branches split by golden ratio", growProgress * 100);
            g2.drawString(info, 20, 55);
        }

        void drawBranch(Graphics2D g2, double x, double y, double angle,
                        double length, double thickness, int depth, int fibIndex) {
            if (depth > 12 || length < 4) return;

            double wind = Math.sin(time + depth * 0.5) * 0.02 * depth;
            angle += wind;

            double x2 = x + length * Math.cos(angle);
            double y2 = y + length * Math.sin(angle);

            // Branch color - brown to green gradient
            float hue = 0.08f + depth * 0.015f;
            float sat = 0.5f + depth * 0.04f;
            float bri = 0.3f + depth * 0.03f;
            g2.setColor(Color.getHSBColor(hue, Math.min(sat, 1f), Math.min(bri, 0.7f)));
            g2.setStroke(new BasicStroke((float) thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Line2D.Double(x, y, x2, y2));

            // Fibonacci branching ratio
            double phi = (1 + Math.sqrt(5)) / 2;
            double ratio1 = 1.0 / phi;       // ~0.618
            double ratio2 = 1.0 / (phi * phi); // ~0.382

            double branchAngle1 = Math.PI / (3 + depth * 0.3);
            double branchAngle2 = Math.PI / (2.5 + depth * 0.2);

            // Main branch (longer, Fibonacci ratio)
            drawBranch(g2, x2, y2, angle - branchAngle1,
                length * ratio1 * (0.9 + rng.nextDouble() * 0.2),
                thickness * 0.7, depth + 1, fibIndex + 1);

            // Secondary branch (shorter, Fibonacci ratio)
            drawBranch(g2, x2, y2, angle + branchAngle2,
                length * ratio2 * (0.9 + rng.nextDouble() * 0.3) * 1.4,
                thickness * 0.55, depth + 1, fibIndex + 1);

            // Third branch at deeper levels
            if (depth > 3 && depth % 2 == 0) {
                drawBranch(g2, x2, y2, angle + branchAngle1 * 0.5,
                    length * ratio2 * 0.8,
                    thickness * 0.4, depth + 2, fibIndex + 1);
            }

            // Add leaves at branch tips
            if (length < 20 && depth > 5) {
                float leafHue = (float) ((depth * 0.07 + fibIndex * 0.05) % 1.0);
                double leafSize = 5 + rng.nextDouble() * 8;
                leaves.add(new Leaf(x2, y2, leafHue, leafSize, rng.nextDouble() * Math.PI * 2));
            }
        }
    }

    static class Leaf {
        double x, y, hue, size, phase;
        Leaf(double x, double y, double hue, double size, double phase) {
            this.x = x; this.y = y; this.hue = hue; this.size = size; this.phase = phase;
        }
    }
}
