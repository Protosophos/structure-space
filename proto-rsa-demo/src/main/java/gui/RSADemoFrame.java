package gui;

import crypto.RSADemoService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import model.DemoSnapshot;
import model.MessageBlock;
import model.RSAKeyData;
import visualization.RSAVisualizationPanel;

public final class RSADemoFrame extends JFrame {
    private final JTextField pField = new JTextField("17", 8);
    private final JTextField qField = new JTextField("19", 8);
    private final JTextField messageField = new JTextField("HALLO RSA");

    private final JLabel nLabel = new JLabel("-");
    private final JLabel phiLabel = new JLabel("-");
    private final JLabel eLabel = new JLabel("-");
    private final JLabel dLabel = new JLabel("-");
    private final JLabel statusLabel = new JLabel("Berechne zuerst den Schluessel.");

    private final JTextArea keyExplanationArea = createReadOnlyArea(8, 32);
    private final JTextArea eulerArea = createReadOnlyArea(5, 32);
    private final JTextArea blockArea = createReadOnlyArea(10, 32);
    private final JTextArea cipherArea = createReadOnlyArea(8, 32);
    private final JTextArea decryptedArea = createReadOnlyArea(3, 32);

    private final RSAVisualizationPanel visualizationPanel = new RSAVisualizationPanel();
    private final RSADemoService service = new RSADemoService();

    private RSAKeyData keyData;
    private List<MessageBlock> blocks = List.of();
    private String decryptedText = "";

    public RSADemoFrame() {
        super("RSA Lernanwendung");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1420, 860);
        setMinimumSize(new Dimension(1160, 760));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        add(buildLeftPanel(), BorderLayout.WEST);

        JScrollPane diagramScroll = new JScrollPane(visualizationPanel);
        diagramScroll.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 12));
        add(diagramScroll, BorderLayout.CENTER);

        refreshView();
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.setPreferredSize(new Dimension(480, 820));

        JLabel title = new JLabel("RSA kompakt erklaert");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitle = new JLabel(
            "<html>Kompakte Lernansicht mit Schluesselwerten, Blockbildung und Visualisierung im selben Fenster.</html>"
        );

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(14));
        panel.add(buildInputPanel());
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildControlPanel());
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildValuePanel());
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildExplanationPanel());
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Eingaben"));

        panel.add(inputRow("Primzahl p", pField,
            "p ist eine geheime Primzahl. Zusammen mit q bestimmt p sowohl n als auch phi(n)."));
        panel.add(Box.createVerticalStrut(8));
        panel.add(inputRow("Primzahl q", qField,
            "q ist die zweite geheime Primzahl. Ohne p und q ist phi(n) nicht direkt greifbar."));
        panel.add(Box.createVerticalStrut(8));
        panel.add(inputRow("Nachricht", messageField,
            "Die Nachricht wird in dieser Demo in einzelne UTF-8-Bytes zerlegt."));

        return panel;
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Aktionen"));

        JButton keyButton = new JButton("Schluessel berechnen");
        JButton encryptButton = new JButton("Verschluesseln");
        JButton decryptButton = new JButton("Entschluesseln");
        JButton resetButton = new JButton("Zuruecksetzen");

        keyButton.addActionListener(this::generateKeys);
        encryptButton.addActionListener(this::encryptMessage);
        decryptButton.addActionListener(this::decryptMessage);
        resetButton.addActionListener(this::resetDemo);

        panel.add(keyButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(encryptButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(decryptButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(resetButton);

        return panel;
    }

    private JPanel buildValuePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Schluesselwerte"));

        panel.add(valueRow("n = p * q", nLabel,
            "n ist das gemeinsame Modul fuer Ver- und Entschluesselung."));
        panel.add(valueRow("phi(n)", phiLabel,
            "phi(n) = (p - 1)(q - 1), weil bei einer Primzahl p genau 1 bis p - 1 teilerfremd zu p sind."));
        panel.add(valueRow("e", eLabel,
            "e ist der oeffentliche Exponent und muss teilerfremd zu phi(n) sein."));
        panel.add(valueRow("d", dLabel,
            "d ist das modulare Inverse von e modulo phi(n). Ohne phi(n) ist d nicht direkt bestimmbar."));

        return panel;
    }

    private JPanel buildExplanationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Erklaerungen"));

        panel.add(wrapArea("Kompakte Schrittfolge", keyExplanationArea));
        panel.add(Box.createVerticalStrut(8));
        panel.add(wrapArea("Eulers Satz", eulerArea));
        panel.add(Box.createVerticalStrut(8));
        panel.add(wrapArea("Blockbildung", blockArea));
        panel.add(Box.createVerticalStrut(8));
        panel.add(wrapArea("Cipher-Bloecke", cipherArea));
        panel.add(Box.createVerticalStrut(8));
        panel.add(wrapArea("Entschluesselte Nachricht", decryptedArea));

        return panel;
    }

    private JPanel inputRow(String labelText, JTextField field, String infoText) {
        JPanel panel = new JPanel(new BorderLayout(8, 6));
        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        labelRow.add(new JLabel(labelText));
        labelRow.add(createInfoButton(infoText));
        panel.add(labelRow, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel valueRow(String labelText, JLabel valueLabel, String infoText) {
        JPanel row = new JPanel(new BorderLayout(8, 4));
        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        labelRow.add(new JLabel(labelText));
        labelRow.add(createInfoButton(infoText));
        row.add(labelRow, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        return row;
    }

    private JButton createInfoButton(String infoText) {
        JButton button = new JButton("i");
        button.setFocusable(false);
        button.setMargin(new Insets(1, 6, 1, 6));
        button.setToolTipText("<html><body style='width:260px'>" + infoText + "</body></html>");
        button.addActionListener(e -> JOptionPane.showMessageDialog(
            this,
            infoText,
            "Kurzinfo",
            JOptionPane.INFORMATION_MESSAGE
        ));
        return button;
    }

    private JPanel wrapArea(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title), BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void generateKeys(ActionEvent event) {
        try {
            BigInteger p = parsePrime(pField.getText(), "p");
            BigInteger q = parsePrime(qField.getText(), "q");
            if (p.equals(q)) {
                throw new IllegalArgumentException("p und q sollten verschieden sein.");
            }

            keyData = service.generateKeyData(p, q);
            blocks = List.of();
            decryptedText = "";
            statusLabel.setText("Schluessel berechnet. Jetzt kann die Nachricht blockweise verschluesselt werden.");
            refreshView();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void encryptMessage(ActionEvent event) {
        try {
            ensureKeyData();
            String message = messageField.getText();
            if (message.isEmpty()) {
                throw new IllegalArgumentException("Bitte eine Nachricht eingeben.");
            }

            blocks = service.encryptUtf8Blocks(message, keyData);
            decryptedText = "";
            statusLabel.setText("Nachricht in UTF-8-Bloecke zerlegt und verschluesselt.");
            refreshView();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void decryptMessage(ActionEvent event) {
        try {
            ensureKeyData();
            if (blocks.isEmpty()) {
                throw new IllegalArgumentException("Bitte zuerst verschluesseln.");
            }

            decryptedText = service.decryptUtf8Blocks(blocks, keyData);
            statusLabel.setText("Cipher-Bloecke wurden wieder in Text zurueckgefuehrt.");
            refreshView();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void resetDemo(ActionEvent event) {
        keyData = null;
        blocks = List.of();
        decryptedText = "";
        statusLabel.setText("Zurueckgesetzt. Berechne einen neuen Schluessel.");
        refreshView();
    }

    private BigInteger parsePrime(String rawText, String label) {
        try {
            BigInteger value = new BigInteger(rawText.trim());
            if (value.compareTo(BigInteger.TWO) < 0) {
                throw new IllegalArgumentException(label + " muss groesser als 1 sein.");
            }
            if (!value.isProbablePrime(20)) {
                throw new IllegalArgumentException(label + " ist keine Primzahl.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " ist keine gueltige ganze Zahl.");
        }
    }

    private void ensureKeyData() {
        if (keyData == null) {
            throw new IllegalArgumentException("Bitte zuerst den Schluessel berechnen.");
        }
    }

    private void refreshView() {
        nLabel.setText(keyData == null ? "-" : keyData.getN().toString());
        phiLabel.setText(keyData == null ? "-" : keyData.getPhi().toString());
        eLabel.setText(keyData == null ? "-" : keyData.getE().toString());
        dLabel.setText(keyData == null ? "-" : keyData.getD().toString());

        keyExplanationArea.setText(keyData == null ? "Noch kein Schluessel berechnet." : service.buildKeyExplanation(keyData));
        eulerArea.setText(keyData == null ? "Noch kein Bezug zu Eulers Satz sichtbar." : service.buildEulerHint(keyData));
        blockArea.setText(
            keyData == null || blocks.isEmpty()
                ? "Noch keine Blockbildung sichtbar."
                : service.buildBlockExplanation(blocks, keyData.getN())
        );
        cipherArea.setText(blocks.isEmpty() ? "Noch keine Cipher-Bloecke sichtbar." : service.buildCipherBlockExplanation(blocks));
        decryptedArea.setText(decryptedText.isEmpty() ? "Noch keine Entschluesselung." : decryptedText);

        visualizationPanel.setSnapshot(new DemoSnapshot(
            keyData,
            messageField.getText(),
            blocks,
            decryptedText
        ));
    }

    private void showError(String message) {
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    private static JTextArea createReadOnlyArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }
}
