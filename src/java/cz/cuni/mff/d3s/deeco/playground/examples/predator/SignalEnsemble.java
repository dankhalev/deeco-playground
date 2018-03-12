package cz.cuni.mff.d3s.deeco.playground.examples.predator;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Sends alert signal to a {@link PreyRobot} when a {@link PredatorRobot} shows up nearby.
 * Also, sends the predator's coordinates.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class SignalEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.target") Coordinates target,
            @In("coord.position") Coordinates predator,
            @In("member.alert") Boolean alert,
            @In("member.position") Coordinates prey
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.position") Coordinates predatorCoords,
            @In("member.position") Coordinates preyCoords,
            @InOut("member.predator") ParamHolder<Coordinates> predator,
            @InOut("member.alert") ParamHolder<Boolean> alert,
            @In("member.rID") Integer rID
    ) {
        predator.value = predatorCoords;
        double xDist = (predatorCoords.x - preyCoords.x);
        double yDist = (predatorCoords.y - preyCoords.y);
        double distance =  xDist*xDist + yDist*yDist;
        alert.value = distance < 400;
    }

}
