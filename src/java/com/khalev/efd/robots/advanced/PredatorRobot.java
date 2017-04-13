package com.khalev.efd.robots.advanced;

import com.khalev.efd.robots.basic.SimpleWheels;
import com.khalev.efd.simulation.*;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PredatorRobot extends DEECoRobot {

    public Coordinates target = new Coordinates(0,0,0);
    public Coordinates position = new Coordinates(0,0,0);
    public List<Double> array = new ArrayList<>();

    public PredatorRobot() {
        wheels = new SimpleWheels();
        sensor.registerSensor("coords");
        Double[] arr = new Double[] {
                7.984093553752103,
                1.4786804015530786,
                30.169450043083145,
                45.158619726179616,
                29.633244089416266,
                56.44767224974389,
                39.497351216552175,
                78.49706893805701,
                38.98425306506562,
                89.51335494840036,
                77.25225624508515,
                57.795144467143885,
                35.26141104781606,
                45.46937728363589,
                43.60162866446585,
                98.33502002015517
        };
        Collections.addAll(array, arr);
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decision(
            @InOut("wheels") ParamHolder<Wheels> wheels,
            @In("sensor") SensorySystem sensor,
            @InOut("target") ParamHolder<Coordinates> target,
            @InOut("position") ParamHolder<Coordinates> position,
            @InOut("array") ParamHolder<List<Double>> array
    ) {
        SimpleWheels sWheels = (SimpleWheels) wheels.value;
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        if (coordinates != null && collisionData != null) {
            position.value = coordinates;
            if (newTargetRequested(collisionData, coordinates, target.value)
                    && collisionData.action.type != Action.Type.ROTATE
                    && sWheels.rotationAngle == 0.0) {
                target.value = setNewTarget(array.value);
                rotateToTarget(sWheels, coordinates, target.value);
            } else if (collisionData.action.type == Action.Type.ROTATE) {
                sWheels.setAction(1,0);
            }
        }
    }

    private static void rotateToTarget(SimpleWheels wheels, Coordinates coordinates, Coordinates target) {
        wheels.rotationAngle = Math.atan2(target.x - coordinates.x, target.y - coordinates.y) - coordinates.angle;
        wheels.speed = 0.0;
    }

    private static Coordinates setNewTarget(List<Double> array) {
        //double x = Math.random() * 100;
        //double y = Math.random() * 100;
        double x = array.remove(0);
        double y = array.remove(0);
        return new Coordinates(x, y, 0);
    }

    private static boolean newTargetRequested(CollisionData collisionData, Coordinates coordinates, Coordinates target) {

        double xDist = (coordinates.x - target.x);
        double yDist = (coordinates.y - target.y);
        double distance =  xDist*xDist + yDist*yDist;

        return (distance < 3.0 || collisionData.collisionPoints.size() > 0);
    }

}
