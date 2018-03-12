package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;
import cz.cuni.mff.d3s.deeco.playground.simulation.RobotPlacement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Map of the temperatures of the field. Evolves in time according to its "laws of physics". Temperatures are affected
 * by fires appearing randomly on the field, and fire extinguishers activated on robots.
 *
 * @author Danylo Khalyeyev
 */
class TemperatureMap {

    private FileOutputStream fileWriter;
    private int[][] values;
    private int sizeX;
    private int sizeY;
    private int generatorValue = 7;
    private int seedValue = 5;
    private int extinguisherSize = 3;
    private int threshold = 40;
    private int coolingValue = 20;
    private List<Fire> seeds = new ArrayList<>();

    TemperatureMap(String fileName, int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        //Initialize file for logging temperatures
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            fileWriter = new FileOutputStream(file);
            fileWriter.write(sizeX);
            fileWriter.write(sizeY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Initialize temperatures with random values
        values = new int[sizeX][sizeY];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = (int)(Math.random() * threshold);
            }
        }
    }

    /**
     * Computes the state of the temperature map for the next cycle
     * @param extinguishers list of coordinates of fire extinguishers that are active in the cycle
     */
    void computeNextCycle(List<Coordinates> extinguishers) {
        int[][] valueBuffer = new int[sizeX][sizeY];
        //Smoothing & Cooling
        for ( int i = 0; i < sizeX; i++ ) {
            for (int j = 0; j < sizeY; j++) {
                if (i == 0 || j == 0 || i == sizeX - 1 || j == sizeY - 1) {
                    valueBuffer[i][j] = (int)(Math.random() * threshold);
                } else {
                    valueBuffer[i][j] = ((values[i + 1][j] + values[i - 1][j] + values[i][j + 1] + values[i][j - 1]) / 4);
                }
                if (valueBuffer[i][j] > threshold && valueBuffer[i][j] < 2*threshold) {
                    valueBuffer[i][j] -= (int)(5 * Math.random());
                } else if (valueBuffer[i][j] < 250){
                    valueBuffer[i][j] += (int)(5 * Math.random());
                }
            }
        }
        //Generating fire
        if (Math.random() * generatorValue < 1) {
            int x = (int) (Math.random() * (sizeX - 10) + 5);
            int y = (int) (Math.random() * (sizeY - 10) + 5);
            seeds.add(new Fire(x,y,seedValue));
        }
        for (int i = seeds.size() - 1; i >= 0; i--) {
            Fire seed = seeds.get(i);
            if (seed.value > 0) {
                valueBuffer[seed.x][seed.y] = 250;
            } else {
                seeds.remove(i);
            }
        }
        values = valueBuffer;

        //Calculating an impact of fire extinguishers
        applyExtinguishers(extinguishers);

        //Each temperature value should be in range of 0-250
        for ( int i = 0; i < sizeX; i++ ) {
            for (int j = 0; j < sizeY; j++) {
                if (values[i][j] < 0) {
                    values[i][j] = 0;
                } else if (values[i][j] > 250) {
                    values[i][j] = 250;
                }
            }
        }
        //Writing logs
        try {
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    fileWriter.write(values[i][j]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calculates temperature data for each robot, based on its position.
     * @param robots list of robots.
     * @return list of temperature data for robots.
     */
    List<TemperatureData> getTemperatureData(List<RobotPlacement> robots) {
        List<TemperatureData> temperatures = new ArrayList<>();
        for (RobotPlacement r : robots) {
            int robotX = (int)r.getX();
            int robotY = (int)r.getY();
            int robotSize = (int)r.getSize();
            int startX = robotX - robotSize;
            int endX = robotX + robotSize;
            int startY = robotY - robotSize;
            int endY = robotY + robotSize;
            int maxTemperature = values[robotX][robotY];
            double angle = 0;
            for (int i = startX; i <= endX; i++ ) {
                for (int j = startY; j <= endY; j++) {
                    if (i >= 0 && j >= 0 && i < sizeX && j < sizeY) {
                        if (values[i][j] > maxTemperature) {
                            maxTemperature = values[i][j];
                            angle = Geometry.subjectiveAngleBetween(r.getX(), r.getY(), i, j, r.getAngle());
                        }
                    }
                }
            }
            temperatures.add(new TemperatureData(maxTemperature, angle));
        }
        return temperatures;
    }

    List<Coordinates> getFireCoordinates() {
        List<Coordinates> fires = new ArrayList<>();
        for (Fire fire : seeds) {
            fires.add(new Coordinates(fire.x, fire.y, 0));
        }
        return fires;
    }

    int getThreshold() {
        return threshold;
    }

    /**
     * Accounts for impact of fire extinguishers on temperatures.
     * @param extinguishers list of field coordinates of fire extinguishers that are active in this cycle.
     */
    private void applyExtinguishers(List<Coordinates> extinguishers) {
        for (Coordinates c : extinguishers) {
            int x = (int)c.x;
            int y = (int)c.y;
            for (int i = x - extinguisherSize; i <= x + extinguisherSize; i++) {
                for (int j = y - extinguisherSize; j <= y + extinguisherSize; j++) {
                    if (i >= 0 && j >=0 && i < sizeX && j < sizeY) {
                        values[i][j] -= coolingValue;
                        for (Fire seed : seeds) {
                            if (i == seed.x && j == seed.y) {
                                seed.value--;
                            }
                        }
                    }
                }
            }
        }
    }

    private class Fire {
        private final int x;
        private final int y;
        private int value;

        public Fire(int x, int y, int value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }
    }
}
