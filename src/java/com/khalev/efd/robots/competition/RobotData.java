package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.Coordinates;

/**
 * Packed data from robot's sensors. {@link ControlledRobot} send them to their {@link CommandStation}.
 *
 * @author Danylo Khalyeyev
 */
public class RobotData {

    Coordinates position;
    int energy;

    public RobotData(Coordinates position, int energy) {
        this.position = position;
        this.energy = energy;
    }
}
