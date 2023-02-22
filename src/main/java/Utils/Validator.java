package Utils;

/**
 * A generic interface for a class that validates a specific object
 */
public interface Validator {
    /**
     * Checks whether an object is valid
     * @param value The object to validate
     * @param name The name of the object for use in error messages
     * @param <T> The type of the object
     * @return True if the object is valid, false otherwise
     */
    <T> boolean Validate(T value, String name);
}
