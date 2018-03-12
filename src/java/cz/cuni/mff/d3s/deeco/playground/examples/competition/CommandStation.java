package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.*;

/**
 * Recharge station for Team Red. Apart from recharging robots' batteries, distributes orders to robots.
 * It decides about the best possible destination for robots, based on their positions, positions of
 * items, and robots' energies.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class CommandStation extends DEECoObject {

    public String teamID = "T1";
    public Map<Integer, RobotOrder> teamOrders = new HashMap<>();
    public Map<Integer, RobotData> teamPlacements = new HashMap<>();
    public Map<Integer, Coordinates> items = new HashMap<>();

    public static final int criticalCharge = 80;
    public static final int fullCharge = 300;

    @Process
    @PeriodicScheduling(period = 1)
    public static void assigning(
            @InOut("teamOrders") ParamHolder<Map<Integer, RobotOrder>> teamOrders,
            @InOut("teamPlacements") ParamHolder<Map<Integer, RobotData>> teamPlacements,
            @In("items") Map<Integer, Coordinates> items
    ) {

        List<PossibleAssignment> assignments = new ArrayList<>();
        Set<Integer> teammateKeys = teamPlacements.value.keySet();
        Set<Integer> itemKeys = items.keySet();

        for (Integer teammate : teammateKeys) {
            for (Integer item : itemKeys) {
                Coordinates tmc = teamPlacements.value.get(teammate).position;
                Coordinates ic = items.get(item);
                PossibleAssignment pa = new PossibleAssignment();
                pa.TMID = teammate;
                pa.itemNumber = item;
                pa.distance = Geometry.distance(tmc.x, tmc.y, ic.x, ic.y);
                assignments.add(pa);
            }
        }
        Collections.sort(assignments);
        for (int i = 0; i < assignments.size(); i++) {
            for (int j = i+1; j < assignments.size();) {
                if (assignments.get(j).itemNumber == assignments.get(i).itemNumber
                        || assignments.get(j).TMID == assignments.get(i).TMID) {
                    assignments.remove(j);
                } else {
                    j++;
                }
            }
        }
        for (Integer key : teammateKeys) {
            RobotData data = teamPlacements.value.get(key);
            if (data.energy < criticalCharge || (teamOrders.value.get(key) != null
                    && teamOrders.value.get(key).type == RobotOrder.Type.RECHARGE && data.energy < fullCharge)) {
                teamOrders.value.put(key, new RobotOrder(RobotOrder.Type.RECHARGE, null));
            } else {
                teamOrders.value.put(key, new RobotOrder(RobotOrder.Type.STAY, null));
                for (PossibleAssignment assignment : assignments) {
                    if (key.equals(assignment.TMID)) {
                        teamOrders.value.put(key, new RobotOrder(RobotOrder.Type.FOLLOW, items.get(assignment.itemNumber)));
                        break;
                    }
                }
            }
        }
    }

    static class PossibleAssignment implements Comparable<PossibleAssignment> {

        int TMID;
        int itemNumber;
        double distance;

        @Override
        public int compareTo(PossibleAssignment possibleAssignment) {
            return Double.compare(this.distance, possibleAssignment.distance);
        }
    }
}
