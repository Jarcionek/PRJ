package exceptions;

/**
 * This exception should be thrown when a method is not used correctly,
 * for example when overridden method is supposed to provide data in particular
 * range, but it contains data out of this range.
 * 
 * @author Jaroslaw Pawlak
 */
public class IncorrectUsageException extends RuntimeException {

    public IncorrectUsageException(String message) {
        super(message);
    }

    public IncorrectUsageException() {}

}
