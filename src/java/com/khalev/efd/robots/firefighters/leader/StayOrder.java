package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.drone.DroneContext;

/**
 * Order to stay without moving.
 *
 * @author Danylo Khalyeyev
 */
class StayOrder extends FirefighterOrder {

    @Override
    public String execute(DroneContext context) {
        context.wheels.setAction(0,0);
        return "ROAM";
    }

}
