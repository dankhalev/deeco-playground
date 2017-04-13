package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.HashMap;
import java.util.Map;

@Ensemble
@PeriodicScheduling(period = 1)
public class ItemPositionSignal {

    @Membership
    public static boolean membership(
            @In("coord.items") Map<Integer, Coordinates> items,
            @In("member.found") Boolean found,
            @In("member.size") Double size,
            @In("member.itemID") Integer id
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("member.found") Boolean found,
            @In("member.size") Double size,
            @InOut("coord.items") ParamHolder<Map<Integer, Coordinates>> items,
            @In("member.position") Coordinates position,
            @In("member.itemID") Integer id
    ) {
        if (size > 0 && !found) {
            items.value.put(id, position);
        } else {
            items.value.remove(id);
        }
    }

}
