package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.Geometry;

/**
 * Order to recharge batteries at charging station.
 *
 * @author Danylo Khalyeyev
 */

class RechargeOrder extends FirefighterOrder {


    @Override
    public String execute(DroneContext context) {
        context.wheels.deactivateExtinguisher();
        if (Geometry.distance(context.coordinates.x, context.coordinates.y, context.charger.x, context.charger.y) > 4) {
            context.wheels.setAction(1, Geometry.subjectiveAngleBetween(context.coordinates, context.charger));
        } else {
            context.wheels.setAction(0,0);
        }
        return "RECHARGE";
    }

    @Override
    public Coordinates getDestination() {
        return new Coordinates(-1,-1,0);
    }

}
