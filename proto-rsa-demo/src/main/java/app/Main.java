package app;

import gui.RSADemoFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Swing falls back to the default look and feel if the system theme is unavailable.
        }

        SwingUtilities.invokeLater(() -> {
            RSADemoFrame frame = new RSADemoFrame();
            frame.setVisible(true);
        });
    }
}
