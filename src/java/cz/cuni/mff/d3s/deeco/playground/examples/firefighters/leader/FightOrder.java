package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;

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
