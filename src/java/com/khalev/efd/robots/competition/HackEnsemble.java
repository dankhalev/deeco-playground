package com.khalev.efd.robots.competition;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.HashMap;

@Ensemble
@PeriodicScheduling(period = 1)
public class HackEnsemble {

    @Membership
    public static boolean membership(
            @In("coord.teamOrders") HashMap<Integer, RobotOrder> teamOrders,
            @In("coord.teamPlacements") HashMap<Integer, RobotData> teamPlacements,
            @In("member.enemyOrders") HashMap<Integer, RobotOrder> enemyOrders,
            @In("member.enemyPlacements") HashMap<Integer, RobotData> enemyPlacements
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.teamOrders") HashMap<Integer, RobotOrder> teamOrders,
            @In("coord.teamPlacements") HashMap<Integer, RobotData> teamPlacements,
            @InOut("member.enemyOrders") ParamHolder<HashMap<Integer, RobotOrder>> enemyOrders,
            @InOut("member.enemyPlacements") ParamHolder<HashMap<Integer, RobotData>> enemyPlacements
    ) {
        enemyOrders.value = teamOrders;
        enemyPlacements.value = teamPlacements;
    }

}
