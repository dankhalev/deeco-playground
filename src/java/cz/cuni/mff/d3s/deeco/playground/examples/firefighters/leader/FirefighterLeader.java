package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureInput;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.FirefighterDrone;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.wheels.FirefighterLeaderWheels;
import cz.cuni.mff.d3s.deeco.playground.simulation.CollisionData;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.AutonomousMode;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.ControlledMode;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.FirefighterMode;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import java.util.*;

/**
 * The leader of the firefighter team. Extends {@link FirefighterDrone} class, and thus has the same decision() process.
 * In addition to that has assining() process, in which it computes the best possible orders for all the drones in the
 * team, and assigns them those orders. Has a special type of wheels, {@link FirefighterLeaderWheels} that enable him to
 * get data about all the fires on the field.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class FirefighterLeader extends FirefighterDrone {

    public Map<Integer, FirefighterOrder> teamOrders = new HashMap<>();
    public Map<Integer, FirefighterData> teamPlacements = new HashMap<>();
    public Boolean dataAvailable = true;

    public FirefighterLeader() {
        super();
        wheels = new FirefighterLeaderWheels();
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void assigning(
            @In("sensor") SensorySystem sensor,
            @In("firefighterID") Integer id,
            @InOut("mode") ParamHolder<FirefighterMode> mode,
            @InOut("teamOrders") ParamHolder<Map<Integer, FirefighterOrder>> teamOrders,
            @InOut("teamPlacements") ParamHolder<Map<Integer, FirefighterData>> teamPlacements,
            @InOut("dataAvailable") ParamHolder<Boolean> dataAvailable
    ) {
        EnergyTemperatureInput energy = sensor.getInputFromSensor("energy", EnergyTemperatureInput.class);
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        if (energy == null || collisionData == null || coordinates == null) {
            return;
        }

        if (energy.energy < FirefighterLeaderWheels.requestThreshold && mode.value instanceof ControlledMode) {
            mode.value = new AutonomousMode();
            dataAvailable.value = false;
        }
        if (energy.energy > FirefighterLeaderWheels.requestThreshold && mode.value instanceof AutonomousMode) {
            mode.value = new ControlledMode();
            dataAvailable.value = true;
        }
        teamPlacements.value.put(id, new FirefighterData(coordinates, energy, collisionData));

        assignOrders(teamOrders.value, teamPlacements.value, energy);

        mode.value.receiveOrder(new StayOrder());
        mode.value.receiveOrder(teamOrders.value.get(id));
    }

    /**
     * Algorithm that determines the most effective set of orders for the team of firefighters at the moment.
     * @param teamOrders Map of orders
     * @param teamPlacements Map of {@link FirefighterData} for the team
     * @param energy {@link EnergyTemperatureInput} of a leader that includes positions of fires on the map.
     */
    private static void assignOrders(Map<Integer, FirefighterOrder> teamOrders, Map<Integer, FirefighterData> teamPlacements, EnergyTemperatureInput energy) {
        List<PossibleAssignment> assignments = new ArrayList<>();
        Set<Integer> keys = teamPlacements.keySet();
        for (Integer key : keys) {
            for (int i = 0; i < energy.fires.size(); i++) {
                Coordinates ffc = teamPlacements.get(key).position;
                PossibleAssignment pa = new PossibleAssignment();
                pa.FFID = key;
                pa.fireNumber = i;
                pa.distance = Geometry.distance(ffc.x, ffc.y, energy.fires.get(i).x, energy.fires.get(i).y);
                assignments.add(pa);
            }
        }
        Collections.sort(assignments);
        for (int i = 0; i < assignments.size(); i++) {
            for (int j = i+1; j < assignments.size();) {
                if (assignments.get(j).fireNumber == assignments.get(i).fireNumber
                        || assignments.get(j).FFID == assignments.get(i).FFID) {
                    assignments.remove(j);
                } else {
                    j++;
                }
            }
        }
        for (Integer key : keys) {
            FirefighterData data = teamPlacements.get(key);
            FirefighterOrder currentOrder = teamOrders.get(key);
            if (data.energyTemperatureInput.energy < 300 || (currentOrder != null && currentOrder instanceof RechargeOrder && data.energyTemperatureInput.energy < 1350)) {
                teamOrders.put(key, new RechargeOrder());
            } else {
                teamOrders.put(key, new StayOrder());
                for (PossibleAssignment assignment : assignments) {
                    if (key.equals(assignment.FFID)) {
                        if (assignment.distance < 16) {
                            teamOrders.put(key, new FightOrder(energy.fires.get(assignment.fireNumber)));
                        } else {
                            teamOrders.put(key, new FollowOrder(energy.fires.get(assignment.fireNumber)));
                        }
                        break;
                    }
                }
            }
        }
    }

    static class PossibleAssignment implements Comparable<PossibleAssignment> {

        int FFID;
        int fireNumber;
        double distance;

        @Override
        public int compareTo(PossibleAssignment possibleAssignment) {
            return Double.compare(this.distance, possibleAssignment.distance);
        }
    }

}
