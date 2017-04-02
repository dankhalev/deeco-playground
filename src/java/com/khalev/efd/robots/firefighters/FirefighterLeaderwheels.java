package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Action;
import com.khalev.efd.simulation.SensorySystem;

public class FirefighterLeaderwheels extends FirefighterWheels {

    boolean requestCoordinatorData = false;
    static final int requestThreshold = 300;
    @Override
    public Action sendCurrentAction(int cycle) {
        EnergyAction action;
        if (this.isActionPossible1 || this.isActionPossible2) {
            action = new EnergyAction(this.speed * MAX_SPEED, this.rotationAngle);
            action.isCoolerActivated = isCoolerActivated;
            action.coordinatorDataRequest = requestCoordinatorData;
            if (!isActionPossible1)
                isActionPossible2 = false;
            isActionPossible1 = false;
        } else {
            action = new EnergyAction(0,0);
        }
        actionSent = true;
        return action;

    }

    public void provideEnergy(SensorySystem sensorySystem) {
        EnergyInput energyInput = sensorySystem.getInputFromSensor("energy", EnergyInput.class);
        isActionPossible1 = energyInput != null && energyInput.energy > 0;
        isActionPossible2 = isActionPossible1;
        requestCoordinatorData = energyInput != null && energyInput.energy > requestThreshold;
    }
}
