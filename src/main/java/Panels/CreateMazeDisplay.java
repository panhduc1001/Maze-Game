package Panels;

import Main.MazeGame;
import Models.Maze;
import Utils.IntValidator;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import static Utils.Utils.addToPanel;
import static Utils.Utils.pickImage;

/**
 * A JPanel that displays a form for creating a maze
 */
public class CreateMazeDisplay extends JPanel implements ActionListener {

    private JPanel formPanel;
    private TextInput titleInput;
    private TextInput authorInput;
    private JSpinner widthInput;
    private JSpinner heightInput;
    private JComboBox<String> solutionComboBox;
    private JComboBox<String> arrowsComboBox;
    private JComboBox<String> startComboBox;
    private JComboBox<String> finishComboBox;
    private JLabel displaySolution;
    private JLabel arrowsDisplay;
    private JComboBox<String> logoComboBox;
    private JLabel displayWidth;
    private JLabel displayHeight;
    private JButton logoFileBtn;
    private JButton startFileBtn;
    private JButton finishFileBtn;
    private JButton createBtn;
    private final JPanel contentPane;
    private int height;
    private int width;
    private String logoSize;
    private String startSize;
    private String finishSize;
    private boolean solve;
    private boolean displayArrows;
    private String logoFile;
    private String startFile;
    private String finishFile;
    private JButton cancelBtn;

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param selected
     * @return
     */
    private int imageArea(String selected){
        int area = 0;
        switch (selected) {
            case "Default" -> area = 0;
            case "Small square" -> area = 1;
            case "Medium square" -> area = 4;
            case "Large square" -> area = 9;
            case "Small rectangle horizontal", "Small rectangle vertical" -> area = 2;
            case "Medium rectangle horizontal", "Medium rectangle vertical" -> area = 6;
            case "Large rectangle horizontal", "Large rectangle vertical" -> area = 12;
        }
        return area;
    }

    /**
     * Implements: Constructs a new CreateMazeDisplay
     * Pre-condition:
     * Post-condition:
     * @param contentPane The content pane of the parent of this
     */
    public CreateMazeDisplay(JPanel contentPane) {
        this.contentPane = contentPane;
        InitUI();
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitUI() {
        setLayout(new GridBagLayout());

        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());

        InitForm();
        LayoutForm();

        add(formPanel, new GridBagConstraints());
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitForm() {
        titleInput = new TextInput("Title", "The title of your new maze", 10);
        authorInput = new TextInput("Author", "The name of the maze's author", 10);

        displayWidth = new JLabel("Width dimensions:");
        displayHeight = new JLabel("Height dimensions:");

        SpinnerNumberModel dimW = new SpinnerNumberModel(10, 2, 100, 1);
        SpinnerNumberModel dimH = new SpinnerNumberModel(10, 2, 100, 1);
        widthInput = new JSpinner(dimW);
        heightInput = new JSpinner(dimH);

        // Display Solution JComboBox GUI components
        displaySolution = new JLabel("Display solution: ");
        String[] solveDisplay = {"Off", "On"};
        solutionComboBox = new JComboBox<>(solveDisplay);
        solutionComboBox.addActionListener(this);

        // Display Solution JComboBox GUI components
        arrowsDisplay = new JLabel("Display Arrows: ");
        String[] arrowDisplay = {"Off", "On"};
        arrowsComboBox = new JComboBox<>(arrowDisplay);
        arrowsComboBox.addActionListener(this);

        // Start location JComboBox GUI components
        String[] startOptions = {"Exclude","Small square", "Medium square","Large square",
                "Small rectangle horizontal", "Medium rectangle horizontal","Large rectangle horizontal",
                "Small rectangle vertical", "Medium rectangle vertical","Large rectangle vertical"};
        startComboBox = new JComboBox<>(startOptions);
        startComboBox.addActionListener(this);
        startFileBtn = new JButton("Select Starting Image");
        startFileBtn.addActionListener(this);

        // Finish location JComboBox GUI components
        String[] finishOptions = {"Exclude","Small square", "Medium square","Large square",
                "Small rectangle horizontal", "Medium rectangle horizontal","Large rectangle horizontal",
                "Small rectangle vertical", "Medium rectangle vertical","Large rectangle vertical"};
        finishComboBox = new JComboBox<>(finishOptions);
        finishComboBox.addActionListener(this);
        finishFileBtn = new JButton("Select Finishing Image");
        finishFileBtn.addActionListener(this);

        // Logo parts of maze JComboBox GUI components
        String[] logoOptions = {"Exclude","Small square", "Medium square","Large square",
                "Small rectangle horizontal", "Medium rectangle horizontal","Large rectangle horizontal",
                "Small rectangle vertical", "Medium rectangle vertical","Large rectangle vertical"};
        logoComboBox = new JComboBox<>(logoOptions);
        logoComboBox.addActionListener(this);
        logoFileBtn = new JButton("Select Logo Image");
        logoFileBtn.addActionListener(this);

        createBtn = new JButton("Create Maze");
        createBtn.addActionListener(this);

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(this);
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void LayoutForm() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 100;
        constraints.weighty = 0;
        // Place the fields
        addToPanel(formPanel, titleInput,constraints,0,0,2,1);
        addToPanel(formPanel, authorInput,constraints,2,0,2,1);
        addToPanel(formPanel, displayWidth,constraints,0,1,2,1);
        addToPanel(formPanel, widthInput,constraints,2,1,2,1);
        addToPanel(formPanel, displayHeight,constraints,0,2,2,1);
        addToPanel(formPanel, heightInput,constraints,2,2,2,1);

        addToPanel(formPanel, startFileBtn,constraints,0,4,2,1);
        addToPanel(formPanel, startComboBox,constraints,2,4,2,1);

        addToPanel(formPanel, finishFileBtn,constraints,0,5,3,1);
        addToPanel(formPanel, finishComboBox,constraints,2,5,2,1);

        addToPanel(formPanel, logoFileBtn,constraints,0,6,3,1);
        addToPanel(formPanel, logoComboBox,constraints,2,6,2,1);

        addToPanel(formPanel, displaySolution,constraints,0,7,2,1);
        addToPanel(formPanel, solutionComboBox,constraints,2,7,2,1);

        addToPanel(formPanel, arrowsDisplay,constraints,0,8,2,1);
        addToPanel(formPanel, arrowsComboBox,constraints,2,8,2,1);

        addToPanel(formPanel, createBtn, constraints, 0, 9, 2, 1);
        addToPanel(formPanel, cancelBtn, constraints, 2, 9, 2, 1);
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == createBtn) {
            try {
                CreatePressed();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if(src == logoFileBtn){
            logoFile = pickImage("Choose a logo image");
        }
        if(src == startFileBtn){
            startFile = pickImage("Choose a start image");
        }
        if(src == finishFileBtn){
            finishFile = pickImage("Choose a finish image");
        }
        if(src == cancelBtn){
            CancelPressed();
        }
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void CancelPressed() {
        MainMenu mainMenu = new MainMenu(contentPane);
        contentPane.add(mainMenu);
        CardLayout layout = (CardLayout) contentPane.getLayout();
        layout.next(contentPane);
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void CreatePressed() throws IOException {
        if(!ValidateForm()) {
            return;
        }
        Maze maze = new Maze(titleInput.getInput(), authorInput.getInput(), width, height, startSize,
                finishSize, logoSize, displayArrows, startFile, finishFile, logoFile);
        EditMazeDisplay md = new EditMazeDisplay(contentPane, maze,solve, logoFile,startFile,finishFile);
        contentPane.add(md, "Maze View and Editor");

        addKeyListener(md);
        md.setFocusable(true);

        CardLayout layout = (CardLayout) contentPane.getLayout();
        layout.next(contentPane);
    }
    /**
     *
     */
    private boolean ValidateForm() {
        if(titleInput.getInput().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "You must enter a title for your maze.", "Enter a title", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (authorInput.getInput().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "You must enter the name of for the maze's author.", "Enter an author", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        IntValidator sizeValidator = new IntValidator(2, 100);

        Integer width = (Integer) widthInput.getValue();
        if(!sizeValidator.Validate(width, "Width"))
            return false;

        Integer height = (Integer) heightInput.getValue();
        if(!sizeValidator.Validate(height, "Height")) {
            return false;
        }

        this.height = height;
        this.width = width;

        String logoSize = (String)logoComboBox.getSelectedItem();
        String startSize = (String)startComboBox.getSelectedItem();
        String finishSize = (String)finishComboBox.getSelectedItem();
        assert logoSize != null;
        assert startSize != null;
        assert finishSize != null;
        int totalImageArea = imageArea(logoSize) + imageArea(startSize) + imageArea(finishSize);
        int allowedImageArea = (int)Math.abs(width * height * 0.3);

        // Ensure images will fit in maze. Images can only take up 3/10 of maze cells.
        if(totalImageArea <= allowedImageArea){
            this.logoSize = logoSize;
            this.startSize = startSize;
            this.finishSize = finishSize;
        }
        else{
            JOptionPane.showMessageDialog(this,
                    "Decrease size of images or increase maze dimensions.", "Image and maze dimensions", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        this.solve = solutionComboBox.getSelectedItem() == "On";

        this.displayArrows = arrowsComboBox.getSelectedItem() == "On";

        return true;
    }
}
