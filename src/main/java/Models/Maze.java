package Models;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static Models.Direction.*;
import static Models.Wall.DOWN;
import static Models.Wall.UP;
import static Utils.Utils.getImageFromPath;
import static Utils.Utils.getScaledImage;

/**
 * Stores data about a maze created using Wilson's algorithm including the actual maze layout as well as metadata about the maze
 */
public class Maze implements Printable {
    private final int width;
    private final int height;
    private Integer id;
    private int start_x;
    private int start_y;
    private int finish_x;
    private int finish_y;
    private int start_Ix = -1;
    private int start_Iy = -1;
    private int finish_Ix = -1;
    private int finish_Iy = -1;
    private int logo_Ix = -1;
    private int logo_Iy = -1;
    private final Cell[][] cells;
    private final Stack<Cell> excludedCells = new Stack<>(); // Stack for storing all excluded cells for image insert
    private Stack<Cell> mazeSolution = new Stack<>(); // Stack for holding the maze solution path
    private final Stack<Cell> logoCells = new Stack<>(); // Stack for holding the cells where the logo is placed
    private final Stack<Cell> logoStartCells = new Stack<>(); // Stack for holding the cells where the logo is placed
    private final Stack<Cell> finishImageCells = new Stack<>(); // Stack for holding the cells where the logo is placed
    private String name;
    private String author;
    private LocalDateTime lastEdited;
    private LocalDateTime creationDate;
    private int solutionNumber;
    private int logoWidth;
    private int logoHeight;
    private int startWidth;
    private int startHeight;
    private int finishWidth;
    private int finishHeight;
    private boolean finishImageExists;
    private boolean startImageExists;
    private boolean logoExists = false;
    private boolean showArrows;
    private BufferedImage startImage;
    private BufferedImage finishImage;
    private BufferedImage logo;
    private String startImagePath;
    private String logoImagePath;
    private String finishImagePath;
    private String startInput;
    private String finishInput;
    private String logoInput;

    /**
     * Implements: Constructs a new Maze with an automatically generated layout using Wilson's algorithm
     * Pre-condition: All input types are valid.
     * Post-condition: A two-dimensional array or cells are produced with a valid solution.
     * @param name the title of the maze.
     * @param author the name of the author who created the maze.
     * @param width the width of the maze in cells.
     * @param height the height of the maze in cells.
     * @param startInput describes the starting point as default, a grouped bunch of cells or arrow
     * @param finishInput describes the starting point as default, a grouped bunch of cells or arrow
     * @param logoInput describes the logo point as None if there is no logo to be inserted, or the size that needs to be left for the logo
     * @param showArrows If true arrows will be drawn pointing to the entrance and exit of the maze, otherwise they will not be drawn
     * @param logoPath A full path to an image file that will be displayed as the logo or null if no logo is wanted
     * @param startImagePath A full path to an image file that will be used as the start of the maze or null if no start image is wanted
     * @param finishImagePath A full path to an image file that will be used as the end of the maze or null if no end image is wanted
     */
    public Maze(String name, String author, int width, int height, String startInput,
                String finishInput, String logoInput, boolean showArrows,
                String startImagePath, String finishImagePath, String logoPath){

        this.name = name;
        this.author = author;
        creationDate = LocalDateTime.now();
        lastEdited = LocalDateTime.now();
        this.width = width;
        this.height = height;
        this.showArrows = showArrows;
        this.startInput = startInput;
        this.finishInput = finishInput;
        this.logoInput = logoInput;
        id = null;
        logo = getImageFromPath(logoPath);
        startImage = getImageFromPath(startImagePath);
        finishImage = getImageFromPath(finishImagePath);
        cells = new Cell[width][height];
        initializeCells();
        startImageInsert();
        finishImageInsert();
        logoImageInsert();
        generateMaze();
        entryAndExit();
        this.mazeSolution = solve();
    }
    /**
     * Implements: Constructor method
     * Pre-condition: All input types are valid.
     * Post-condition: Assigns each input parameter to their associated variable.
     * @param id ????
     * @param name the title of the maze.
     * @param author the name of the author who created the maze.
     * @param creationDate the date the maze was constructed.
     * @param lastEdited the date the maze was last edited.
     * @param cells the maze as a two-dimensional array.
     * @param start_x starting x coordinate for entry
     * @param start_y starting y coordinate for entry
     * @param finish_x finishing x coordinate for entry
     * @param finish_y finishing y coordinate for entry
     */
    public Maze(int id, String name, String author, LocalDateTime creationDate, LocalDateTime lastEdited, Cell[][] cells, int start_x,
                int start_y, int finish_x, int finish_y, int start_Ix, int start_Iy, int finish_Ix, int finish_Iy,
                int logo_Ix, int logo_Iy, BufferedImage logo, BufferedImage startImage, BufferedImage finishImage){
        this.name = name;
        this.author = author;
        this.creationDate = creationDate;
        this.lastEdited = lastEdited;
        this.cells = cells;
        this.width = cells.length;
        this.height = cells[0].length;
        this.start_x = start_x;
        this.start_y = start_y;
        this.finish_x = finish_x;
        this.finish_y = finish_y;
        this.start_Ix = start_Ix;
        this.start_Iy = start_Iy;
        this.finish_Ix = finish_Ix;
        this.finish_Iy = finish_Iy;
        this.logo_Ix = logo_Ix;
        this.logo_Iy = logo_Iy;
        this.startImage = startImage;
        this.finishImage = finishImage;
        this.logo = logo;
        this.id = id;
    }

    public Maze(int id, String name, String author, LocalDateTime creationDate, LocalDateTime lastEdited, Cell[][] cells, int start_x,
                int start_y, int finish_x, int finish_y, int start_Ix, int start_Iy, int finish_Ix, int finish_Iy,
                int logo_Ix, int logo_Iy, BufferedImage logo, BufferedImage startImage, BufferedImage finishImage,
                int logoWidth, int logoHeight, int startWidth, int startHeight, int finishWidth, int finishHeight){
        this(id, name, author, creationDate, lastEdited, cells, start_x, start_y, finish_x, finish_y, start_Ix, start_Iy,
                finish_Ix, finish_Iy, logo_Ix, logo_Iy, logo, startImage, finishImage);
        this.logoWidth = logoWidth;
        this.logoHeight = logoHeight;
        this.startWidth = startWidth;
        this.startHeight = startHeight;
        this.finishWidth = finishWidth;
        this.finishHeight = finishHeight;
    }

    /**
     * Changes the creation date, for use only in testing!
     * @param date The new date to set the creation date to
     */
    public void setCreationDate(LocalDateTime date) {
        creationDate = date;
    }
    /**
     * Changes the last edited date, for use only in testing!
     * @param date The new date to set the last edited date to
     */
    public void setLastEdited(LocalDateTime date) {
        lastEdited = date;
    }
    /**
     * Gets the cells of the maze
     * @return A 2D array containing the cells of the maze where the first index of the array is x position
     * and the second is y position
     * For example cells[1][2] gets the cell at x = 1, y = 2
     */
    public Cell[][] getCells(){
        return cells;
    }
    /**
     * Gets a specific cell of the maze
     * @param x The horizontal position of the cell starting from 0
     * @param y The vertical position of the cell starting from 0
     * @return The cell at the specified position
     */
    public Cell getCell(int x, int y) {
        return cells[x][y];
    }
    /**
     * Determines if a cell already exists.
     * @param x The horizontal position of the cell starting from 0
     * @param y The vertical position of the cell starting from 0
     * @return true if the specified cell exists, false otherwise.
     */
    private boolean cellExists(int x, int y) {
        try {
            Cell cell =  cells[x][y];
            return true;
        } catch(ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
    /**
     * Changes a specified cell of the maze
     * @param x The horizontal position of the cell starting from 0
     * @param y The vertical position of the cell starting from 0
     * @param newCell The cell to replace the old one with
     */
    public void setCell(int x, int y, Cell newCell) {
        updateLastEdited();
        cells[x][y] = newCell;
        // Update the walls of any adjacent cells
        if(cellExists(x + 1, y))
            cells[x + 1][y].setWall(WEST, newCell.getWall(EAST));
        if(cellExists(x - 1, y))
            cells[x - 1][y].setWall(EAST, newCell.getWall(WEST));
        if(cellExists(x, y + 1))
            cells[x][y + 1].setWall(NORTH, newCell.getWall(SOUTH));
        if(cellExists(x, y - 1))
            cells[x][y - 1].setWall(SOUTH, newCell.getWall(NORTH));
    }
    /**
     * Sets the type of specific wall of a specific cell
     * @param x The horizontal position of the cell starting from 0
     * @param y The vertical position of the cell starting from 0
     * @param position The position of the wall around the cell
     * @param wallType The type to set the wall to
     */
    public void setWall(int x, int y, Direction position, Wall wallType ) {
        Cell cell = cells[x][y];
        cell.setWall(position, wallType);
        setCell(x, y, cell);
    }
    /**
     * Gets the type of specific wall of a specified cell
     * @param x The horizontal position of the cell starting from 0
     * @param y The vertical position of the cell starting from 0
     * @param position The position of the wall around the cell
     * @return The type of the wall at position of cell x, y
     */
    public Wall getWall(int x, int y, Direction position) {
        return cells[x][y].getWall(position);
    }
    /**
     * Gets the minimum number of cells that the optimal solution goes through
     * @return The number of cells the optimal solution goes through
     */
    public int getSolutionNumber(){
        return solutionNumber;
    }
    /**
     * Sets the minimum number of cells that the optimal solution goes through
     * @param numberCells integer representing the minimum number of cells the optimal solution goes through
     */
    public void setSolutionNumber(int numberCells){
        solutionNumber = numberCells;
        updateLastEdited();
    }
    /**
     * Gets the x position of the maze's start
     * @return The x position of the maze's start
     */
    public int getStart_x(){return start_x;}
    /**
     * Gets the y position of the maze's start
     * @return The y position of the maze's start
     */
    public int getStart_y(){return start_y;}
    /**
     * Gets the x position of the maze's end
     * @return The x position of the maze's end
     */
    public int getFinish_x(){return finish_x;}
    /**
     * Gets the y position of the maze's end
     * @return The y position of the maze's end
     */
    public int getFinish_y(){return finish_y;}
    /**
     * Gets the y position of the maze's end
     * @return The y position of the maze's end
     */
    public int getLogo_x(){return logo_Ix;}
    /**
     * Gets the y position of the maze's end
     * @return The y position of the maze's end
     */
    public int getLogo_y(){return logo_Iy;}
    /**
     * Gets the x position of the maze's start image
     * @return The x position of the maze's start image
     */
    public int getStart_Ix(){return start_Ix;}
    /**
     * Gets the y position of the maze's start image
     * @return The y position of the maze's start image
     */
    public int getStart_Iy(){return start_Iy;}
    /**
     * Gets the x position of the maze's finish image
     * @return The x position of the maze's finish image
     */
    public int getFinish_Ix(){return finish_Ix;}
    /**
     * Gets the y position of the maze's finish image
     * @return The y position of the maze's finish image
     */
    public int getFinish_Iy(){return finish_Iy;}
    /**
     * Gets the name of the maze's author
     * @return The name of the maze's author
     */
    public String getAuthor() {
        return author;
    }
    /**
     * Changes the author of the maze
     * @param newAuthor The new author of the maze
     */
    public void setAuthor(String newAuthor) {
        author = newAuthor;
        updateLastEdited();
    }
    /**
     * Gets the name of the maze
     * @return The name of the maze
     */
    public String getName() {
        return name;
    }
    /**
     * Changes the name of the maze
     * @param newName The new name of the maze
     */
    public void setName(String newName) {
        name = newName;
        updateLastEdited();
    }
    /**
     * Gets the time the maze was created
     * @return The time the maze was created
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    /**
     * Gets the time the maze was last edited
     * @return The time the maze was last edited
     */
    public LocalDateTime getLastEdited() {
        return lastEdited;
    }
    /**
     * Gets the integer value of width
     * @return the integer value of width
     */
    public int getWidth(){
        return width;
    }
    /**
     * Gets the integer value of height
     * @return the integer value of height
     */
    public int getHeight(){
        return height;
    }
    /**
     * Gets the logo width
     * @return The logo width
     */
    public int getLogoWidth() {
        return logoWidth;
    }
    /**
     * Gets the logo height
     * @return The logo height
     */
    public int getLogoHeight() {
        return logoHeight;
    }
    /**
     * Gets the start image width
     * @return The start image width
     */
    public int getStartWidth() {
        return startWidth;
    }
    /**
     * Gets the start image height
     * @return The start image height
     */
    public int getStartHeight() {
        return startHeight;
    }
    /**
     * Gets the finish image width
     * @return The finish image width
     */
    public int getFinishWidth() {
        return finishWidth;
    }
    /**
     * Gets the finish image height
     * @return The finish image height
     */
    public int getFinishHeight() {
        return finishHeight;
    }
    /**
     * Implements: Initialises all the cells of the maze.
     * Pre-condition: The two-dimensional array of the maze has be constructed
     * Post-condition: All cells in the maze have their walls up.
     */
    private void initializeCells(){
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                cells[i][j] = new Cell(i, j);
            }
        }
    }
    /**
     * Implements: Inserts starting image into maze before constructing maze.
     * Pre-condition: Maze has been constructed and initialised.
     * Post-condition: Space has been made to insert the starting image into the maze. Cells where image will be inserted
     * have been added to excluded stack.
     */
    private void startImageInsert(){
        Random rand = new Random();
        // Assign start dimensions
        switch (startInput) {
            case "Exclude", "Arrow","Small square" -> {
                startWidth = 0;
                startHeight = 0;
            }
            case "Medium square" -> {
                startWidth = 1;
                startHeight = 1;
            }
            case "Large square" -> {
                startWidth = 2;
                startHeight = 2;
            }
            case "Small rectangle horizontal" -> {
                startWidth = 1;
                startHeight = 0;
            }
            case "Medium rectangle horizontal" -> {
                startWidth = 2;
                startHeight = 1;
            }
            case "Large rectangle horizontal" -> {
                startWidth = 3;
                startHeight = 2;
            }
            case "Small rectangle vertical" -> {
                startWidth = 0;
                startHeight = 1;
            }
            case "Medium rectangle vertical" -> {
                startWidth = 1;
                startHeight = 2;
            }
            case "Large rectangle vertical" -> {
                startWidth = 2;
                startHeight = 3;
            }
        }
        // Set start position along edge of maze CAN UPDATE THIS CODE TO BE LIKE LOGO RANDOM POSITION
        start_x = rand.nextInt(width - startWidth - 1);
        if((start_x == 0) || (start_x == width - startWidth - 1)){
            start_y = rand.nextInt(height - startHeight - 1);
        }
        else{
            start_y = rand.nextBoolean() ? 0 : height - startHeight - 1;
        }
        if(!Objects.equals(startInput, "Exclude") && !Objects.equals(startInput, "Arrow")){
            int startX_1 = start_x;
            int startY_1 = start_y;
            int startX_2 = startX_1 + startWidth;
            int startY_2 = startY_1 + startHeight;
            for(int i = startX_1; i < startX_2 + 1; i++){
                for(int j = startY_1; j < startY_2 + 1; j++){
                    excludedCells.push(cells[i][j]);
                    // clear right wall
                    if(i < startX_2){
                        setWall(i, j, EAST, DOWN);
                    }
                    // clear left wall
                    if(i > startX_1){
                        setWall(i, j, WEST, DOWN);
                    }
                    // clear top wall
                    if(j > startY_1){
                        setWall(i, j, NORTH, DOWN);
                    }
                    // clear bottom wall
                    if(j < startY_2){
                        setWall(i, j, SOUTH, DOWN);
                    }
                    if(i == startX_1){
                        setWall(i, j, WEST, UP);
                    }
                    if(j == startY_1){
                        setWall(i, j, NORTH, UP);
                    }
                    if(i == startX_2){
                        setWall(i, j, EAST, UP);
                    }
                    if(j == startY_2){
                        setWall(i, j, SOUTH, UP);
                    }
                }
            }
            startImageExists = true;
            start_Ix = startX_1;
            start_Iy = startY_1;
        }
    }
    /**
     * Implements: Inserts finishing image into maze before constructing maze, in a different location to starting location.
     * Pre-condition: Maze has been constructed and initialised with starting location set.
     * Post-condition: Space has been made to insert the finishing image into the maze. Cells where image will be inserted
     * have been added to excluded stack.
     */
    private void finishImageInsert(){
        Random rand = new Random();
        // Assign finish dimensions
        switch (finishInput) {
            case "Exclude", "Arrow", "Small square" -> {
                finishWidth = 0;
                finishHeight = 0;
            }
            case "Medium square" -> {
                finishWidth = 1;
                finishHeight = 1;
            }
            case "Large square" -> {
                finishWidth = 2;
                finishHeight = 2;
            }
            case "Small rectangle horizontal" -> {
                finishWidth = 1;
                finishHeight = 0;
            }
            case "Medium rectangle horizontal" -> {
                finishWidth = 2;
                finishHeight = 1;
            }
            case "Large rectangle horizontal" -> {
                finishWidth = 3;
                finishHeight = 2;
            }
            case "Small rectangle vertical" -> {
                finishWidth = 0;
                finishHeight = 1;
            }
            case "Medium rectangle vertical" -> {
                finishWidth = 1;
                finishHeight = 2;
            }
            case "Large rectangle vertical" -> {
                finishWidth = 2;
                finishHeight = 3;
            }
        }
        // Set finish position along edge
        boolean end = false;
        while(!end){
            finish_x = rand.nextInt(width - finishWidth - 1);
            if((finish_x == 0) || (finish_x == width - finishWidth - 1)){
                finish_y = rand.nextInt(height - finishHeight - 1);
            }
            else{
                finish_y = rand.nextBoolean() ? 0 : height - finishWidth - 1;
            }
            if(start_x != finish_x || start_y != finish_y){
                for(int i = finish_x; i <= finish_x + finishWidth; i++){
                    for(int j = finish_y; j<= finish_y + finishHeight; j++){
                        if(excludedCells.contains(cells[i][j])){
                            finishImageCells.clear();
                            break;
                        }
                        else{
                            finishImageCells.push(cells[i][j]);
                            if(i == (finish_x + finishWidth) && j == (finish_y + finishHeight)){
                                end = true;
                            }
                        }
                    }
                    if(finishImageCells.isEmpty()){
                        break;
                    }
                }
            }
        }
        if(!Objects.equals(finishInput, "Exclude") && !Objects.equals(finishInput, "Arrow")){
            int finishX_1 = finish_x;
            int finishY_1 = finish_y;
            int finishX_2 = finishX_1 + finishWidth;
            int finishY_2 = finishY_1 + finishHeight;
            for(int i = finishX_1; i < finishX_2 + 1; i++){
                for(int j = finishY_1; j < finishY_2 + 1; j++){
                    excludedCells.push(cells[i][j]);
                    // Ensure walls are up around boarder
                    if(i == finishX_1){
                        setWall(i, j, WEST, UP);
                    }
                    if(j == finishY_1){
                        setWall(i, j, NORTH, UP);
                    }
                    if(i == finishX_2){
                        setWall(i, j, EAST, UP);
                    }
                    if(j == finishY_2){
                        setWall(i, j, SOUTH, UP);
                    }
                    // clear right wall
                    if(i < finishX_2){
                        setWall(i, j, EAST, DOWN);
                    }
                    // clear left wall
                    if(i > finishX_1){
                        setWall(i, j, WEST, DOWN);
                    }
                    // clear top wall
                    if(j > finishY_1){
                        setWall(i, j, NORTH, DOWN);
                    }
                    // clear bottom wall
                    if(j < finishY_2){
                        setWall(i, j, SOUTH, DOWN);
                    }
                }
            }
            // Select cell to have opening into grouped cells
            finishImageExists = true;
            cells[finishX_1][finishY_1].finishImage = 1; // Can Update this if we want start to be just group of cells without image
            finish_Ix = finishX_1;
            finish_Iy = finishY_1;
        }
    }
    /**
     * Implements: Inserts logo into maze in a different location to starting and finishing locations.
     * Pre-condition: Maze has been constructed with start and finish positions set.
     * Post-condition: Space has been made to insert the logo image into the maze. Cells where image will be inserted
     * have been added to excluded stack.
     */
    private void logoImageInsert(){
        Random rand = new Random();
        // If logo is included into
        if(!logoInput.equals("Exclude")){
            // Assign logo dimensions
            switch (logoInput) {
                case "Small square" -> {
                    logoWidth = 1;
                    logoHeight = 1;
                }
                case "Medium square" -> {
                    logoWidth = 2;
                    logoHeight = 2;
                }
                case "Large square" -> {
                    logoWidth = 3;
                    logoHeight = 3;
                }
                case "Small rectangle horizontal" -> {
                    logoWidth = 2;
                    logoHeight = 1;
                }
                case "Medium rectangle horizontal" -> {
                    logoWidth = 3;
                    logoHeight = 2;
                }
                case "Large rectangle horizontal" -> {
                    logoWidth = 4;
                    logoHeight = 3;
                }
                case "Small rectangle vertical" -> {
                    logoWidth = 1;
                    logoHeight = 2;
                }
                case "Medium rectangle vertical" -> {
                    logoWidth = 2;
                    logoHeight = 3;
                }
                case "Large rectangle vertical" -> {
                    logoWidth = 3;
                    logoHeight = 4;
                }
            }
            int scale_x = logoWidth - 1;
            int scale_y = logoHeight - 1;
            int logoX_1 = rand.nextInt(0,width - logoWidth - 1);
            int logoY_1 = rand.nextInt(0,height - logoHeight - 1);
            int logoX_2 = logoX_1 + scale_x;
            int logoY_2 = logoY_1 + scale_y;
            boolean setLogo = false;
            // Ensure logo isn't inserted in already restricted section of maze
            while(!setLogo){
                // Iterate over cells' logo will take up to ensure they aren't inserted in already restricted
                // section of maze or solution
                logoStartCells.push(cells[logoX_1][logoY_1]);
                for(int i = logoX_1; i <= logoX_2; i++){
                    for(int j = logoY_1; j <= logoY_2; j++){
                        if(mazeSolution.contains(cells[i][j]) || excludedCells.contains(cells[i][j])){
                            logoCells.clear();
                            break;
                        }
                        else{
                            logoCells.push(cells[i][j]);
                            if(i == logoX_2 && j == logoY_2){
                                setLogo = true;
                            }
                        }
                    }
                    if(logoCells.isEmpty()){break;}
                }
                if(!setLogo) {
                    int totalAvailableCells = width*height - excludedCells.size();
                    if(logoStartCells.size() == totalAvailableCells){
                        logoWidth--;
                        logoHeight--;
                        if(logoWidth < 0 || logoHeight < 0){
                            break;
                        }
                    }
                    logoX_1 = rand.nextInt(0, width - logoWidth - 1);
                    logoY_1 = rand.nextInt(0, height - logoHeight - 1);
                    logoX_2 = logoX_1 + scale_x;
                    logoY_2 = logoY_1 + scale_y;
                }
            }

            for(int i = logoX_1; i <= logoX_2; i++){
                for(int j = logoY_1; j <= logoY_2; j++){
                    excludedCells.push(cells[i][j]);
                    // Ensure walls are up around boarder
                    if(i == logoX_1){
                        setWall(i, j, WEST, UP);
                    }
                    if(j == logoY_1){
                        setWall(i, j, NORTH,UP);
                    }
                    if(i == logoX_2){
                        setWall(i, j, EAST, UP);
                    }
                    if(j == logoY_2){
                        setWall(i, j, SOUTH, UP);
                    }
                    // clear right wall
                    if(i < logoX_2){
                        setWall(i, j, EAST,  DOWN);
                    }
                    // clear left wall
                    if(i > logoX_1){
                        setWall(i, j, WEST, DOWN);
                    }
                    // clear top wall
                    if(j > logoY_1){
                        setWall(i, j, NORTH, DOWN);
                    }
                    // clear bottom wall
                    if(j < logoY_2){
                        setWall(i, j, SOUTH, DOWN);
                    }
                }
            }
            logo_Ix = logoX_1;
            logo_Iy = logoY_1;
            logoExists = true;
        }
    }
    /**
     *  Implements: Wilson's algorithm method to construct maze. When constructing the maze cells that are a part of the
     *  start, finish or logo are excluded.
     *  Pre-condition: Maze has been initialised with start, finish and logo cells set.
     *  Post-condition: Solvable maze is produced in the form of a two-dimensional array.
     */
    private void generateMaze(){
        Random rand = new Random();
        Stack<Cell> cellStack = new Stack<>();  // Stack for storing cells that need to be added to maze
        Stack<Cell> randomWalk = new Stack<>(); // Stack for storing cells of random walk
        int totalCells = width * height;        // Total number of cells

        // Random starting locations
        int x_1 = rand.nextInt(width);
        int y_1 = rand.nextInt(height);
        if(!excludedCells.isEmpty()){
            while(excludedCells.contains(cells[x_1][y_1])){
                x_1 = rand.nextInt(width);
                y_1 = rand.nextInt(height);
            }
        }
        Cell startLocation = cells[x_1][y_1];
        cellStack.push(startLocation);

        int x_2 = rand.nextInt(width);
        int y_2 = rand.nextInt(height);
        if(!excludedCells.isEmpty()){
            while(excludedCells.contains(cells[x_1][y_1]) || ((x_1 == x_2) && (y_1 == y_2))){
                x_1 = rand.nextInt(width);
                y_1 = rand.nextInt(height);
            }
        }

        int addedCells = 1;                        // Counter for number of cells that have been added to the maze
        Cell currentCell = cells[x_2][y_2];        // Variable for holding the current cell
        ArrayList<Information> neighbourCellList = new ArrayList<>(); // Array list for holding neighbour cells
        Information info;
        boolean newWalk = false;

        while(addedCells < (totalCells - excludedCells.size())){
            if(newWalk){
                x_2 = rand.nextInt(width);
                y_2 = rand.nextInt(height);

                currentCell = cells[x_2][y_2];

                // Ensures new starting location is not in maze or excluded section
                while ((cellStack.search(currentCell) > -1) || (excludedCells.contains(currentCell))){
                    x_2 = rand.nextInt(width);
                    y_2 = rand.nextInt(height);
                    currentCell = cells[x_2][y_2];
                }
            }
            // Push the current cell to the random walk stack
            randomWalk.push(currentCell);
            // Reassign x_2 and y_2 values
            x_2 = currentCell.getX();
            y_2 = currentCell.getY();

            // Finding current cells neighbour cells
            neighbourCellList.clear(); // Clear List
            info = new Information(); // Clear neighbour variable
            // Neighbour cell above
            if(((y_2 - 1) >= 0) && !(excludedCells.contains(cells[x_2][y_2 - 1]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2;
                info.neighbour_y = y_2 - 1;
                info.current_wall = NORTH;
                info.neighbour_wall = SOUTH;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Neighbour cell bellow
            info = new Information(); // Clear neighbour variable
            if((y_2 + 1 < height) && !(excludedCells.contains(cells[x_2][y_2 + 1]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2;
                info.neighbour_y = y_2 + 1;
                info.current_wall = SOUTH;
                info.neighbour_wall = NORTH;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Neighbour cell left
            info = new Information(); // Clear neighbour variable
            if((x_2 - 1 >= 0) && !(excludedCells.contains(cells[x_2 - 1][y_2]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2 - 1;
                info.neighbour_y = y_2;
                info.current_wall = WEST;
                info.neighbour_wall = EAST;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Neighbour cell right
            info = new Information(); // Clear neighbour variable
            if((x_2 + 1 < width) && !(excludedCells.contains(cells[x_2 + 1][y_2]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2 + 1;
                info.neighbour_y = y_2;
                info.current_wall = EAST;
                info.neighbour_wall = WEST;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Find next cell to move to
            // Randomly choose a neighbor from the list
            int next = rand.nextInt(neighbourCellList.size());
            info = neighbourCellList.get(next);
            Cell nextStep = cells[info.neighbour_x][info.neighbour_y];

            // Test to see if next step is in current random walk
            if (randomWalk.search(nextStep) > -1){
                // Loop has occurred retrace steps until cross over part has been reached
                while(randomWalk.peek() != nextStep){
                    // Reset cells
                    Cell reset = randomWalk.peek();
                    cells[reset.getX()][reset.getY()] = new Cell(reset.getX(), reset.getY());

                    // Decrease number of added cells counter
                    addedCells--;

                    // remove loop cells from random walk
                    randomWalk.pop();
                }
                // Remove cross over cell from random walk
                randomWalk.pop();
                // Assign current cell the value of next step
                currentCell = nextStep;
                // set new random walk to false
                newWalk = false;
            }
            // Test to see if next step is already apart off maze
            else if(cellStack.search(nextStep) > - 1){
                // break wall between cells
                cells[info.current_x][info.current_y].setWall(info.current_wall, DOWN);
                cells[info.neighbour_x][info.neighbour_y].setWall(info.neighbour_wall, DOWN);

                // add all cells from random Walk stack to cell stack
                while(!randomWalk.isEmpty()){
                    cellStack.push(randomWalk.peek());
                    randomWalk.pop();
                }
                // set new random walk to true
                newWalk = true;
                // increment added cells
                addedCells++;
            }
            // Next step is not in random walk or apart off maze
            else{
                // Break down wall between current cell and next step
                cells[info.current_x][info.current_y].setWall(info.current_wall, DOWN);
                cells[info.neighbour_x][info.neighbour_y].setWall(info.neighbour_wall, DOWN);

                // Assign current cell the value of next step
                currentCell = nextStep;

                // set new random walk to false
                newWalk = false;

                //increment added cell
                addedCells++;
            }
        }
    }
    /**
     * Implements: Sets entry and exit point into maze by removing the appropriate walls.
     * Pre-condition: Maze has been fully generated.
     * Post-condition: Maze's entry and exit cells have had their appropriate walls removed.
     */
    private void entryAndExit(){
        Random rand = new Random();
        // Break start and exit walls
        if(startHeight == 0 && startWidth == 0){
            if(start_x == 0){
                cells[start_x][start_y].setWall(WEST, DOWN);
                cells[start_x][start_y].solution[3] = 1;
                cells[start_x][start_y].entry[3] = 1;
            }
            else if(start_x == width - 1){
                cells[start_x][start_y].setWall(EAST, DOWN);
                cells[start_x][start_y].solution[1] = 1;
                cells[start_x][start_y].entry[1] = 1;
            }
            else if(start_y == 0){
                cells[start_x][start_y].setWall(NORTH, DOWN);
                cells[start_x][start_y].solution[0] = 1;
                cells[start_x][start_y].entry[0] = 1;
            }
            else{
                cells[start_x][start_y].setWall(SOUTH, DOWN);
                cells[start_x][start_y].solution[2] = 1;
                cells[start_x][start_y].entry[2] = 1;
            }
        }
        else{
            int entryX;
            int entryY;
            if(start_x == 0){
                entryX = start_x + startWidth;
                entryY = rand.nextInt(start_y, start_y + startHeight);
                setWall(entryX, entryY, WEST, DOWN);
                setWall(entryX - 1, entryY, EAST, DOWN);
                cells[entryX][entryY].solution[3] = 1;
                cells[entryX][entryY].entry[3] = 1;
                start_x = entryX;
                start_y = entryY;
            }
            else if(start_x == width - startWidth- 1){
                entryX = start_x;
                entryY = rand.nextInt(start_y, start_y + startHeight);
                setWall(entryX, entryY, EAST, DOWN);
                setWall(entryX + 1, entryY, WEST, DOWN);
                cells[entryX][entryY].solution[1] = 1;
                cells[entryX][entryY].entry[1] = 1;
                start_x = entryX;
                start_y = entryY;
            }
            else{
                entryY = rand.nextBoolean() ? start_y: start_y + startHeight;
                entryX = rand.nextBoolean() ? start_x: start_x + startWidth;
                if(entryX == start_x){
                    setWall(entryX, entryY, WEST, DOWN);
                    setWall(entryX - 1, entryY, EAST, DOWN);
                    cells[entryX][entryY].solution[3] = 1;
                    cells[entryX][entryY].entry[3] = 1;
                }
                else{
                    setWall(entryX, entryY, EAST, DOWN);
                    setWall(entryX + 1, entryY, WEST, DOWN);
                    cells[entryX][entryY].solution[1] = 1;
                    cells[entryX][entryY].entry[1] = 1;
                }
                start_x = entryX;
                start_y = entryY;
            }
        }
        if(finishHeight == 0 && finishWidth == 0){
            // Exit
            if(finish_x == 0){
                setWall(finish_x, finish_y, WEST, DOWN);
                cells[finish_x][finish_y].solution[3] = 1;
                cells[finish_x][finish_y].exit[3] = 1;
            }
            else if(finish_x == width - 1){
                setWall(finish_x, finish_y, EAST, DOWN);
                cells[finish_x][finish_y].solution[1] = 1;
                cells[finish_x][finish_y].exit[1] = 1;
            }
            else if(finish_y == 0){
                setWall(finish_x, finish_y, NORTH, DOWN);
                cells[finish_x][finish_y].solution[0] = 1;
                cells[finish_x][finish_y].exit[0] = 1;
            }
            else{
                setWall(finish_x, finish_y, SOUTH, DOWN);
                cells[finish_x][finish_y].solution[2] = 1;
                cells[finish_x][finish_y].exit[2] = 1;
            }
        }
        else{
            int exitX;
            int exitY;
            // randomly select start x value
            if(finish_x == 0){
                exitX = finish_x + finishWidth;
                exitY = rand.nextInt(finish_y, finish_y + finishHeight);
                setWall(exitX, exitY, EAST, DOWN);
                setWall(exitX + 1, exitY, WEST, DOWN);
                cells[exitX][exitY].solution[1] = 1;
                cells[exitX][exitY].exit[1] = 1;
                finish_y = exitY;
                finish_x = exitX;
            }
            else if(finish_x == width - finishWidth- 1){
                exitX = finish_x;
                exitY = rand.nextInt(finish_y, finish_y + finishHeight);
                setWall(exitX, exitY, WEST, DOWN);
                setWall(exitX - 1, exitY, EAST, DOWN);
                cells[exitX][exitY].solution[3] = 1;
                cells[exitX][exitY].exit[3] = 1;
                finish_y = exitY;
                finish_x = exitX;
            }
            else{
                exitY = rand.nextBoolean() ? finish_y: finish_y + finishHeight;
                exitX = rand.nextBoolean() ? finish_x: finish_x + finishWidth;
                if(exitX == finish_x){
                    setWall(exitX, exitY, WEST, DOWN);
                    setWall(exitX - 1, exitY, EAST, DOWN);
                    cells[exitX][exitY].solution[3] = 1;
                    cells[exitX][exitY].exit[3] = 1;
                }
                else{
                    setWall(exitX, exitY, EAST, DOWN);
                    setWall(exitX + 1, exitY, WEST, DOWN);
                    cells[exitX][exitY].solution[1] = 1;
                    cells[exitX][exitY].exit[1] = 1;
                }
                finish_y = exitY;
                finish_x = exitX;
            }
        }
        excludedCells.push(cells[start_x][start_y]);
        excludedCells.push(cells[finish_x][finish_y]);
    }
    /**
     * Implements: Updates start and end cells in the maze.
     * Pre-condition: Specified new start and exit cells have been selected and a fully generated maze have been constructed.
     * Post-condition: Updates start and end cells if they are different cells.
     * @param start string specifying the new starting location.
     * @param exit string specifying the new ending location.
     * @return Boolean value returns true if start and exit cells have been updated, false otherwise because specified start
     * and exit position are the same.
     */
    public Boolean entryAndExit(String start, String exit){
        Random rand = new Random();
        int w_s = 0;
        int w_f = 0;
        int start_new_x = -1;
        int start_new_y = -1;
        int finish_new_x = -1;
        int finish_new_y = -1;
        // Change starting location images
        // Set start position on maze
        switch (start) {
            case "Unchanged" ->{
                start_new_x = getStart_x();
                start_new_y = getStart_y();
            }
            case "Top Left" -> {
                start_new_x = 0;
                start_new_y = 0;
                setWall(start_new_x, start_new_y, WEST, DOWN);
                cells[start_new_x][start_new_y].solution[3] = 1;
                cells[start_new_x][start_new_y].entry[3] = 1;
            }
            case "Top Right" -> {
                start_new_x = width - 1;
                start_new_y = 0;
                setWall(start_new_x, start_new_y, EAST, DOWN);
                cells[start_new_x][start_new_y].solution[1] = 1;
                cells[start_new_x][start_new_y].entry[1] = 1;
            }
            case "Bottom Left" -> {
                start_new_x = 0;
                start_new_y = height - 1;
                setWall(start_new_x, start_new_y, WEST, DOWN);
                cells[start_new_x][start_new_y].solution[3] = 1;
                cells[start_new_x][start_new_y].entry[3] = 1;
            }
            case "Bottom Right" -> {
                start_new_x = width - 1;
                start_new_y = height - 1;
                setWall(start_new_x, start_new_y, EAST, DOWN);
                cells[start_new_x][start_new_y].solution[1] = 1;
                cells[start_new_x][start_new_y].entry[1] = 1;
            }
            case "Top Middle" -> {
                start_new_x = Math.abs(width / 2);
                start_new_y = 0;
                setWall(start_new_x, start_new_y, NORTH, DOWN);
                cells[start_new_x][start_new_y].solution[0] = 1;
                cells[start_new_x][start_new_y].entry[0] = 1;
            }
            case "Bottom Middle" -> {
                start_new_x = Math.abs(width / 2);
                start_new_y = height - 1;
                setWall(start_new_x, start_new_y, SOUTH, DOWN);
                cells[start_new_x][start_new_y].solution[2] = 1;
                cells[start_new_x][start_new_y].entry[2] = 1;
            }
            case "Middle Right" -> {
                start_new_x = width - 1;
                start_new_y = Math.abs(height / 2);
                setWall(start_new_x, start_new_y, EAST, DOWN);
                cells[start_new_x][start_new_y].solution[1] = 1;
                cells[start_new_x][start_new_y].entry[1] = 1;
            }
            case "Middle Left" -> {
                start_new_x = 0;
                start_new_y = Math.abs(height / 2);
                setWall(start_new_x, start_new_y, WEST, DOWN);
                cells[start_new_x][start_new_y].solution[3] = 1;
                cells[start_new_x][start_new_y].entry[3] = 1;
            }
            case "Upward" -> {
                if(start_new_y == start_Iy){
                    // unable to move entry any higher
                    setWall(start_new_x, start_new_y, Direction.getDirection(w_s), DOWN);
                    cells[start_new_x][start_new_y].solution[w_s] = 1;
                    cells[start_new_x][start_new_y].entry[w_s] = 1;
                }
                else{
                    // set y
                    if(start_new_y - 1 == start_Iy){
                        start_new_y = start_Iy;
                    }
                    else{
                        start_new_y = rand.nextInt(start_new_y-1,start_Iy);
                    }
                    //set x
                    if(start_Ix == 0){
                        start_new_x = start_Ix + startWidth;
                        setWall(start_new_x, start_new_y, EAST, DOWN);
                        cells[start_new_x][start_new_y].solution[1] = 1;
                        cells[start_new_x][start_new_y].entry[1] = 1;
                        setWall(start_new_x + 1, start_new_y, WEST, DOWN);
                    }
                    else if(start_Ix == width - startWidth - 1){
                        start_new_x = start_Ix;
                        setWall(start_new_x, start_new_y, WEST, DOWN);
                        cells[start_new_x][start_new_y].solution[3] = 1;
                        cells[start_new_x][start_new_y].entry[3] = 1;
                        setWall(start_new_x - 1, start_new_y, EAST, DOWN);
                    }
                    else{
                        start_new_x = rand.nextBoolean() ? start_Ix : start_Ix+startWidth;
                        if(start_new_x == start_Ix){
                            setWall(start_new_x, start_new_y, WEST, DOWN);
                            cells[start_new_x][start_new_y].solution[3] = 1;
                            cells[start_new_x][start_new_y].entry[3] = 1;
                            setWall(start_new_x - 1, start_new_y, EAST, DOWN);
                        }
                        else{
                            setWall(start_new_x, start_new_y, EAST, DOWN);
                            cells[start_new_x][start_new_y].solution[1] = 1;
                            cells[start_new_x][start_new_y].entry[1] = 1;
                            setWall(start_new_x + 1, start_new_y, WEST, DOWN);
                        }
                    }
                }
            }
            case "Downward" -> {
                if(start_new_y == start_Iy + startHeight){
                    // unable to move entry any higher
                    setWall(start_new_x, start_new_y, Direction.getDirection(w_s), DOWN);
                    cells[start_new_x][start_new_y].solution[w_s] = 1;
                    cells[start_new_x][start_new_y].entry[w_s] = 1;
                }
                else{
                    // set y
                    if(start_new_y + 1 == start_Iy+ startHeight){
                        start_new_y = start_Iy + startHeight;
                    }
                    else{
                        start_new_y = rand.nextInt(start_new_y + 1,start_Iy + startHeight);
                    }
                    //set x
                    if(start_Ix == 0){
                        start_new_x = start_Ix + startWidth;
                        setWall(start_new_x, start_new_y, EAST, DOWN);
                        cells[start_new_x][start_new_y].solution[1] = 1;
                        cells[start_new_x][start_new_y].entry[1] = 1;
                        setWall(start_new_x + 1, start_new_y, WEST, DOWN);
                    }
                    else if(start_Ix == width - startWidth - 1){
                        start_new_x = start_Ix;
                        setWall(start_new_x, start_new_y, WEST, DOWN);
                        cells[start_new_x][start_new_y].solution[3] = 1;
                        cells[start_new_x][start_new_y].entry[3] = 1;
                        setWall(start_new_x - 1, start_new_y, EAST, DOWN);
                    }
                    else{
                        start_new_x = rand.nextBoolean() ? start_Ix : start_Ix + startWidth;
                        if(start_new_x == start_Ix){
                            setWall(start_new_x, start_new_y, WEST, DOWN);
                            cells[start_new_x][start_new_y].solution[3] = 1;
                            cells[start_new_x][start_new_y].entry[3] = 1;
                            setWall(start_new_x - 1, start_new_y, EAST, DOWN);
                        }
                        else{
                            setWall(start_new_x, start_new_y, EAST, DOWN);
                            cells[start_new_x][start_new_y].solution[1] = 1;
                            cells[start_new_x][start_new_y].entry[1] = 1;
                            setWall(start_new_x + 1, start_new_y, WEST, DOWN);
                        }
                    }
                }
            }
            case "Leftward" -> {
                if(start_new_x == start_Ix){
                    // unable to move entry any higher
                    setWall(start_new_x, start_new_y, Direction.getDirection(w_s), DOWN);
                    cells[start_new_x][start_new_y].solution[w_s] = 1;
                    cells[start_new_x][start_new_y].entry[w_s] = 1;
                }
                else{
                    // set x
                    if(start_new_x - 1 == start_Ix){
                        start_new_x = start_Ix;
                    }
                    else{
                        start_new_x = rand.nextInt(start_Ix,start_new_x - 1);
                    }
                    //set y
                    if(start_Iy == 0){
                        start_new_y = start_Iy + startHeight;
                        setWall(start_new_x, start_new_y, SOUTH, DOWN);
                        cells[start_new_x][start_new_y].solution[2] = 1;
                        cells[start_new_x][start_new_y].entry[2] = 1;
                        setWall(start_new_x, start_new_y + 1, NORTH, DOWN);
                    }
                    else if(start_Iy == height - startHeight - 1){
                        start_new_y = start_Iy;
                        setWall(start_new_x, start_new_y, NORTH, DOWN);
                        cells[start_new_x][start_new_y].solution[0] = 1;
                        cells[start_new_x][start_new_y].entry[0] = 1;
                        setWall(start_new_x, start_new_y - 1,SOUTH, DOWN);
                    }
                    else{
                        start_new_y = rand.nextBoolean() ? start_Iy : start_Iy + startHeight;
                        if(start_new_y == start_Iy){
                            setWall(start_new_x, start_new_y, NORTH, DOWN);
                            cells[start_new_x][start_new_y].solution[0] = 1;
                            cells[start_new_x][start_new_y].entry[0] = 1;
                            setWall(start_new_x, start_new_y - 1,SOUTH, DOWN);
                        }
                        else{
                            setWall(start_new_x, start_new_y, SOUTH, DOWN);
                            cells[start_new_x][start_new_y].solution[2] = 1;
                            cells[start_new_x][start_new_y].entry[2] = 1;
                            setWall(start_new_x, start_new_y + 1, NORTH, DOWN);
                        }
                    }
                }
            }
            case "Rightward" -> {
                if(start_new_x == start_Ix + startWidth){
                    // unable to move entry any higher
                    setWall(start_new_x, start_new_y, Direction.getDirection(w_s), DOWN);
                    cells[start_new_x][start_new_y].solution[w_s] = 1;
                    cells[start_new_x][start_new_y].entry[w_s] = 1;
                }
                else{
                    // set x
                    if(start_new_x + 1 == start_Ix + startWidth){
                        start_new_x = start_Ix + startWidth;
                    }
                    else{
                        start_new_x = rand.nextInt(start_new_x + 1,start_Ix + startWidth);
                    }
                    //set y
                    if(start_Iy == 0){
                        start_new_y = start_Iy + startHeight;
                        setWall(start_new_x, start_new_y, SOUTH, DOWN);
                        cells[start_new_x][start_new_y].solution[2] = 1;
                        cells[start_new_x][start_new_y].entry[2] = 1;
                        setWall(start_new_x, start_new_y + 1, NORTH, DOWN);
                    }
                    else if(start_Iy == height - startHeight - 1){
                        start_new_y = start_Iy;
                        setWall(start_new_x, start_new_y, NORTH, DOWN);
                        cells[start_new_x][start_new_y].solution[0] = 1;
                        cells[start_new_x][start_new_y].entry[0] = 1;
                        setWall(start_new_x, start_new_y - 1,SOUTH, DOWN);
                    }
                    else{
                        start_new_y = rand.nextBoolean() ? start_Iy : start_Iy + startHeight;
                        if(start_new_x == start_Iy){
                            setWall(start_new_x, start_new_y, NORTH, DOWN);
                            cells[start_new_x][start_new_y].solution[0] = 1;
                            cells[start_new_x][start_new_y].entry[0] = 1;
                            setWall(start_new_x, start_new_y - 1,SOUTH, DOWN);
                        }
                        else{
                            setWall(start_new_x, start_new_y, SOUTH, DOWN);
                            cells[start_new_x][start_new_y].solution[2] = 1;
                            cells[start_new_x][start_new_y].entry[2] = 1;
                            setWall(start_new_x, start_new_y + 1, NORTH, DOWN);
                        }
                    }
                }
            }
        }
        // Set finish position of maze
        switch (exit) {
            case "Unchanged" ->{
                finish_new_x = getFinish_x();
                finish_new_y = getFinish_y();
            }
            case "Top Left" -> {
                finish_new_x = 0;
                finish_new_y = 0;
                setWall(finish_new_x, finish_new_y, WEST, DOWN);
                cells[finish_new_x][finish_new_y].solution[3] = 1;
                cells[finish_new_x][finish_new_y].exit[3] = 1;
            }
            case "Top Right" -> {
                finish_new_x = width - 1;
                finish_new_y = 0;
                setWall(finish_new_x, finish_new_y, EAST, DOWN);
                cells[finish_new_x][finish_new_y].solution[1] = 1;
                cells[finish_new_x][finish_new_y].exit[1] = 1;
            }
            case "Bottom Left" -> {
                finish_new_x = 0;
                finish_new_y = height - 1;
                setWall(finish_new_x, finish_new_y, WEST, DOWN);
                cells[finish_new_x][finish_new_y].solution[3] = 1;
                cells[finish_new_x][finish_new_y].exit[3] = 1;
            }
            case "Bottom Right" -> {
                finish_new_x = width - 1;
                finish_new_y = height - 1;
                setWall(finish_new_x, finish_new_y, EAST, DOWN);
                cells[finish_new_x][finish_new_y].solution[1] = 1;
                cells[finish_new_x][finish_new_y].exit[1] = 1;
            }
            case "Top Middle" -> {
                finish_new_x = Math.abs(width / 2);
                finish_new_y = 0;
                setWall(finish_new_x, finish_new_y, NORTH, DOWN);
                cells[finish_new_x][finish_new_y].solution[0] = 1;
                cells[finish_new_x][finish_new_y].exit[0] = 1;
            }
            case "Bottom Middle" -> {
                finish_new_x = Math.abs(width / 2);
                finish_new_y = height - 1;
                setWall(finish_new_x, finish_new_y, SOUTH, DOWN);
                cells[finish_new_x][finish_new_y].solution[2] = 1;
                cells[finish_new_x][finish_new_y].exit[2] = 1;
            }
            case "Middle Right" -> {
                finish_new_x = width - 1;
                finish_new_y = Math.abs(height / 2);
                setWall(finish_new_x, finish_new_y, EAST, DOWN);
                cells[finish_new_x][finish_new_y].solution[1] = 1;
                cells[finish_new_x][finish_new_y].exit[1] = 1;
            }
            case "Middle Left" -> {
                finish_new_x = 0;
                finish_new_y = Math.abs(height / 2);
                setWall(finish_new_x, finish_new_y, WEST, DOWN);
                cells[finish_new_x][finish_new_y].solution[3] = 1;
                cells[finish_new_x][finish_new_y].exit[3] = 1;
            }
            case "Upward" -> {
                if(finish_new_y == finish_Iy){
                    // unable to move entry any higher
                    setWall(finish_new_x, finish_new_y, Direction.getDirection(w_f), DOWN);
                    cells[finish_new_x][finish_new_y].solution[w_f] = 1;
                    cells[finish_new_x][finish_new_y].exit[w_f] = 1;
                }
                else{
                    // set y
                    if(finish_new_y - 1 == finish_Iy){
                        finish_new_y = finish_Iy;
                    }
                    else{
                        finish_new_y = rand.nextInt(finish_new_y-1,finish_Iy);
                    }
                    //set x
                    if(finish_Ix == 0){
                        finish_new_y = finish_Ix + finishWidth;
                        setWall(finish_new_x, finish_new_y, EAST, DOWN);
                        cells[finish_new_x][finish_new_y].solution[1] = 1;
                        cells[finish_new_x][finish_new_y].exit[1] = 1;
                        setWall(finish_new_x + 1, finish_new_y, WEST, DOWN);
                    }
                    else if(finish_Ix == width - finishWidth - 1){
                        finish_new_y = finish_Ix;
                        setWall(finish_new_x, finish_new_y, WEST, DOWN);
                        cells[finish_new_x][finish_new_y].solution[3] = 1;
                        cells[finish_new_x][finish_new_y].exit[3] = 1;
                        setWall(finish_new_x - 1, finish_new_y, EAST, DOWN);
                    }
                    else{
                        finish_new_x = rand.nextBoolean() ? finish_Ix : finish_Ix+finishWidth;
                        if(finish_new_x == finish_Ix){
                            setWall(finish_new_x, finish_new_y, WEST, DOWN);
                            cells[finish_new_x][finish_new_y].solution[3] = 1;
                            cells[finish_new_x][finish_new_y].exit[3] = 1;
                            setWall(finish_new_x - 1, finish_new_y, EAST, DOWN);
                        }
                        else{
                            setWall(finish_new_x, finish_new_y, EAST, DOWN);
                            cells[finish_new_x][finish_new_y].solution[1] = 1;
                            cells[finish_new_x][finish_new_y].exit[1] = 1;
                            setWall(finish_new_x + 1, finish_new_y, WEST, DOWN);
                        }
                    }
                }
            }
            case "Downward" -> {
                if(finish_new_y == finish_Iy + finishHeight){
                    // unable to move entry any higher
                    setWall(finish_new_x, finish_new_y, Direction.getDirection(w_f), DOWN);
                    cells[finish_new_x][finish_new_y].solution[w_f] = 1;
                    cells[finish_new_x][finish_new_y].exit[w_f] = 1;
                }
                else{
                    // set y
                    if(finish_new_y + 1 == finish_Iy+ finishHeight){
                        finish_new_y = finish_Iy + startHeight;
                    }
                    else{
                        finish_new_y = rand.nextInt(finish_new_y + 1,finish_Iy + finishHeight);
                    }
                    //set x
                    if(finish_Ix == 0){
                        finish_new_x = finish_Ix + finishWidth;
                        setWall(finish_new_x, finish_new_y, EAST, DOWN);
                        cells[finish_new_x][finish_new_y].solution[1] = 1;
                        cells[finish_new_x][finish_new_y].exit[1] = 1;
                        setWall(finish_new_x + 1, finish_new_y, WEST, DOWN);
                    }
                    else if(finish_Ix == width - finishWidth - 1){
                        finish_new_x = finish_Ix;
                        setWall(finish_new_x, finish_new_y, WEST, DOWN);
                        cells[finish_new_x][finish_new_y].solution[3] = 1;
                        cells[finish_new_x][finish_new_y].exit[3] = 1;
                        setWall(finish_new_x - 1, finish_new_y, EAST, DOWN);
                    }
                    else{
                        finish_new_x = rand.nextBoolean() ? finish_Ix : finish_Ix + finishWidth;
                        if(finish_new_x == finish_Ix){
                            setWall(finish_new_x, finish_new_y, WEST, DOWN);
                            cells[finish_new_x][finish_new_y].solution[3] = 1;
                            cells[finish_new_x][finish_new_y].exit[3] = 1;
                            setWall(finish_new_x - 1, finish_new_y, EAST, DOWN);
                        }
                        else{
                            setWall(finish_new_x, finish_new_y, EAST, DOWN);
                            cells[finish_new_x][finish_new_y].solution[1] = 1;
                            cells[finish_new_x][finish_new_y].exit[1] = 1;
                            setWall(finish_new_x + 1, finish_new_y, WEST, DOWN);
                        }
                    }
                }
            }
            case "Leftward" -> {
                if(finish_new_x == finish_Ix){
                    // unable to move entry any higher
                    setWall(finish_new_x, finish_new_y, Direction.getDirection(w_f), DOWN);
                    cells[finish_new_x][finish_new_y].solution[w_f] = 1;
                    cells[finish_new_x][finish_new_y].exit[w_f] = 1;
                }
                else{
                    // set x
                    if(finish_new_x - 1 == finish_Ix){
                        finish_new_x = finish_Ix;
                    }
                    else{
                        finish_new_x = rand.nextInt(finish_Ix,finish_new_x - 1);
                    }
                    //set y
                    if(finish_Iy == 0){
                        finish_new_y = finish_Iy + finishHeight;
                        setWall(finish_new_x, finish_new_y, SOUTH, DOWN);
                        cells[finish_new_x][finish_new_y].solution[2] = 1;
                        cells[finish_new_x][finish_new_y].exit[2] = 1;
                        setWall(finish_new_x, finish_new_y + 1, NORTH, DOWN);
                    }
                    else if(finish_Iy == height - finishHeight - 1){
                        finish_new_y = finish_Iy;
                        setWall(finish_new_x, finish_new_y, NORTH, DOWN);
                        cells[finish_new_x][finish_new_y].solution[0] = 1;
                        cells[finish_new_x][finish_new_y].exit[0] = 1;
                        setWall(finish_new_x, finish_new_y - 1, SOUTH, DOWN);
                    }
                    else{
                        finish_new_y = rand.nextBoolean() ? finish_Iy : finish_Iy + finishHeight;
                        if(finish_new_y == finish_Iy){
                            setWall(finish_new_x, finish_new_y, NORTH, DOWN);
                            cells[finish_new_x][finish_new_y].solution[0] = 1;
                            cells[finish_new_x][finish_new_y].exit[0] = 1;
                            setWall(finish_new_x, finish_new_y - 1, SOUTH, DOWN);
                        }
                        else{
                            setWall(finish_new_x, finish_new_y, SOUTH, DOWN);
                            cells[finish_new_x][finish_new_y].solution[2] = 1;
                            cells[finish_new_x][finish_new_y].exit[2] = 1;
                            setWall(finish_new_x, finish_new_y + 1, NORTH, DOWN);
                        }
                    }
                }
            }
            case "Rightward" -> {
                if(finish_new_x == finish_Ix + finishWidth){
                    // unable to move entry any higher
                    setWall(finish_new_x, finish_new_y, Direction.getDirection(w_f), DOWN);
                    cells[finish_new_x][finish_new_y].solution[w_f] = 1;
                    cells[finish_new_x][finish_new_y].exit[w_f] = 1;
                }
                else{
                    // set x
                    if(finish_new_x + 1 == finish_Ix + finishWidth){
                        finish_new_x = finish_Ix + finishWidth;
                    }
                    else{
                        finish_new_x = rand.nextInt(finish_new_x + 1,finish_Ix + finishWidth);
                    }
                    //set y
                    if(finish_Iy == 0){
                        finish_new_y = finish_Iy + finishHeight;
                        setWall(finish_new_x, finish_new_y, SOUTH, DOWN);
                        cells[finish_new_x][finish_new_y].solution[2] = 1;
                        cells[finish_new_x][finish_new_y].exit[2] = 1;
                        setWall(finish_new_x, finish_new_y + 1, NORTH, DOWN);
                    }
                    else if(finish_Iy == height - finishHeight - 1){
                        finish_new_y = finish_Iy;
                        setWall(finish_new_x, finish_new_y, NORTH, DOWN);
                        cells[finish_new_x][finish_new_y].solution[0] = 1;
                        cells[finish_new_x][finish_new_y].exit[0] = 1;
                        setWall(finish_new_x, finish_new_y - 1, SOUTH, DOWN);
                    }
                    else{
                        finish_new_y = rand.nextBoolean() ? finish_Iy : finish_Iy + finishHeight;
                        if(finish_new_y == finish_Iy){
                            setWall(finish_new_x, finish_new_y, NORTH, DOWN);
                            cells[finish_new_x][finish_new_y].solution[0] = 1;
                            cells[finish_new_x][finish_new_y].exit[0] = 1;
                            setWall(finish_new_x, finish_new_y - 1, SOUTH, DOWN);
                        }
                        else{
                            setWall(finish_new_x, finish_new_y, SOUTH, DOWN);
                            cells[finish_new_x][finish_new_y].solution[2] = 1;
                            cells[finish_new_x][finish_new_y].exit[2] = 1;
                            setWall(finish_new_x, finish_new_y + 1, NORTH, DOWN);
                        }
                    }
                }
            }
        }
        // Determine if start and finish points are valid
        if(validate(start_x,start_y,finish_x, finish_y)){
            // Reset old entry points default
            if(validate(start_x,start_y,start_new_x,start_new_y)){
                for(int i = 0; i < 4; i++){
                    if(cells[start_x][start_y].entry[i] == 1){
                        w_s = i;
                    }
                }
                cells[start_x][start_y].entry[w_s] = 0;
                setWall(start_x, start_y, Direction.getDirection(w_s), UP);
                cells[start_x][start_y].solution[w_s] = 0;
            }
            if(validate(finish_x,finish_y,finish_new_x,finish_new_y)){
                for(int i = 0; i < 4; i++){
                    if(cells[finish_x][finish_y].exit[i] == 1){
                        w_f = i;
                    }
                }
                cells[finish_x][finish_y].exit[w_f] = 0;
                setWall(finish_x, finish_y, Direction.getDirection(w_f), UP);
                cells[finish_x][finish_y].solution[w_f] = 0;
            }
            start_x = start_new_x;
            start_y = start_new_y;
            finish_x = finish_new_x;
            finish_y = finish_new_y;
            return true;
        }
        else{
            // Reset new entry points
            for(int i = 0; i < 4; i++){
                if(cells[start_x][start_y].entry[i] == 1){
                    w_s = i;

                }
            }
            cells[start_x][start_y].entry[w_s] = 0;
            setWall(start_x, start_y, Direction.getDirection(w_s), UP);
            cells[start_x][start_y].solution[w_s] = 0;

            for(int i = 0; i < 4; i++){
                if(cells[finish_x][finish_y].exit[i] == 1){
                    w_f = i;
                }
            }
            cells[finish_x][finish_y].exit[w_f] = 0;
            setWall(finish_x, finish_y, Direction.getDirection(w_f), UP);
            cells[finish_x][finish_y].solution[w_f] = 0;

            return false;
        }
    }
    /**
     * Implements: Recursive back tracking is used starting from the start cell to find the optimal path to the exit cell.
     * Pre-condition: Maze has been generated with entry and exit cells set.
     * Post-condition: Maze solution is found and method records directions cells move from one another.
     */
    private Stack<Cell> solve(){
        Random rand = new Random();
        mazeSolution.clear();
        Cell pointer = cells[start_x][start_y];
        Cell finish = cells[finish_x][finish_y];

        Stack<Cell> visitedCells = new Stack<>(); // Stack for holding the cells visited
        mazeSolution.push(cells[start_x][start_y]);
        visitedCells.push(cells[start_x][start_y]);

        boolean solution = false;
        ArrayList<Information> neighbourCellList = new ArrayList<>(); // Array list for holding neighbour cells
        Information info;
        // Keep performing until solution is complete
        while (!solution){
            int x_2 = pointer.getX();
            int y_2 = pointer.getY();

            // decide which direction to move
            // Finding current cells neighbour without walls between cells
            neighbourCellList.clear(); // Clear List
            info = new Information(); // Clear neighbour variable
            // Neighbour cell above
            if(((y_2 - 1) >= 0) && (getWall(x_2, y_2, NORTH) == DOWN) && (getWall(x_2, y_2 - 1, SOUTH) == DOWN)
                    && !(visitedCells.contains(cells[x_2][y_2 - 1]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2;
                info.neighbour_y = y_2 - 1;
                info.current_wall = NORTH;
                info.neighbour_wall = SOUTH;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Neighbour cell bellow
            info = new Information(); // Clear neighbour variable
            if(((y_2 + 1) < height) && (cells[x_2][y_2].getWall(SOUTH) == DOWN) && (cells[x_2][y_2 + 1].getWall(NORTH) == DOWN)
                    && !(visitedCells.contains(cells[x_2][y_2 + 1]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2;
                info.neighbour_y = y_2 + 1;
                info.current_wall = SOUTH;
                info.neighbour_wall = NORTH;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Neighbour cell left
            info = new Information(); // Clear neighbour variable
            if(((x_2 - 1) >= 0) && (cells[x_2][y_2].getWall(WEST) == DOWN) && (cells[x_2 - 1][y_2].getWall(EAST) == DOWN)
                    && !(visitedCells.contains(cells[x_2 - 1][y_2]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2 - 1;
                info.neighbour_y = y_2;
                info.current_wall = WEST;
                info.neighbour_wall = EAST;
                neighbourCellList.add(info); // Add the neighbor to list
            }
            // Neighbour cell right
            info = new Information(); // Clear neighbour variable
            if(((x_2 + 1) < width) && (cells[x_2][y_2].getWall(EAST) == DOWN) && (cells[x_2 + 1][y_2].getWall(WEST) == DOWN)
                    && !(visitedCells.contains(cells[x_2 + 1][y_2]))){
                info.current_x = x_2;
                info.current_y = y_2;
                info.neighbour_x = x_2 + 1;
                info.neighbour_y = y_2;
                info.current_wall = EAST;
                info.neighbour_wall = WEST;
                neighbourCellList.add(info); // Add the neighbor to list
            }

            // Ensure cell has neighbour cells to move to
            if(!neighbourCellList.isEmpty()){
                // Find next cell to move to
                // Randomly choose a neighbor from the list
                int next = rand.nextInt(neighbourCellList.size());
                info = neighbourCellList.get(next);
                Cell pathStep = cells[info.neighbour_x][info.neighbour_y];

                // Add cell to visitedCells
                visitedCells.push(pathStep);
                // Add cell to mazeSolution
                mazeSolution.push(pathStep);

                if(x_2 == info.neighbour_x){
                    // Up
                    if(info.neighbour_y < y_2){
                        cells[x_2][y_2].solution[0] = 1;
                        cells[info.neighbour_x][info.neighbour_y].solution[2] = 1;
                    }
                    // Down
                    else{
                        cells[x_2][y_2].solution[2] = 1;
                        cells[info.neighbour_x][info.neighbour_y].solution[0] = 1;
                    }
                }
                if(y_2 == info.neighbour_y){
                    // Right
                    if(info.neighbour_x > x_2){
                        cells[x_2][y_2].solution[1] = 1;
                        cells[info.neighbour_x][info.neighbour_y].solution[3] = 1;
                    }
                    // Left
                    else{
                        cells[x_2][y_2].solution[3] = 1;
                        cells[info.neighbour_x][info.neighbour_y].solution[1] = 1;
                    }
                }

                if (pathStep == finish){
                    if(!(x_2 == 0 || x_2 == width -1)){
                        if(finish.getX() == 0 || finish.getX() == width - 1){
                            cells[finish.getX()][finish.getY()].solution[2] = 0;
                        }
                    }
                    solution = true;
                }
                pointer = pathStep;
            }
            else
            {
                cells[x_2][y_2].solution[0]= 0;
                cells[x_2][y_2].solution[1]= 0;
                cells[x_2][y_2].solution[2]= 0;
                cells[x_2][y_2].solution[3]= 0;

                mazeSolution.pop();
                pointer = mazeSolution.peek();

                if (x_2 == pointer.getX()){
                    // up
                    if (y_2 < pointer.getY()){
                        cells[pointer.getX()][pointer.getY()].solution[0] = 0;
                    }
                    // Down
                    else{
                        cells[pointer.getX()][pointer.getY()].solution[2] = 0;
                    }
                }
                if (y_2 == pointer.getY()){
                    // Right
                    if (x_2 > pointer.getX()){
                        cells[pointer.getX()][pointer.getY()].solution[1] = 0;
                    }
                    // Left
                    else{
                        cells[pointer.getX()][pointer.getY()].solution[3] = 0;
                    }
                }
            }
        }
        setSolutionNumber(mazeSolution.size());
        return mazeSolution;
    }
    /**
     * Method which finds the solution to the maze using recursive backtracking.
     * Method records directions cells move from one another.
     * Implements: Method is used when entry and exit positions have been updated. Method resets old solution and uses
     * private method solve to find the new solution.
     * Pre-condition: Maze entry and exit cells have been updated.
     * Post-condition: New solution to the maze has been found and method records directions cells move from one another.
     */
    public void solver(){
        // Clear old solution
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                cells[i][j].solution[0] = 0;
                cells[i][j].solution[1] = 0;
                cells[i][j].solution[2] = 0;
                cells[i][j].solution[3] = 0;
            }
        }
        setCoordinates();
        solve();
    }
    /**
     * Implements: Calculates the percentage of cells used in solution.
     * Pre-condition: Maze solution has been found and cells required for solution have been added to a stack.
     * Post-condition: Percentage of cells required for solution have been calculated.
     * @return the percentage of cells used in solution as an int.
     */
    public int percentUsed(){
        double totalCells = width * height;
        if(getSolutionNumber() == 0){
            solver();
        }
        double required = getSolutionNumber();
        return (int)Math.abs(required/totalCells * 100);
    }
    /**
     * Implements: Cycles through the two-dimensional array to calculates the number of dead ends in the maze by see which
     * cells have three walls up.
     * Pre-condition: None.
     * Post-condition: None.
     * @return returns the number of dead ends as an int.
     */
    public int deadEnds(){
        int deadEnds = 0;
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                Cell deadEndCell = cells[i][j];
                if (deadEndCell.getWall(NORTH) == UP && deadEndCell.getWall(EAST) == UP
                        && deadEndCell.getWall(SOUTH) == UP && deadEndCell.getWall(WEST) == DOWN){
                    deadEnds++;
                }
                else if(deadEndCell.getWall(NORTH) == UP && deadEndCell.getWall(EAST) == UP
                        && deadEndCell.getWall(SOUTH) == DOWN && deadEndCell.getWall(WEST) == UP){
                    deadEnds++;
                }
                else if(deadEndCell.getWall(NORTH) == UP && deadEndCell.getWall(EAST) == DOWN
                        && deadEndCell.getWall(SOUTH) == UP && deadEndCell.getWall(WEST) == UP){
                    deadEnds++;
                }
                else if(deadEndCell.getWall(NORTH) == DOWN && deadEndCell.getWall(EAST) == UP
                        && deadEndCell.getWall(SOUTH) == UP && deadEndCell.getWall(WEST) == UP){
                    deadEnds++;
                }
            }
        }
        return deadEnds;
    }
    /**
     * Implements: Updates last edited time.
     * Pre-condition: None.
     * Post-condition: Last edited time has been updated with the new time.
     */
    private void updateLastEdited() {
        lastEdited = LocalDateTime.now();
    }
    /**
     * Implements: Returns whether starting image is in the maze.
     * Pre-condition: startImageExists variable has been initialised.
     * Post-condition: None.
     * @return true if starting image is in maze, false otherwise.
     */
    public Boolean hasStartImage(){
        return startImageExists;
    }
    /**
     * Implements: Returns whether finishing image is in the maze.
     * Pre-condition: startImageExists variable has been initialised.
     * Post-condition: None.
     * @return true if finishing image is in maze, false otherwise.
     */
    public Boolean hasFinishImage(){
        return finishImageExists;
    }
    /**
     * Implements: Checks if the coordinates of one cell are the same as another cell.
     * Pre-condition: Both cells are within the bounds of the maze.
     * Post-condition: Both cells have been analysed to ensure they are not the same.
     * @param cell1X x coordinate of first cell as an int
     * @param cell1Y y coordinate of first cell as an int
     * @param cell2X x coordinate of second cell as an int
     * @param cell2Y y coordinate of second cell as an int
     * @return true if the maze's cell1 and cell2 position are different, false otherwise
     */
    public Boolean validate(int cell1X, int cell1Y, int cell2X, int cell2Y){
        return cell1X != cell2X || cell1Y != cell2Y;
    }
    /**
     * Implements: Gets the id of the maze or null if the id has not been set
     * Pre-condition: None.
     * Post-condition: None.
     * @return the id of the maze or null if the id has not been set
     */
    public Integer getId() {
        return id;
    }
    /**
     * Implements: Sets the Maze's ID, if the maze's ID is already set does nothing
     * Pre-condition: Maze ID is not currently set (is null)
     * Post-condition: Maze ID will not be null
     * @param id The id to set the maze to
     */
    public void setId(int id) {
        if(this.id == null)
            this.id = id;
    }
    /**
     * Implements: Sets the x and y coordinates for each cell in the maze
     * Pre-condition: None.
     * Post-condition: All cells coordinates are set.
     */
    private void setCoordinates(){
        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[0].length; j++){
                cells[i][j].setX(i);
                cells[i][j].setY(j);
            }
        }
    }
    /**
     * Implements: Draws the maze onto a {@link Graphics2D} object
     * Pre-condition: Maze has been fully constructed with all the required information.
     * Post-condition: Maze has been drawn ont a {@link Graphics2D} object.
     * @param g2d The {@link Graphics2D} to draw the maze onto
     * @param size The size of the resulting image (excluding offsets and insets)
     * @param insets The insets of the image
     * @param offsetX Shifts the entire image in the x direction on the canvas
     * @param offsetY Shifts the entire image in the y direction on the canvas
     * @param cellSize The size each cell should be on the image
     * @param solve Whether the solution should be displayed or not, if true a red line will be drawn showing the optimal solution
     */
    public void drawMaze(Graphics2D g2d, Dimension size, Insets insets, int offsetX, int offsetY, int cellSize, Boolean solve){
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);

        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;

        g2d.setBackground(Color.white);
        g2d.clearRect(0, 0, w, h);

        // Models.Maze shape was originally used as the clip for the walls of the
        // Need to come back and revisit this idea
        Path2D mazeShape = new Path2D.Double();
        Path2D lineSolution = new Path2D.Double();
        Path2D arrowEntry = new Path2D.Double();
        Path2D arrowExit = new Path2D.Double();

        int x,y;
        for (int i = 0; i < getWidth(); i++){
            x = i * cellSize + offsetX;
            for (int j = 0; j < getHeight(); j++) {
                y = j * cellSize + offsetY;
                g2d.setColor(Color.BLACK);
                // Draw walls of cells
                if (getWall(i, j, NORTH) == UP){
                    mazeShape.moveTo(x,y);
                    mazeShape.lineTo(x + cellSize, y);
                    g2d.drawLine(x,y, x + cellSize, y);
                }
                if (getWall(i, j, EAST) == UP){
                    mazeShape.moveTo(x + cellSize,y);
                    mazeShape.lineTo(x + cellSize, y + cellSize);
                    g2d.drawLine(x + cellSize,y, x + cellSize, y + cellSize);
                }
                if (getWall(i, j, SOUTH) == UP){
                    mazeShape.moveTo(x,y + cellSize);
                    mazeShape.lineTo(x + cellSize, y + cellSize);
                    g2d.drawLine(x,y + cellSize, x + cellSize, y + cellSize);
                }
                if (getWall(i, j, WEST) == UP){
                    mazeShape.moveTo(x,y);
                    mazeShape.lineTo(x, y + cellSize);
                    g2d.drawLine(x,y, x, y + cellSize);
                }

                // Draw solution
                if(solve){
                    // Set colour to red
                    g2d.setColor(Color.RED);
                    // Up
                    if(getCells()[i][j].solution[0] == 1){
                        int start_x = x + cellSize/2;
                        int start_y = y + cellSize/2;
                        int finish_y = start_y - cellSize/2;
                        lineSolution.moveTo(start_x, start_y);
                        lineSolution.lineTo(start_x, finish_y);
                        g2d.drawLine(start_x, start_y, start_x, finish_y);
                    }
                    // Down
                    if(getCells()[i][j].solution[2] == 1){
                        int start_x = x + cellSize/2;
                        int start_y = y + cellSize/2;
                        int finish_y = start_y + cellSize/2;
                        lineSolution.moveTo(start_x, start_y);
                        lineSolution.lineTo(start_x, finish_y);
                        g2d.drawLine(start_x, start_y, start_x, finish_y);}
                    // Right
                    if(getCells()[i][j].solution[1] == 1){
                        int start_x = x + cellSize/2;
                        int start_y = y + cellSize/2;
                        int finish_x = start_x + cellSize/2;
                        lineSolution.moveTo(start_x, start_y);
                        lineSolution.lineTo(finish_x, start_y);
                        g2d.drawLine(start_x, start_y, finish_x, start_y);
                    }
                    // Left
                    if(getCells()[i][j].solution[3] == 1){
                        int start_x = x + cellSize/2;
                        int start_y = y + cellSize/2;
                        int finish_x = start_x - cellSize/2;
                        lineSolution.moveTo(start_x, start_y);
                        lineSolution.lineTo(finish_x, start_y);
                        g2d.drawLine(start_x, start_y, finish_x, start_y);
                    }
                    // Set colour back to black
                    g2d.setColor(Color.BLACK);
                }
                // Insert start image
                if(i == start_Ix && j == start_Iy){
                    startImageExists = true;
                    // Resize
                    BufferedImage startScaled = getScaledImage(startImage,cellSize* (getStartWidth()+1),
                            cellSize*(getStartHeight()+1));
                    g2d.drawImage(startScaled,x,y,null);
                    // Draw walls of cells
                    if (getWall(i, j, NORTH) == UP){
                        mazeShape.moveTo(x,y);
                        mazeShape.lineTo(x + cellSize, y);
                        g2d.drawLine(x,y, x + cellSize, y);
                    }
                    if (getWall(i, j, EAST) == UP){
                        mazeShape.moveTo(x + cellSize,y);
                        mazeShape.lineTo(x + cellSize, y + cellSize);
                        g2d.drawLine(x + cellSize,y, x + cellSize, y + cellSize);
                    }
                    if (getWall(i, j, SOUTH) == UP){
                        mazeShape.moveTo(x,y + cellSize);
                        mazeShape.lineTo(x + cellSize, y + cellSize);
                        g2d.drawLine(x,y + cellSize, x + cellSize, y + cellSize);
                    }
                    if (getWall(i, j, WEST) == UP){
                        mazeShape.moveTo(x,y);
                        mazeShape.lineTo(x, y + cellSize);
                        g2d.drawLine(x,y, x, y + cellSize);
                    }
                }
                // Insert finish image
                if(i == finish_Ix && j == finish_Iy){
                    finishImageExists = true;
                    // Resize
                    BufferedImage finishScaled = getScaledImage(finishImage,cellSize* (getFinishWidth()+1),
                            cellSize*(getFinishHeight()+1));
                    g2d.drawImage(finishScaled,x,y,null);
                    // Draw walls of cells
                    if (getWall(i, j, NORTH) == UP){
                        mazeShape.moveTo(x,y);
                        mazeShape.lineTo(x + cellSize, y);
                        g2d.drawLine(x,y, x + cellSize, y);
                    }
                    if (getWall(i, j, EAST) == UP){
                        mazeShape.moveTo(x + cellSize,y);
                        mazeShape.lineTo(x + cellSize, y + cellSize);
                        g2d.drawLine(x + cellSize,y, x + cellSize, y + cellSize);
                    }
                    if (getWall(i, j, SOUTH) == UP){
                        mazeShape.moveTo(x,y + cellSize);
                        mazeShape.lineTo(x + cellSize, y + cellSize);
                        g2d.drawLine(x,y + cellSize, x + cellSize, y + cellSize);
                    }
                    if (getWall(i, j, WEST) == UP){
                        mazeShape.moveTo(x,y);
                        mazeShape.lineTo(x, y + cellSize);
                        g2d.drawLine(x,y, x, y + cellSize);
                    }
                }
                // Insert Logo
                if(i == logo_Ix && j == logo_Iy){
                    logoExists = true;
                    // Resize
                    BufferedImage logoScaled = getScaledImage(logo,cellSize* getLogoWidth(),
                            cellSize*getLogoHeight());
                    g2d.drawImage(logoScaled,x,y,null);
                    // Draw walls of cells
                    if (getWall(i, j, NORTH) == UP){
                        mazeShape.moveTo(x,y);
                        mazeShape.lineTo(x + cellSize, y);
                        g2d.drawLine(x,y, x + cellSize, y);
                    }
                    if (getWall(i, j, EAST) == UP){
                        mazeShape.moveTo(x + cellSize,y);
                        mazeShape.lineTo(x + cellSize, y + cellSize);
                        g2d.drawLine(x + cellSize,y, x + cellSize, y + cellSize);
                    }
                    if (getWall(i, j, SOUTH) == UP){
                        mazeShape.moveTo(x,y + cellSize);
                        mazeShape.lineTo(x + cellSize, y + cellSize);
                        g2d.drawLine(x,y + cellSize, x + cellSize, y + cellSize);
                    }
                    if (getWall(i, j, WEST) == UP){
                        mazeShape.moveTo(x,y);
                        mazeShape.lineTo(x, y + cellSize);
                        g2d.drawLine(x,y, x, y + cellSize);
                    }
                }
                // Draws entry and exit arrows
                if(showArrows){
                    // Draw entry arrow
                    if (getCells()[i][j].entry[0] == 1){
                        int arrowX = x + cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowEntry.moveTo(arrowX, y); // Start top left
                        arrowEntry.lineTo(arrowX + arrowMove, y); // Top right
                        g2d.drawLine(arrowX, y, arrowX + arrowMove, y); // Draw Line

                        arrowEntry.lineTo(x + cellSize / 2, y + cellSize/4); // Middle
                        g2d.drawLine(arrowX + arrowMove, y, x + cellSize/2, y + cellSize/4); // Draw line

                        arrowEntry.lineTo(arrowX, y); // Top left
                        g2d.drawLine(x + cellSize/2, y + cellSize/4, arrowX, y);
                        g2d.setColor(Color.white);
                        g2d.fill(arrowEntry); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    if (getCells()[i][j].entry[1] == 1){
                        int arrowX = x + cellSize;
                        int arrowY = y + cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowEntry.moveTo(arrowX,arrowY); // Top right
                        arrowEntry.lineTo(arrowX, arrowY + arrowMove); // Bottom right
                        g2d.drawLine(arrowX,arrowY, arrowX, arrowY + arrowMove); // Draw Line

                        arrowEntry.lineTo(arrowX - cellSize/4, y + cellSize/2); // Middle
                        g2d.drawLine(arrowX, arrowY + arrowMove, arrowX - cellSize/4, y + cellSize/2); // Draw Line

                        arrowEntry.lineTo(arrowX,arrowY); // Top right
                        g2d.drawLine(arrowX - cellSize/4, y + cellSize/2, arrowX,arrowY); // Draw Line

                        g2d.setColor(Color.white);
                        g2d.fill(arrowEntry); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    if (getCells()[i][j].entry[2] == 1){
                        int arrowX = x + cellSize/4;
                        int arrowY = y + cellSize;
                        int arrowMove = cellSize/2;

                        arrowEntry.moveTo(arrowX,arrowY); // Bottom left
                        arrowEntry.lineTo(arrowX + arrowMove, arrowY); // Bottom right
                        g2d.drawLine(arrowX,arrowY, arrowX + arrowMove, arrowY); // Draw Line

                        arrowEntry.lineTo(x + cellSize/2, arrowY - cellSize/4); // Middle
                        g2d.drawLine(arrowX + arrowMove, arrowY, x + cellSize/2, arrowY - cellSize/4); // Draw Line

                        arrowEntry.lineTo(arrowX,arrowY); // Bottom left
                        g2d.drawLine(x + cellSize/2, arrowY - cellSize/4, arrowX,arrowY); // Draw Line

                        g2d.setColor(Color.white);
                        g2d.fill(arrowEntry); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    if (getCells()[i][j].entry[3] == 1){
                        int arrowX = x;
                        int arrowY = y + cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowEntry.moveTo(arrowX,arrowY); // Top left
                        arrowEntry.lineTo(arrowX,arrowY + arrowMove); // Bottom left
                        g2d.drawLine(arrowX,arrowY, arrowX,arrowY + arrowMove); // Draw Line

                        arrowEntry.lineTo(arrowX + cellSize/4, y + cellSize/2); // Middle
                        g2d.drawLine(arrowX,arrowY + arrowMove, arrowX + cellSize/4, y + cellSize/2); // Draw Line

                        arrowEntry.lineTo(arrowX,arrowY); // Top left
                        g2d.drawLine(arrowX + cellSize/4, y + cellSize/2, arrowX,arrowY); // Draw Line

                        g2d.setColor(Color.white);
                        g2d.fill(arrowEntry); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    // Draw exit arrow
                    if (getCells()[i][j].exit[0] == 1){
                        int arrowX = x + cellSize/4;
                        int arrowY = y + cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowExit.moveTo(arrowX,arrowY);
                        arrowExit.lineTo(arrowX + arrowMove, arrowY);
                        g2d.drawLine(arrowX,arrowY, arrowX + arrowMove, arrowY);

                        arrowExit.lineTo(x + cellSize/2, y); // Point
                        g2d.drawLine(arrowX + arrowMove, arrowY, x + cellSize/2, y);

                        arrowExit.lineTo(arrowX,arrowY);
                        g2d.drawLine(x + cellSize/2, y, arrowX,arrowY);
                        g2d.setColor(Color.white);
                        g2d.fill(arrowExit); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    if (getCells()[i][j].exit[1] == 1){
                        int arrowX = x + cellSize - cellSize/4;
                        int arrowY = y + cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowExit.moveTo(arrowX,arrowY);
                        arrowExit.lineTo(arrowX, arrowY + arrowMove);
                        g2d.drawLine(arrowX,arrowY, arrowX, arrowY + arrowMove); // Draw Line

                        arrowExit.lineTo(x + cellSize, y + cellSize/2); // Middle
                        g2d.drawLine(arrowX, arrowY + arrowMove, x + cellSize, y + cellSize/2); // Draw Line

                        arrowExit.lineTo(arrowX,arrowY);
                        g2d.drawLine(x + cellSize, y + cellSize/2, arrowX,arrowY); // Draw Line

                        g2d.setColor(Color.white);
                        g2d.fill(arrowExit); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    if (getCells()[i][j].exit[2] == 1){
                        int arrowX = x + cellSize/4;
                        int arrowY = y + cellSize - cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowExit.moveTo(arrowX,arrowY); // Bottom left
                        arrowExit.lineTo(arrowX + arrowMove, arrowY); // Bottom right
                        g2d.drawLine(arrowX,arrowY, arrowX + arrowMove, arrowY); // Draw Line

                        arrowExit.lineTo(x + cellSize/2, y + cellSize); // Middle
                        g2d.drawLine(arrowX + arrowMove, arrowY, x + cellSize/2, y + cellSize); // Draw Line

                        arrowExit.lineTo(arrowX,arrowY); // Bottom left
                        g2d.drawLine(x + cellSize/2, y + cellSize, arrowX,arrowY); // Draw Line

                        g2d.setColor(Color.white);
                        g2d.fill(arrowExit); // fill arrow
                        g2d.setColor(Color.black);
                    }
                    if (getCells()[i][j].exit[3] == 1){
                        int arrowX = x + cellSize/4;
                        int arrowY = y + cellSize/4;
                        int arrowMove = cellSize/2;

                        arrowExit.moveTo(arrowX,arrowY);
                        arrowExit.lineTo(arrowX,arrowY + arrowMove);
                        g2d.drawLine(arrowX,arrowY, arrowX,arrowY + arrowMove); // Draw Line

                        arrowExit.lineTo(x, y + cellSize/2); // Middle
                        g2d.drawLine(arrowX,arrowY + arrowMove, x, y + cellSize/2); // Draw Line

                        arrowExit.lineTo(arrowX,arrowY);
                        g2d.drawLine(x, y + cellSize/2, arrowX,arrowY); // Draw Line

                        g2d.setColor(Color.white);
                        g2d.fill(arrowExit); // fill arrow
                        g2d.setColor(Color.black);
                    }
                }
            }
        }
    }
    /**
     * Sets whether arrows should be shown pointing to the entrance and exit of the maze
     * @param showArrows If true arrows will be drawn pointing to the entrance and exit of the maze, otherwise they will not be drawn
     */
    public void setShowArrows(boolean showArrows) {
        this.showArrows = showArrows;
    }
    /**
     * Checks whether arrows pointing to the entrance and exit of the maze are being drawn
     * @return true if arrows will be drawn pointing to the entrance and exit of the maze, false otherwise
     */
    public boolean isShowingArrows() {
        return showArrows;
    }
    /**
     * Exports the current Maze as an image to the specified path
     *
     * @param path         The path to save the image to
     * @param showSolution If true the optimal solution will be added as a red line, otherwise it will be excluded
     */
    public void exportAsImage(String path, boolean showSolution) throws IOException {
        int cellSize = calculateCellSize();
        int imageWidth = cellSize * (width + 2);
        int imageHeight = cellSize * (height + 2);
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        drawMaze(g2d, new Dimension(imageWidth, imageHeight), new Insets(0, 0, 0, 0), cellSize, cellSize, cellSize, showSolution);
        ImageIO.write(image, "jpg", new File(path));
    }
    /**
     * Calculates the size of the cells in pixels based off of the width and height of the maze in cells
     * @return the size of the cells in pixels based off of the width and height of the maze in cells
     */
    private int calculateCellSize() {
        int maxSize;
        if(width > height)
            maxSize = width;
        else
            maxSize = height;
        if(maxSize <= 25)
            return 64;
        else if(maxSize <= 50)
            return 32;
        else if(maxSize <= 75)
            return 21;
        else
            return 16;
    }
    /**
     * Changes the logo of the maze to the specified image file
     * @param logoPath The full path to the image file to set the logo to
     * @param logoScale The scale of the logo as one of the following:
     *                 "Exclude","Small square", "Medium square","Large square",
     *                 "Small rectangle horizontal", "Medium rectangle horizontal","Large rectangle horizontal",
     *                 "Small rectangle vertical", "Medium rectangle vertical","Large rectangle vertical"
     */
    public void setLogo(String logoPath, String logoScale) throws IOException {
        logoImagePath = logoPath;
        BufferedImage image = getImageFromPath(logoPath);
        if(image == null) {
            throw new IOException("Could not get an image from the file " + logoPath);
        }
        logo = image;
        if(!logoExists)
            logoImageInsert();
    }
    /**
     * Gets the current logo as a {@link BufferedImage} or null if there is currently no logo
     * @return the current logo as a {@link BufferedImage} or null if there is currently no logo
     */
    public BufferedImage getLogo() {
        return logo;
    }
    /**
     * Changes the start image of the maze to the specified image file
     * @param startImgPath The full path to the image file to set the start image to
     */
    public void setStartImage(String startImgPath) throws IOException {
        startImagePath = startImgPath;
        BufferedImage image = getImageFromPath(startImagePath);
        if(image == null) {
            throw new IOException("Could not get an image from the file " + startImagePath);
        }
        startImage = image;
    }
    /**
     * Changes the finish image of the maze to the specified image file
     * @param finishImgPath The full path to the image file to set the finish image to
     */
    public void setFinishImage(String finishImgPath) throws IOException {
        finishImagePath = finishImgPath;
        BufferedImage image = getImageFromPath(finishImagePath);
        if(image == null) {
            throw new IOException("Could not get an image from the file " + finishImagePath);
        }
        finishImage = image;
    }
    /**
     * Gets the current start image as a {@link BufferedImage} or null if there is currently no start image
     * @return the current start image as a {@link BufferedImage} or null if there is currently no start image
     */
    public BufferedImage getStartImage() {
        return startImage;
    }
    /**
     * Gets the current finish image as a {@link BufferedImage} or null if there is currently no finish image
     * @return the current finish image as a {@link BufferedImage} or null if there is currently no finish image
     */
    public BufferedImage getFinishImage() {
        return finishImage;
    }

    private boolean includeSolution = false;
    /**
     * Prints the maze to the native printer queue
     * @param includeSolution If true the printed maze will have the solution drawn in a red line, otherwise it will not
     */
    public void myPrint(boolean includeSolution) {
        this.includeSolution = includeSolution;
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        job.setJobName("Models.Maze");
        if(job.printDialog()){
            try {
                job.print();
            }
            catch (PrinterException ex) {
                /* The job did not successfully complete */
                System.out.println("Print failed");
            }
        }
    }
    /**
     *
     * @param g
     * @param pf
     * @param page
     * @return
     */
    @Override
    public int print(Graphics g, PageFormat pf, int page) {

        if (page > 0){
            return NO_SUCH_PAGE; /* We have only one page, and 'page' is zero-based */
        }

        // User (0,0) is typically outside the imaginable area, so we must translate by the X
        // and Y values in the PageFormat to avoid clipping
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        //Now we perform our rendering
        int cellSize = 30;
        int imageWidth = cellSize * (width + 2);
        int imageHeight = cellSize * (height + 2);
        drawMaze(g2d, new Dimension(imageWidth, imageHeight), new Insets(0, 0, 0, 0), cellSize, cellSize, cellSize, includeSolution);

        // tell the caller that this page is part of the printed document
        return PAGE_EXISTS;
    }
}