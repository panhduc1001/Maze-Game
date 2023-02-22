package Panels;

import Main.MazeGame;
import Utils.PropertyReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static Utils.Utils.addToPanel;

/**
 * Class used to construct the main menu of the GUI
 */
public class MainMenu extends JPanel implements ActionListener {
    private final JPanel contentPane;
    private JPanel formPanel;
    private JButton createNew;
    private JButton viewDatabase;
    private JLabel title;

    /**
     * Constructor method to set up Main Menu
     * @param contentPane JPanel used as the building block for the GUI
     */
    public MainMenu(JPanel contentPane) {
        this.contentPane = contentPane;
        InitUI();
        File file = new File("db.props");
        if(!file.exists()) {
            PropertyReader propertyReader = new PropertyReader();
            propertyReader.getProperties("db.props");
            JOptionPane.showMessageDialog(this, "Please setup the db.props file at " + file.getName() + " before continuing with the application.",
                    "Setup db.props", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Initialises UI
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
     * Initialises JLabels and JButtons
     */
    private void InitForm(){
        title = new JLabel("Maze Design Tool");

        createNew = new JButton("Create New Maze");
        createNew.addActionListener(this);

        viewDatabase = new JButton("View Database");
        viewDatabase.addActionListener(this);
    }

    /**
     * Add all content to JPanel
     */
    private void LayoutForm() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 100;
        constraints.weighty = 0;
        // Place the fields
        addToPanel(formPanel, title, constraints, 0, 0, 2, 1);
        addToPanel(formPanel, createNew, constraints, 0, 1, 2, 1);
        addToPanel(formPanel, viewDatabase, constraints, 2, 1, 2, 1);
    }

    /**
     * Implements: Changes GUI screens to the specified screen
     * Pre-condition: One of the button has been pressed on the screen.
     * Post-condition: Appropriate method is run and contentPane is updated as well as GUI
     * @param e the press of one of the buttons
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == createNew){
            CreateMazeDisplay createNewMaze = new CreateMazeDisplay(contentPane);
            contentPane.add(createNewMaze);
            CardLayout layout = (CardLayout) contentPane.getLayout();
            layout.next(contentPane);
        }
        if(e.getSource() == viewDatabase){
            MazeDatabase dbGUI = new MazeDatabase(contentPane);
            contentPane.add(dbGUI);
            CardLayout layout = (CardLayout) contentPane.getLayout();
            layout.next(contentPane);
        }
    }

}
