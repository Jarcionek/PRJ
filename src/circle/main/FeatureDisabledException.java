package circle.main;

/**
 * @author Jaroslaw Pawlak
 */
public class FeatureDisabledException extends RuntimeException {

    public FeatureDisabledException(String message) {
        super(message);
    }

    public FeatureDisabledException() {
    }

}
