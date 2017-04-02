package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.HashMap;

@Ensemble
@PeriodicScheduling(period = 1)
public class DataAggregationAndOrderDistribution {

    @Membership
    public static boolean membership(
            @In("coord.teamPlacements") HashMap<Integer, FirefighterData> teamPlacements,
            @In("coord.firefighterID") Integer cID,
            @In("member.firefighterID") Integer fID
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @InOut("coord.teamPlacements") ParamHolder<HashMap<Integer, FirefighterData>> teamPlacements,
            @In("coord.teamOrders") HashMap<Integer, FirefighterOrder> teamOrders,
            @InOut("member.order") ParamHolder<FirefighterOrder> order,
            @In("member.firefighterID") Integer fID,
            @In("member.sensor") SensorySystem sensor,
            @In("member.position") Coordinates position
    ) {
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        EnergyInput energy = sensor.getInputFromSensor("energy", EnergyInput.class);
        if (energy != null && coordinates != null) {
            teamPlacements.value.put(fID, new FirefighterData(coordinates, energy.energy));
        }
        order.value = teamOrders.get(fID);
    }
}
