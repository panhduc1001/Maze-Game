package Models.Exceptions;

/**
 * For exceptions to do with connecting to or interacting with the database
 */
public class DatabaseException extends Exception{
    /**
     * Creates a new DatabaseException with the supplied message
     * @param message A message with information about the exception
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Creats a new DatabaseException with no additional information
     */
    public DatabaseException() {
        super();
    }

    /**
     * Returns the message that was supplied to this exception with information about setting up db.props appended to it
     * @return the message that was supplied to this exception with information about setting up db.props appended to it
     */
    @Override
    public String getMessage() {
        return super.getMessage() +
                "\nMake sure you have correctly set up the db.props file located in the same folder as this program and have your database server running.";
    }
}
