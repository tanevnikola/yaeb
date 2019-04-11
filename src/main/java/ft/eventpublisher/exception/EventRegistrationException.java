package ft.eventpublisher.exception;

public class EventRegistrationException extends Exception {
    public EventRegistrationException() {

    }

    public EventRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventRegistrationException(String message) {
        super(message);
    }

    public EventRegistrationException(Throwable cause) {
        super(cause);
    }
}
