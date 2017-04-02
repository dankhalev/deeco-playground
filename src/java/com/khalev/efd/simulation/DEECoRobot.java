package com.khalev.efd.simulation;

/**
 * This is the class that all robot components must extend.
 */
public class DEECoRobot {

    /**
     * This field is required for any DEECo component by JDEECo implementation
     */
    public String id;

    /**
     * Unique ID of a robot. Must not be changed during simulation.
     */
    public final Integer rID = RID++;
    private static int RID = 0;
    private final int oID = -1;

    /**
     * Contains all sensors that are present on this robot.
     */
    public SensorySystem sensor;

    /**
     * This object is used to send actions to environment.
     */
    public Wheels wheels;

    /**
     * Tag string attached to robot. Can be changed at any time.
     */
    public String tag;

    public DEECoRobot() {
        this("");
    }

    DEECoRobot(String tag) {
        this.tag = tag;
        sensor = new SensorySystem();
        sensor.registerSensor("collisions", CollisionData.class);
        wheels = new Wheels() {
            @Override
            public Action sendCurrentAction(int cycle) {
                return new Action(0,0);
            }

            @Override
            public void setAction(double speed, double angle) {

            }
        };
    }

    /**
     * Sets tag of this robot to specified value. This method is always called during initialization of the simulation.
     */
    protected void setParameters(String tag) {
        this.tag = tag;
    }

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified in simulation parameters XML file
     */
    protected void processArg(String arg) {

    }
}
