package com.khalev.efd.robots.advanced;

import com.khalev.efd.robots.basic.SimpleWheels;
import com.khalev.efd.simulation.*;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Component
public class CatchRobot extends DEECoRobot {

    public Coordinates currentTarget = new Coordinates(0,0,0);
    public Coordinates newTarget = new Coordinates(0,0,0);
    public Coordinates position = null;

    public CatchRobot() {
        wheels = new SimpleWheels();
        sensor.registerSensor("coords");
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decision(
            @InOut("wheels") ParamHolder<Wheels> wheels,
            @In("sensor") SensorySystem sensor,
            @InOut("treasure") ParamHolder<Coordinates> newTarget,
            @InOut("currentTarget") ParamHolder<Coordinates> currentTarget,
            @InOut("position") ParamHolder<Coordinates> position
    ) {
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        if (coordinates != null) {
            position.value = coordinates;
        }
        if (!currentTarget.value.equals(newTarget.value)) {
            currentTarget.value = newTarget.value;
            rotateToTarget(wheels.value, position.value, newTarget.value);
        } else if (collisionData != null && collisionData.action.type == Action.Type.ROTATE) {
            wheels.value.setAction(1,0);
        }
    }

    private static void rotateToTarget(Wheels wheels, Coordinates coordinates, Coordinates target) {
        wheels.setAction(0, Math.atan2(target.x - coordinates.x, target.y - coordinates.y) - coordinates.angle);
    }

}
