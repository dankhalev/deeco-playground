package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;

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
