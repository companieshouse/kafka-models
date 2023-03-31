package consumer.exception;

public class RetryableErrorException extends RuntimeException {

    public RetryableErrorException(Exception ex) {
        super(ex);
    }

    public RetryableErrorException(String message) {
        super(message);
    }

    public RetryableErrorException(String message, Exception exception) {
        super(message, exception);
    }
}