package fu.se.swd392csms.exception;

/**
 * Unauthorized Exception
 * Thrown when a user is not authorized to perform an action
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
