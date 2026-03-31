package visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;
import model.DemoSnapshot;
import model.MessageBlock;
import model.RSAKeyData;

public final class RSAVisualizationPanel extends JPanel {
    private DemoSnapshot snapshot;

    public RSAVisualizationPanel() {
        setPreferredSize(new Dimension(900, 760));
        setBackground(new Color(247, 249, 251));
    }

    public void setSnapshot(DemoSnapshot snapshot) {
        this.snapshot = snapshot;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g2);
        drawHeader(g2);
        drawCompactKeyFlow(g2);
        drawBlockFlow(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(252, 253, 255),
            0, getHeight(), new Color(228, 237, 246)
        );
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawHeader(Graphics2D g2) {
        g2.setColor(new Color(20, 35, 54));
        g2.setFont(getFont().deriveFont(Font.BOLD, 28f));
        g2.drawString("RSA auf einen Blick", 24, 40);

        g2.setColor(new Color(74, 86, 102));
        g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        g2.drawString("Kompakte Schrittfolge: Schluesselbildung oben, Nachrichtenbloecke unten.", 24, 64);
    }

    private void drawCompactKeyFlow(Graphics2D g2) {
        RSAKeyData keyData = snapshot == null ? null : snapshot.getKeyData();

        int x = 24;
        int y = 110;
        int width = 160;
        int height = 86;
        int gap = 22;

        drawBox(g2, x, y, width, height, new Color(255, 241, 208), "p und q",
            keyData == null ? "Primzahlen eingeben" : "p = " + keyData.getP() + ", q = " + keyData.getQ());
        drawArrow(g2, x + width, y + 43, x + width + gap, y + 43);

        x += width + gap;
        drawBox(g2, x, y, width, height, new Color(224, 243, 255), "n = p * q",
            keyData == null ? "-" : keyData.getN().toString());
        drawArrow(g2, x + width, y + 43, x + width + gap, y + 43);

        x += width + gap;
        drawBox(g2, x, y, width, height, new Color(230, 248, 225), "phi(n)",
            keyData == null ? "-" : keyData.getPhi().toString());
        drawArrow(g2, x + width, y + 43, x + width + gap, y + 43);

        x += width + gap;
        drawBox(g2, x, y, width, height, new Color(237, 231, 255), "e",
            keyData == null ? "-" : keyData.getE().toString());
        drawArrow(g2, x + width, y + 43, x + width + gap, y + 43);

        x += width + gap;
        drawBox(g2, x, y, width, height, new Color(255, 230, 235), "d",
            keyData == null ? "-" : keyData.getD().toString());

        g2.setColor(new Color(67, 79, 94));
        g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        g2.drawString("Warum phi(n)? Nur damit kann man e und d als inverse Exponenten modulo phi(n) koppeln.", 24, 232);
    }

    private void drawBlockFlow(Graphics2D g2) {
        RSAKeyData keyData = snapshot == null ? null : snapshot.getKeyData();
        List<MessageBlock> blocks = snapshot == null ? List.of() : snapshot.getBlocks();
        String decrypted = snapshot == null ? "" : snapshot.getDecryptedText();
        String plaintext = snapshot == null ? "" : snapshot.getPlaintext();

        int y = 300;
        drawBox(g2, 24, y, 190, 98, new Color(255, 249, 221), "Text",
            plaintext == null || plaintext.isEmpty() ? "Noch keine Nachricht" : preview(plaintext));
        drawArrow(g2, 214, y + 49, 298, y + 49);
        drawBox(g2, 298, y, 220, 98, new Color(221, 241, 255), "UTF-8-Bloecke",
            keyData == null || blocks.isEmpty() ? "Bytewerte sichtbar nach Verschluesselung" : summarizePlainBlocks(blocks, keyData));
        drawArrow(g2, 518, y + 49, 602, y + 49);
        drawBox(g2, 602, y, 250, 98, new Color(231, 255, 236), "Cipher-Bloecke",
            blocks.isEmpty() ? "Noch keine Cipher-Bloecke" : summarizeCipherBlocks(blocks));

        drawArrow(g2, 727, y + 98, 727, y + 176);
        drawBox(g2, 602, y + 196, 250, 98, new Color(255, 236, 222), "Entschluesselt",
            decrypted == null || decrypted.isEmpty() ? "Noch keine Rueckwandlung" : preview(decrypted));

        g2.setColor(new Color(67, 79, 94));
        g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        g2.drawString("Jeder Block m muss kleiner als n sein. Diese Demo nutzt einzelne UTF-8-Bytes als Bloecke.", 24, 470);

        if (keyData != null) {
            g2.drawString("Aktuelles n = " + keyData.getN() + " bestimmt die maximal zulaessige Blockgroesse.", 24, 492);
        }
    }

    private String summarizePlainBlocks(List<MessageBlock> blocks, RSAKeyData keyData) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Math.min(blocks.size(), 4); i++) {
            MessageBlock block = blocks.get(i);
            if (i > 0) {
                builder.append(" | ");
            }
            builder.append(block.getPlainBlock()).append('<').append(keyData.getN());
        }
        if (blocks.size() > 4) {
            builder.append(" ...");
        }
        return builder.toString();
    }

    private String summarizeCipherBlocks(List<MessageBlock> blocks) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < Math.min(blocks.size(), 4); i++) {
            if (i > 0) {
                builder.append(" | ");
            }
            builder.append(blocks.get(i).getCipherBlock());
        }
        if (blocks.size() > 4) {
            builder.append(" ...");
        }
        return builder.toString();
    }

    private void drawBox(Graphics2D g2, int x, int y, int width, int height, Color fill, String title, String value) {
        g2.setColor(fill);
        g2.fillRoundRect(x, y, width, height, 24, 24);
        g2.setColor(new Color(76, 89, 104));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, width, height, 24, 24);

        g2.setColor(new Color(23, 38, 56));
        g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
        g2.drawString(title, x + 14, y + 28);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
        drawWrappedText(g2, value, x + 14, y + 52, width - 28, 18);
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setColor(new Color(84, 99, 117));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrowSize = 10;
        int ax1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int ay1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        int ax2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int ay2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));
        g2.drawLine(x2, y2, ax1, ay1);
        g2.drawLine(x2, y2, ax2, ay2);
    }

    private void drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        if (text == null || text.isEmpty()) {
            return;
        }

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int currentY = y;

        for (String word : words) {
            String candidate = line.length() == 0 ? word : line + " " + word;
            if (g2.getFontMetrics().stringWidth(candidate) > maxWidth) {
                g2.drawString(line.toString(), x, currentY);
                line = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                line = new StringBuilder(candidate);
            }
        }

        if (line.length() > 0) {
            g2.drawString(line.toString(), x, currentY);
        }
    }

    private String preview(String text) {
        return text.length() > 36 ? text.substring(0, 33) + "..." : text;
    }
}
