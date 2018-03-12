package cz.cuni.mff.d3s.deeco.playground.examples.firefighters;

import cz.cuni.mff.d3s.deeco.playground.simulation.CollisionData;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains some algorithms that help firefighters in their daily lives.
 *
 * @author Danylo Khalyeyev
 */
public class Algorithms {

    /**
     * Analyzes robot's collision data to determine whether a frontal collision has happened (thus, whether robot has
     * collided with something while moving forward).
     * @param collisionData robot's collision data
     * @return true if a frontal collision has happened
     */
    public static boolean detectFrontalCollision(CollisionData collisionData) {
        for (double collision : collisionData.collisionPoints) {
            if (collision < Math.PI / 2 && collision > - Math.PI / 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Constructs a trajectory (list of points on the field to follow) that helps robot to exit the collision.
     * @param coordinates current position of the robot
     * @param collisionData robot's collision data
     * @param type number of collision in a sequence
     * @return an avoidance trajectory
     */
    public static List<Coordinates> constructAvoidanceTrajectory(Coordinates coordinates,
                                                                 CollisionData collisionData, int type) {
        List<Coordinates> trajectory = new ArrayList<>();
        double angle1 = Geometry.normalizeAngle(coordinates.angle + Math.PI);
        double x1 = coordinates.x + 6 * Math.sin(angle1);
        double y1 = coordinates.y + 6 * Math.cos(angle1);
        trajectory.add(new Coordinates(x1, y1, 0));
        double collisionAngle = 0;
        for (double collision : collisionData.collisionPoints) {
            if (collision < Math.PI / 2 && collision > - Math.PI / 2) {
                collisionAngle = collision;
            }
        }
        if ((collisionAngle < 0 && type % 2 == 0) || (collisionAngle > 0 && type % 2 == 1)) {
            angle1 = Geometry.normalizeAngle(coordinates.angle + Math.PI / 4);
        } else {
            angle1 = Geometry.normalizeAngle(coordinates.angle - Math.PI / 4);
        }
        double x2 = x1 + 13 * Math.sin(angle1);
        double y2 = y1 + 13 * Math.cos(angle1);
        trajectory.add(new Coordinates(x2, y2, 0));
        return trajectory;
    }

}
