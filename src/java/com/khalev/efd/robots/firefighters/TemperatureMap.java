package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.Geometry;
import com.khalev.efd.simulation.RobotPlacement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemperatureMap {

    FileOutputStream fileWriter;
    int[][] values;
    int size = 100;
    int generatorValue = 7;
    int generatorDecrease = 2;
    int threshold = 40;
    int coolingValue = 20;
    List<Coordinates> seeds = new ArrayList<>();

    public TemperatureMap(String file) {
        try {
            fileWriter = new FileOutputStream(new File(file));
            fileWriter.write(size);
            fileWriter.write(size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        values = new int[size][size];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = (int)(Math.random() * threshold);
            }
        }
    }

    void cycle(List<Coordinates> coolers) {
        int[][] valueBuffer = new int[size][size];
        //Smoothing & Cooling
        for ( int i = 0; i < size; i++ ) {
            for (int j = 0; j < size; j++) {
                if (i == 0 || j == 0 || i == size - 1 || j == size - 1) {
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
            int x = (int) (Math.random() * (size - 10) + 5);
            int y = (int) (Math.random() * (size - 10) + 5);
            seeds.add(new Coordinates(x,y,5));
        }
        for (int i = seeds.size() - 1; i >= 0; i--) {
            Coordinates seed = seeds.get(i);
            if (seed.angle > 0) {
                int x = (int) seed.x;
                int y = (int) seed.y;
                valueBuffer[x][y] = 250;
            } else {
                seeds.remove(i);
            }
        }
        values = valueBuffer;
        applyCoolers(coolers);
        for ( int i = 0; i < size; i++ ) {
            for (int j = 0; j < size; j++) {
                if (values[i][j] < 0) {
                    values[i][j] = 0;
                } else if (values[i][j] > 250) {
                    values[i][j] = 250;
                }
            }
        }
        //Writing logs
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    fileWriter.write(values[i][j]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TemperatureData> getTemperatureData(List<RobotPlacement> robots) {
        List<TemperatureData> temperatures = new ArrayList<>();
        for (RobotPlacement r : robots) {
            int robotX = (int)r.getX();
            int robotY = (int)r.getY();
            int startX = (int)r.getX() - (int)r.getSize();
            int endX = (int)r.getX() + (int)r.getSize();
            int startY = (int)r.getY() - (int)r.getSize();
            int endY = (int)r.getY() + (int)r.getSize();
            int maxTemperature = values[robotX][robotY];
            double angle = 0;
            for (int i = startX; i <= endX; i++ ) {
                for (int j = startY; j <= endY; j++) {
                    if (i >= 0 && j >= 0 && i < size && j < size) {
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

    private void applyCoolers(List<Coordinates> coolers) {
        for (Coordinates c : coolers) {
            int x = (int)c.x;
            int y = (int)c.y;
            for (int i = x - 3; i <= x + 3; i++) {
                for (int j = y - 3; j <= y + 3; j++) {
                    if (i >= 0 && j >=0 && i < size && j < size) {
                        values[i][j] -= coolingValue;
                        for (Coordinates seed : seeds) {
                            if (i == (int)seed.x && j == (int)seed.y) {
                                seed.angle -= 1;
                            }
                        }
                    }
                }
            }
        }
    }
}
