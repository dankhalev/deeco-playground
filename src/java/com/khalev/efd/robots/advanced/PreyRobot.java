package com.khalev.efd.robots.advanced;

import com.khalev.efd.robots.basic.SimpleWheels;
import com.khalev.efd.simulation.*;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Component
public class PreyRobot extends DEECoRobot {

    public Coordinates predator = new Coordinates(0,0,0);
    public Coordinates position = new Coordinates(0,0,0);
    public Boolean alert = false;
    public Boolean reaction = false;

    public PreyRobot() {
        wheels = new SimpleWheels();
        sensor.registerSensor("coords");
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decision(
            @InOut("wheels") ParamHolder<Wheels> wheels1,
            @In("sensor") SensorySystem sensor,
            @InOut("alert") ParamHolder<Boolean> alert,
            @InOut("reaction") ParamHolder<Boolean> reaction,
            @InOut("predator") ParamHolder<Coordinates> predator,
            @InOut("position") ParamHolder<Coordinates> position,
            @In("rID") Integer rID
    ) {
        SimpleWheels wheels = (SimpleWheels) wheels1.value;
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);

        if (coordinates != null && collisionData != null) {
            position.value = coordinates;
            if (alert.value && (!reaction.value || collisionData.action.type == Action.Type.MOVE)) {
                runAway(coordinates, predator.value, wheels);
                reaction.value = true;
            } else if (alert.value && collision(collisionData)) {
                rotate(collisionData, wheels);
            } else if (!alert.value) {
                reaction.value = false;
                calm(wheels);
            } else {
                wheels.rotationAngle = 0.0;
                wheels.speed = 1.0;
            }
        }
    }

    private static void runAway(Coordinates position, Coordinates predator, SimpleWheels wheels) {
        wheels.rotationAngle = Math.atan2(predator.x - position.x, predator.y - position.y) - position.angle + Math.PI;
        wheels.speed = 0.0;
    }

    private static void rotate(CollisionData collisionData, SimpleWheels wheels) {
        if (collisionData.action.type != Action.Type.ROTATE) {
            wheels.speed = 0.0;
            wheels.rotationAngle = Math.PI / 3.0;
        } else {
            wheels.rotationAngle = 0.0;
            wheels.speed = 1.0;
        }
    }

    private static void calm(SimpleWheels wheels) {
        wheels.rotationAngle = 0.0;
        wheels.speed = 0.0;
    }

    private static boolean collision(CollisionData collisionData) {
        return collisionData != null && collisionData.collisionPoints.size() > 0;
    }

}
