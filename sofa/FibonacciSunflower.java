import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Animated Fibonacci Sunflower - dots arranged by the golden angle (137.5 degrees).
 * Shows the natural Fibonacci spirals found in sunflowers.
 */
public class FibonacciSunflower extends JFrame {

    private static final double GOLDEN_ANGLE = Math.toRadians(137.508);
    private int maxDots = 0;
    private final int totalDots = 2000;
    private double time = 0;
    private double scaleFactor = 4.0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FibonacciSunflower().setVisible(true));
    }

    public FibonacciSunflower() {
        super("Fibonacci Sunflower");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 900);
        setLocationRelativeTo(null);

        SunflowerPanel panel = new SunflowerPanel();
        add(panel);

        // Growth animation
        Timer growTimer = new Timer(5, e -> {
            if (maxDots < totalDots) {
                maxDots += 2;
            }
            time += 0.02;
            panel.repaint();
        });
        growTimer.start();

        // Mouse wheel zoom
        panel.addMouseWheelListener(e -> {
            scaleFactor -= e.getWheelRotation() * 0.3;
            scaleFactor = Math.max(1.0, Math.min(12.0, scaleFactor));
            panel.repaint();
        });
    }

    class SunflowerPanel extends JPanel {

        SunflowerPanel() {
            setBackground(new Color(10, 10, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cx = w / 2;
            int cy = h / 2;

            // Background glow
            for (int r = 300; r > 0; r -= 5) {
                float alpha = 0.02f;
                g2.setColor(new Color(40, 20, 60, (int) (alpha * 255)));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            }

            // Draw dots
            for (int i = 0; i < maxDots; i++) {
                double angle = i * GOLDEN_ANGLE;
                double radius = scaleFactor * Math.sqrt(i);

                double x = cx + radius * Math.cos(angle);
                double y = cy + radius * Math.sin(angle);

                // Pulsing effect
                double pulse = 1.0 + 0.3 * Math.sin(time * 2.0 + i * 0.05);

                // Size based on distance from center
                double baseSize = 2.0 + (radius / 80.0);
                double size = baseSize * pulse;

                // Rainbow color based on index
                float hue = (float) ((i * 0.003 + time * 0.05) % 1.0);
                float saturation = 0.8f + 0.2f * (float) Math.sin(time + i * 0.01);
                float brightness = 0.7f + 0.3f * (float) Math.sin(time * 1.5 + i * 0.02);
                Color dotColor = Color.getHSBColor(hue, saturation, brightness);

                // Glow effect
                int glowSize = (int) (size * 3);
                g2.setColor(new Color(dotColor.getRed(), dotColor.getGreen(), dotColor.getBlue(), 30));
                g2.fillOval((int) (x - glowSize / 2), (int) (y - glowSize / 2), glowSize, glowSize);

                // Main dot
                g2.setColor(dotColor);
                g2.fillOval((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);

                // Bright center
                double coreSize = size * 0.4;
                g2.setColor(new Color(255, 255, 255, 120));
                g2.fillOval((int) (x - coreSize / 2), (int) (y - coreSize / 2), (int) coreSize, (int) coreSize);
            }

            // Draw Fibonacci spiral lines
            g2.setStroke(new BasicStroke(1.5f));
            int[] fibs = {1, 2, 3, 5, 8, 13, 21, 34, 55, 89};
            for (int f = 0; f < fibs.length; f++) {
                float hue = (float) (f * 0.1 + time * 0.03) % 1.0f;
                g2.setColor(new Color(Color.getHSBColor(hue, 0.5f, 1.0f).getRed(),
                    Color.getHSBColor(hue, 0.5f, 1.0f).getGreen(),
                    Color.getHSBColor(hue, 0.5f, 1.0f).getBlue(), 40));

                GeneralPath path = new GeneralPath();
                boolean started = false;
                for (int i = 0; i < maxDots; i += fibs[f]) {
                    double angle = i * GOLDEN_ANGLE;
                    double radius = scaleFactor * Math.sqrt(i);
                    float px = (float) (cx + radius * Math.cos(angle));
                    float py = (float) (cy + radius * Math.sin(angle));
                    if (!started) {
                        path.moveTo(px, py);
                        started = true;
                    } else {
                        path.lineTo(px, py);
                    }
                }
                g2.draw(path);
            }

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("Fibonacci Sunflower", 20, 35);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            g2.setColor(new Color(200, 200, 200, 150));
            g2.drawString("Golden Angle: 137.508 degrees | Seeds: " + maxDots + " | Scroll to zoom", 20, 55);
        }
    }
}
