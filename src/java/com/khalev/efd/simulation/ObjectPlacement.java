package com.khalev.efd.simulation;

/**
 * Represents position of an object in the Environment. Can be used by input processors to access object's parameters.
 */
public class ObjectPlacement {

    DEECoObject object;

    Double x;
    Double y;
    Double size;
    String tag;
    final int id;

    ObjectPlacement(DEECoObject object, Double x, Double y, Double size, String tag) {
        this.object = object;
        this.x = x;
        this.y = y;
        this.size = size;
        this.tag = tag;
        this.id = object.oID;
    }

    /**
     * @return X-coordinate of the object.
     */
    public double getX() {
        return x;
    }

    /**
     * @return Y-coordinate of the object.
     */
    public double getY() {
        return y;
    }

    /**
     * @return Size of the object. Size is distance from the center of the object to its edge.
     */
    public double getSize() {
        return size;
    }

    /**
     * @return Tag string attached to the object.
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return Object's class name.
     */
    public String getObjectType() {
        return object.getClass().getName();
    }
}
