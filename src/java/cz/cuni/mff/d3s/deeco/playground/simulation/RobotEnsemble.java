package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;


/**
 * The function of this Ensemble is to collect actions and tags from robots and to store them on {@link Coordinator}.
 * This Ensemble is always present in simulation. Membership in this ensemble is always true for each pair
 * {@link Coordinator} - {@link DEECoRobot}.
 *
 * It calls sendCurrentAction() on robot's Wheels to obtain Action. Then stores action and tag on {@link Coordinator}.
 * Robot's {@link Wheels} are also stored back, so all the changes that were made during sendCurrentAction() call
 * are preserved. However, due to the specifics of JDEECo implementation, Knowledge Exchange is performed
 * separately on robot and Coordinator, so sendCurrentAction() is called twice in each cycle and Wheels are stored
 * back only once. Those calls are executed in unknown order and any other processes can be called between them.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = Environment.CYCLE)
public class RobotEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.rID") Integer rid,
            @In("member.wheels") Wheels wheels,
            @In("member.tag") String tag,
            @In("coord.robotData") RobotData[] robots
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.phase") Coordinator.Phase phase,
            @In("coord.cycle") Integer cycle,
            @In("member.rID") Integer rid,
            @InOut("member.wheels") ParamHolder<Wheels> wheels,
            @In("member.tag") String tag,
            @InOut("coord.robotData") ParamHolder<RobotData[]> robots
    ) {
        try {
            if (phase.equals(Coordinator.Phase.FETCHING)) {
                Action action = wheels.value.sendCurrentAction(cycle);
                if (robots.value[rid] == null) {
                    RobotData robotData = new RobotData(action, tag);
                    robots.value[rid] = robotData;
                }
            }
        } catch (Exception e) {
            Environment.getInstance().exitWithException(e);
        }
    }

}
