package sample.cypher;

public class Keys {
    private static Keys _keys;
    private final PublicKey _publicKey;
    private final PrivateKey _privateKey;

    public Keys(PublicKey publicKey, PrivateKey privateKey) {
        _publicKey = publicKey;
        _privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return _publicKey;
    }

    public PrivateKey getPrivateKey() {
        return _privateKey;
    }

    public static void setKeys(Keys keys) {
        _keys = keys;
    }

    public static Keys getKeys() {
        return _keys;
    }

    @Override
    public String toString() {
        return "Public key: {\n\tn: " + _publicKey.getN() +
                "\n\tb: " + _publicKey.getB() + "\n}\n" +
                "Private key: {\n\tp: " + _privateKey.getP() +
                "\n\tq: " + _privateKey.getQ() + "\n}";
    }
}
