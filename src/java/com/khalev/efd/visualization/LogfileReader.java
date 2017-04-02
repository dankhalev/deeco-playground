package com.khalev.efd.visualization;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

class LogfileReader {

    private int cycle = -1;

    private BufferedReader logs;
    private ArrayList<ComponentParameters> robotCoordinates = new ArrayList<ComponentParameters>();
    private ArrayList<ComponentParameters> objectCoordinates = new ArrayList<>();
    private String status = "";

    LogfileReader(BufferedReader logs, ArrayList<ComponentParameters> robotCoordinates, ArrayList<ComponentParameters> objectCoordinates) {
        this.logs = logs;
        this.robotCoordinates = robotCoordinates;
        this.objectCoordinates = objectCoordinates;
    }

    ArrayList<ComponentParameters> getRobots(int cycle) {
        if (cycle != this.cycle) {
            updateArrays();
        }
        assert cycle  == this.cycle: "Cycles are not synchronized";
        return robotCoordinates;
    }

    ArrayList<ComponentParameters> getObjects(int cycle) {
        if (cycle != this.cycle) {
            updateArrays();
        }
        assert cycle == this.cycle: "Cycles are not synchronized";
        return objectCoordinates;
    }

    private void updateArrays() {
        int ZOOM = Visualizer.getZoom();
        try {
            String s;
            if ((s = logs.readLine()) != null) {
                String[] ss = s.split("&&", -1);
                String[] robots, objects;
                if (ss.length != 3) {
                    throw new RuntimeException("Simulation logs file is not correct");
                }
                status = unprefixString(ss[0]);
                if (ss[1].contains(";;")) {
                    robots = ss[1].split(";;", 0);
                } else if (ss[1].isEmpty()) {
                    robots = new String[0];
                } else {
                    throw new RuntimeException("Simulation logs file is not correct");
                }
                if (ss[2].contains(";;")) {
                    objects = ss[2].split(";;", 0);
                } else if (ss[2].isEmpty()) {
                    objects = new String[0];
                } else {
                    throw new RuntimeException("Simulation logs file is not correct");
                }
                try {
                    for (int i = 0; i < robots.length; i++) {
                        String[] sss = robots[i].split(",,", -1);
                        if (sss.length != 4) {
                            throw new RuntimeException("Simulation logs file is not correct");
                        }
                        ComponentParameters r = robotCoordinates.get(i);
                        r.x = (float) Double.parseDouble(sss[0]) * ZOOM;
                        r.y = (float) Double.parseDouble(sss[1]) * ZOOM;
                        r.angle = (float) Math.toDegrees(Double.parseDouble(sss[2]));
                        r.tag = unprefixString(sss[3]);
                    }
                    for (int i = 0; i < objects.length; i++) {
                        String[] sss = objects[i].split(",,", -1);
                        if (sss.length != 4) {
                            throw new RuntimeException("Simulation logs file is not correct");
                        }
                        ComponentParameters o = objectCoordinates.get(i);
                        o.x = (float) Double.parseDouble(sss[0]) * ZOOM;
                        o.y = (float) Double.parseDouble(sss[1]) * ZOOM;
                        o.size = (float) Double.parseDouble(sss[2]) * ZOOM;
                        o.tag = unprefixString(sss[3]);
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

    String getStatus() {
        return status;
    }
}
