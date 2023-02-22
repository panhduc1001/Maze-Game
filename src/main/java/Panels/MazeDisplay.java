package Panels;

import Database.MazeRepository;
import Main.MazeGame;
import Models.Exceptions.DatabaseException;
import Models.Maze;

import java.awt.*;

import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;

import javax.swing.*;

/**
 * A JPanel that displays a Maze using Graphics2D
 */
public class MazeDisplay extends JPanel implements Printable {
    Maze maze;
    private static int cellSize = 20;
    private Boolean solve;

    /**
     * Retrieves if the solution is showing
     * @return True if the solution is displayed, false otherwise.
     */
    public Boolean getSolve(){
        return solve;
    }

    /**
     * Implements: Constructs a new MazeDisplay
     * Pre-condition:
     * Post-condition:
     * @param maze      The Maze to display
     * @param solve     If true the optimal solution to the maze will be displayed in red, otherwise it will not be displayed
     */
    public MazeDisplay(Maze maze, Boolean solve) {
        this.maze = maze;
        this.solve = solve;
    }

    /**
     * Implements: Gets the size each cell is drawn at as an int
     * Pre-condition:
     * Post-condition:
     * @return The size each cell is drawn at as an int
     */
    public static int getCellSize() {
        return cellSize;
    }

    public int print(Graphics g, PageFormat pf, int page) {

        if (page > 0) {
            return NO_SUCH_PAGE; /* We have only one page, and 'page' is zero-based */
        }

        // User (0,0) is typically outside the imaginable area, so we must translate by the X
        // and Y values in the PageFormat to avoid clipping
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        //Now we perform our rendering
        doDrawing(g2d);

        // tell the caller that this page is part of the printed document
        return PAGE_EXISTS;
    }

    /**
     * Implements: Creates a new print job that prints the maze to the os print service.
     * This will cause a native printer pop up to appear for the user
     * Pre-condition:
     * Post-condition:
     */
    public void myPrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        job.setJobName("Models.Maze");
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
                /* The job did not successfully complete */
                System.out.println("Print failed");
            }
        }
    }

    /**
     * Implements: Saves the current version of the maze to the database, showing a message dialog that indicates
     * whether this succeeded or not
     * Pre-condition:
     * Post-condition:
     */
    protected void SaveMaze() {
        int id;
        try {
            id = MazeRepository.getInstance().SaveMaze(maze);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Saving error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        maze.setId(id);
        JOptionPane.showMessageDialog(this,
                "Maze saved successfully", "Saving successful", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Implements: Draws the maze
     * Pre-condition:
     * Post-condition:
     * @param g A {@link Graphics} object to draw the maze onto
     */
    private void doDrawing(Graphics g) {
        // Set cellSize
        int cellSizeW = 360/maze.getWidth();
        int cellSizeH = 462/maze.getHeight();
        cellSize = Math.min(cellSizeW,cellSizeH);
        int offsetX = 10;
        int offsetY = 10;
        maze.drawMaze((Graphics2D) g, getSize(), getInsets(), offsetX, offsetY, cellSize, solve);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    /**
     * Toggles whether the solution to the maze is shown
     */
    public void toggleSolved() {
        solve = !solve;
        repaint();
    }

    /**
     * Toggles whether arrows are drawn pointing to the start and end of the maze
     */
    public void toggleArrows() {
        maze.setShowArrows(!maze.isShowingArrows());
        repaint();
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param newLogoFilePath The full path to the image file to set the logo to or null if it should not be changed
     * @param logoScale       The scale of the logo as one of the following:
     *                        "Exclude","Small square", "Medium square","Large square",
     *                        "Small rectangle horizontal", "Medium rectangle horizontal","Large rectangle horizontal",
     *                        "Small rectangle vertical", "Medium rectangle vertical","Large rectangle vertical"
     * @param newStartImage   The full path to the image file to set the start image to or null if it should not be changed
     * @param newFinishImage  The full path to the image file to set the finish image to or null if it should not be changed
     * @throws IOException thrown if any of the supplied image files cannot be read correctly
     */
    public void refresh(String newLogoFilePath, String logoScale, String newStartImage,
                        String newFinishImage) throws IOException {
        // Logo
        if (newLogoFilePath != null)
            maze.setLogo(newLogoFilePath, logoScale);
        if (newStartImage != null)
            maze.setStartImage(newStartImage);
        if (newFinishImage != null)
            maze.setFinishImage(newFinishImage);
        // Scale update
        maze.percentUsed();
        repaint();
    }
}
