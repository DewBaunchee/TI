package digital.sign.implementation.keys;

public class Keys {
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
}
