package Models;

public enum Direction {
    NORTH(0), EAST(1), SOUTH(2), WEST(3);
    private final int index;
    Direction(int index) {
        this.index = index;
    }

    /**
     * Gets the 0 based index of the direction going North, East, South, West
     * @return the 0 based index of the direction going North, East, South, West
     */
    public int getIndex() {
        return index;
    }

    public static Direction getDirection(int index) {
        return switch (index) {
            case 0 -> NORTH;
            case 1 -> EAST;
            case 2 -> SOUTH;
            case 3 -> WEST;
            default -> NORTH;
        };
    }
}
