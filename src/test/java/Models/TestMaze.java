package Models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static Models.Direction.*;
import static Models.Wall.*;
import static Utils.Utils.compareImages;
import static Utils.Utils.getImageFromPath;
import static org.junit.jupiter.api.Assertions.*;

public class TestMaze {

    static Maze maze;

    @BeforeAll
    static void Setup() {
        maze = new Maze("Test", "Test", 20, 20, "Exclude", "Exclude", "Exclude", false, null, null, null);
        maze.setCreationDate(LocalDateTime.MIN);
    }

    @BeforeEach
    void ResetLastEdited() {
        maze.setLastEdited(LocalDateTime.of(12, 1, 1,1, 1));
    }

    @Test
    void testUpdateMaze() {
        LocalDateTime startTime = LocalDateTime.now();
        maze.setCell(0, 0, new Cell());
        assertTrue(startTime.compareTo(maze.getLastEdited()) <= 0, "Last edited was not updated");
    }

    @Test
    void testChangeAuthor() {
        LocalDateTime startTime = LocalDateTime.now();
        maze.setAuthor("The new author");
        assertEquals(maze.getAuthor(), "The new author", "Author did not update");
        assertTrue(startTime.compareTo(maze.getLastEdited()) <= 0, "Last edited was not updated");
    }

    @Test
    void testChangeName() {
        LocalDateTime startTime = LocalDateTime.now();
        maze.setName("The new name");
        assertEquals(maze.getName(), "The new name", "Name did not update");
        assertTrue(startTime.compareTo(maze.getLastEdited()) <= 0, "Last edited was not updated");
    }

    @Test
    void testChangeSolutionNumber() {
        LocalDateTime startTime = LocalDateTime.now();
        maze.setSolutionNumber(21);
        assertEquals(maze.getSolutionNumber(), 21, "Solution number did not update");
        assertTrue(startTime.compareTo(maze.getLastEdited()) <= 0, "Last edited was not updated");
    }

    @Test
    void testSetCell() {
        Cell cell = new Cell();
        maze.setCell(2, 3, cell);
        assertEquals(cell, maze.getCell(2, 3));
    }

    @Test
    void testSetCellWithSurroundings() {
        Cell allUpCell = new Cell();
        maze.setCell(1, 1, allUpCell);
        maze.setCell(2, 1, allUpCell);
        maze.setCell(0, 1, allUpCell);
        maze.setCell(1, 0, allUpCell);
        maze.setCell(1, 1, new Cell(0, 0, DOWN, START, END, DOWN));
        assertEquals(START, maze.getCell(2, 1).getWall(WEST), "Changing cell (1, 1) did not change the wall of cell (2, 1)");
        assertEquals(DOWN, maze.getCell(0, 1).getWall(EAST), "Changing cell (1, 1) did not change the wall of cell (0, 1)");
        assertEquals(END, maze.getCell(1, 2).getWall(NORTH), "Changing cell (1, 1) did not change the wall of cell (1, 2)");
        assertEquals(DOWN, maze.getCell(1, 0).getWall(SOUTH), "Changing cell (1, 1) did not change the wall of cell (1, 0)");
    }

    @Test
    void testSetWall() {
        maze.setWall(1, 1, SOUTH, UP);
        assertEquals(UP, maze.getCell(1, 1).getWall(SOUTH), "Wall did not update");
        assertEquals(UP, maze.getCell(2, 2).getWall(NORTH), "Changing cell (1, 1) did not change the wall of cell (1, 2)");
    }

    @Test
    void testGetWall() {
        assertEquals(maze.getCell(1, 1).getWall(SOUTH), maze.getWall(1, 1, SOUTH));
    }

    @Test
    void testShowArrows() {
        maze.setShowArrows(true);
        assertTrue(maze.isShowingArrows());
        maze.setShowArrows(false);
        assertFalse(maze.isShowingArrows());
    }

    @Test
    void testSetLogo() throws IOException {
        String exampleLogo = "src/main/resources/Eric_Cartman.png";
        maze.setLogo(exampleLogo, "Small square");
        BufferedImage image = getImageFromPath(exampleLogo);
        assertTrue(compareImages(image, maze.getLogo()));
    }

    @Test
    void testSetStartImage() throws IOException {
        String exampleImage = "src/main/resources/Kenny McCormick.jpg";
        maze.setStartImage(exampleImage);
        BufferedImage image = getImageFromPath(exampleImage);
        assertTrue(compareImages(image, maze.getStartImage()));
    }

    @Test
    void testSetFinishImage() throws IOException {
        String exampleImage = "src/main/resources/Stan-Marsh.jpeg";
        maze.setFinishImage(exampleImage);
        BufferedImage image = getImageFromPath(exampleImage);
        assertTrue(compareImages(image, maze.getFinishImage()));
    }

    @Test
    void testExportMaze() throws IOException {
        maze.exportAsImage("testMaze.png", false);
        File imageFile = new File("testMaze.png");
        assertTrue(imageFile.exists(), "testMaze.png was not created");
        assertTrue(imageFile.length() > 0, "The created image (testMaze.png) is empty!");
    }

    @Test
    void testExportSolution() throws IOException {
        maze.exportAsImage("testMazeSolution.png", true);
        File imageFile = new File("testMazeSolution.png");
        assertTrue(imageFile.exists(), "testMazeSolution.png was not created");
        assertTrue(imageFile.length() > 0, "The created image (testMazeSolution.png) is empty!");
    }

    @Test
    void testExportWithImages() throws IOException {
        String finishImage = "src/main/resources/Stan-Marsh.jpeg";
        maze.setFinishImage(finishImage);
        String startImage = "src/main/resources/Eric_Cartman.png";
        maze.setStartImage(startImage);
        String exampleLogo = "src/main/resources/logo_example.png";
        maze.setLogo(exampleLogo, "Small square");

        maze.exportAsImage("testMazeWithImages.png", false);
        File imageFile = new File("testMazeWithImages.png");
        assertTrue(imageFile.exists(), "testMazeWithImages.png was not created");
        assertTrue(imageFile.length() > 0, "The created image (testMazeWithImages.png) is empty!");
    }
}
