package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.wheels;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureInput;
import cz.cuni.mff.d3s.deeco.playground.simulation.Action;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.playground.simulation.Wheels;

/**
 * Basic wheels for firefighter robot. They will execute a correct action only if provided with energy. If robot wants to
 * move, it has to call {@link FirefighterWheels#provideEnergy(SensorySystem)} method before choosing an action. Only if
 * robot has enough (more than 0) energy, these wheels will execute it.
 * These wheels also allow firefighter to turn its fire extinguisher on or off through corresponding methods.
 *
 * @author Danylo Khalyeyev
 */
public class FirefighterWheels implements Wheels {

    static final double MAX_SPEED = 1.0;
    double rotationAngle = 0.0;
    double speed = 0.0;
    int allowedCycle = -1;
    int lastCycle = -1;
    boolean actionSent = false;
    boolean isExtinguisherActivated = false;

    @Override
    public Action sendCurrentAction(int cycle) {
        lastCycle = cycle;
        EnergyTemperatureAction action = new EnergyTemperatureAction(0,0);
        if (cycle <= allowedCycle) {
            action = new EnergyTemperatureAction(this.speed * MAX_SPEED, this.rotationAngle);
            action.isExtinguisherActivated = isExtinguisherActivated;
        }
        actionSent = true;
        return action;
    }

    @Override
    public void setAction(double speed, double angle) {
        this.speed = speed;
        this.rotationAngle = angle;
        this.actionSent = false;
    }

    public void provideEnergy(SensorySystem sensorySystem) {
        EnergyTemperatureInput energyTemperatureInput = sensorySystem.getInputFromSensor("energy", EnergyTemperatureInput.class);
        if (energyTemperatureInput != null && energyTemperatureInput.energy > 0) {
            allowedCycle = lastCycle + 2;
        }
    }

    public void activateExtinguisher() {
        isExtinguisherActivated = true;
    }

    public void deactivateExtinguisher() {
        isExtinguisherActivated = false;
    }

}

