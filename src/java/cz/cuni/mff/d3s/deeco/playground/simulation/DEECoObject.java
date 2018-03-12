package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * This is the class that all object components must extend. Object can change its position, size and tag at any time.
 *
 * @author Danylo Khalyeyev
 */
public class DEECoObject {

    /**
     * This field is required for any DEECo component by JDEECo implementation
     */
    public String id;

    /**
     * Coordinates of the object in the environment
     */
    public Coordinates position;

    /**
     * Tag string attached to the object.
     */
    public String tag;

    /**
     * Size of the object. Measures distance from object's center (position) to its side (the same thing as radius).
     */
    public Double size;

    /**
     * Unique ID of an object. Must not be changed during simulation.
     */
    public final Integer oID = OID++;

    private final int rID = -1;

    private static int OID = 0;

    public DEECoObject() {
        this(0.0, 0.0, "", 0.0);
    }

    private DEECoObject(Double x, Double y, String tag, Double size) {
        this.position = new Coordinates(x, y, 0);
        this.tag = tag;
        this.size = size;
    }

    static void resetCounter() {
        OID = 0;
    }

    /**
     * Sets parameters of this object to specified values. This method is always called during initialization of the
     * simulation.
     * @param x X-coordinate specified for this object in scenario XML file
     * @param y Y-coordinate specified for this object in scenario XML file
     * @param tag tag specified for this object in scenario XML file
     * @param size size specified for this object in scenario XML file
     */
    protected void setParameters(Double x, Double y, String tag, Double size) {
        this.position = new Coordinates(x, y, 0);
        this.tag = tag;
        this.size = size;
    }

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified for this object in scenario XML file
     */
    protected void processArg(String arg) {

    }

}
