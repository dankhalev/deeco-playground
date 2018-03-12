package cz.cuni.mff.d3s.deeco.playground.visualization;

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

    private int cycles = 0;
    private BufferedReader logs;
    private List<ComponentParameters> robotCoordinates = new ArrayList<>();
    private List<ComponentParameters> objectCoordinates = new ArrayList<>();
    private List<String> statuses = new ArrayList<>();
    private List<List<ComponentParameters>> robotLogs = new ArrayList<>();
    private List<List<ComponentParameters>> objectLogs = new ArrayList<>();

    /**
     *
     * @param logs a BufferedReader to read simulation logs from. The header of the logfile should be already read in
     *             this reader.
     * @param robotCoordinates list of {@link ComponentParameters} for robots
     * @param objectCoordinates list of {@link ComponentParameters} for objects
     * @throws VisualizationParametersException if a simulation logs file is not correct
     */
    LogfileReader(BufferedReader logs, List<ComponentParameters> robotCoordinates,
                  List<ComponentParameters> objectCoordinates) throws VisualizationParametersException {
        this.logs = logs;
        this.robotCoordinates = robotCoordinates;
        this.objectCoordinates = objectCoordinates;
        processLogfile();
    }

    /**
     * Returns a list of {@link ComponentParameters} for robots for a given cycle.
     * @param cycle current cycle
     * @return a list of {@link ComponentParameters} for robots for a given cycle
     */
    List<ComponentParameters> getRobots(int cycle) {
        if (cycle < 0) {
            cycle = 0;
        }
        if (cycle > this.cycles) {
            cycle = this.cycles;
        }
        return robotLogs.get(cycle);
    }

    /**
     * Returns a list of {@link ComponentParameters} for objects for a given cycle.
     * @param cycle current cycle
     * @return a list of {@link ComponentParameters} for objects for a given cycle
     */
    List<ComponentParameters> getObjects(int cycle) {
        if (cycle < 0) {
            cycle = 0;
        }
        if (cycle > this.cycles) {
            cycle = this.cycles;
        }
        return objectLogs.get(cycle);
    }

    /**
     * Returns a status string for a cycle that is currently being visualized.
     * @param cycle current cycle
     * @return a status string for a cycle that is currently being visualized
     */
    String getStatus(int cycle) {
        if (cycle < 0) {
            cycle = 0;
        }
        if (cycle > this.cycles) {
            cycle = this.cycles;
        }
        return statuses.get(cycle);
    }

    /**
     * Reads the data for the next cycle from simulation logs; updates lists of {@link ComponentParameters} for robots
     * and objects.
     * @throws VisualizationParametersException if simulation logs file contains mistakes or if IOException occurs during reading
     * simulation logs file
     */
    private void processLogfile() throws VisualizationParametersException {
        int ZOOM = Visualizer.getZoom();
        try {
            String wholeLine  = logs.readLine();
            while (wholeLine != null) {
                //divide the line to its 3 main parts (status, robots, objects)
                String[] dividedLine = wholeLine.split("&&", -1);
                if (dividedLine.length != 3) {
                    throw new VisualizationParametersException("Simulation logs file is not correct");
                }

                statuses.add(unprefixString(dividedLine[0]));
                //divide lists of robots and objects to access individual components
                String[] robots, objects;
                if (dividedLine[1].contains(";;")) {
                    robots = dividedLine[1].split(";;", 0);
                } else if (dividedLine[1].isEmpty()) {
                    robots = new String[0];
                } else {
                    throw new VisualizationParametersException("Simulation logs file is not correct");
                }

                if (dividedLine[2].contains(";;")) {
                    objects = dividedLine[2].split(";;", 0);
                } else if (dividedLine[2].isEmpty()) {
                    objects = new String[0];
                } else {
                    throw new VisualizationParametersException("Simulation logs file is not correct");
                }
                if (robots.length != robotCoordinates.size() || objects.length != objectCoordinates.size()) {
                    throw new VisualizationParametersException("Simulation logs file is not correct");
                }
                List<ComponentParameters> robotList = new ArrayList<>();
                List<ComponentParameters> objectList = new ArrayList<>();
                //go through lists of robots and objects and parse them individually
                try {
                    for (int i = 0; i < robots.length; i++) {
                        String[] robotString = robots[i].split(",,", -1);
                        if (robotString.length != 4) {
                            throw new VisualizationParametersException("Simulation logs file is not correct");
                        }
                        ComponentParameters r = new ComponentParameters(robotCoordinates.get(i).size);
                        r.x = (float) Double.parseDouble(robotString[0]) * ZOOM;
                        r.y = (float) Double.parseDouble(robotString[1]) * ZOOM;
                        r.angle = (float) Math.toDegrees(Double.parseDouble(robotString[2]));
                        r.tag = unprefixString(robotString[3]);
                        robotList.add(r);
                    }
                    for (int i = 0; i < objects.length; i++) {
                        String[] objectString = objects[i].split(",,", -1);
                        if (objectString.length != 4) {
                            throw new VisualizationParametersException("Simulation logs file is not correct");
                        }
                        ComponentParameters o = new ComponentParameters();
                        o.x = (float) Double.parseDouble(objectString[0]) * ZOOM;
                        o.y = (float) Double.parseDouble(objectString[1]) * ZOOM;
                        o.size = (float) Double.parseDouble(objectString[2]) * ZOOM;
                        o.tag = unprefixString(objectString[3]);
                        objectList.add(o);
                    }
                    robotLogs.add(robotList);
                    objectLogs.add(objectList);
                } catch (NumberFormatException ex) {
                    throw new VisualizationParametersException("Simulation logs file is not correct", ex);
                }
                ++cycles;
                wholeLine  = logs.readLine();
            }
            logs.close();
        } catch (IOException e) {
            throw new VisualizationParametersException(e);
        }
        cycles = statuses.size() - 1;
        Visualizer.setCycles(cycles);
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
