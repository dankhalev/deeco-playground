package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Class with static methods that are useful for geometric calculations in simulation.
 *
 * @author Danylo Khalyeyev
 */
public class Geometry {

    /**
     * Returns a square of the distance between points (x1, y1) and (x2, y2) in 2D space.
     * @param x1 X-coordinate of the first point
     * @param y1 Y-coordinate of the first point
     * @param x2 X-coordinate of the second point
     * @param y2 Y-coordinate of the second point
     * @return a square of the distance between points (x1, y1) and (x2, y2) in 2D space
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        double xDist = (x1 - x2);
        double yDist = (y1 - y2);
        return xDist*xDist + yDist*yDist;
    }

    /**
     * Returns an angle between lines {(subjectX,subjectY),(objectX,objectY)} and
     * {(subjectX,subjectY),(subjectX,subjectY + 1)}
     * @param subjectX X-coordinate of a subject
     * @param subjectY Y-coordinate of a subject
     * @param objectX X-coordinate of an object
     * @param objectY Y-coordinate of an object
     * @return an angle between lines {(subjectX,subjectY),(objectX,objectY)} and
     * {(subjectX,subjectY),(subjectX,subjectY + 1)}
     */
    public static double angleBetween(double subjectX, double subjectY, double objectX, double objectY) {
        return Math.atan2(objectX - subjectX, objectY - subjectY);
    }

    /**
     * Returns an angle between lines {(subjectX,subjectY),(objectX,objectY)} and
     * {(subjectX,subjectY),(subjectX + sin(subjectAngle),subjectY + cos(subjectAngle))}
     * @param subjectX X-coordinate of a subject
     * @param subjectY Y-coordinate of a subject
     * @param objectX X-coordinate of an object
     * @param objectY Y-coordinate of an object
     * @param subjectAngle rotation angle of a subject
     * @return an angle between lines {(subjectX,subjectY),(objectX,objectY)} and
     * {(subjectX,subjectY),(subjectX + sin(subjectAngle),subjectY + cos(subjectAngle))}
     */
    public static double subjectiveAngleBetween(double subjectX, double subjectY, double objectX, double objectY, double subjectAngle) {
        return normalizeAngle(angleBetween(subjectX, subjectY, objectX, objectY) - subjectAngle);
    }

    /**
     * Returns an angle between a subject and an object, from the subject's point of view.
     * @param subject subject's coordinates
     * @param object object's coordinates
     * @return an angle between a subject and an object, from the subject's point of view
     */
    public static double subjectiveAngleBetween(Coordinates subject, Coordinates object) {
        return subjectiveAngleBetween(subject.x, subject.y, object.x, object.y, subject.angle);
    }

    /**
     * Normalizes a given angle to a range of (-pi, pi).
     * @param angle angle to normalize
     * @return angle normalized to a range of (-pi, pi)
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
