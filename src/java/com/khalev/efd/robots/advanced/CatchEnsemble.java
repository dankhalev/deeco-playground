package com.khalev.efd.robots.advanced;

import com.khalev.efd.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Ensemble
@PeriodicScheduling(period = 1)
public class CatchEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.found") Boolean found,
            @In("member.treasure") Coordinates newTarget
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @InOut("coord.found") ParamHolder<Boolean> found,
            @In("coord.position") Coordinates objectPosition,
            @InOut("member.treasure") ParamHolder<Coordinates> target,
            @In("member.position") Coordinates robotPosition
    ) {
        if(robotPosition != null) {
            target.value = objectPosition;
            double xDist = (robotPosition.x - objectPosition.x);
            double yDist = (robotPosition.y - objectPosition.y);
            double distance = xDist * xDist + yDist * yDist;
            if (distance < 6.0) {
                found.value = true;
            }
        }
    }
}
