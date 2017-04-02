package com.khalev.efd.simulation;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Representation of the map of the environment. Map is represented as a list of horizontal and vertical line segments.
 * Those segments can have only integer coordinates because the map is given by a bitmap image with discrete pixels. A
 * robot can never cross any of those lines as they represent physical walls.
 */
public class EnvironmentMap {

    ArrayList<Line>[] vertical;
    ArrayList<Line>[] horizontal;
    int sizeX;
    int sizeY;

    /**
     * @return A list of all vertical lines with X-coordinate at i.
     */
    public ArrayList<Line> getVerticalLines(int i) {
        ArrayList<Line> list = new ArrayList<>();
        Collections.copy(vertical[i], list);
        return list;
    }

    /**
     * @return A list of all horizontal lines with Y-coordinate at i.
     */
    public ArrayList<Line> getHorizontalLines(int i) {
        ArrayList<Line> list = new ArrayList<>();
        Collections.copy(horizontal[i], list);
        return list;
    }

    /**
     * @return Width of the map as it was specified in simulation parameters.
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * @return Height of the map as it was specified in simulation parameters.
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
