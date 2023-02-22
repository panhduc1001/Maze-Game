package Main;

import Database.MazeRepository;
import Models.Maze;
import Panels.CreateMazeDisplay;
import Panels.MainMenu;
import Panels.MazeDisplay;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * The main class that spawns all the GUI
 */
public class MazeGame extends JFrame {
    /**
     * The singleton instance of this
     */
    private static MazeGame INSTANCE;

    /**
     * Constructs a new Main.MazeGame which will in turn create the GUI for the app
     */
    private MazeGame(){
        INSTANCE = this;
        initUI();
    }

    /**
     * Initialise the app
     * @param args Any command line arguments passed in (not used)
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            MazeGame mz = new MazeGame();
            mz.setVisible(true);
        });
    }

    /**
     * Initialises the UI
     */
    private void initUI() {
        setTitle("Maze Design Tool");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ignored) {}
        setTitle("Maze Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(25 * MazeDisplay.getCellSize() + 200, 25 * MazeDisplay.getCellSize() + 75);
        JPanel contentPane = new JPanel(new CardLayout());
        setContentPane(contentPane);
        contentPane.setFocusable(true);
        MainMenu mainMenu = new MainMenu(contentPane);
        getContentPane().add(mainMenu, "Creation");
        setLocationRelativeTo(null);
    }
}
