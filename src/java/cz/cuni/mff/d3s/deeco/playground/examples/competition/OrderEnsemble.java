package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.Map;


/**
 * Distributes orders from {@link CommandStation} to {@link ControlledRobot}s.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class OrderEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.teamOrders") Map<Integer, RobotOrder> teamOrders,
            @In("coord.teamPlacements") Map<Integer, RobotData> teamPlacements,
            @In("member.TMID") Integer id
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.teamOrders") Map<Integer, RobotOrder> teamOrders,
            @InOut("coord.teamPlacements") ParamHolder<Map<Integer, RobotData>> teamPlacements,
            @InOut("member.order") ParamHolder<RobotOrder> order,
            @In("member.sensor") SensorySystem sensor,
            @In("member.TMID") Integer id
    ) {
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        EnergyInput energy = sensor.getInputFromSensor("energy", EnergyInput.class);
        if (coordinates != null && energy != null) {
            teamPlacements.value.put(id, new RobotData(coordinates, energy.energy));
        }
        order.value = teamOrders.get(id);
    }

}
