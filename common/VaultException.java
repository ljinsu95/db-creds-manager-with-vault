package common;

public class VaultException extends Exception {
    public VaultException() {
        super();
    }

    public VaultException(String message) {
        super("Vault Error Message : " + message);
    }
}
