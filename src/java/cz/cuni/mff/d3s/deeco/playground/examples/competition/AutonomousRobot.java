package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.*;

/**
 * A robot from Team Blue. Acts autonomously, does not take into account actions of his teammates. However,
 * it receives information about actions of his opponents through {@link HackEnsemble} which gives him an
 * advantage (sometimes).
 *
 * @author Danylo Khalyeyev
 */
@Component
public class AutonomousRobot extends DEECoRobot {

    public String teamID = "T2";

    public Coordinates position = null;
    public Coordinates previousPosition = null;
    public Coordinates powerStation = null;
    public Mode mode = Mode.IDLE;
    public RobotOrder order;
    public RobotOrder trajectoryOrder;
    public List<Coordinates> trajectory = new ArrayList<>();
    public Map<Integer, Coordinates> items = new HashMap<>();
    public Map<Integer, RobotOrder> enemyOrders = new HashMap<>();
    public Map<Integer, RobotData> enemyPlacements = new HashMap<>();


    public static final int criticalCharge = 80;
    public static final int fullCharge = 300;

    public AutonomousRobot() {
        wheels = new PoweredWheels();
        sensor.registerSensor("coords");
        sensor.registerSensor("energy");
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void assigning(
            @In("items") Map<Integer, Coordinates> items,
            @In("sensor") SensorySystem sensor,
            @InOut("order")ParamHolder<RobotOrder> order,
            @In("enemyOrders") Map<Integer, RobotOrder> enemyOrders,
            @In("enemyPlacements") Map<Integer, RobotData> enemyPlacements
    ) {
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        EnergyInput energy = sensor.getInputFromSensor("energy", EnergyInput.class);
        if (energy == null || coordinates == null) {
            return;
        }
        int assignedItem = -1;
        double distanceToItem = Double.MAX_VALUE;
        Set<Integer> itemKeys = items.keySet();

        for (int item : itemKeys) {
            Coordinates ic = items.get(item);
            double dist = Geometry.distance(coordinates.x, coordinates.y, ic.x, ic.y);
            if (dist < distanceToItem) {
                if (!enemyIsCloser(ic, dist, enemyOrders, enemyPlacements)) {
                    assignedItem = item;
                    distanceToItem = dist;
                }
            }
        }

        if (energy.energy < criticalCharge || (order.value != null
                && order.value.type == RobotOrder.Type.RECHARGE && energy.energy < fullCharge)) {
            order.value = new RobotOrder(RobotOrder.Type.RECHARGE, null);
        } else {
            if (assignedItem != -1) {
                order.value = new RobotOrder(RobotOrder.Type.FOLLOW, items.get(assignedItem));
            } else {
                order.value = new RobotOrder(RobotOrder.Type.STAY, null);
            }
        }
    }

    private static boolean enemyIsCloser(Coordinates item, double distance, Map<Integer, RobotOrder> enemyOrders,
                                         Map<Integer, RobotData> enemyPlacements) {
        Set<Integer> enemies = enemyOrders.keySet();
        for (int enemy : enemies) {
            RobotOrder order = enemyOrders.get(enemy);
            if (order.type == RobotOrder.Type.FOLLOW && order.target != null && order.target.equals(item)) {
                RobotData data = enemyPlacements.get(enemy);
                if (data != null) {
                    Coordinates enemyCoordinates = data.position;
                    if (Geometry.distance(order.target.x, order.target.y, enemyCoordinates.x, enemyCoordinates.y) < distance) {
                        return true;
                    }
                }
            }
        }
        return false;
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
            @In("order") RobotOrder order,
            @InOut("trajectoryOrder") ParamHolder<RobotOrder> trajectoryOrder,
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
        PoweredWheels pw = (PoweredWheels) wheels.value;
        pw.provideEnergy(sensor);

        //Decision-making
        if (order != null) {
            executeOrder(collisionData, order, pw, mode, charger.value, coordinates, trajectoryOrder, trajectory);
        }

        if (energy.energy > 0) {
            tag.value = String.valueOf(energy.energy);
        } else {
            tag.value = "BROKEN";
        }
    }

    private static void executeOrder(CollisionData collisionData, RobotOrder order, PoweredWheels pw, ParamHolder<Mode> mode,
                                     Coordinates charger, Coordinates coordinates, ParamHolder<RobotOrder> trajectoryOrder, ParamHolder<List<Coordinates>> trajectory) {

        if (order.type != RobotOrder.Type.STAY && frontalCollisionDetected(collisionData)
                && (mode.value != Mode.CHARGE || Geometry.distance(coordinates.x, coordinates.y, charger.x, charger.y) > 40)) {
            mode.value = Mode.AVOID;
            trajectory.value = constructAvoidanceTrajectory(coordinates, collisionData);
            trajectoryOrder.value = order;
        }
        if (mode.value == Mode.AVOID && order.type.equals(trajectoryOrder.value.type) &&
                ((order.target == null && trajectoryOrder.value.target == null) ||
                        (order.target != null && order.target.equals(trajectoryOrder.value.target)))) {
            if (trajectory.value.size() == 0) {
                mode.value = Mode.IDLE;
            } else {
                rotateToTarget(pw, coordinates, trajectory.value.get(0));
                if (Geometry.distance(coordinates.x, coordinates.y, trajectory.value.get(0).x, trajectory.value.get(0).y) < 4) {
                    trajectory.value.remove(0);
                }
            }
        } else if (order.type == RobotOrder.Type.RECHARGE) {
            mode.value = Mode.CHARGE;
            if (Geometry.distance(coordinates.x, coordinates.y, charger.x, charger.y) > 4) {
                rotateToTarget(pw, coordinates, charger);
            } else {
                pw.setAction(0,0);
            }
        } else if (order.type == RobotOrder.Type.FOLLOW) {
            mode.value = Mode.FOLLOW;
            rotateToTarget(pw, coordinates, order.target);
        } else if (order.type == RobotOrder.Type.STAY) {
            mode.value = Mode.IDLE;
            pw.setAction(0,0);
        }
    }

    private static List<Coordinates> constructAvoidanceTrajectory(Coordinates coordinates, CollisionData collisionData) {
        List<Coordinates> trajectory = new ArrayList<>();
        double collisionAngle = 0;
        for (double collision : collisionData.collisionPoints) {
            if (collision < Math.PI / 2 && collision > - Math.PI / 2) {
                collisionAngle = collision;
            }
        }
        double angle1 = Geometry.normalizeAngle(coordinates.angle + Math.PI + collisionAngle);
        double x1 = coordinates.x + 6 * Math.sin(angle1);
        double y1 = coordinates.y + 6 * Math.cos(angle1);
        trajectory.add(new Coordinates(x1, y1, 0));

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

    private static void rotateToTarget(Wheels wheels, Coordinates coordinates, Coordinates target) {
        wheels.setAction(1, Math.atan2(target.x - coordinates.x, target.y - coordinates.y) - coordinates.angle);
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
        IDLE, FOLLOW, FIGHT, CHARGE, AVOID
    }
}
