package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Represents a position of a robot in the Environment. Can be used by input processors to access robot's parameters.
 *
 * @author Danylo Khalyeyev
 */
public final class RobotPlacement {

    DEECoRobot robot;
    Double x;
    Double y;
    Double angle;
    final int id;
    final Double size;
    final Double sizeSquared;
    String tag;

    /**
     * Action that was taken by this robot in current cycle.
     */
    public Action currentAction = new Action(0,0);

    RobotPlacement(DEECoRobot robot, Double x, Double y, Double angle, Double size, String tag) {
        this.robot = robot;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.tag = tag;
        this.size = size;
        this.sizeSquared =size*size;
        this.id = robot.rID;
    }

    private RobotPlacement(int id, double size) {
        this.id = id;
        this.size = size;
        this.sizeSquared =size*size;
    }

    RobotPlacement copy() {
        RobotPlacement rp = new RobotPlacement(id, size);
        rp.x = x;
        rp.y = y;
        rp.robot = robot;
        rp.angle = angle;
        rp.tag = tag;
        rp.currentAction = currentAction;
        return rp;
    }

    /**
     * @return X-coordinate of the robot.
     */
    public double getX() {
        return x;
    }

    /**
     * @return Y-coordinate of the robot.
     */
    public double getY() {
        return y;
    }

    /**
     * @return Radius of the robot.
     */
    public double getSize() {
        return this.size;
    }

    /**
     * @return Angle of robot's rotation in the environment
     */
    public double getAngle() {
        return angle;
    }

    /**
     * @return Tag string attached to the robot.
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return Robot's class name.
     */
    public String getRobotType() {
        return robot.getClass().getName();
    }

}
