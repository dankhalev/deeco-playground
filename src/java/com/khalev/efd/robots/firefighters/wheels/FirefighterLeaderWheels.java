package com.khalev.efd.robots.firefighters.wheels;

import com.khalev.efd.robots.firefighters.environment.EnergyTemperatureInput;
import com.khalev.efd.simulation.Action;
import com.khalev.efd.simulation.SensorySystem;

/**
 * These wheels are mostly the same as {@link FirefighterWheels} they extend. However, in addition they send requests of
 * extended temperature data, provided the robot has enough energy (see {@link EnergyTemperatureInput}. Because of this,
 * these wheels have higher energy consumption.
 *
 * @author Danylo Khalyeyev
 */
public class FirefighterLeaderWheels extends FirefighterWheels {

    private boolean requestCoordinatorData = false;
    public static final int requestThreshold = 300;

    @Override
    public Action sendCurrentAction(int cycle) {
        lastCycle = cycle;
        EnergyTemperatureAction action = new EnergyTemperatureAction(0,0);
        if (cycle <= allowedCycle) {
            action = new EnergyTemperatureAction(this.speed * MAX_SPEED, this.rotationAngle);
            action.isExtinguisherActivated = isExtinguisherActivated;
            action.coordinatorDataRequest = requestCoordinatorData;
        }
        actionSent = true;
        return action;
    }

    public void provideEnergy(SensorySystem sensorySystem) {
        EnergyTemperatureInput energyTemperatureInput = sensorySystem.getInputFromSensor("energy", EnergyTemperatureInput.class);
        if (energyTemperatureInput != null && energyTemperatureInput.energy > 0) {
            allowedCycle = lastCycle + 2;
            requestCoordinatorData = energyTemperatureInput.energy > requestThreshold;
        }
    }
}
