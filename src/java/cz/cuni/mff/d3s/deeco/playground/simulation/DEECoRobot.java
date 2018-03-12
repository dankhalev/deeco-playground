package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * This is the class that all robot components must extend.
 *
 * @author Danylo Khalyeyev
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
     * Contains all the sensors that are present on this robot; provides access to their inputs.
     */
    public SensorySystem sensor;

    /**
     * This object sends robot's actions to the environment.
     */
    public Wheels wheels;

    /**
     * Tag string attached to the robot. Can be changed at any time.
     */
    public String tag;

    public DEECoRobot() {
        this("");
    }

    private DEECoRobot(String tag) {
        this.tag = tag;
        sensor = new SensorySystem(rID);
        sensor.registerSensor(Environment.collisionSensorName);
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

    static void resetCounter() {
        RID = 0;
    }

    /**
     * Sets tag of this robot to a specified value. This method is always called during initialization of the simulation.
     * @param tag tag specified for this robot in scenario XML file
     */
    protected void setParameters(String tag) {
        this.tag = tag;
    }

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified for this robot in scenario XML file
     */
    protected void processArg(String arg) {

    }

}
