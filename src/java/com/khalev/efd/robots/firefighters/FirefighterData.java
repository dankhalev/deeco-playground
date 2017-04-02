package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;

public class FirefighterData {

    Coordinates position;
    int energy;

    public FirefighterData(Coordinates position, int energy) {
        this.position = position;
        this.energy = energy;
    }
}
