package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Represents a collision that was found in initial parameters.
 *
 * @author Danylo Khalyeyev
 */
class Collision {

    Type type;
    int robot1;
    int robot2;

    Collision(Type type, int robot1, int robot2) {
        this.type = type;
        this.robot1 = robot1;
        this.robot2 = robot2;
    }

    /**
     * Represents a type of collision:
     *  WALL - collision between a robot and a physical obstacle
     *  ROBOT - collision between two robots
     *  NONE - no collision was found
     */
    enum Type {
        NONE, WALL, ROBOT
    }

}
