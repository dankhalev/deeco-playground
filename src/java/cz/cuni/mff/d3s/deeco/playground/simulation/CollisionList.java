package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of collisions between robots and physical obstacles. When {@link SimulationEngine} notices a collision between
 * a robot and a physical obstacle, it adds it to the list. Later, when it generates inputs for collision sensors,
 * {@link CollisionList#computeCollisionsWithWalls} method is called to check which of those collisions are still actual
 * (some of them may not be actual due to subsequent rollbacks), and add them to collision inputs.
 *
 * @author Danylo Khalyeyev
 */
class CollisionList {

    private List<PotentialCollision>[] potentialCollisionList;

    CollisionList(int size) {
        @SuppressWarnings("unchecked")
        List<PotentialCollision>[] list = new ArrayList[size];
        potentialCollisionList = list;
        for (int i = 0; i < size; i++) {
            potentialCollisionList[i] = new ArrayList<>();
        }


    }

    /**
     * Adds a new collision to the list if it is not already present.
     * @param robotNumber a number of robot for which collision has occurred
     * @param x X-coordinate of the robot at the moment of collision
     * @param y Y-coordinate of the robot at the moment of collision
     * @param robotAngle a rotation angle of the robot at the moment of collision
     * @param angle a subjective angle of collision to the robot
     * @return true if a collision was added to the list, false if it is already in the list
     */
    boolean addWallCollision(int robotNumber, double x, double y, double robotAngle, double angle){
        for (PotentialCollision pc : potentialCollisionList[robotNumber]) {
            if (pc.subjectiveCollisionAngle == angle && pc.originalX == x && pc.originalY == y) {
                return false;
            }
        }
        potentialCollisionList[robotNumber].add(new PotentialCollision(angle, x, y, robotAngle));
        return true;
    }

    /**
     * Checks which of collisions that have been resolved by {@link SimulationEngine} are still actual (some of them may
     * not be actual due to subsequent rollbacks), and adds them to collision inputs. If robot has moved since that
     * collision has occurred, removes it from the list.
     * @param robots list of robots
     * @param inputs list of {@link CollisionData} that will be sent to robots' collision sensors
     */
    void computeCollisionsWithWalls(List<RobotPlacement> robots, List<CollisionData> inputs) {
        for (int i = 0; i < potentialCollisionList.length; i++) {
            for (int j = potentialCollisionList[i].size() - 1; j >= 0; j--) {
                PotentialCollision coll = potentialCollisionList[i].get(j);
                RobotPlacement r = robots.get(i);
                if (r.x.equals(coll.originalX) && r.y.equals(coll.originalY) && r.angle.equals(coll.originalAngle)) {
                    inputs.get(i).collisionPoints.add(coll.subjectiveCollisionAngle);
                } else {
                    potentialCollisionList[i].remove(j);
                }
            }
        }
    }

    private class PotentialCollision {
        double subjectiveCollisionAngle;
        double originalAngle;
        double originalX;
        double originalY;

        PotentialCollision(double subjectiveCollisionAngle, double originalX, double originalY, double originalAngle) {
            this.subjectiveCollisionAngle = subjectiveCollisionAngle;
            this.originalX = originalX;
            this.originalY = originalY;
            this.originalAngle = originalAngle;
        }
    }

}

