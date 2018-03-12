package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Interface for robot's wheels. Wheels are a part of the robot that is responsible for communicating actions to the
 * environment.
 *
 * @author Danylo Khalyeyev
 */
public interface Wheels {

    /**
     * This method is called by {@link RobotEnsemble} in each cycle to get an action from robot and transfer it to the
     * environment. Note, that due to the specifics of JDEECo implementation, this method is called twice in each cycle
     * but its side effects on the Wheels object are stored back only once. It is not possible to know in advance which one
     * of those calls will occur first and the state of the object may change between them (by means of user-defined processes).
     * @param cycle current cycle at the moment of call.
     * @return Action that this robot wants to perform in this cycle. May or may not be performed due to collisions with
     * other robots or physical obstacles.
     */
    Action sendCurrentAction(int cycle);

    /**
     * This method can be used to set speed and rotation angle to robot. It exists only for convenience and is not called
     * in the simulation code. Particular implementations of Wheels may choose not to use this method, but some other way
     * to determine the desired action instead.
     * @param speed speed component of an action
     * @param angle angle component of an action
     */
    void setAction(double speed, double angle);

}
