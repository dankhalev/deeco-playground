package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinator;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.Map;

/**
 * Ensemble that updates the global simulation status and finishes the simulation, when a team
 * receives enough points.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class StatusUpdate {

    static int scoreToWin = 30;

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
            @In("member.cycle") Integer cycle,
            @InOut("member.endSignal") ParamHolder<Boolean> endSignal
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
        if (team1Score >= scoreToWin) {
            status.value = "CYCLE: " + cycle + "; Team Red wins the game!";
            endSignal.value = true;
        }
        if (team2Score >= scoreToWin){
            status.value = "CYCLE: " + cycle + "; Team Blue wins the game!";
            endSignal.value = true;
        }
    }

}
