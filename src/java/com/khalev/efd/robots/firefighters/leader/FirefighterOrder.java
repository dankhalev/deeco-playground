package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.robots.firefighters.drone.FirefighterDrone;
import com.khalev.efd.simulation.Coordinates;

/**
 * Represents an order issued by {@link FirefighterLeader} to {@link FirefighterDrone}. Can be executed automatically on
 * provided {@link DroneContext}.
 *
 * @author Danylo Khalyeyev
 */
public abstract class FirefighterOrder {

    public abstract String execute(DroneContext context);

    public Coordinates getDestination() {
        return null;
    }
}
