package cz.cuni.mff.d3s.deeco.playground.examples.predator;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.Random;

/**
 * Walks a random path around the field.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class PredatorRobot extends DEECoRobot {

    public Coordinates target = new Coordinates(0,0,0);
    public Coordinates position = new Coordinates(0,0,0);
    public Random generator = new Random(9081257361234123445L);


    public PredatorRobot() {
        wheels = new SingleActionWheels();
        sensor.registerSensor("coords");
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decision(
            @InOut("wheels") ParamHolder<Wheels> wheels,
            @In("sensor") SensorySystem sensor,
            @InOut("target") ParamHolder<Coordinates> target,
            @InOut("position") ParamHolder<Coordinates> position,
            @InOut("generator") ParamHolder<Random> generator
    ) {
        SingleActionWheels sWheels = (SingleActionWheels) wheels.value;
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        if (coordinates != null && collisionData != null) {
            position.value = coordinates;
            if (newTargetRequested(collisionData, coordinates, target.value)
                    && collisionData.action.type != Action.Type.ROTATE
                    && sWheels.rotationAngle == 0.0) {
                target.value = setNewTarget(generator.value);
                rotateToTarget(sWheels, coordinates, target.value);
            } else if (collisionData.action.type == Action.Type.ROTATE) {
                sWheels.setAction(1,0);
            }
        }
    }

    private static void rotateToTarget(SingleActionWheels wheels, Coordinates coordinates, Coordinates target) {
        wheels.rotationAngle = Math.atan2(target.x - coordinates.x, target.y - coordinates.y) - coordinates.angle;
        wheels.speed = 0.0;
    }

    private static Coordinates setNewTarget(Random generator) {
        double x = generator.nextDouble() * 100;
        double y = generator.nextDouble() * 100;
        return new Coordinates(x, y, 0);
    }

    private static boolean newTargetRequested(CollisionData collisionData, Coordinates coordinates, Coordinates target) {

        double xDist = (coordinates.x - target.x);
        double yDist = (coordinates.y - target.y);
        double distance =  xDist*xDist + yDist*yDist;

        return (distance < 3.0 || collisionData.collisionPoints.size() > 0);
    }

}
