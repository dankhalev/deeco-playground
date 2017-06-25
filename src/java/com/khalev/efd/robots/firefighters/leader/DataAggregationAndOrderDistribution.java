package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.environment.EnergyTemperatureInput;
import com.khalev.efd.robots.firefighters.drone.mode.FirefighterMode;
import com.khalev.efd.simulation.CollisionData;
import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import java.util.Map;

/**
 * This ensemble informs firefighters' leader about the situation of each drone by passing their sensory data to him.
 * It also manages the distribution of orders from leader to drones.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class DataAggregationAndOrderDistribution {

    @Membership
    public static boolean membership(
            @In("coord.teamPlacements") Map<Integer, FirefighterData> teamPlacements,
            @In("coord.firefighterID") Integer cID,
            @In("member.firefighterID") Integer fID
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @InOut("coord.teamPlacements") ParamHolder<Map<Integer, FirefighterData>> teamPlacements,
            @In("coord.teamOrders") Map<Integer, FirefighterOrder> teamOrders,
            @InOut("member.mode") ParamHolder<FirefighterMode> mode,
            @In("member.firefighterID") Integer fID,
            @In("member.sensor") SensorySystem sensor
    ) {
        //Get sensory inputs
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        EnergyTemperatureInput energy = sensor.getInputFromSensor("energy", EnergyTemperatureInput.class);
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        //Inform leader about drone's situation
        if (energy != null && coordinates != null && collisionData != null) {
            teamPlacements.value.put(fID, new FirefighterData(coordinates, energy, collisionData));
        }
        //Pass leader's order to drone
        mode.value.receiveOrder(teamOrders.get(fID));
    }
}
