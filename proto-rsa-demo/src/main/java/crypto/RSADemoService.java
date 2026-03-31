package crypto;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import model.MessageBlock;
import model.RSAKeyData;

public final class RSADemoService {
    public RSAKeyData generateKeyData(BigInteger p, BigInteger q) {
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = chooseExponent(phi);
        BigInteger d = e.modInverse(phi);

        String eExplanation = "e muss kleiner als phi(n) sein und ggT(e, phi(n)) = 1 erfuellen. "
            + "Hier wurde e = " + e + " gewaehlt.";
        String dExplanation = "d ist das modulare Inverse von e modulo phi(n). "
            + "Es gilt also e * d mod phi(n) = 1, hier mit d = " + d + ".";

        return new RSAKeyData(p, q, n, phi, e, d, eExplanation, dExplanation);
    }

    public List<MessageBlock> encryptUtf8Blocks(String message, RSAKeyData keyData) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        List<MessageBlock> blocks = new ArrayList<>();

        for (int i = 0; i < bytes.length; i++) {
            int unsignedByte = Byte.toUnsignedInt(bytes[i]);
            BigInteger plainBlock = BigInteger.valueOf(unsignedByte);
            if (plainBlock.compareTo(keyData.getN()) >= 0) {
                throw new IllegalArgumentException(
                    "Mindestens ein UTF-8-Block ist nicht kleiner als n. Waehle groessere Primzahlen."
                );
            }

            BigInteger cipherBlock = plainBlock.modPow(keyData.getE(), keyData.getN());
            blocks.add(new MessageBlock(i, byteLabel(bytes[i]), unsignedByte, plainBlock, cipherBlock));
        }

        return blocks;
    }

    public String decryptUtf8Blocks(List<MessageBlock> blocks, RSAKeyData keyData) {
        byte[] bytes = new byte[blocks.size()];
        for (int i = 0; i < blocks.size(); i++) {
            BigInteger plainBlock = blocks.get(i).getCipherBlock().modPow(keyData.getD(), keyData.getN());
            bytes[i] = (byte) plainBlock.intValueExact();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String buildKeyExplanation(RSAKeyData keyData) {
        return "Kompakte Schrittfolge\n"
            + "1. p = " + keyData.getP() + ", q = " + keyData.getQ() + '\n'
            + "2. n = p * q = " + keyData.getP() + " * " + keyData.getQ() + " = " + keyData.getN() + '\n'
            + "3. phi(n) = (p - 1)(q - 1) = (" + keyData.getP() + " - 1) * (" + keyData.getQ() + " - 1) = "
            + keyData.getPhi() + '\n'
            + "   Warum minus eins? Bei einer Primzahl p sind genau 1 bis p - 1 zu p teilerfremd.\n"
            + "4. " + keyData.getEExplanation() + '\n'
            + "5. " + keyData.getDExplanation() + '\n'
            + "6. Public Key = (" + keyData.getE() + ", " + keyData.getN() + "), Private Key = ("
            + keyData.getD() + ", " + keyData.getN() + ")";
    }

    public String buildEulerHint(RSAKeyData keyData) {
        return "Eulers Satz erklaert den Rueckweg:\n"
            + "Wenn ggT(m, n) = 1, dann gilt m^phi(n) mod n = 1.\n"
            + "Hier ist phi(n) = " + keyData.getPhi() + ". Weil e und d invers modulo phi(n) sind,\n"
            + "wird aus m^(e*d) modulo n wieder m. Darum funktioniert die Entschluesselung.";
    }

    public String buildBlockExplanation(List<MessageBlock> blocks, BigInteger n) {
        if (blocks.isEmpty()) {
            return "Noch keine Blockdaten vorhanden.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Nachricht -> UTF-8-Bytes -> Zahlenbloecke\n");
        builder.append("Jeder Block muss kleiner als n = ").append(n).append(" sein.\n\n");

        for (MessageBlock block : blocks) {
            builder.append('#').append(block.getIndex()).append(": ");
            builder.append(block.getDisplay()).append(" -> Byte ").append(block.getUtf8Value());
            builder.append(" -> m = ").append(block.getPlainBlock());
            builder.append(" < ").append(n).append('\n');
        }

        builder.append("\nHinweis zu Padding:\n");
        builder.append("Echte RSA-Systeme fuegen vor der Verschluesselung strukturiertes Padding hinzu.\n");
        builder.append("Diese Demo zeigt nur den Grundgedanken der Blockbildung, nicht sichere Praxis.");
        return builder.toString();
    }

    public String buildCipherBlockExplanation(List<MessageBlock> blocks) {
        if (blocks.isEmpty()) {
            return "Noch keine verschluesselten Bloecke vorhanden.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Verschluesselte Zahlenbloecke\n");
        builder.append("Formel pro Block: c = m^e mod n\n\n");
        for (MessageBlock block : blocks) {
            builder.append('#').append(block.getIndex()).append(": m = ").append(block.getPlainBlock());
            builder.append(" -> c = ").append(block.getCipherBlock()).append('\n');
        }
        return builder.toString();
    }

    private BigInteger chooseExponent(BigInteger phi) {
        BigInteger[] preferred = {
            BigInteger.valueOf(65537),
            BigInteger.valueOf(17),
            BigInteger.valueOf(5),
            BigInteger.valueOf(3)
        };

        for (BigInteger candidate : preferred) {
            if (candidate.compareTo(phi) < 0 && candidate.gcd(phi).equals(BigInteger.ONE)) {
                return candidate;
            }
        }

        BigInteger candidate = BigInteger.valueOf(3);
        while (candidate.compareTo(phi) < 0) {
            if (candidate.gcd(phi).equals(BigInteger.ONE)) {
                return candidate;
            }
            candidate = candidate.add(BigInteger.TWO);
        }

        throw new IllegalArgumentException("Es konnte kein geeigneter Exponent e gefunden werden.");
    }

    private String byteLabel(byte value) {
        int unsigned = Byte.toUnsignedInt(value);
        char ch = (char) unsigned;
        if (Character.isISOControl(ch)) {
            return "Byte";
        }
        if (ch == ' ') {
            return "' '";
        }
        return "'" + ch + "'";
    }
}
