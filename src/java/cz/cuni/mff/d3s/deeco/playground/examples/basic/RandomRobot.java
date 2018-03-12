package cz.cuni.mff.d3s.deeco.playground.examples.basic;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * This robot moves forward with random speed till it hits some obstacle; then, it rotates a random degree.
 * It can also rotate at any other moment with probability of 2%.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class RandomRobot extends DEECoRobot {

    public RandomRobot() {
        wheels = new SimpleWheels();
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decisionProcess(
            @In("rID") Integer rid,
            @InOut("wheels") ParamHolder<Wheels> wheels,
            @In("sensor") SensorySystem sensor
    ) {
        CollisionData input = sensor.getInputFromSensor("collisions", CollisionData.class);
        if (input != null && !input.collisionPoints.isEmpty() && input.action.type != Action.Type.ROTATE) {
            wheels.value.setAction(0, Math.PI * Math.random());
        } else {
            if (Math.random() < 0.998) {
                wheels.value.setAction(Math.random(), 0);
            } else {
                wheels.value.setAction(0, Math.PI * Math.random());
            }
        }
    }

}
