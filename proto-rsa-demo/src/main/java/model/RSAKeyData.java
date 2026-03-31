package model;

import java.math.BigInteger;

public final class RSAKeyData {
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger n;
    private final BigInteger phi;
    private final BigInteger e;
    private final BigInteger d;
    private final String eExplanation;
    private final String dExplanation;

    public RSAKeyData(
        BigInteger p,
        BigInteger q,
        BigInteger n,
        BigInteger phi,
        BigInteger e,
        BigInteger d,
        String eExplanation,
        String dExplanation
    ) {
        this.p = p;
        this.q = q;
        this.n = n;
        this.phi = phi;
        this.e = e;
        this.d = d;
        this.eExplanation = eExplanation;
        this.dExplanation = dExplanation;
    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getPhi() {
        return phi;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }

    public String getEExplanation() {
        return eExplanation;
    }

    public String getDExplanation() {
        return dExplanation;
    }
}
