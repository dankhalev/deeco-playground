package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.Map;

/**
 * Ensemble that updates the score of the game.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class ScoreUpdate {

    @Membership
    public static boolean membership(
            @In("coord.team1") Map<Integer, Integer> team1,
            @In("coord.team2") Map<Integer, Integer> team2,
            @In("member.itemID") Integer itemID
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @InOut("coord.team1") ParamHolder<Map<Integer, Integer>> team1,
            @InOut("coord.team2") ParamHolder<Map<Integer, Integer>> team2,
            @In("member.itemID") Integer itemID,
            @In("member.collectedByTeam1") Integer collectedByTeam1,
            @In("member.collectedByTeam2") Integer collectedByTeam2
    ) {
        team1.value.put(itemID, collectedByTeam1);
        team2.value.put(itemID, collectedByTeam2);
    }
}
