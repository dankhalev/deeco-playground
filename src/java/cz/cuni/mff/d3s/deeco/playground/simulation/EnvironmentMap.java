package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representation of the map of physical obstacles in the environment. Map is represented as a list of horizontal and
 * vertical line segments. Those segments can have only integer coordinates because the map is given by a bitmap image
 * with discrete pixels. A robot can never cross any of those lines as they represent physical walls.
 *
 * @author Danylo Khalyeyev
 */
public class EnvironmentMap {

    List<Line>[] vertical;
    List<Line>[] horizontal;
    int sizeX;
    int sizeY;

    /**
     * Returns an unmodifiable list of all vertical lines with X-coordinate at i.
     * @param i X-coordinate of walls in the list
     * @return an unmodifiable list of all vertical lines with X-coordinate at i.
     */
    public List<Line> getVerticalLines(int i) {
        return Collections.unmodifiableList(vertical[i]);
    }

    /**
     * Returns an unmodifiable list of all horizontal lines with Y-coordinate at i.
     * @param i Y-coordinate of walls in the list
     * @return an unmodifiable list of all horizontal lines with Y-coordinate at i.
     */
    public List<Line> getHorizontalLines(int i) {
        return Collections.unmodifiableList(horizontal[i]);
    }

    /**
     * Returns a width of the map as it was specified in a scenario file.
     * @return width of the map as it was specified in a scenario file.
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Returns a height of the map as it was specified in a scenario file.
     * @return height of the map as it was specified in a scenario file.
     */
    public int getSizeY() {
        return sizeY;
    }

    @SuppressWarnings("unchecked")
    EnvironmentMap(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        vertical = new ArrayList[sizeX+1];
        for (int i = 0; i <= sizeX; i++) {
            vertical[i] = new ArrayList();
        }
        horizontal = new ArrayList[sizeY+1];
        for (int i = 0; i <= sizeY; i++) {
            horizontal[i] = new ArrayList();
        }

    }

}
