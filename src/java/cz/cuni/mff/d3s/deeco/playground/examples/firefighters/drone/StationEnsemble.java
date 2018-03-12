package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Through this ensemble, the charging station advertises its position to firefighter robots.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class StationEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.tag") String tag,
            @In("member.powerStation") Coordinates target
    ) {
        return tag.equals("Charging Station");
    }

    @KnowledgeExchange
    public static void map(
            @InOut("member.powerStation") ParamHolder<Coordinates> target,
            @In("coord.position") Coordinates objectPosition
    ) {
        target.value = objectPosition;
    }
}
