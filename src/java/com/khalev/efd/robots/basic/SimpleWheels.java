package com.khalev.efd.robots.basic;

import com.khalev.efd.simulation.Action;
import com.khalev.efd.simulation.Wheels;

public class SimpleWheels implements Wheels {

    private static final double MAX_SPEED = 1.0;
    public double rotationAngle = 0.0;
    public double speed = 1.0;

    @Override
    public Action sendCurrentAction(int cycle) {
        if (this.speed != 0) {
            this.rotationAngle = 0;
        }
        return new Action(this.speed * MAX_SPEED, this.rotationAngle);
    }

    @Override
    public void setAction(double speed, double angle) {
        this.speed = speed;
        this.rotationAngle = angle;
    }

}

