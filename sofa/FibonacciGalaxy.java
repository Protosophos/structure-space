import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
 * Animated Golden Spiral Galaxy - a golden spiral with orbiting particles,
 * glowing stars, and nebula effects.
 */
public class FibonacciGalaxy extends JFrame {

    private double time = 0;
    private final java.util.List<Star> stars = new ArrayList<>();
    private final java.util.List<Particle> particles = new ArrayList<>();
    private final Random rng = new Random(42);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FibonacciGalaxy().setVisible(true));
    }

    public FibonacciGalaxy() {
        super("Fibonacci Galaxy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 900);
        setLocationRelativeTo(null);

        // Generate background stars
        for (int i = 0; i < 300; i++) {
            stars.add(new Star(rng.nextDouble(), rng.nextDouble(),
                rng.nextDouble() * 2 + 0.5, rng.nextDouble() * Math.PI * 2));
        }

        // Generate spiral particles
        double phi = (1 + Math.sqrt(5)) / 2; // golden ratio
        for (int i = 0; i < 1500; i++) {
            double t = i * 0.05;
            double spiralR = 8.0 * Math.pow(phi, t * 0.15);
            double angle = t * Math.PI * 0.5;
            double offsetR = (rng.nextGaussian() * 0.15) * spiralR;
            double offsetA = rng.nextGaussian() * 0.15;
            double r = spiralR + offsetR;
            double a = angle + offsetA;
            float hue = (float) ((t * 0.01 + rng.nextDouble() * 0.1) % 1.0);
            double size = 1.0 + rng.nextDouble() * 3.0;
            double speed = 0.2 + rng.nextDouble() * 0.8;
            int brightness = 150 + rng.nextInt(105);
            particles.add(new Particle(r, a, hue, size, speed, brightness));
        }

        GalaxyPanel panel = new GalaxyPanel();
        add(panel);

        javax.swing.Timer timer = new javax.swing.Timer(16, e -> {
            time += 0.01;
            panel.repaint();
        });
        timer.start();
    }

    class GalaxyPanel extends JPanel {

        GalaxyPanel() {
            setBackground(Color.BLACK);
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

            // Background gradient
            for (int r = 400; r > 0; r -= 4) {
                int alpha = (int) (15 * (1.0 - r / 400.0));
                float hue = (float) ((time * 0.02 + r * 0.001) % 1.0);
                Color c = Color.getHSBColor(hue, 0.6f, 0.3f);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            }

            // Background stars
            for (Star s : stars) {
                double twinkle = 0.5 + 0.5 * Math.sin(time * 3 + s.phase);
                int alpha = (int) (255 * twinkle);
                g2.setColor(new Color(255, 255, 255, alpha));
                int sx = (int) (s.x * w);
                int sy = (int) (s.y * h);
                int ss = (int) (s.size * twinkle);
                g2.fillOval(sx, sy, ss, ss);
            }

            // Golden spiral (the main spiral line)
            double phi = (1 + Math.sqrt(5)) / 2;
            g2.setStroke(new BasicStroke(2f));
            for (int arm = 0; arm < 2; arm++) {
                GeneralPath spiral = new GeneralPath();
                boolean started = false;
                for (int i = 0; i < 800; i++) {
                    double t = i * 0.02;
                    double r = 5.0 * Math.pow(phi, t * 0.35);
                    double angle = t * Math.PI * 0.5 + arm * Math.PI + time * 0.3;
                    float px = (float) (cx + r * Math.cos(angle));
                    float py = (float) (cy + r * Math.sin(angle));
                    if (r > Math.min(w, h) * 0.48) break;
                    if (!started) {
                        spiral.moveTo(px, py);
                        started = true;
                    } else {
                        spiral.lineTo(px, py);
                    }
                }
                float hue = (float) ((time * 0.05 + arm * 0.5) % 1.0);
                Color spiralColor = Color.getHSBColor(hue, 0.7f, 1.0f);
                g2.setColor(new Color(spiralColor.getRed(), spiralColor.getGreen(),
                    spiralColor.getBlue(), 80));
                g2.draw(spiral);
            }

            // Particles
            for (Particle p : particles) {
                double angle = p.baseAngle + time * p.speed * 0.3;
                double wobble = Math.sin(time * 2 + p.baseAngle) * 5;
                double r = p.baseRadius + wobble;
                if (r > Math.min(w, h) * 0.48) continue;

                double x = cx + r * Math.cos(angle);
                double y = cy + r * Math.sin(angle);

                float hue = (float) ((p.hue + time * 0.02) % 1.0);
                Color c = Color.getHSBColor(hue, 0.8f, 1.0f);

                // Glow
                int glowSize = (int) (p.size * 4);
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 25));
                g2.fillOval((int) x - glowSize / 2, (int) y - glowSize / 2, glowSize, glowSize);

                // Core
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), p.brightness));
                int s = (int) p.size;
                g2.fillOval((int) x - s / 2, (int) y - s / 2, s, s);
            }

            // Center glow
            for (int r = 60; r > 0; r -= 2) {
                float hue = (float) ((time * 0.1) % 1.0);
                Color c = Color.getHSBColor(hue, 0.3f, 1.0f);
                int alpha = (int) (200 * (1.0 - r / 60.0));
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            }

            // Fibonacci numbers along spiral
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            int a = 1, b = 1;
            for (int i = 0; i < 12; i++) {
                double t = a * 0.15;
                double r = 30 + a * 2.5;
                double angle = t * Math.PI * 2 + time * 0.3;
                if (r > Math.min(w, h) * 0.45) break;
                int fx = (int) (cx + r * Math.cos(angle));
                int fy = (int) (cy + r * Math.sin(angle));
                float hue = (float) ((i * 0.08 + time * 0.05) % 1.0);
                g2.setColor(Color.getHSBColor(hue, 0.5f, 1.0f));
                g2.drawString(String.valueOf(a), fx, fy);
                int next = a + b;
                a = b;
                b = next;
            }

            // Title
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.setColor(new Color(255, 255, 255, 200));
            g2.drawString("Fibonacci Galaxy", 20, 35);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(new Color(200, 200, 200, 150));
            g2.drawString("Golden Ratio Spiral with 1500 orbiting particles", 20, 55);
        }
    }

    static class Star {
        double x, y, size, phase;
        Star(double x, double y, double size, double phase) {
            this.x = x; this.y = y; this.size = size; this.phase = phase;
        }
    }

    static class Particle {
        double baseRadius, baseAngle, hue, size, speed;
        int brightness;
        Particle(double r, double a, float hue, double size, double speed, int brightness) {
            this.baseRadius = r; this.baseAngle = a; this.hue = hue;
            this.size = size; this.speed = speed; this.brightness = brightness;
        }
    }
}
