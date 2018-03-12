package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.Objects;

/**
 * This class represents an action that a robot can perform in a cycle of simulation. Each action consists of two
 * components: speed and rotation angle.
 *
 * @author Danylo Khalyeyev
 */
public class Action {

    /**
     * Maximum speed allowed to robots by simulation
     */
    public static final double MAX_SPEED = 1.0;

    /**
     * Type of action: STAY, MOVE, ROTATE, ROTATE_AND_MOVE
     */
    public final Type type;

    public final double speed;

    public final double angle;

    /**
     * Can have values from 0.0 to 1.0. Represents a fraction of action that was performed. If less than 1.0, collision
     * has happened. For STAY and ROTATE types of action always equals 0.0, even though those actions are always fully
     * performed.
     */
    public double degreeOfRealization;

    /**
     * Creates new Action for specified speed and rotation angle. Action's type is determined from those values.
     * @param speed Speed in units per cycle. Cannot be less than 0.0 or more than {@value #MAX_SPEED}. In case when
     *              this constructor receives a value outside of those boundaries, it is automatically reduced/increased
     *              to the boundary.
     * @param rotation Rotation angle in radians
     */
    public Action(double speed, double rotation) {
        if (speed != 0 && rotation != 0) {
            type = Type.ROTATE_AND_MOVE;
            if (speed > MAX_SPEED) {
                this.speed = MAX_SPEED;
            } else if (speed < 0.0) {
                this.speed = 0.0;
            } else {
                this.speed = speed;
            }
            angle = Geometry.normalizeAngle(rotation);
        } else if (speed != 0) {
            type = Type.MOVE;
            if (speed > MAX_SPEED) {
                this.speed = MAX_SPEED;
            } else if (speed < 0.0) {
                this.speed = 0.0;
            } else {
                this.speed = speed;
            }
            angle = 0;
        } else if (rotation != 0) {
            type = Type.ROTATE;
            this.speed = 0;
            angle = Geometry.normalizeAngle(rotation);
        } else {
            type = Type.STAY;
            this.speed = 0;
            angle = 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof Action)) {
            return false;
        }
        Action act = (Action) o;
        return speed == act.speed &&
                angle == act.angle &&
                degreeOfRealization == act.degreeOfRealization &&
                type == act.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(speed, angle, degreeOfRealization, type);
    }

    public enum Type {
        STAY, MOVE, ROTATE, ROTATE_AND_MOVE
    }
}
