package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.NaN;

/**
 * This class serves two main functions: it moves robots through the environment according to their {@link Action}s;
 * and it generates inputs for robots' collision sensors. In each simulation cycle {@link SimulationEngine#performActions}
 * method is called followed by {@link SimulationEngine#sendInputs} call.
 *
 * @author Danylo Khalyeyev
 */
class SimulationEngine extends SensoryInputsProcessor<CollisionData> {

    private List<RobotPlacement> robots;
    private EnvironmentMap environmentMap;
    private CollisionList collisionList;
    private List<Action> actions;
    private static final double ST_ERR = 0.01;

    SimulationEngine(List<RobotPlacement> robots, EnvironmentMap map) {
        this.environmentMap = map;
        this.collisionList = new CollisionList(robots.size());
        this.robots = robots;
    }

    /**
     * Computes new positions for robots in this cycle by performing their actions and resolving resulting collisions.
     * First, it moves robots forward, then it resolves collisions with physical obstacles, and finally it resolves
     * collisions between robots themselves.
     * @param actions list of robots' actions
     * @return a list of new robot placements, after all actions are performed and all collisions are resolved.
     */
    List<RobotPlacement> performActions(List<Action> actions) {
        this.actions = actions;
        // --- Creating a placement list for the next cycle:
        List<RobotPlacement> next = new ArrayList<>();
        for (RobotPlacement rp : robots) {
            next.add(rp.copy());
        }

        moveRobots(actions, next);
        resolveRobotWallCollisions(actions, next);
        resolveRobotRobotCollisions(actions, next);

        this.robots = next;
        return this.robots;
    }

    /**
     * Changes positions of robots according to their actions.
     * @param actions a list of robots' actions
     * @param newRobotPlacements resulting list of {@link RobotPlacement}s
     */
    private void moveRobots(List<Action> actions, List<RobotPlacement> newRobotPlacements) {
        for (int i = 0; i < actions.size(); i++) {
            Action act = actions.get(i);
            RobotPlacement r = newRobotPlacements.get(i);

            switch (act.type) {
                case ROTATE:
                    r.angle = Geometry.normalizeAngle(r.angle + act.angle);
                    act.degreeOfRealization = 0;
                    break;
                case STAY:
                    act.degreeOfRealization = 0;
                    break;
                case MOVE:
                    r.x += act.speed * Math.sin(r.angle);
                    r.y += act.speed * Math.cos(r.angle);
                    act.degreeOfRealization = 1;
                    break;
                case ROTATE_AND_MOVE:
                    r.angle = Geometry.normalizeAngle(r.angle + act.angle);
                    robots.get(i).angle = r.angle;
                    r.x += act.speed * Math.sin(r.angle);
                    r.y += act.speed * Math.cos(r.angle);
                    act.degreeOfRealization = 1;
                    break;
            }
        }
    }

    /**
     * Resolves all collisions between robots and physical obstacles by rolling robots back in time to the point of
     * collision. Adds all potential collisions to {@link SimulationEngine#collisionList}.
     * @param actions list of robots' actions
     * @param newRobotPlacements list of robots' placements
     */
    private void resolveRobotWallCollisions(List<Action> actions, List<RobotPlacement> newRobotPlacements) {
        //first, we are adding all the robots to the list to test them for collisions
        List<Integer> wallCollisionList = new ArrayList<>();
        for (int i = 0; i < robots.size(); i++) {
            wallCollisionList.add(i);
        }

        while (!wallCollisionList.isEmpty()) {
            int i = wallCollisionList.get(0);
            RobotPlacement r = newRobotPlacements.get(i);
            RobotPlacement rp = robots.get(i);
            Action act = actions.get(i);
            boolean noCollision = true;
            //then we go through the surrounding lines to test collisions with them
            int lowerVerticalBound = (int) Math.max(Math.ceil(r.x - r.size - ST_ERR), 0);
            int upperVerticalBound = (int) Math.min(Math.floor(r.x + r.size + ST_ERR), environmentMap.sizeX);
            int lowerHorizontalBound = (int) Math.max(Math.ceil(r.y - r.size - ST_ERR), 0);
            int upperHorizontalBound = (int) Math.min(Math.floor(r.y + r.size + ST_ERR), environmentMap.sizeY);

            for (int v = lowerVerticalBound; v <= upperVerticalBound; v++) {
                for (Line line : environmentMap.vertical[v]) {
                    if ((line.start <= r.y + r.size) && (line.end >= r.y - r.size)) {//Bounding box check
                        if (!resolvePossibleCollisions(i, r, rp, act, line)) {
                            noCollision = false;
                        }
                    }
                }
            }
            for (int h = lowerHorizontalBound; h <= upperHorizontalBound; h++) {
                for (Line line : environmentMap.horizontal[h]) {
                    if ((line.start <= r.x + r.size) && (line.end >= r.x - r.size)) {//Bounding box check
                        if (!resolvePossibleCollisions(i, r, rp, act, line)) {
                            noCollision = false;
                        }
                    }
                }
            }
            //only if no collision was found we can remove this robot from the list
            //if a collision was found and resolved we still have to go through this cycle once again to see whether
            //this have caused another collision to occur
            if (noCollision) {
                wallCollisionList.remove(0);
            }
        }
    }

    /**
     * Resolves all collisions between robots by rolling robots back in time to the point of collision.
     * @param actions list of robots' actions
     * @param newRobotPlacements list of robots' placements
     */
    private void resolveRobotRobotCollisions(List<Action> actions, List<RobotPlacement> newRobotPlacements) {
        //first, we are testing each pair of robots for collision and add those collisions to the list
        List<Collision> listOfCollisions = new ArrayList<>();
        for (int i = 0; i < robots.size(); i++) {
            for (int j = 0; j < robots.size(); j++) {
                if (i > j) {
                    if (Geometry.distance(newRobotPlacements.get(i), newRobotPlacements.get(j)) <
                            doubleRadiusSquared(newRobotPlacements.get(i), newRobotPlacements.get(j)) - ST_ERR) {
                        listOfCollisions.add(new Collision(Collision.Type.ROBOT, i, j));
                    }
                }
            }
        }

        //then we go through that list and rolling robots back to the point of collision
        while (!listOfCollisions.isEmpty()) {
            Collision p = listOfCollisions.remove(0);
            int i = p.robot1;
            int j = p.robot2;

            if (Geometry.distance(newRobotPlacements.get(i), newRobotPlacements.get(j)) <
                    doubleRadiusSquared(newRobotPlacements.get(i), newRobotPlacements.get(j)) - ST_ERR) {
                if (actions.get(i).degreeOfRealization > actions.get(j).degreeOfRealization) {
                    resolveCollisionBetweenRobots(robots.get(i), newRobotPlacements.get(i), actions.get(i),
                            robots.get(j), newRobotPlacements.get(j), actions.get(j));
                } else {
                    resolveCollisionBetweenRobots(robots.get(j), newRobotPlacements.get(j), actions.get(j),
                            robots.get(i), newRobotPlacements.get(i), actions.get(i));
                }
                //after each rollback we have to test whether this has caused another collision to occur
                for (int k = 0; k < robots.size(); k++) {
                    if (i != k && j != k) {
                        if (Geometry.distance(newRobotPlacements.get(i), newRobotPlacements.get(k)) <
                                doubleRadiusSquared(newRobotPlacements.get(i), newRobotPlacements.get(k)) - ST_ERR) {
                            listOfCollisions.add(new Collision(Collision.Type.ROBOT, i, k));
                        }
                    }
                }
                for (int k = 0; k < robots.size(); k++) {
                    if (i != k && j != k) {
                        if (Geometry.distance(newRobotPlacements.get(j), newRobotPlacements.get(k)) <
                                doubleRadiusSquared(newRobotPlacements.get(j), newRobotPlacements.get(k)) - ST_ERR) {
                            listOfCollisions.add(new Collision(Collision.Type.ROBOT, j, k));
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates inputs for collision sensors. Collision with physical obstacles are computed using {@link CollisionList}
     * which has to be filled before calling this method (in {@link SimulationEngine#performActions}).
     * @param robots list of robots in the simulation.
     * @param objects list of objects in the simulation.
     * @return list of inputs for collision sensors.
     */
    @Override
    protected List<CollisionData> sendInputs(List<RobotPlacement> robots, List<ObjectPlacement> objects) {
        assert actions.size() == robots.size(): "Actions were not updated";

        List<CollisionData> inputs = new ArrayList<>();
        for (int i = 0; i < robots.size(); i++) {
            inputs.add(new CollisionData(actions.get(i)));
        }

        collisionList.computeCollisionsWithWalls(robots, inputs);
        //Test each pair of robots for collisions:
        for (int i = 0; i < robots.size(); i++) {
            for (int j = 0; j < robots.size(); j++) {
                if (i > j && Geometry.distance(robots.get(i), robots.get(j)) < doubleRadiusSquared(robots.get(i), robots.get(j)) + ST_ERR) {
                    RobotPlacement r1 = robots.get(i);
                    RobotPlacement r2 = robots.get(j);
                    inputs.get(i).collisionPoints.add(Geometry.subjectiveAngleBetween(r1, r2));
                    inputs.get(j).collisionPoints.add(Geometry.subjectiveAngleBetween(r2, r1));
                }
            }
        }

        return inputs;
    }

    /**
     * Checks whether there is a collision between a robot and a physical obstacle, and if there is one, calls
     * {@link SimulationEngine#resolveWallCollision} to resolve it.
     * @param robotNumber number of robot that is being checked
     * @param r a new robot placement
     * @param rp an old robot placement (in previous cycle)
     * @param act a robot's action in this cycle
     * @param line a line which is being tested for collision with robot
     * @return true if no collision was found, false otherwise
     */
    private boolean resolvePossibleCollisions(int robotNumber, RobotPlacement r, RobotPlacement rp, Action act, Line line) {
        double lineDistance, startDistance, endDistance, coordinate1, coordinate2;
        //Depending on whether line is vertical we need to check diffeent coordinates
        if (line.isVertical) {
            coordinate1 = r.x;
            coordinate2 = r.y;
        } else {
            coordinate1 = r.y;
            coordinate2 = r.x;
        }
        //Compute distances between a robot and a line, line's start, and line's end
        if (line.start > coordinate2 || line.end < coordinate2) {
            lineDistance = Double.MAX_VALUE;
        } else {
            lineDistance = Math.abs(line.horizon - coordinate1);
        }
        startDistance = Math.pow(coordinate1 - line.horizon, 2) + Math.pow(coordinate2 - line.start, 2);
        endDistance = Math.pow(coordinate1 - line.horizon, 2) + Math.pow(coordinate2 - line.end, 2);
        //if any of those distances is less then the robot's radius, then collision has occured
        if (lineDistance <= r.size || startDistance <= r.sizeSquared || endDistance <= r.sizeSquared) {
            double a = resolveWallCollision(r, rp, act, line, startDistance <= r.sizeSquared, endDistance <= r.sizeSquared);
            if (a == a) {
                collisionList.addWallCollision(robotNumber, r.x, r.y, r.angle, a);
            }
        }
        return !(lineDistance < r.size - ST_ERR || startDistance < r.sizeSquared - ST_ERR ||
                endDistance < r.sizeSquared - ST_ERR);
    }

    /**
     * Resolves a collision that has occurred between a robot and a physical obstacle by rolling robot back to the point
     * where there is no collision.
     * @param r a new robot placement
     * @param rp an old robot placement (in previous cycle)
     * @param act a robot's action in this cycle
     * @param line a line which is being tested for collision with robot
     * @param startCollision true if there is a collision with the first endpoint of the line, false otherwise
     * @param endCollision true if there is a collision with the second endpoint of the line, false otherwise
     * @return a subjective angle of collision for a robot
     */
    private double resolveWallCollision(RobotPlacement r, RobotPlacement rp, Action act, Line line,
                                        boolean startCollision, boolean endCollision) {
        //if there is no intersection, just touch, we do not need to resolve anything, just compute an angle of collision
        if (act.degreeOfRealization == 0 || act.speed == 0) {
            assert !startCollision || !endCollision: "Two collision points for static wall collision";
            if (startCollision) {
                if (line.isVertical) {
                    return Geometry.subjectiveAngleBetween(r, line.horizon, line.start);
                }
                return Geometry.subjectiveAngleBetween(r, line.start, line.horizon);
            } else if (endCollision) {
                if (line.isVertical) {
                    return Geometry.subjectiveAngleBetween(r, line.horizon, line.end);
                }
                return Geometry.subjectiveAngleBetween(r, line.end, line.horizon);
            } else {
                if (line.isVertical) {
                    return Geometry.subjectiveAngleBetween(r, line.horizon, r.y);
                }
                return Geometry.subjectiveAngleBetween(r, r.x, line.horizon);
            }
        }

        double degree = act.degreeOfRealization;
        double endpointCollisionTime = NaN;
        double pointOfCollision = 0;
        double lineCollisionTime;
        //resolving endpoint collisions:
        if (startCollision) {
            if (line.isVertical) {
                endpointCollisionTime = solveQuadraticEquation(rp, act, line.horizon, line.start, r.sizeSquared, 0, degree);
            } else  {
                endpointCollisionTime = solveQuadraticEquation(rp, act, line.start, line.horizon, r.sizeSquared, 0, degree);
            }
            updateCoordinates(r, rp, act, endpointCollisionTime);
            //if position is still inconsistent, we can't use this solution:
            if (!checkConsistency(r, line)) {
                endpointCollisionTime = NaN;
            }
            pointOfCollision = line.start;
        }
        if (endCollision && endpointCollisionTime != endpointCollisionTime) {
            if (line.isVertical) {
                endpointCollisionTime = solveQuadraticEquation(rp, act, line.horizon, line.end, r.sizeSquared, 0, degree);
            } else {
                endpointCollisionTime = solveQuadraticEquation(rp, act, line.end, line.horizon, r.sizeSquared, 0, degree);
            }
            updateCoordinates(r, rp, act, endpointCollisionTime);
            if (!checkConsistency(r, line)) {
                endpointCollisionTime = NaN;
            }
            pointOfCollision = line.end;
        }
        //resolving line collision:
        if (line.isVertical) {
            if (line.horizon < rp.x) {
                lineCollisionTime = bound((r.size - rp.x + line.horizon)/(act.speed *Math.sin(rp.angle)), 0, degree);
            } else {
                lineCollisionTime = bound((line.horizon - rp.x - r.size)/(act.speed *Math.sin(rp.angle)), 0, degree);
            }
        } else {
            if (line.horizon < rp.y) {
                lineCollisionTime = bound((r.size - rp.y + line.horizon)/(act.speed *Math.cos(rp.angle)), 0, degree);
            } else {
                lineCollisionTime = bound((line.horizon - rp.y - r.size)/(act.speed *Math.cos(rp.angle)), 0, degree);
            }
        }
        assert endpointCollisionTime == endpointCollisionTime || lineCollisionTime == lineCollisionTime:
                "No solution was found for robot-wall collision";
        assert (endpointCollisionTime != endpointCollisionTime || lineCollisionTime != lineCollisionTime) ||
                endpointCollisionTime > lineCollisionTime:
                        "Impossible state occurred: endpointCollisionTime < lineCollisionTime, endpointCollisionTime = " +
                                endpointCollisionTime + ", lineCollisionTime = " + lineCollisionTime;
        //if there is an endpoint collision, we can ignore line collision:
        if (endpointCollisionTime == endpointCollisionTime) {
            if (line.isVertical) {
                return Geometry.subjectiveAngleBetween(r, line.horizon, pointOfCollision);
            } else {
                return Geometry.subjectiveAngleBetween(r, pointOfCollision, line.horizon);
            }
        } else {
            updateCoordinates(r, rp, act, lineCollisionTime);
            if (line.isVertical) {
                return Geometry.subjectiveAngleBetween(r, line.horizon, r.y);
            } else {
                return Geometry.subjectiveAngleBetween(r, r.x, line.horizon);
            }
        }
    }

    /**
     * Resolves collision between two robots by rolling them back in time to the moment when collision has happened
     * @param rp1 an old robot placement of robot 1 (in previous cycle)
     * @param r1 a new robot placement of robot 1
     * @param act1 an action of the robot 1 in this cycle
     * @param rp2 an old robot placement of robot 2 (in previous cycle)
     * @param r2 a new robot placement of robot 2
     * @param act2 an action of the robot 2 in this cycle
     */
    private void resolveCollisionBetweenRobots(RobotPlacement rp1, RobotPlacement r1, Action act1,
                                               RobotPlacement rp2, RobotPlacement r2, Action act2) {
        assert Geometry.distance(r1, r2) < doubleRadiusSquared(r1, r2) - ST_ERR: "Illegal resolveCollisionBetweenRobots call";
        //if one robot is at more recent point in time then the other one, we have to roll it back first to the point of
        //a second robot
        if (act1.degreeOfRealization != act2.degreeOfRealization) {
            double t = solveQuadraticEquation(rp1, act1, r2.x, r2.y, doubleRadiusSquared(r1, r2), act2.degreeOfRealization,
                    act1.degreeOfRealization);
            //if this didn't help to resolve collision, we have to roll back both robots simultaneously
            if (t == t) {
                updateCoordinates(r1, rp1, act1, t);
            } else {
                resolveCollisionForTwoMovingBodies(rp1, r1, act1, rp2, r2, act2);
            }
        } else {
            resolveCollisionForTwoMovingBodies(rp1, r1, act1, rp2, r2, act2);
        }
    }

    /**
     * Resolves collision between two robots by rolling back both robots simultaneously. First it computes a point in
     * time at which two robots have collided, then it updates the coordinates of those robots to that point in time.
     * @param rp1 an old robot placement of robot 1 (in previous cycle)
     * @param r1 a new robot placement of robot 1
     * @param act1 an action of the robot 1 in this cycle
     * @param rp2 an old robot placement of robot 2 (in previous cycle)
     * @param r2 a new robot placement of robot 2
     * @param act2 an action of the robot 2 in this cycle
     */
    private void resolveCollisionForTwoMovingBodies(RobotPlacement rp1, RobotPlacement r1, Action act1,
                                                    RobotPlacement rp2, RobotPlacement r2, Action act2) {
        double bound = Math.min(act1.degreeOfRealization, act2.degreeOfRealization);
        double A = rp1.x - rp2.x;
        double B = rp1.y - rp2.y;
        double N = act1.speed * Math.sin(rp1.angle) - act2.speed * Math.sin(rp2.angle);
        double M = act1.speed * Math.cos(rp1.angle) - act2.speed * Math.cos(rp2.angle);

        double a = N*N + M*M;
        double b = 2*A*N + 2*B*M;
        double c = A*A + B*B - doubleRadiusSquared(r1, r2);

        double det = b*b - 4*a*c;
        double t1 = bound(( - b + Math.sqrt(det)) / (2*a), 0, bound);
        double t2 = bound(( - b - Math.sqrt(det)) / (2*a), 0, bound);
        assert t1 == t1 || t2 == t2: "Quadratic equation for 2 moving bodies could not be solved correctly with values: " +
                t1 + " and " + t2;
        double t = 0;
        if(t2 == t2 && t1 == t1) {
            t = Math.max(t1, t2);
        } else if (t1 == t1) {
            t = t1;
        } else if (t2 == t2) {
            t = t2;
        }
        updateCoordinates(r1, rp1, act1, t);
        updateCoordinates(r2, rp2, act2, t);
    }

    /**
     * Changes coordinates of robot as they should be at time t (where 0.0 is the end of previous cycle, 1.0 is the end
     * of this one).
     * @param r a new robot placement
     * @param rp an old robot placement (in previous cycle)
     * @param act a robot's action in this cycle
     * @param t a point in time we want to rollback the robot to
     */
    private void updateCoordinates(RobotPlacement r, RobotPlacement rp, Action act, double t) {
        assert t >= 0 && t <= 1 && t <= act.degreeOfRealization: "There was an attempt to update action " +
                "with greater value: " + t + " instead of " + act.degreeOfRealization;
        r.x = rp.x + act.speed * Math.sin(rp.angle) * t;
        r.y = rp.y + act.speed * Math.cos(rp.angle) * t;
        act.degreeOfRealization = t;
    }

    /**
     * Computes a point in time at which the robot has collided with a point (x,y).
     * @param rp an old robot placement (in previous cycle)
     * @param act a robot's action in this cycle
     * @param x X-coordinate of the collision point
     * @param y Y-coordinate of the collision point
     * @param distanceSquared a square of the distance between the collision point and the center of the robot
     * @param lowerBound a lower bound on return value
     * @param upperBound an upper bound on return value
     * @return a point in time at which the robot has collided with a point (x,y) if it lies between the lowerBound and
     * the upperBound, NaN otherwise
     */
    private double solveQuadraticEquation(RobotPlacement rp, Action act, double x, double y, double distanceSquared,
                                          double lowerBound, double upperBound) {
        double A = rp.x - x;
        double B = rp.y - y;
        double N = act.speed * Math.sin(rp.angle);
        double M = act.speed * Math.cos(rp.angle);

        double a = N*N + M*M;
        double b = 2*A*N + 2*B*M;
        double c = A*A + B*B - distanceSquared;

        double det = b*b - 4*a*c;
        double t1 = bound(( - b + Math.sqrt(det)) / (2*a), lowerBound, upperBound);
        double t2 = bound(( - b - Math.sqrt(det)) / (2*a), lowerBound, upperBound);
        if (t1 == t1) {
            return t1;
        } else if (t2 == t2) {
            return t2;
        } else {
            return NaN;
        }
    }

    private double bound(double t, double lowerBound, double upperBound) {
        if (t > lowerBound - ST_ERR && t <= lowerBound) {
            return lowerBound;
        } else if (t >= upperBound && t < upperBound + ST_ERR) {
            return upperBound;
        } else if (t > lowerBound && t < upperBound) {
            return t;
        } else {
            return NaN;
        }
    }

    /**
     * Checks whether a robot and a line intersect.
     * @param r {@link RobotPlacement} to check
     * @param l {@link Line} to check
     * @return true if there is no intersection, false otherwise
     */
    private static boolean checkConsistency(RobotPlacement r, Line l) {
        double lineDistance, startDistance, endDistance, coordinate1, coordinate2;

        if (l.isVertical) {
            coordinate1 = r.x;
            coordinate2 = r.y;
        } else {
            coordinate1 = r.y;
            coordinate2 = r.x;
        }

        if (l.start > coordinate2 || l.end < coordinate2) {
            lineDistance = Double.MAX_VALUE;
        } else {
            lineDistance = Math.abs(l.horizon - coordinate1);
        }
        startDistance = Math.pow(coordinate1 - l.horizon, 2) + Math.pow(coordinate2 - l.start, 2);
        endDistance = Math.pow(coordinate1 - l.horizon, 2) + Math.pow(coordinate2 - l.end, 2);

        return !(lineDistance < r.size - ST_ERR || startDistance < r.sizeSquared - ST_ERR ||
                endDistance < r.sizeSquared - ST_ERR);
    }

    /**
     * Checks whether a given map with a given list of robots contains collisions (intersections between two robots or a
     * robot and a physical obstacle).
     * @param robots list of robots on the map
     * @param map a map of physical obstacles
     * @param booleanMap a boolean representation of a map of physical obstacles
     * @return a {@link Collision} object representing the first collision that was found
     */
    static Collision checkMapConsistency(List<RobotPlacement> robots, EnvironmentMap map, boolean[][] booleanMap) {
        //Checking for collisions with walls
        for (int i = 0; i < robots.size(); i++) {
            RobotPlacement r = robots.get(i);
            int lowerVerticalBound = (int) Math.max(Math.floor(r.x - r.size), 0);
            int upperVerticalBound = (int) Math.min(Math.ceil(r.x + r.size), map.sizeX);
            int lowerHorizontalBound = (int) Math.max(Math.floor(r.y - r.size), 0);
            int upperHorizontalBound = (int) Math.min(Math.ceil(r.y + r.size), map.sizeY);
            for (int v = lowerVerticalBound; v <= upperVerticalBound; v++) {
                for (Line line : map.vertical[v]) {
                    if (!checkConsistency(r, line)) {
                        return new Collision(Collision.Type.WALL, i, -1);
                    }
                }
            }
            for (int h = lowerHorizontalBound; h <= upperHorizontalBound; h++) {
                for (Line line : map.horizontal[h]) {
                    if (!checkConsistency(r, line)) {
                        return new Collision(Collision.Type.WALL, i, -1);
                    }
                }
            }
            if (booleanMap != null && booleanMap[r.x.intValue()][r.y.intValue()]) {
                return new Collision(Collision.Type.WALL, i, -1);
            }
        }
        //Checking for collisions with robots
        for (int i = 0; i < robots.size(); i++) {
            for (int j = 0; j < robots.size(); j++) {
                if (i != j && Geometry.distance(robots.get(i), robots.get(j)) < doubleRadiusSquared(robots.get(i), robots.get(j)))
                    return new Collision(Collision.Type.ROBOT, i, j);
            }
        }

        return new Collision(Collision.Type.NONE, -1, -1);
    }

    private static double doubleRadiusSquared(RobotPlacement r1, RobotPlacement r2) {
        double doubleRadius = r1.size + r2.size;
        return doubleRadius * doubleRadius;
    }

}
