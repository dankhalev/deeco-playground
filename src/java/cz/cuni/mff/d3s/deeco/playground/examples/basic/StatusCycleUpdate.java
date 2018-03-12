package cz.cuni.mff.d3s.deeco.playground.examples.basic;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinator;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Updates the simulation status to display a current cycle number.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class StatusCycleUpdate {

    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("coord.status") String status,
            @In("coord.cycle") Integer cycle
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @InOut("coord.status") ParamHolder<String> status,
            @In("coord.cycle") Integer cycle
    ) {
        status.value = "CYCLE: " + cycle;
    }

}
