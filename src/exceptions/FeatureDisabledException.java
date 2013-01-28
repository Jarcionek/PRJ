package exceptions;

/**
 * Should be thrown when the application (usually GUI) tries to use
 * a feature that was internally disabled in the back-end simulation.
 * 
 * @author Jaroslaw Pawlak
 */
public class FeatureDisabledException extends RuntimeException {

    public FeatureDisabledException(String message) {
        super(message);
    }

    public FeatureDisabledException() {}

}
