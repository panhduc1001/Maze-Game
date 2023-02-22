package Models;

import static Models.Wall.UP;

/**
 * Represents an individual Cell in a Maze
 */
public class Cell {
    /**
     * A four long array representing the walls around a cell where 1 represents a wall that is up and 0 represents one that's down.
     * Each element of the array refers to a wall on a different side of the cell (North, East, South, West in that order)
     */
    private final Wall[] walls;
    /**
     * The x position of the cell in the maze
     */
    private int x;
    /**
     * The y position of the cell in the maze
     */
    private int y;
    /**
     * A four long array representing which the walls of the cell the solution goes through where 1 means the solution does go through the wall and 0 means it does not.
     * Each element of the array refers to a wall on a different side of the cell (North, East, South, West in that order)
     * For example if the solution array is [1, 1, 0, 0] the solution enters the top of the cell and exits through the right (or the reverse, entering/exiting isn't specified)
     */
    public byte[] solution = {0,0,0,0};
    /**
     * A byte value of 1 if entry point to maze, 0 otherwise.
     */
    public byte[] entry = {0,0,0,0};
    /**
     * A byte value of 1 if exit point to maze, 0 otherwise.
     */
    public byte[] exit = {0,0,0,0};
    /**
     * Byte value 1 for cell top left corner of the starting image will be, zero otherwise
     */
    public byte finishImage = 0;
    /**
     * Creates a cell with the specified walls
     * @param x The horizontal position of the cell starting from 0
     * @param y The vertical position of the cell starting from 0
     * @param north The type of the north wall of the cell
     * @param east The type of the east wall of the cell
     * @param south The type of the south wall of the cell
     * @param west The type of the west wall of the cell
     */
    public Cell(int x, int y, Wall north, Wall east, Wall south, Wall west) {
        walls = new Wall[] {north, east, south, west};
    }
    /**
     * Creates a cell with all walls UP with a specified position
     *  @param x The horizontal position of the cell starting from 0
     *  @param y The vertical position of the cell starting from 0
     */
    public Cell(int x, int y) {
        walls = new Wall[] {UP, UP, UP, UP};
        this.x = x;
        this.y = y;
    }
    /**
     * Creates a cell with all walls UP with position 0, 0
     */
    public Cell() {
        this(0, 0);
    }
    /**
     * Gets the type of wall at a specified position of the cell
     * @param position The position of the cell to get the wall of
     * @return The type of wall at position
     */
    public Wall getWall(Direction position) {
        return walls[position.getIndex()];
    }
    /**
     * Gets an array of all the walls of the cell
     * The walls in the array are ordered North, East, South, West
     * @return an array of all the walls of the cell ordered North, East, South, West
     */
    public Wall[] getWalls() {
        return walls;
    }
    /**
     * Sets the type of specified wall of the cell
     * @param position The position of the wall around the cell
     * @param wallType The type to set the wall to
     */
    void setWall(Direction position, Wall wallType) {
        walls[position.getIndex()] = wallType;
    }
    /**
     * Gets the horizontal position of the cell starting from 0
     * @return The horizontal position of the cell starting from 0
     */
    public int getX() {
        return x;
    }
    /**
     * Gets the vertical position of the cell starting from 0
     * @return The vertical position of the cell starting from 0
     */
    public int getY() {
        return y;
    }

    public void setY(int newY){
        y = newY;
    }
    public void setX(int newX){
        x = newX;
    }
}
