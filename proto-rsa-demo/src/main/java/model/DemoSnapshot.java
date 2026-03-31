package model;

import java.util.List;

public final class DemoSnapshot {
    private final RSAKeyData keyData;
    private final String plaintext;
    private final List<MessageBlock> blocks;
    private final String decryptedText;

    public DemoSnapshot(RSAKeyData keyData, String plaintext, List<MessageBlock> blocks, String decryptedText) {
        this.keyData = keyData;
        this.plaintext = plaintext;
        this.blocks = List.copyOf(blocks);
        this.decryptedText = decryptedText;
    }

    public RSAKeyData getKeyData() {
        return keyData;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public List<MessageBlock> getBlocks() {
        return blocks;
    }

    public String getDecryptedText() {
        return decryptedText;
    }
}
