package com.khalev.efd.robots.firefighters.drone.mode;

import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.robots.firefighters.leader.FirefighterOrder;

/**
 * Represents a firefighter robot's operating mode. Can be executed on provided {@link DroneContext}.
 *
 * @author Danylo Khalyeyev
 */
public abstract class FirefighterMode {

    private FirefighterOrder order;

    public abstract String execute(DroneContext context);

    public void receiveOrder(FirefighterOrder order) {
        this.order = order;
    }

}
