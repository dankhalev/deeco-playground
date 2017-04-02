package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 1)
public class StationEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.tag") String tag,
            @In("member.powerStation") Coordinates target
    ) {
        return tag.equals("Power Station");
    }

    @KnowledgeExchange
    public static void map(
            @InOut("member.powerStation") ParamHolder<Coordinates> target,
            @In("coord.position") Coordinates objectPosition
    ) {
        target.value = objectPosition;
    }
}
