package com.khalev.efd.simulation;

/**
 * This is the class that all object components must extend. Object can change its position, size and tag at any time.
 */
public class DEECoObject {

    /**
     * This field is required for any DEECo component by JDEECo implementation
     */
    public String id;

    /**
     * Coordinates of an object in the environment
     */
    public Coordinates position;

    /**
     * Tag string attached to object.
     */
    public String tag;

    /**
     * Size of an object. Measures distance from object's center (position) to its side.
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

    DEECoObject(Double x, Double y, String tag, Double size) {
        this.position = new Coordinates(x, y, 0);
        this.tag = tag;
        this.size = size;
    }

    /**
     * Sets parameters of this object to specified values. This method is always called during initialization of the
     * simulation.
     */
    protected void setParameters(Double x, Double y, String tag, Double size) {
        this.position = new Coordinates(x, y, 0);
        this.tag = tag;
        this.size = size;
    }

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified in simulation parameters XML file
     */
    protected void processArg(String arg) {

    }
}
