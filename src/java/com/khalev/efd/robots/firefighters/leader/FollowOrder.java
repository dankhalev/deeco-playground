package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.Geometry;

/**
 * Order to follow to some particular point of the field.
 *
 * @author Danylo Khalyeyev
 */
class FollowOrder extends FirefighterOrder {

    Coordinates target;

    FollowOrder(Coordinates target) {
        this.target = target;
    }

    @Override
    public String execute(DroneContext context) {
        context.wheels.deactivateExtinguisher();
        context.wheels.setAction(1, Geometry.subjectiveAngleBetween(context.coordinates, target));
        return "FOLLOW";
    }

    @Override
    public Coordinates getDestination() {
        return target;
    }

}
