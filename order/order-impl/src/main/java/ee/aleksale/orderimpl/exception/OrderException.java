package ee.aleksale.orderimpl.exception;

public class OrderException extends IllegalArgumentException {
    public OrderException(String message) {
        super(message);
    }
}
