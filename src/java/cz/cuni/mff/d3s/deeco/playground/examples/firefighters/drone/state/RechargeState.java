package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;

/**
 * Robot switches to this state, when its energy level is low. It drives to charging station.
 *
 * @author Danylo Khalyeyev
 */
class RechargeState extends FirefighterState {

    RechargeState() {
        name = "RECHARGE";
    }

    @Override
    FirefighterState makeDecision(DroneContext context) {
        if (context.energyTemperatureInput.energy > 1000) {
            return StateFactory.getInstance().getState("ROAM");
        }
        context.wheels.setAction(1, Geometry.subjectiveAngleBetween(context.coordinates, context.charger));
        return this;
    }

    @Override
    FirefighterState checkCollision(DroneContext context) {
        if (Geometry.distance(context.coordinates.x, context.coordinates.y, context.charger.x, context.charger.y) <= 40) {
            return null;
        }
        return super.checkCollision(context);
    }

    @Override
    FirefighterState checkEnergy(DroneContext context) {
        return null;
    }

}
