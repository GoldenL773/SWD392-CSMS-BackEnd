package fu.se.swd392csms.exception;

/**
 * Bad Request Exception
 * Thrown when a request contains invalid data
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
