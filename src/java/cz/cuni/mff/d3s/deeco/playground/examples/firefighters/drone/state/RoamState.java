package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;

/**
 * In this state, robot walks randomly through the field, until it detects an unusually high temperature level.
 *
 * @author Danylo Khalyeyev
 */
class RoamState extends FirefighterState {

    RoamState() {
        name = "ROAM";
    }

    @Override
    FirefighterState makeDecision(DroneContext context) {
        if (context.energyTemperatureInput.data.maxDetectedTemperature > 45) {
            return StateFactory.getInstance().getState("FOLLOW");
        }
        context.wheels.setAction(1, 0);
        if (Math.random() >= 0.95) {
            context.wheels.setAction(1, Math.PI * Math.random());
        }
        return this;
    }
}
