package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Action;
import com.khalev.efd.simulation.SensorySystem;
import com.khalev.efd.simulation.Wheels;

public class FirefighterWheels implements Wheels {

    static final double MAX_SPEED = 1.0;
    double rotationAngle = 0.0;
    double speed = 0.0;
    boolean isActionPossible1 = false;
    boolean isActionPossible2 = false;
    boolean actionSent = false;
    boolean isCoolerActivated = false;

    @Override
    public Action sendCurrentAction(int cycle) {
        EnergyAction action;
        if (this.isActionPossible1 || this.isActionPossible2) {
            action = new EnergyAction(this.speed * MAX_SPEED, this.rotationAngle);
            action.isCoolerActivated = isCoolerActivated;
            if (!isActionPossible1)
                isActionPossible2 = false;
            isActionPossible1 = false;
        } else {
            action = new EnergyAction(0,0);
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

