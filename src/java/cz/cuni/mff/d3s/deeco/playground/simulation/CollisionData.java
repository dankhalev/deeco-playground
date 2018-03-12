package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Input for robot's collision sensor.
 *
 * @author Danylo Khalyeyev
 */
public class CollisionData {

    /**
     * List of collision points. Each value is a double in range (-pi, pi) representing an angle of collision relative to
     * robot's front point
     */
    public List<Double> collisionPoints = new ArrayList<>();

    /**
     * Action that was executed in the last cycle (or in the cycle this CollisionData object was created/obtained).
     */
    public Action action;

    public CollisionData(Action action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (!(o instanceof CollisionData)) {
            return false;
        }
        CollisionData inp = (CollisionData) o;
        return action.equals(inp.action) &&
                collisionPoints.equals(inp.collisionPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collisionPoints, action);
    }

}
