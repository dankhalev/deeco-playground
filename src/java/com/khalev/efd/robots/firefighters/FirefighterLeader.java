package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.Geometry;
import com.khalev.efd.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Component
public class FirefighterLeader extends FirefighterDrone {

    public HashMap<Integer, FirefighterOrder> teamOrders = new HashMap<>();
    public HashMap<Integer, FirefighterData> teamPlacements = new HashMap<>();

    public FirefighterLeader() {
        super();
        wheels = new FirefighterLeaderwheels();
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void assigning(
            @In("sensor") SensorySystem sensor,
            @In("firefighterID") Integer id,
            @InOut("autonomous") ParamHolder<Boolean> autonomous,
            @InOut("order") ParamHolder<FirefighterOrder> order,
            @In("position") Coordinates position,
            @InOut("teamOrders") ParamHolder<HashMap<Integer, FirefighterOrder>> teamOrders,
            @InOut("teamPlacements") ParamHolder<HashMap<Integer, FirefighterData>> teamPlacements
    ) {
        EnergyInput energy = sensor.getInputFromSensor("energy", EnergyInput.class);
        if (energy == null) {
            return;
        }
        autonomous.value = energy.energy < FirefighterLeaderwheels.requestThreshold;
        teamPlacements.value.put(id, new FirefighterData(position, energy.energy));
        ArrayList<PossibleAssignment> assignments = new ArrayList<>();
        Set<Integer> keys = teamPlacements.value.keySet();
        for (Integer key : keys) {
            for (int i = 0; i < energy.fires.size(); i++) {
                Coordinates ffc = teamPlacements.value.get(key).position;
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
            FirefighterData data = teamPlacements.value.get(key);
            if (data.energy < 300 || (teamOrders.value.get(key) != null && teamOrders.value.get(key).type == FirefighterOrder.Type.RECHARGE && data.energy < 1350)) {
                teamOrders.value.put(key, new FirefighterOrder(FirefighterOrder.Type.RECHARGE, null));
            } else {
                teamOrders.value.put(key, new FirefighterOrder(FirefighterOrder.Type.STAY, null));
                for (PossibleAssignment assignment : assignments) {
                    if (key.equals(assignment.FFID)) {
                        if (assignment.distance < 16) {
                            teamOrders.value.put(key, new FirefighterOrder(FirefighterOrder.Type.FIGHT, energy.fires.get(assignment.fireNumber)));
                        } else {
                            teamOrders.value.put(key, new FirefighterOrder(FirefighterOrder.Type.FOLLOW, energy.fires.get(assignment.fireNumber)));
                        }
                        break;
                    }
                }
            }
        }
        order.value = new FirefighterOrder(FirefighterOrder.Type.STAY, null);
        order.value = teamOrders.value.get(id);
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
