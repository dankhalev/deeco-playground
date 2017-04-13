package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.Coordinator;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.HashMap;
import java.util.Map;

@Ensemble
@PeriodicScheduling(period = 1)
public class StatusUpdate {

    @Membership
    public static boolean membership(
            @In("coord.team1") Map<Integer, Integer> team1,
            @In("coord.team2") Map<Integer, Integer> team2,
            @In("member.phase") Coordinator.Phase phase,
            @In("member.status") String status,
            @In("member.cycle") Integer cycle
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.team1") Map<Integer, Integer> team1,
            @In("coord.team2") Map<Integer, Integer> team2,
            @InOut("member.status") ParamHolder<String> status,
            @In("member.cycle") Integer cycle
    ) {
        int team1Score = 0;
        int team2Score = 0;
        for (int integer : team1.values()) {
            team1Score += integer;
        }
        for (Integer integer : team2.values()) {
            team2Score += integer;
        }
        status.value = "CYCLE: " + cycle + "; SCORE: " + team1Score + "/" + team2Score;
    }

}
