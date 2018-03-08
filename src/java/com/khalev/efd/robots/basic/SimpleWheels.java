package com.khalev.efd.robots.basic;

import com.khalev.efd.simulation.Action;
import com.khalev.efd.simulation.Wheels;

/**
 * The most simple wheels that work.
 *
 * @author Danylo Khalyeyev
 */
public class SimpleWheels implements Wheels {

    public double rotationAngle = 0.0;
    public double speed = 1.0;

    @Override
    public Action sendCurrentAction(int cycle) {
        return new Action(this.speed, this.rotationAngle);
    }

    @Override
    public void setAction(double speed, double angle) {
        this.speed = speed;
        this.rotationAngle = angle;
    }

}

