package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureInput;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;

/**
 * In this state, robot follows a temperature vector, to get to the fire.
 *
 * @author Danylo Khalyeyev
 */
class FollowState extends FirefighterState {

    FollowState() {
        name = "FOLLOW";
    }

    @Override
    FirefighterState makeDecision(DroneContext context) {
        if (worthFighting(context.energyTemperatureInput)) {
            return StateFactory.getInstance().getState("FIGHT");
        }
        if (context.energyTemperatureInput.data.maxDetectedTemperature < 45){
            return StateFactory.getInstance().getState("ROAM");
        }
        context.wheels.setAction(1, context.energyTemperatureInput.data.temperatureVector);
        return this;
    }

    private boolean worthFighting(EnergyTemperatureInput energyTemperatureInput) {
        return energyTemperatureInput.damage > 4 ||
                (energyTemperatureInput.data.temperatureVector == 0 && energyTemperatureInput.data.maxDetectedTemperature >= 45);
    }
}
