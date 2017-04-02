package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 1)
public class ChargerPositionEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.tag") String tag,
            @In("coord.oID") Integer oid,
            @In("coord.teamID") String teamID1,
            @In("member.teamID") String teamID2,
            @In("member.rID") Integer rid
    ) {
        return ("Power Station".equals(tag) && teamID2.equals(teamID1));
    }

    @KnowledgeExchange
    public static void map(
            @InOut("member.powerStation") ParamHolder<Coordinates> powerStation,
            @In("coord.position") Coordinates position
    ) {
        powerStation.value = position;
    }

}
