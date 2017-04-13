package com.khalev.efd.robots.advanced;

import com.khalev.efd.simulation.Coordinator;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 1)
public class GameOverEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.spawns") Integer spawns
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @InOut("coord.endSignal") ParamHolder<Boolean> endSignal,
            @In("member.spawns") Integer spawns
    ) {
        endSignal.value = spawns >= 15;
    }

}
