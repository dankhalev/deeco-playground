package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.Coordinates;

public class RobotData {

    Coordinates position;
    int energy;

    public RobotData(Coordinates position, int energy) {
        this.position = position;
        this.energy = energy;
    }
}
