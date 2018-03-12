package cz.cuni.mff.d3s.deeco.playground.examples.basic;

import cz.cuni.mff.d3s.deeco.playground.simulation.Action;
import cz.cuni.mff.d3s.deeco.playground.simulation.Wheels;

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

