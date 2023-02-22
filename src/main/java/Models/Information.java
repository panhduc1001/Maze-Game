package Models;

/**
 * Stores information that is useful when generating Mazes
 */
public class Information {
    /**
     * The current x position of the cell
     */
    int current_x;
    /**
     * The current y position of the cell
     */
    int current_y;
    /**
     * The x position of the cell's neighbour
     */
    int neighbour_x;
    /**
     * The y position of the cell's neighbour
     */
    int neighbour_y;
    /**
     * The status of the current cell's wall (0 or 1 for down or up)
     */
    Direction current_wall;
    /**
     * The status of the neighbour cell's wall (0 or 1 for down or up)
     */
    Direction neighbour_wall;
}