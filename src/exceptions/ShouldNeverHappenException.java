package exceptions;

/**
 * This exception should never be thrown during runtime.
 * It should indicate incorrect implementation or internal bugs.
 * It should be thrown only in branches that should be dead code.
 * 
 * 
 * @author Jaroslaw Pawlak
 */
public class ShouldNeverHappenException extends RuntimeException {

    public ShouldNeverHappenException(String message) {
        super(message);
    }

    public ShouldNeverHappenException() {}

}
