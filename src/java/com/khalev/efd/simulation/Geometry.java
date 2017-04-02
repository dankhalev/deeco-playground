package com.khalev.efd.simulation;

/**
 * Class with static methods that are useful for geometric calculations in simulation.
 */
public class Geometry {

    /**
     * @return square of the distance between points (x1, y1) and (x2, y2) in 2D space
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        double xDist = (x1 - x2);
        double yDist = (y1 - y2);
        return xDist*xDist + yDist*yDist;
    }

    /**
     * @return angle between lines {(subjectX,subjectY),(objectX,objectY)} and {(subjectX,subjectY),(subjectX,subjectY + 1)}
     */
    public static double angleBetween(double subjectX, double subjectY, double objectX, double objectY) {
        return Math.atan2(objectX - subjectX, objectY - subjectY);
    }

    /**
     * @return angle between lines {(subjectX,subjectY),(objectX,objectY)} and
     * {(subjectX,subjectY),(subjectX + sin(subjectAngle),subjectY + cos(subjectAngle))}
     */
    public static double subjectiveAngleBetween(double subjectX, double subjectY, double objectX, double objectY, double subjectAngle) {
        return normalizeAngle(angleBetween(subjectX, subjectY, objectX, objectY) - subjectAngle);
    }

    /**
     * @return angle nolmalized to (-pi, pi)
     */
    public static double normalizeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    static double subjectiveAngleBetween(RobotPlacement subject, RobotPlacement object) {
        return subjectiveAngleBetween(subject, object.x, object.y);
    }

    static double subjectiveAngleBetween(RobotPlacement subject, double objectX, double objectY) {
        return normalizeAngle(angleBetween(subject.x, subject.y, objectX, objectY) - subject.angle);
    }

    static double distance(RobotPlacement r1, RobotPlacement r2) {
        return distance(r1.x, r1.y, r2.x, r2.y);
    }
}
