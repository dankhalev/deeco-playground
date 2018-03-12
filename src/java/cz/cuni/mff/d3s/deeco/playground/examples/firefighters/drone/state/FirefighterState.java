package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.Algorithms;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.AutonomousMode;

/**
 * Represents a state of a firefighter robot that acts autonomously. Contains a template method
 * {@link FirefighterState#pickAction(DroneContext)} that calls other methods overrided in concrete states. This template
 * method is called by {@link AutonomousMode}.
 *
 * @author Danylo Khalyeyev
 */
public abstract class FirefighterState {

    String name;

    public final FirefighterState pickAction(DroneContext context) {
        context.wheels.deactivateExtinguisher();
        FirefighterState state = checkCollision(context);
        if (state != null) {
            return state;
        }
        state = checkEnergy(context);
        if (state != null) {
            return state;
        }
        return makeDecision(context);
    }

    FirefighterState checkCollision(DroneContext context) {
        if (Algorithms.detectFrontalCollision(context.collisionData)) {
            return StateFactory.getInstance().getAvoidState(context);
        }
        return null;
    }

    FirefighterState checkEnergy(DroneContext context) {
        if (context.energyTemperatureInput.energy < 300 && context.charger != null) {
            return StateFactory.getInstance().getState("RECHARGE");
        }
        return null;
    }

    abstract FirefighterState makeDecision(DroneContext context);

    public String getName() {
        return name;
    }

}
