package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.simulation.Coordinates;

/**
 * Order to extinguish the fire at some particular point of the field.
 *
 * @author Danylo Khalyeyev
 */
class FightOrder extends FollowOrder {

    FightOrder(Coordinates target) {
        super(target);
    }

    @Override
    public String execute(DroneContext context) {
        super.execute(context);
        context.wheels.activateExtinguisher();
        return "FIGHT";
    }

}
