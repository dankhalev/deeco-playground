package cz.cuni.mff.d3s.deeco.playground.examples.predator;

import cz.cuni.mff.d3s.deeco.playground.simulation.Action;
import cz.cuni.mff.d3s.deeco.playground.simulation.Wheels;

/**
 * Wheels that do not allow to move and rotate at the same time.
 *
 * @author Danylo Khalyeyev
 */
public class SingleActionWheels implements Wheels {

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

