package com.khalev.efd.robots.firefighters.drone.state;

import com.khalev.efd.robots.firefighters.environment.EnergyTemperatureInput;
import com.khalev.efd.robots.firefighters.drone.DroneContext;

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
