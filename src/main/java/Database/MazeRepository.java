package Database;

import Models.Cell;
import Models.Direction;
import Models.Exceptions.DatabaseException;
import Models.Maze;
import Utils.PropertyReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static Models.Wall.*;
import static Utils.Utils.imageToInputStream;

/**
 * A class for accessing information from a maze database
 */
public class MazeRepository implements IMazeRepository {
    private PostGreSQLConnection _dbConnection;

    private static MazeRepository INSTANCE;

    /**
     * Constructs a new MazeRepository which is used for accessing information in the maze db
     */
    private void SetupPostGreConnection() {
        Utils.PropertyReader reader = new Utils.PropertyReader();
        Properties properties = reader.getProperties("db.props");
        _dbConnection = new PostGreSQLConnection(properties);
    }

    /**
     * Gets the instance of the MazeRepository
     *
     * @return The instance of the MazeRepository
     */
    public static MazeRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MazeRepository();
        }
        return INSTANCE;
    }

    /**
     * Creates the database schema if it doesn't already exist
     *
     * @param schema        The name of the schema in the database
     * @param fullTableName The full name of the table in the database (schema.table)
     * @param connection    A connection to the database
     */
    private void createSchema(String schema, String fullTableName, Connection connection) throws DatabaseException {
        if(connection == null)
            throw new DatabaseException("Unable to connect to the database when creating schema");
        String createSchemaSql = "CREATE SCHEMA IF NOT EXISTS " + schema;
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + fullTableName + "(\n" +
                "  maze_Id SERIAL PRIMARY KEY, \n " +
                "  name varchar(45) NOT NULL,\n" +
                "  author varchar(450) NOT NULL,\n" +
                "  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_DATE  ,\n" +
                "  last_edited_date TIMESTAMP NOT NULL DEFAULT CURRENT_DATE , \n" +
                "  start_x smallint, \n" +
                "  start_y smallint, \n" +
                "  finish_x smallint, \n" +
                "  finish_y smallint, \n" +
                "  logo_x smallint, \n" +
                "  logo_y smallint, \n" +
                "  logo_image bytea, \n" +
                "  cells smallint[][][4], \n " +
                "  solution smallint[][][4], \n" +
                "  start_Ix smallint, \n" +
                "  start_Iy smallint, \n" +
                "  start_image bytea, \n" +
                "  finish_Ix smallint, \n" +
                "  finish_Iy smallint, \n" +
                "  finish_image bytea, \n" +
                "  logo_width int, \n" +
                "  logo_height int, \n" +
                "  start_width int, \n" +
                "  start_height int, \n" +
                "  finish_width int, \n" +
                "  finish_height int, \n" +
                "  entry smallint[][][4], \n" +
                "  exit smallint[][][4]);";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createSchemaSql);
            stmt.executeUpdate(createTableSql);
        } catch (SQLException ex) {
            throw new DatabaseException("Error creating database schema");
        }
    }

    /**
     * Gets the full table name (schema.table) from the "db.props" file
     * Also, creates the schema if it doesn't already exist (since this will naturally be used in every db operation)
     *
     * @param connection A connection to the database
     * @return The full table name (schema.table) for the maze database table
     */
    private String getTableName(Connection connection) throws DatabaseException {
        PropertyReader reader = new PropertyReader();
        Properties properties = reader.getProperties("db.props");
        String schema = properties.getProperty("schema");
        String table = properties.getProperty("table");
        String fullTableName = schema + "." + table;
        createSchema(schema, fullTableName, connection);
        return fullTableName;
    }

    @Override
    public int SaveMaze(Maze maze) throws DatabaseException {
        SetupPostGreConnection();
        int savedId = -1;
        if (_dbConnection != null) {
            try (Connection connection = _dbConnection.getPostgreSqlConnection()) {
                String saveMazeSql;
                Integer id = maze.getId();
                boolean existed = id != null && mazeExists(id);
                String fullTableName = getTableName(connection);

                if (existed) {
                    saveMazeSql = "UPDATE " + fullTableName + " SET name=?, author=?, creation_date=?, " +
                            "last_edited_date=?, start_x=?, start_y=?, finish_x=?, finish_y=?, logo_image=?, " +
                            "logo_x=?, logo_y=?, cells=?, solution=?, start_Ix=?, start_Iy=?, finish_Ix=?, finish_Iy=?," +
                            "start_image=?, finish_image=?, logo_width=?, logo_height=?, start_width=?, start_height=?," +
                            "finish_width=?, finish_height=?, entry=?, exit=? WHERE maze_id=?";
                } else {
                    saveMazeSql = "INSERT INTO " + fullTableName + "(name,author,creation_date,last_edited_date," +
                            "start_x,start_y,finish_x,finish_y,logo_image,logo_x,logo_y,cells,solution," +
                            "start_Ix,start_Iy,finish_Ix,finish_Iy,start_image,finish_image,logo_width,logo_height, " +
                            "start_width,start_height,finish_width,finish_height,entry,exit) " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                }

                PreparedStatement ps = connection.prepareStatement(saveMazeSql, Statement.RETURN_GENERATED_KEYS);
                if (existed) {
                    ps.setInt(28, maze.getId());
                }
                ps.setString(1, maze.getName());
                ps.setString(2, maze.getAuthor());
                ZoneId zoneId = ZoneId.systemDefault();
                ps.setTimestamp(3, new java.sql.Timestamp(maze.getCreationDate().atZone(zoneId).toEpochSecond()*1000));
                ps.setTimestamp(4, new java.sql.Timestamp(maze.getLastEdited().atZone(zoneId).toEpochSecond()*1000));
                ps.setInt(5, maze.getStart_x());
                ps.setInt(6, maze.getStart_y());
                ps.setInt(7, maze.getFinish_x());
                ps.setInt(8, maze.getFinish_y());
                ps.setInt(10, maze.getLogo_x());
                ps.setInt(11, maze.getLogo_y());
                ps.setInt(20, maze.getLogoWidth());
                ps.setInt(21, maze.getLogoHeight());
                ps.setInt(22, maze.getStartWidth());
                ps.setInt(23, maze.getStartHeight());
                ps.setInt(24, maze.getFinishWidth());
                ps.setInt(25, maze.getFinishHeight());

                if (maze.getLogo_x() != -1 && maze.getLogo_y() != -1) {
                    try {
                        if (maze.getLogo() == null) {
                            ps.setNull(9, Types.BINARY);
                        } else {
                            ps.setBinaryStream(9, imageToInputStream(maze.getLogo()));
                        }
                    } catch (IOException ex) {
                        ps.setNull(9, Types.BINARY);
                    }
                } else {
                    ps.setNull(9, Types.BINARY);
                }
                ps.setInt(14, maze.getStart_Ix());
                ps.setInt(15, maze.getStart_Iy());
                if (maze.getStart_Ix() != -1 && maze.getStart_Iy() != -1) {
                    try {
                        if (maze.getStartImage() == null) {
                            ps.setNull(18, Types.BINARY);
                        } else {
                            ps.setBinaryStream(18, imageToInputStream(maze.getStartImage()));
                        }
                    } catch (IOException ex) {
                        ps.setNull(18, Types.BINARY);
                    }
                } else {
                    ps.setNull(18, Types.BINARY);
                }
                ps.setInt(16, maze.getFinish_Ix());
                ps.setInt(17, maze.getFinish_Iy());
                if (maze.getFinish_Ix() != -1 && maze.getFinish_Iy() != -1) {
                    try {
                        if (maze.getStartImage() == null) {
                            ps.setNull(19, Types.BINARY);
                        } else {
                            ps.setBinaryStream(19, imageToInputStream(maze.getFinishImage()));
                        }
                    } catch (IOException ex) {
                        ps.setNull(19, Types.BINARY);
                    }
                } else {
                    ps.setNull(19, Types.BINARY);
                }

                Cell[][] cells = maze.getCells();
                int width = maze.getWidth();
                int height = maze.getHeight();
                int[][][] cellArray = new int[width][height][4];
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++)
                        for (int k = 0; k < 4; k++) {
                            cellArray[i][j][k] = cells[i][j].getWall(Direction.getDirection(k)).getValue();
                        }
                Array cellMultiArray = connection.createArrayOf("integer", cellArray);
                ps.setArray(12, cellMultiArray);

                int[][][] solutionArray = new int[width][height][4];
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++)
                        for (int k = 0; k < 4; k++) {
                            solutionArray[i][j][k] = cells[i][j].solution[k];
                        }
                Array solutionMultiArray = connection.createArrayOf("integer", solutionArray);
                ps.setArray(13, solutionMultiArray);

                int[][][] entryArray = new int[width][height][4];
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++)
                        for (int k = 0; k < 4; k++) {
                            entryArray[i][j][k] = cells[i][j].entry[k];
                        }
                Array entryMultiArray = connection.createArrayOf("integer", entryArray);
                ps.setArray(26, entryMultiArray);

                int[][][] exitArray = new int[width][height][4];
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++)
                        for (int k = 0; k < 4; k++) {
                            exitArray[i][j][k] = cells[i][j].exit[k];
                        }
                Array exitMultiArray = connection.createArrayOf("integer", exitArray);
                ps.setArray(27, exitMultiArray);
                ps.execute();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    savedId = rs.getInt(1);
                }
            } catch (SQLException e) {
                throw new DatabaseException("Unable to save maze to the database");
            }
        }
        return savedId;

    }

    @Override
    public Maze GetMazeById(int mazeId) throws DatabaseException {
        SetupPostGreConnection();
        Maze result;
        if (_dbConnection == null)
            throw new DatabaseException("No connection to the database available");
        try (Connection connection = _dbConnection.getPostgreSqlConnection()) {
            String fullTableName = getTableName(connection);
            String getMazeSql = "SELECT * FROM " + fullTableName + " WHERE maze_id = ?";
            PreparedStatement pst = connection.prepareStatement(getMazeSql);
            pst.setInt(1, mazeId);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                pst.close();
                throw new DatabaseException();
            }
            result = mazeFromResult(rs);
            pst.close();

        } catch (Exception e) {
            throw new DatabaseException("Unable to retrieve maze from the database");
        }
        return result;
    }

    /**
     * Checks if a specified maze exists in the database
     *
     * @param mazeId The id of the maze to search for
     * @return True if a maze with the id exists in the database, false otherwise
     */
    private boolean mazeExists(int mazeId) {
        if (_dbConnection == null)
            return false;
        boolean result = true;
        try (Connection connection = _dbConnection.getPostgreSqlConnection()) {
            String fullTableName = getTableName(connection);
            String getMazeSql = "SELECT maze_id FROM " + fullTableName + " WHERE maze_id = ?";
            PreparedStatement pst = connection.prepareStatement(getMazeSql);
            pst.setInt(1, mazeId);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                pst.close();
                result = false;
            }
            pst.close();
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    public ArrayList<Maze> GetAllMazes() throws DatabaseException {
        var result = new ArrayList<Maze>();
        SetupPostGreConnection();
        if (_dbConnection != null) {
            try (Connection connection = _dbConnection.getPostgreSqlConnection()) {
                String fullTableName = getTableName(connection);
                String getMazesSql = "Select * from " + fullTableName;

                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(getMazesSql);
                while (rs.next()) {
                    result.add(mazeFromResult(rs));
                }
            } catch (Exception e) {
                throw new DatabaseException("Unable to retrieve mazes from the database");
            }

            return result;
        }
        throw new DatabaseException("Unable to connect to the database");
    }

    /**
     * Creates a {@link Maze} from the current element of a result set containing all the information about the maze
     *
     * @param rs A {@link ResultSet} containing all the information about the maze (* from maze_data) positioned on the desired maze
     * @return A {@link Maze} representing the given data
     * @throws Exception Thrown if there are problems getting any of the data about the maze (probably a {@link ResultSet} from a bad query)
     */
    private Maze mazeFromResult(ResultSet rs) throws Exception {
        //Display values
        String name = rs.getString("name");
        String author = rs.getString("author");
        int id = rs.getInt("maze_id");
        int start_x = rs.getInt("start_x");
        int finish_x = rs.getInt("finish_x");
        int start_y = rs.getInt("start_y");
        int finish_y = rs.getInt("finish_y");
        int logo_Ix = rs.getInt("logo_x");
        int logo_Iy = rs.getInt("logo_y");
        int start_Ix = rs.getInt("start_Ix");
        int finish_Ix = rs.getInt("finish_Ix");
        int start_Iy = rs.getInt("start_Iy");
        int finish_Iy = rs.getInt("finish_Iy");
        int logoWidth = rs.getInt("logo_width");
        int logoHeight = rs.getInt("logo_height");
        int startWidth = rs.getInt("start_width");
        int startHeight = rs.getInt("start_height");
        int finishWidth = rs.getInt("finish_width");
        int finishHeight = rs.getInt("finish_height");
        LocalDateTime creation_date = rs.getTimestamp("creation_date").toLocalDateTime();
        LocalDateTime last_edited_date = rs.getTimestamp("last_edited_date").toLocalDateTime();
        Array cellArray = rs.getArray("cells");
        Array solutionArray = rs.getArray("solution");
        Short[][][] cellsInfo = (Short[][][]) cellArray.getArray();
        Short[][][] solutionInfo = (Short[][][]) solutionArray.getArray();
        Array entryArray = rs.getArray("entry");
        Array exitArray = rs.getArray("exit");
        Short[][][] entryInfo = (Short[][][]) entryArray.getArray();
        Short[][][] exitInfo = (Short[][][]) exitArray.getArray();
        InputStream logoStream = rs.getBinaryStream("logo_image");
        BufferedImage logoImg = logoStream == null ? null : ImageIO.read(logoStream);
        InputStream startStream = rs.getBinaryStream("start_image");
        BufferedImage startImg = startStream == null ? null : ImageIO.read(startStream);
        InputStream finishStream = rs.getBinaryStream("finish_image");
        BufferedImage finishImg = finishStream == null ? null : ImageIO.read(finishStream);

        int width = cellsInfo.length;
        int height = cellsInfo[0].length;
        Cell[][] cells = new Cell[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Short[] curCellInfo = cellsInfo[i][j];
                Cell cell = new Cell(j, i, fromShort(curCellInfo[0]), fromShort(curCellInfo[1]), fromShort(curCellInfo[2]), fromShort(curCellInfo[3]));
                cell.solution = new byte[]{solutionInfo[i][j][0].byteValue(), solutionInfo[i][j][1].byteValue(), solutionInfo[i][j][2].byteValue(), solutionInfo[i][j][3].byteValue()};
                cell.entry = new byte[]{entryInfo[i][j][0].byteValue(), entryInfo[i][j][1].byteValue(), entryInfo[i][j][2].byteValue(), entryInfo[i][j][3].byteValue()};
                cell.exit = new byte[]{exitInfo[i][j][0].byteValue(), exitInfo[i][j][1].byteValue(), exitInfo[i][j][2].byteValue(), exitInfo[i][j][3].byteValue()};
                cells[i][j] = cell;
            }

        return new Maze(id, name, author, creation_date, last_edited_date, cells, start_x, start_y, finish_x, finish_y,
                start_Ix, start_Iy, finish_Ix, finish_Iy, logo_Ix, logo_Iy, logoImg, startImg, finishImg, logoWidth, logoHeight,
                startWidth, startHeight, finishWidth, finishHeight);
    }

    @Override
    public void DeleteMaze(Maze maze) throws DatabaseException {
        if (_dbConnection != null) {
            if (mazeExists(maze.getId())) {
                try (Connection connection = _dbConnection.getPostgreSqlConnection()) {
                    String fullTableName = getTableName(connection);
                    String deleteMazeSql = "DELETE FROM " + fullTableName + " WHERE maze_Id = " + maze.getId() + ";";
                    PreparedStatement ps = connection.prepareStatement(deleteMazeSql);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new DatabaseException("Unable to delete maze");
                }
            }
        }
    }

    @Override
    public boolean ClearAll(List<Maze> allMazes) {
        Maze currentMaze;
        for (int i = 0; i < allMazes.size(); i++) {
            currentMaze = allMazes.get(i);
            try {
                DeleteMaze(currentMaze);
                if (i == allMazes.size() - 1) {
                    return true;
                }
            } catch (DatabaseException ignored) {}
        }
        return false;
    }
}
