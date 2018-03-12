package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * A position of an object in the Environment. Can be used by {@link SensoryInputsProcessor}s to access object's
 * parameters.
 *
 * @author Danylo Khalyeyev
 */
public final class ObjectPlacement {

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
     * Returns an X-coordinate of the object.
     * @return X-coordinate of the object.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns a Y-coordinate of the object.
     * @return Y-coordinate of the object.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns a size of the object. Size is the distance from the center of the object to its edge.
     * @return size of the object
     */
    public double getSize() {
        return size;
    }

    /**
     * Returns a tag string attached to the object.
     * @return tag string attached to the object.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Returns the object's class name.
     * @return object's class name.
     */
    public String getObjectType() {
        return object.getClass().getName();
    }

}
