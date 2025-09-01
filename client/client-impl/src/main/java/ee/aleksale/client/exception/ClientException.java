package ee.aleksale.client.exception;

public class ClientException extends IllegalArgumentException {
    public ClientException(String message) {
        super(message);
    }
}
