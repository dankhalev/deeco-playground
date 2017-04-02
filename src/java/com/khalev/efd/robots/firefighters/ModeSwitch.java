package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.HashMap;

@Ensemble
@PeriodicScheduling(period = 1)
public class ModeSwitch {

    @Membership
    public static boolean membership(
            @In("coord.teamOrders") HashMap teamOrders,
            @In("coord.teamPlacements") HashMap teamPlacements,
            @In("member.autonomous") Boolean autonomous
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.sensor") SensorySystem sensor,
            @InOut("member.autonomous") ParamHolder<Boolean> autonomous
    ) {
        EnergyInput energy = sensor.getInputFromSensor("energy", EnergyInput.class);
        autonomous.value = energy != null && energy.energy < FirefighterLeaderwheels.requestThreshold;
    }
}
