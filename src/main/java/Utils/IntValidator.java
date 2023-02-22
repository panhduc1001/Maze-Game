package Utils;

import javax.swing.*;


/**
 * Validates an Integer checking that it is indeed an int and that it is between defined bounds
 */
public class IntValidator implements Validator{

    private final int minValue;
    private final int maxValue;

    /**
     * Creates an int validator
     * @param minValue The minimum value that valid ints can be (inclusive)
     * @param maxValue The maximum value that valid ints can be (inclusive)
     */
    public IntValidator(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Checks if an int is a valid int between the set minimum and maximum values
     * creating error message dialogs if it isn't
     * @param value The Integer to validate
     * @param name The name of the int to be displayed in error messages such as "name must be an integer"
     * @return True if the integer is valid, false otherwise
     * @param <T> Either Integer or int
     */
    public <T> boolean Validate(T value, String name) {
        if(value == null || (value.getClass() != Integer.class && value.getClass() != int.class)) {
            JOptionPane.showMessageDialog(null, name + " must be an integer", "Invalid " + name, JOptionPane.ERROR_MESSAGE);
            return false;
        } else if((int)value < minValue || (int)value > maxValue) {
            JOptionPane.showMessageDialog(null, String.format("%s must be between %d and %d", name, minValue, maxValue), "Invalid " + name, JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
