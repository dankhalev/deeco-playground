package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;

/**
 * Orders that {@link CommandStation} sends to {@link ControlledRobot}s.
 *
 * @author Danylo Khalyeyev
 */
public class RobotOrder {

    Type type;
    Coordinates target;

    public RobotOrder(Type type, Coordinates target) {
        this.type = type;
        this.target = target;
    }

    enum Type {
        FOLLOW, RECHARGE, STAY
    }
}
