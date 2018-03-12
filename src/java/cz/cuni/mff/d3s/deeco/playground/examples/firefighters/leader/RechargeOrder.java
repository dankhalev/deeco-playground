package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;

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
