package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.*;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.ArrayList;
import java.util.List;

@Component
public class FirefighterDrone extends DEECoRobot {

    public Coordinates position = null;
    public Coordinates previousPosition = null;
    public Coordinates powerStation = null;
    public Mode mode = Mode.RAMBLE;
    public Boolean autonomous = true;
    public FirefighterOrder order;
    public FirefighterOrder trajectoryOrder;
    public List<Coordinates> trajectory = new ArrayList<>();
    static Integer nextID = 0;
    public Integer firefighterID = nextID++;

    public FirefighterDrone() {
        wheels = new FirefighterWheels();
        sensor.registerSensor("coords");
        sensor.registerSensor("energy");
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decision(
            @InOut("wheels") ParamHolder<Wheels> wheels,
            @In("sensor") SensorySystem sensor,
            @InOut("position") ParamHolder<Coordinates> position,
            @InOut("mode") ParamHolder<Mode> mode,
            @InOut("previousPosition") ParamHolder<Coordinates> previousPosition,
            @InOut("powerStation") ParamHolder<Coordinates> charger,
            @InOut("tag") ParamHolder<String> tag,
            @In("autonomous") Boolean autonomous,
            @In("order") FirefighterOrder order,
            @InOut("trajectoryOrder") ParamHolder<FirefighterOrder> trajectoryOrder,
            @InOut("trajectory") ParamHolder<List<Coordinates>> trajectory
    ) {
        //Getting sensory data, preparing actuators
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        EnergyInput energy = sensor.getInputFromSensor("energy", EnergyInput.class);
        if (energy == null || coordinates == null || collisionData == null) {
            return;
        }
        previousPosition.value = position.value;
        position.value = coordinates;
        FirefighterWheels pw = (FirefighterWheels) wheels.value;
        pw.provideEnergy(sensor);

        //Decision-making

        //if (!position.value.equals(previousPosition.value)) {
            if (autonomous) {
                pw.isCoolerActivated = false;
                if (frontalCollisionDetected(collisionData) && (mode.value != Mode.CHARGE || Geometry.distance(coordinates.x, coordinates.y, charger.value.x, charger.value.y) > 40)) {
                    mode.value = Mode.AVOID;
                    trajectory.value = constructAvoidanceTrajectory(coordinates, collisionData);
                }
                if (mode.value == Mode.CHARGE || (energy.energy < 300 && charger.value != null && mode.value != Mode.AVOID)) {
                    locateCharger(energy, coordinates, pw, mode, charger.value);
                } else {
                    makeAutonomousDecision(collisionData, energy, coordinates, pw, mode, trajectory);
                }
            } else {
                executeOrder(collisionData, order, pw, mode, charger.value, coordinates, trajectoryOrder, trajectory);
            }
        //}
        if (energy.energy > 0) {
            tag.value = mode.value.toString();
        } else {
            tag.value = "BROKEN";
        }
    }

    private static void executeOrder(CollisionData collisionData, FirefighterOrder order, FirefighterWheels pw, ParamHolder<Mode> mode,
                                     Coordinates charger, Coordinates coordinates, ParamHolder<FirefighterOrder> trajectoryOrder, ParamHolder<List<Coordinates>> trajectory) {

        if (order.type != FirefighterOrder.Type.STAY && frontalCollisionDetected(collisionData)
                && (mode.value != Mode.CHARGE || Geometry.distance(coordinates.x, coordinates.y, charger.x, charger.y) > 40)) {
            mode.value = Mode.AVOID;
            trajectory.value = constructAvoidanceTrajectory(coordinates, collisionData);
            trajectoryOrder.value = order;
        }
        if (mode.value == Mode.AVOID && order.type.equals(trajectoryOrder.value.type) &&
                ((order.target == null && trajectoryOrder.value.target == null) ||
                        (order.target != null && order.target.equals(trajectoryOrder.value.target)))) {
            if (trajectory.value.size() == 0) {
                mode.value = Mode.RAMBLE;
            } else {
                rotateToTarget(pw, coordinates, trajectory.value.get(0));
                if (Geometry.distance(coordinates.x, coordinates.y, trajectory.value.get(0).x, trajectory.value.get(0).y) < 4) {
                    trajectory.value.remove(0);
                }
            }
        } else if (order.type == FirefighterOrder.Type.RECHARGE) {
            mode.value = Mode.CHARGE;
            pw.isCoolerActivated = false;
            if (Geometry.distance(coordinates.x, coordinates.y, charger.x, charger.y) > 4) {
                rotateToTarget(pw, coordinates, charger);
            } else {
                pw.setAction(0,0);
            }
        } else if (order.type == FirefighterOrder.Type.FOLLOW) {
            mode.value = Mode.FOLLOW;
            pw.isCoolerActivated = false;
            rotateToTarget(pw, coordinates, order.target);
        } else if (order.type == FirefighterOrder.Type.FIGHT) {
            mode.value = Mode.FIGHT;
            pw.isCoolerActivated = true;
            rotateToTarget(pw, coordinates, order.target);
        } else if (order.type == FirefighterOrder.Type.STAY) {
            mode.value = Mode.RAMBLE;
            pw.isCoolerActivated = false;
            pw.setAction(0,0);
        }
    }

    private static List<Coordinates> constructAvoidanceTrajectory(Coordinates coordinates, CollisionData collisionData) {
        List<Coordinates> trajectory = new ArrayList<>();
        double angle1 = Geometry.normalizeAngle(coordinates.angle + Math.PI);
        double x1 = coordinates.x + 6 * Math.sin(angle1);
        double y1 = coordinates.y + 6 * Math.cos(angle1);
        trajectory.add(new Coordinates(x1, y1, 0));
        double collisionAngle = 0;
        for (double collision : collisionData.collisionPoints) {
            if (collision < Math.PI / 2 && collision > - Math.PI / 2) {
                collisionAngle = collision;
            }
        }
        if (collisionAngle < 0) {
            angle1 = Geometry.normalizeAngle(coordinates.angle + Math.PI / 4);
        } else {
            angle1 = Geometry.normalizeAngle(coordinates.angle - Math.PI / 4);
        }
        double x2 = x1 + 13 * Math.sin(angle1);
        double y2 = y1 + 13 * Math.cos(angle1);
        trajectory.add(new Coordinates(x2, y2, 0));
        return trajectory;
    }

    private static void locateCharger(EnergyInput energy, Coordinates coordinates, FirefighterWheels pw,
                                      ParamHolder<Mode> mode, Coordinates charger) {
        if (energy.energy < 500) {
            mode.value = Mode.CHARGE;
        } else {
            mode.value = Mode.RAMBLE;
            return;
        }
        if (!isAligned(coordinates, charger)) {
            rotateToTarget(pw, coordinates, charger);
        } else {
            pw.setAction(1,0);
        }
    }

    public static void makeAutonomousDecision(CollisionData collisionData, EnergyInput energy, Coordinates coordinates,
                                              FirefighterWheels pw, ParamHolder<Mode> mode, ParamHolder<List<Coordinates>> trajectory) {


        if (mode.value == Mode.AVOID) {
            if (trajectory.value.size() == 0) {
                mode.value = Mode.RAMBLE;
            } else {
                rotateToTarget(pw, coordinates, trajectory.value.get(0));
                if (Geometry.distance(coordinates.x, coordinates.y, trajectory.value.get(0).x, trajectory.value.get(0).y) < 4) {
                    trajectory.value.remove(0);
                }
            }
        }
        if (mode.value == Mode.RAMBLE) {
            if (energy.data.maxDetectedTemperature > 45) {
                mode.value = Mode.FOLLOW;
            } else if (!collisionData.collisionPoints.isEmpty()) {
                pw.setAction(1, Math.PI * Math.random());
            } else {
                if (Math.random() < 0.95) {
                    pw.setAction(1, 0);
                } else {
                    pw.setAction(1, Math.PI * Math.random());
                }
            }
        }
        if (mode.value == Mode.FOLLOW) {
            if (energy.damage > 4 || (energy.data.temperatureVector == 0 && energy.data.maxDetectedTemperature > 45)) {
                mode.value = Mode.FIGHT;
            } else if (energy.data.maxDetectedTemperature < 42){
                mode.value = Mode.RAMBLE;
            } else {
                pw.setAction(1, energy.data.temperatureVector);
            }
        }
        if (mode.value == Mode.FIGHT) {
            if (energy.data.maxDetectedTemperature < 50){
                mode.value = Mode.RAMBLE;
            } else {
                pw.isCoolerActivated = true;
                pw.setAction(1, energy.data.temperatureVector);
            }
        }
    }

    private static void rotateToTarget(Wheels wheels, Coordinates coordinates, Coordinates target) {
        wheels.setAction(1, Math.atan2(target.x - coordinates.x, target.y - coordinates.y) - coordinates.angle);
    }

    private static boolean isAligned(Coordinates coordinates, Coordinates target) {
        double diff = (Math.atan2(target.x - coordinates.x, target.y - coordinates.y) - coordinates.angle);
        return diff < 0.01 && diff > -0.01;
    }

    private static boolean frontalCollisionDetected(CollisionData collisionData) {
        for (double collision : collisionData.collisionPoints) {
            if (collision < Math.PI / 2 && collision > - Math.PI / 2) {
                return true;
            }
        }
        return false;
    }
    enum Mode {
        RAMBLE, FOLLOW, FIGHT, CHARGE, AVOID
    }
}
