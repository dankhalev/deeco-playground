package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Action;

public class EnergyAction extends Action {

    public boolean recharge = false;
    public boolean isCoolerActivated = false;
    public boolean coordinatorDataRequest = false;

    public EnergyAction(double velocity, double rotation) {
        super(velocity, rotation);
    }
}
