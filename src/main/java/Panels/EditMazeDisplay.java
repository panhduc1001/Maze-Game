package Panels;

import Main.MazeGame;
import Models.Maze;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Objects;

import static Utils.Utils.addToPanel;
import static Utils.Utils.pickImage;

/**
 * A JPanel that displays a form for editing a Maze including an image of the Maze
 */
public class EditMazeDisplay extends JPanel implements ActionListener, KeyListener {

    private final MazeDisplay mazeDisplay;
    private JButton saveButton;
    private JButton toggleSolutionButton;
    private JButton toggleArrowsButton;
    private JButton viewDatabaseButton;
    private JButton refreshButton;
    private final JPanel contentPane;
    private JButton logoFileBtn;
    private JComboBox<String> logoComboBox;
    private String logoFile;
    private String startFile;
    private String finishFile;
    private JComboBox<String> startPositionComboBox;
    private JComboBox<String> finishPositionComboBox;
    private JButton startFileBtn;
    private JButton finishFileBtn;
    private JLabel changeStartLabel;
    private JLabel changeFinishLabel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel deadEndsLabel;
    private JLabel percentLabel;
    private JButton mainMenuBtn;

    /**
     * Constructs a new EditMazeDisplay
     * @param contentPane The content pane of the parent of this
     * @param maze The Maze to display
     * @param solve If true the optimal solution to the maze will be displayed in red, otherwise it will not be displayed
     */
    public EditMazeDisplay(JPanel contentPane, Maze maze, Boolean solve,
                           String logoFile, String startFile, String finishFile) {
        this.contentPane = contentPane;
        this.logoFile = logoFile;
        this.startFile = startFile;
        this.finishFile = finishFile;
        contentPane.addKeyListener(this);
        mazeDisplay = new MazeDisplay(maze, solve);
        setLayout(new BorderLayout());
        add(mazeDisplay, BorderLayout.WEST);
        InitButtonPanel();
        InitInfoPanel();
    }

    /**
     *
     * @param contentPane
     * @param maze
     * @param solve
     */
    public EditMazeDisplay(JPanel contentPane, Maze maze, Boolean solve)  {
        this.contentPane = contentPane;
        contentPane.addKeyListener(this);
        mazeDisplay = new MazeDisplay(maze, solve);
        setLayout(new BorderLayout());
        add(mazeDisplay, BorderLayout.WEST);
        InitButtonPanel();
        InitInfoPanel();
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitInfoPanel() {
        InitInfo();
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 100;
        constraints.weighty = 0;
        addToPanel(infoPanel, titleLabel,constraints,0,0,1,1);
        addToPanel(infoPanel, authorLabel,constraints,0,1,1,1);
        addToPanel(infoPanel, deadEndsLabel,constraints,0,2,1,1);
        addToPanel(infoPanel, percentLabel,constraints,0,3,1,100);
        add(infoPanel, BorderLayout.SOUTH);
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitInfo() {
        titleLabel = new JLabel("Title: " + mazeDisplay.maze.getName());
        authorLabel = new JLabel("Author: " + mazeDisplay.maze.getAuthor());
        deadEndsLabel = new JLabel("Number of dead ends: " + mazeDisplay.maze.deadEnds());
        percentLabel = new JLabel("Percentage of cells required for solution: " + mazeDisplay.maze.percentUsed() +"%");
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitButtonPanel() {
        InitButtons();
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 100;
        constraints.weighty = 0;
        addToPanel(buttonPanel, toggleSolutionButton,constraints,0,0,1,1);
        addToPanel(buttonPanel, toggleArrowsButton,constraints,2,0,1,1);
        addToPanel(buttonPanel, startFileBtn, constraints, 0, 1, 1, 1);
        addToPanel(buttonPanel, finishFileBtn, constraints, 2, 1, 1, 1);
        addToPanel(buttonPanel, logoFileBtn,constraints,0,2,1,1);
        addToPanel(buttonPanel, logoComboBox,constraints,2,2,1,1);
        addToPanel(buttonPanel, changeStartLabel, constraints, 0, 3, 1, 1);
        addToPanel(buttonPanel, startPositionComboBox, constraints, 2, 3, 1, 1);
        addToPanel(buttonPanel, changeFinishLabel, constraints, 0, 4, 1, 1);
        addToPanel(buttonPanel, finishPositionComboBox, constraints, 2, 4, 1, 1);
        addToPanel(buttonPanel, refreshButton, constraints, 2, 5, 1, 1);
        addToPanel(buttonPanel, saveButton,constraints,0,5,1,1);
        addToPanel(buttonPanel, viewDatabaseButton, constraints, 2, 6, 1, 1);
        addToPanel(buttonPanel, mainMenuBtn, constraints, 0, 6, 1, 1);
        add(buttonPanel, BorderLayout.EAST);
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitButtons() {
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        toggleSolutionButton = new JButton("Toggle Solution");
        toggleSolutionButton.addActionListener(this);
        toggleArrowsButton = new JButton("Toggle Arrows");
        toggleArrowsButton.addActionListener(this);
        // Logo parts of maze JComboBox GUI components
        String[] logoOptions = {"Exclude","Small square", "Medium square","Large square",
                "Small rectangle horizontal", "Medium rectangle horizontal","Large rectangle horizontal",
                "Small rectangle vertical", "Medium rectangle vertical","Large rectangle vertical"};
        logoComboBox = new JComboBox<>(logoOptions);
        logoComboBox.addActionListener(this);
        logoFileBtn = new JButton("Select Logo Image");
        logoFileBtn.addActionListener(this);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);

        viewDatabaseButton = new JButton("Database");
        viewDatabaseButton.addActionListener(this);

        startFileBtn = new JButton("Change Starting Image");
        startFileBtn.addActionListener(this);

        finishFileBtn = new JButton("Change Finishing Image");
        finishFileBtn.addActionListener(this);

        mainMenuBtn = new JButton("Main Menu");
        mainMenuBtn.addActionListener(this);

        changeStartLabel = new JLabel("Change start position : ");
        if(mazeDisplay.maze.hasStartImage()){
            String[] startOptions1 = {"Unchanged", "Upward", "Downward","Leftward","Rightward"};
            startPositionComboBox = new JComboBox<>(startOptions1);
        }
        else{
            String[] startOptions2 = {"Unchanged","Top Left", "Top Middle", "Top Right", "Middle Left", "Middle Right", "Bottom Left",
                    "Bottom Middle", "Bottom Right"};
            startPositionComboBox = new JComboBox<>(startOptions2);
        }

        changeFinishLabel = new JLabel("Change finish position : ");
        if(mazeDisplay.maze.hasFinishImage()){
            String[] finishOptions1 = {"Unchanged", "Upward", "Downward","Leftward","Rightward"};
            finishPositionComboBox = new JComboBox<>(finishOptions1);
        }
        else{
            String[] finishOptions2 = {"Unchanged","Top Left", "Top Middle", "Top Right", "Middle Left", "Middle Right", "Bottom Left",
                    "Bottom Middle", "Bottom Right"};
            finishPositionComboBox = new JComboBox<>(finishOptions2);
        }

    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == saveButton) {
            mazeDisplay.SaveMaze();
        }
        if(e.getSource() == toggleSolutionButton) {
            mazeDisplay.toggleSolved();
            repaint();
        }
        if(e.getSource() == toggleArrowsButton) {
            mazeDisplay.toggleArrows();
            repaint();
        }
        if(e.getSource() == viewDatabaseButton){
            MazeDatabase dbGUI = new MazeDatabase(contentPane);
            contentPane.add(dbGUI);
            CardLayout layout = (CardLayout) contentPane.getLayout();
            layout.next(contentPane);
        }
        if(e.getSource() == logoFileBtn){
            logoFile = pickImage("Choose a logo image");
        }
        if(e.getSource() == startFileBtn){
            startFile = pickImage("Choose a start image");
        }
        if(e.getSource() == finishFileBtn){
            finishFile = pickImage("Choose a finish image");
        }
        if(e.getSource() == refreshButton && validInformation()){
            try {
                mazeDisplay.refresh(logoFile, (String)logoComboBox.getSelectedItem(),startFile, finishFile);
                if(logoFile != null || startFile != null || finishFile != null){
                    EditMazeDisplay editmazeDisplay = new EditMazeDisplay(contentPane, mazeDisplay.maze,
                            mazeDisplay.getSolve(),logoFile, startFile, finishFile);
                    contentPane.add(editmazeDisplay);
                    CardLayout layout = (CardLayout) contentPane.getLayout();
                    layout.next(contentPane);
                }
                else{
                    EditMazeDisplay editmazeDisplay = new EditMazeDisplay(contentPane, mazeDisplay.maze, mazeDisplay.getSolve());
                    contentPane.add(editmazeDisplay);
                    CardLayout layout = (CardLayout) contentPane.getLayout();
                    layout.next(contentPane);
                }
            }
            catch (IOException | EmptyStackException ex) {
                ex.printStackTrace();
            }
            repaint();
        }
        if(e.getSource() == mainMenuBtn){
            mainMenuPressed();
        }
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void mainMenuPressed() {
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
    private boolean validInformation(){
        if(mazeDisplay.maze.entryAndExit((String) Objects.requireNonNull(startPositionComboBox.getSelectedItem()),
                (String) finishPositionComboBox.getSelectedItem())){
            mazeDisplay.maze.solver();
            return true;
        }
        else{
            JOptionPane.showMessageDialog(this,
                    "Start and end location cannot be the same.", "Start and End location", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param e
     */
    public void keyTyped(KeyEvent e) {

    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param key
     */
    public void keyPressed(KeyEvent key) {
        if (key.getKeyCode() == KeyEvent.VK_P){
            mazeDisplay.myPrint();
            repaint();
        }
    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param e
     */
    public void keyReleased(KeyEvent e) {

    }
    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param g
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        mazeDisplay.paintComponent(g);
    }
}