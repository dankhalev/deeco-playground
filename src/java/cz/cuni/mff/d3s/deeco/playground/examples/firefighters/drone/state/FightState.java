package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;


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
