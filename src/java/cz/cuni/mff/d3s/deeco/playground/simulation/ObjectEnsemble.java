package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * The function of this Ensemble is to collect objects' positions, sizes and tags from robots and to store them on
 * {@link Coordinator}. This Ensemble is always present in simulation. Membership in this ensemble is always true for
 * each pair {@link Coordinator} - {@link DEECoObject}.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = Environment.CYCLE)
public class ObjectEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.oID") Integer oid,
            @In("member.position") Coordinates position,
            @In("member.tag") String tag,
            @In("coord.objectData") ObjectData[] objects
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.oID") Integer oid,
            @In("member.position") Coordinates position,
            @In("member.size") Double size,
            @In("member.tag") String tag,
            @InOut("coord.objectData") ParamHolder<ObjectData[]> objects
    ) {
        try {
            if (phase.equals(Coordinator.Phase.FETCHING)) {
                if (objects.value[oid] == null) {
                    ObjectData objectData = new ObjectData(position, size, tag);
                    objects.value[oid] = objectData;
                }
            }
        } catch (Exception e) {
            Environment.getInstance().exitWithException(e);
        }
    }

}
