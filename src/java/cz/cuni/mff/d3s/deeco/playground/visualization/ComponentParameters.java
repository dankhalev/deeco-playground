package cz.cuni.mff.d3s.deeco.playground.visualization;

/**
 * Represents coordinates and other parameters (tag, size and rotation angle) of a component (either robot or object).
 *
 * @author Danylo Khalyeyev
 */
class ComponentParameters {

    float x, y, angle, size;
    String tag = "";

    /**
     * A constructor that should be used to create {@link ComponentParameters} for a robot (since robot's size cannot
     * change).
     * @param size a size of the robot
     */
    ComponentParameters(double size) {
        this.size = (float) size;
    }

    /**
     * A constructor that should be used to create {@link ComponentParameters} for an object (since objects do not have
     * a rotation angle).
     */
    ComponentParameters() {
        this.angle = 0f;
    }

}
