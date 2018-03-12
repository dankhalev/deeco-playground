package cz.cuni.mff.d3s.deeco.playground.examples.basic;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * This robot moves forward till it hits some obstacle; then, it changes its direction to opposite.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class SimpleRobot extends DEECoRobot {

    public SimpleRobot() {
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
            wheels.value.setAction(0, Math.PI);
        } else {
            wheels.value.setAction(1,0);
        }
    }

}
