package cz.cuni.mff.d3s.deeco.playground.visualization;

/**
 * Represents a map of physical obstacles that was used in simulation.
 *
 * @author Danylo Khalyeyev
 */
class EnvironmentMap {

    Boolean[][] map;

    /**
     * Creates a new map of a given size.
     * @param sizeX width of a map
     * @param sizeY height of a map
     */
    EnvironmentMap(int sizeX, int sizeY) {
        map = new Boolean[sizeX][sizeY];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = false;
            }
        }
    }

    /**
     * Adds an obstacle to the map at a specified coordinate.
     * @param x X-coordinate of the obstacle
     * @param y Y-coordinate of the obstacle
     */
    void addObstacle(int x, int y) {
        map[x][y] = true;
    }

    /**
     * Returns true if there is an obstacle at a given coordinate, false otherwise.
     * @param x X-coordinate of the obstacle
     * @param y Y-coordinate of the obstacle
     * @return true if there is an obstacle at a given coordinate, false otherwise.
     */
    boolean isOccupied(int x, int y) {
        return map[x][y];
    }

    /**
     * Returns a height of the map
     * @return height of the map
     */
    int getHeight() {
        return map[0].length;
    }

    /**
     * Returns a width of the map
     * @return width of the map
     */
    int getWidth() {
        return map.length;
    }

}
