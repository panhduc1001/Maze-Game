package Models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static Models.Direction.*;
import static Models.Wall.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCell {

    @Test
    void testCreateDefaultCell() {
        Cell cell = new Cell();
        assertArrayEquals(new Wall[]{UP, UP, UP, UP}, cell.getWalls());
    }

    @Test
    void testGetPosition() {
        Cell cell = new Cell(3,4);
        assertEquals(3, cell.getX());
        assertEquals(4, cell.getY());
    }

    @Test
    void testGetDefaultPosition() {
        Cell cell = new Cell();
        assertEquals(0, cell.getX());
        assertEquals(0, cell.getY());
    }

    @Test
    void testCreateCell() {
        Cell cell = new Cell(0, 0, UP, UP, DOWN, UP);
        assertEquals(UP, cell.getWall(NORTH));
        assertEquals(UP, cell.getWall(EAST));
        assertEquals(DOWN, cell.getWall(SOUTH));
        assertEquals(UP, cell.getWall(WEST));
    }

    @Test
    void testSetWall() {
        Cell cell = new Cell();
        cell.setWall(EAST, START);
        assertEquals(cell.getWall(EAST), START);
    }
}
