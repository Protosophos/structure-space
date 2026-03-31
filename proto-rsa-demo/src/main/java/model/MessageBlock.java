package model;

import java.math.BigInteger;

public final class MessageBlock {
    private final int index;
    private final String display;
    private final int utf8Value;
    private final BigInteger plainBlock;
    private final BigInteger cipherBlock;

    public MessageBlock(int index, String display, int utf8Value, BigInteger plainBlock, BigInteger cipherBlock) {
        this.index = index;
        this.display = display;
        this.utf8Value = utf8Value;
        this.plainBlock = plainBlock;
        this.cipherBlock = cipherBlock;
    }

    public int getIndex() {
        return index;
    }

    public String getDisplay() {
        return display;
    }

    public int getUtf8Value() {
        return utf8Value;
    }

    public BigInteger getPlainBlock() {
        return plainBlock;
    }

    public BigInteger getCipherBlock() {
        return cipherBlock;
    }
}
