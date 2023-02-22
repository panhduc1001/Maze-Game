package Panels;

import javax.swing.*;
import java.awt.*;

import static Utils.Utils.addToPanel;

/**
 * A helper class for quickly creating a JLabel next to a JTextField (like you see in a form)
 */
public class TextInput extends JPanel {

    private final JTextField textField;

    /**
     * Creates a text input (a JLabel next to a JTextField)
     * @param field The name of the field displayed on the label,
     *              e.g "Name" would create a label with "Name: " next to the text field
     * @param tooltip The tooltip to display when hovering over either the label or text field (null for no tooltip)
     * @param columns The number of columns the text field should take up (how long the text field should be)
     */
    public TextInput(String field, String tooltip, int columns) {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        add(inputPanel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 100;
        constraints.weighty = 100;

        JLabel label = new JLabel(field + ": ");
        label.setToolTipText(tooltip);
        textField = new JTextField(columns);
        textField.setToolTipText(tooltip);

        addToPanel(inputPanel, label, constraints, 0, 0, 1, 1);
        addToPanel(inputPanel, textField, constraints, 1, 0, 2, 1);
    }

    /**
     * Gets the text that is currently in the text field
     * @return The text in the text field
     */
    public String getInput() {
        return textField.getText();
    }
}