package com.khalev.efd.simulation;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represents an input for robot's collision sensor.
 */
public class CollisionData {

    /**
     * List of collision points. Each value is double in range (-pi, pi) representing an angle of collision relative to
     * robot's front point
     */
    public ArrayList<Double> collisionPoints = new ArrayList<>();

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

    //TODO: remove this method
    /**
     * @param inp Object to copy
     * @return Copy of the specified object
     */
    public static CollisionData copy(CollisionData inp) {
        CollisionData collisionData = new CollisionData(new Action(inp.action.speed, inp.action.angle));
        collisionData.action.degreeOfRealization = inp.action.degreeOfRealization;
        for (int i = 0; i < inp.collisionPoints.size(); i++) {
            collisionData.collisionPoints.add(inp.collisionPoints.get(i));
        }
        return collisionData;
    }
}
