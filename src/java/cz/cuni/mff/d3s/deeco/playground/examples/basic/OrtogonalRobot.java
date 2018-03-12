package cz.cuni.mff.d3s.deeco.playground.examples.basic;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * This robot moves forward till it hits some obstacle; then it rotates to the direction which is opposite to the
 * collision point.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class OrtogonalRobot extends DEECoRobot {

    public OrtogonalRobot() {
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
        if (input != null && input.action.type != Action.Type.ROTATE && input.collisionPoints.size() == 1) {
            wheels.value.setAction(0, input.collisionPoints.get(0) + Math.PI);
        } else {
            wheels.value.setAction(1,0);
        }
    }

}
