package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Action;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.playground.simulation.Wheels;

/**
 * These wheels work only if a robot has at least some energy. Robots need to recharge from
 * time to time to keep going.
 *
 * @author Danylo Khalyeyev
 */
public class PoweredWheels implements Wheels {

    private static final double MAX_SPEED = 1.0;
    private double rotationAngle = 0.0;
    private double speed = 0.0;
    private boolean isActionPossible1 = false;
    private boolean isActionPossible2 = false;
    boolean actionSent = false;

    @Override
    public Action sendCurrentAction(int cycle) {
        Action action;
        if (this.isActionPossible1 || this.isActionPossible2) {
            action = new Action(this.speed * MAX_SPEED, this.rotationAngle);
            if (!isActionPossible1)
                isActionPossible2 = false;
            isActionPossible1 = false;
        } else {
            action = new Action(0,0);
        }
        actionSent = true;
        return action;

    }

    @Override
    public void setAction(double speed, double angle) {
        if (this.isActionPossible1 || this.isActionPossible2) {
            this.speed = speed;
            this.rotationAngle = angle;
            this.actionSent = false;
        }
    }

    public void provideEnergy(SensorySystem sensorySystem) {
        EnergyInput energyInput = sensorySystem.getInputFromSensor("energy", EnergyInput.class);
        isActionPossible1 = energyInput != null && energyInput.energy > 0;
        isActionPossible2 = isActionPossible1;
    }
}
