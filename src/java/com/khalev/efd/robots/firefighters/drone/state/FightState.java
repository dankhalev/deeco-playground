package com.khalev.efd.robots.firefighters.drone.state;

import com.khalev.efd.robots.firefighters.drone.DroneContext;


/**
 * In this state, robot actively extinguishes an encountered fire.
 *
 * @author Danylo Khalyeyev
 */
class FightState extends FirefighterState {

    FightState() {
        name = "FIGHT";
    }

    @Override
    FirefighterState makeDecision(DroneContext context) {
        if (context.energyTemperatureInput.data.maxDetectedTemperature < 45){
            return StateFactory.getInstance().getState("ROAM");
        }
        context.wheels.activateExtinguisher();
        context.wheels.setAction(1, context.energyTemperatureInput.data.temperatureVector);
        return this;
    }

}
