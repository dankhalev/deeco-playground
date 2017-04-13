package com.khalev.efd.robots.competition;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.HashMap;
import java.util.Map;

@Ensemble
@PeriodicScheduling(period = 1)
public class HackEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.teamOrders") Map<Integer, RobotOrder> teamOrders,
            @In("coord.teamPlacements") Map<Integer, RobotData> teamPlacements,
            @In("member.enemyOrders") Map<Integer, RobotOrder> enemyOrders,
            @In("member.enemyPlacements") Map<Integer, RobotData> enemyPlacements
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.teamOrders") Map<Integer, RobotOrder> teamOrders,
            @In("coord.teamPlacements") Map<Integer, RobotData> teamPlacements,
            @InOut("member.enemyOrders") ParamHolder<Map<Integer, RobotOrder>> enemyOrders,
            @InOut("member.enemyPlacements") ParamHolder<Map<Integer, RobotData>> enemyPlacements
    ) {
        enemyOrders.value = teamOrders;
        enemyPlacements.value = teamPlacements;
    }

}
