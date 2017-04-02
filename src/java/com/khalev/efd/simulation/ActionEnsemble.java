package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

//TODO: merge ActionEnsemble & InputEnsemble
/**
 * The function of this Ensemble is to collect actions and tags from robots and to store them on {@link Coordinator}
 * object. This Ensemble is always present in simulation.
 */
@Ensemble
@PeriodicScheduling(period = Environment.CYCLE)
public class ActionEnsemble {

    /**
     * Membership is always true for pair {@link Coordinator} - {@link DEECoRobot}
     */
    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.rID") Integer rid,
            @In("member.wheels") Wheels wheels,
            @In("member.tag") String tag,
            @In("coord.actions") RobotData[] actions,
            @In("coord.actionReceived") Boolean[] received
    ) {
        return true;
    }

    //TODO: move part of this doc to Wheels
    /**
     * Calls sendCurrentAction() on robot's Wheels to obtain Action. Then stores action and tag on {@link Coordinator}.
     * Robot's {@link Wheels} are also stored back, so all the changes that were made during sendCurrentAction() call
     * are preserved. Note, that due to the specifics of JDEECo implementation, Knowledge Exchange is performed
     * separately on robot and Coordinator, so sendCurrentAction() is called twice in each cycle and Wheels are stored
     * only once. Those calls are executed in unknown order and any other processes can be called between them.
     */
    @KnowledgeExchange
    public static void map(
            @In("coord.phase") Coordinator.Phase phase,
            @In("coord.cycle") Integer cycle,
            @In("member.rID") Integer rid,
            @InOut("member.wheels") ParamHolder<Wheels> wheels,
            @In("member.tag") String tag,
            @InOut("coord.actions") ParamHolder<RobotData[]> actions,
            @InOut("coord.actionReceived") ParamHolder<Boolean[]> received
    ) {
        if (phase.equals(Coordinator.Phase.FETCHING)) {
            Action action = wheels.value.sendCurrentAction(cycle);
            if (actions.value[rid] == null) {
                RobotData robotData = new RobotData(action, tag);
                actions.value[rid] = robotData;
                received.value[rid] = true;
            }
        }
    }
}
