package com.khalev.efd.visualization;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the body of a simulation logs file, decoding it cycle by cycle
 *
 * @author Danylo Khalyeyev
 */
class LogfileReader {

    private int cycle = -1;
    private BufferedReader logs;
    private List<ComponentParameters> robotCoordinates = new ArrayList<ComponentParameters>();
    private List<ComponentParameters> objectCoordinates = new ArrayList<>();
    private String status = "";

    /**
     * @param logs a BufferedReader to read simulation logs from. The header of the logfile should be already read in
     *             this reader.
     * @param robotCoordinates list of {@link ComponentParameters} for robots
     * @param objectCoordinates list of {@link ComponentParameters} for objects
     */
    LogfileReader(BufferedReader logs, List<ComponentParameters> robotCoordinates, List<ComponentParameters> objectCoordinates) {
        this.logs = logs;
        this.robotCoordinates = robotCoordinates;
        this.objectCoordinates = objectCoordinates;
    }

    /**
     * Returns a list of {@link ComponentParameters} for robots for a given cycle.
     * @param cycle current cycle
     * @return a list of {@link ComponentParameters} for robots for a given cycle
     */
    List<ComponentParameters> getRobots(int cycle) {
        if (cycle != this.cycle) {
            updateArrays();
        }
        assert cycle  == this.cycle: "Cycles are not synchronized";
        return robotCoordinates;
    }

    /**
     * Returns a list of {@link ComponentParameters} for objects for a given cycle.
     * @param cycle current cycle
     * @return a list of {@link ComponentParameters} for objects for a given cycle
     */
    List<ComponentParameters> getObjects(int cycle) {
        if (cycle != this.cycle) {
            updateArrays();
        }
        assert cycle == this.cycle: "Cycles are not synchronized";
        return objectCoordinates;
    }

    /**
     * Returns a status string for a cycle that is currently being visualized.
     * @return a status string for a cycle that is currently being visualized
     */
    String getStatus() {
        return status;
    }

    /**
     * Reads the data for the next cycle from simulation logs; updates lists of {@link ComponentParameters} for robots
     * and objects.
     * @throws RuntimeException if simulation logs file contains mistakes or if IOException occurs during reading
     * simulation logs file
     */
    private void updateArrays() {
        int ZOOM = Visualizer.getZoom();
        try {
            String wholeLine  = logs.readLine();
            if (wholeLine != null) {
                //divide the line to its 3 main parts (status, robots, objects)
                String[] dividedLine = wholeLine.split("&&", -1);
                if (dividedLine.length != 3) {
                    throw new RuntimeException("Simulation logs file is not correct");
                }

                status = unprefixString(dividedLine[0]);
                //divide lists of robots and objects to access individual components
                String[] robots, objects;
                if (dividedLine[1].contains(";;")) {
                    robots = dividedLine[1].split(";;", 0);
                } else if (dividedLine[1].isEmpty()) {
                    robots = new String[0];
                } else {
                    throw new RuntimeException("Simulation logs file is not correct");
                }

                if (dividedLine[2].contains(";;")) {
                    objects = dividedLine[2].split(";;", 0);
                } else if (dividedLine[2].isEmpty()) {
                    objects = new String[0];
                } else {
                    throw new RuntimeException("Simulation logs file is not correct");
                }
                //go through lists of robots and objects and parse them individually
                try {
                    for (int i = 0; i < robots.length; i++) {
                        String[] robotString = robots[i].split(",,", -1);
                        if (robotString.length != 4) {
                            throw new RuntimeException("Simulation logs file is not correct");
                        }
                        ComponentParameters r = robotCoordinates.get(i);
                        r.x = (float) Double.parseDouble(robotString[0]) * ZOOM;
                        r.y = (float) Double.parseDouble(robotString[1]) * ZOOM;
                        r.angle = (float) Math.toDegrees(Double.parseDouble(robotString[2]));
                        r.tag = unprefixString(robotString[3]);
                    }
                    for (int i = 0; i < objects.length; i++) {
                        String[] objectString = objects[i].split(",,", -1);
                        if (objectString.length != 4) {
                            throw new RuntimeException("Simulation logs file is not correct");
                        }
                        ComponentParameters o = objectCoordinates.get(i);
                        o.x = (float) Double.parseDouble(objectString[0]) * ZOOM;
                        o.y = (float) Double.parseDouble(objectString[1]) * ZOOM;
                        o.size = (float) Double.parseDouble(objectString[2]) * ZOOM;
                        o.tag = unprefixString(objectString[3]);
                    }
                } catch (NumberFormatException ex) {
                    throw new RuntimeException("Simulation logs file is not correct", ex);
                }
            }
            ++cycle;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes '\' prefixes from special characters used in simulation logs.
     * @param s string to unprefix
     * @return unprefixed string
     */
    private String unprefixString(String s) {
        char[] array = s.toCharArray();
        StringBuilder builder = new StringBuilder("");
        for (char c : array) {
            if (c == '&' || c == ',' || c == ';') {
                builder.deleteCharAt(builder.length() - 1);
                builder.append(c);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

}
