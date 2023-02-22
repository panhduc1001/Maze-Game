package Models;

/**
 * Represents a specific wall of a cell in a maze
 */
public enum Wall {
    UP(1), DOWN(0), START(2), END(3);
    private final int value;
    /**
     * A short representing the wall type
     * @param value a short representing the type of wall
     */
    Wall(int value) {
        this.value = value;
    }
    /**
     * Gets a wall enum from a short value
     * @param num the value to convert into an enum (0 = DOWN, 1 = UP, 2 = START, 3 = END, anything else = DOWN)
     * @return a wall enum dependent on the provided short (0 = DOWN, 1 = UP, 2 = START, 3 = END, anything else = DOWN)
     */
    public static Wall fromShort(short num) {
        return switch (num) {
            case 0 -> DOWN;
            case 1 -> UP;
            case 2 -> START;
            case 3 -> END;
            default -> DOWN;
        };
    }
    /**
     * Gets the short value that represents the wall enum
     * @return a short based on the current enum (DOWN = 0, UP = 1, START = 2, END = 3)
     */
    public int getValue() {
        return value;
    }
}
