package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * The function of this Ensemble is to collect objects' positions, sizes and tags from robots and to store them on {@link Coordinator}
 * object. This Ensemble is always present in simulation.
 */
@Ensemble
@PeriodicScheduling(period = Environment.CYCLE)
public class ObjectEnsemble {

    /**
     * Membership is always true for pair {@link Coordinator} - {@link DEECoObject}
     */
    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.oID") Integer oid,
            @In("member.position") Coordinates position,
            @In("member.tag") String tag,
            @In("coord.objects") ObjectData[] objects,
            @In("coord.objectReceived") Boolean[] received
    ) {
        return true;
    }

    /**
     * Stores object's position, size and tag on {@link Coordinator}.
     */
    @KnowledgeExchange
    public static void map(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.oID") Integer oid,
            @In("member.position") Coordinates position,
            @In("member.size") Double size,
            @In("member.tag") String tag,
            @InOut("coord.objects") ParamHolder<ObjectData[]> objects,
            @InOut("coord.objectReceived") ParamHolder<Boolean[]> received
    ) {
        if (phase.equals(Coordinator.Phase.FETCHING)) {
            if (!received.value[oid]) {
                ObjectData objectData = new ObjectData(position, size, tag);
                objects.value[oid] = objectData;
                received.value[oid] = true;
            }
        }
    }
}
