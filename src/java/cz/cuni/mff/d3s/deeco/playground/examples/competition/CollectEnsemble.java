package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Responsible for interaction between robots and {@link CollectibleItem}s, when they are close to each other.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class CollectEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.itemID") Integer itemID,
            @In("member.rID") Integer rID
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("member.sensor") SensorySystem sensorySystem,
            @In("coord.position") Coordinates position,
            @InOut("coord.found") ParamHolder<Boolean> found,
            @InOut("coord.collectedByTeam1") ParamHolder<Integer> team1,
            @InOut("coord.collectedByTeam2") ParamHolder<Integer> team2,
            @In("member.teamID") String tID
    ) {
        Coordinates coordinates = sensorySystem.getInputFromSensor("coords", Coordinates.class);
        if (coordinates != null && position != null && !found.value) {
            if (Geometry.distance(coordinates.x, coordinates.y, position.x, position.y) < 4) {
                found.value = true;
                if ("T1".equals(tID)) {
                    team1.value++;
                } else if ("T2".equals(tID)) {
                    team2.value++;
                }
            }
        }
    }

}
