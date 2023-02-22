package Database;

import Models.Exceptions.DatabaseException;
import Models.Maze;

import java.util.List;

/**
 * An interface containing several methods to support the MazeRepository class
 */
public interface IMazeRepository {
    /**
     * Saves a maze to the database
     * @param maze The Maze to save to the database
     * @return True if the Maze successfully saved, false otherwise
     */
    int SaveMaze(Maze maze) throws DatabaseException;

    /**
     * Searches the database for a Maze with a specified id, returning a Maze object representing it
     * @param mazeId The id of the maze to search for
     * @return The maze that was found, if none was found null instead
     */
    Maze GetMazeById(int mazeId) throws DatabaseException;

    /**
     * Searches the database for all mazes, returning a list of them as Maze objects
     * @return A list containing all the mazes created by the author that are stored in the database
     */
    List<Maze> GetAllMazes() throws DatabaseException;

    /**
     * Deletes a maze from the database
     * @param maze The Maze to save to the database
     */
    void DeleteMaze(Maze maze) throws DatabaseException;
    /**
     * Deletes all mazes from database
     * @param allMazes a list of all the mazes in the database
     * @return True if all mazes are successfully deleted, false otherwise
     */
    boolean ClearAll(List<Maze> allMazes);
}
