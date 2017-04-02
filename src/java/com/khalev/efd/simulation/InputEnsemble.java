package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.ArrayList;

/**
 * The function of this Ensemble is to get robots' inputs from {@link Coordinator} and to store them on robots' sensors.
 * This Ensemble is always present in simulation.
 */
@Ensemble
@PeriodicScheduling(period = Environment.CYCLE)
public class InputEnsemble {

    /**
     * Membership is always true for pair {@link Coordinator} - {@link DEECoRobot}
     */
    @Membership
    public static boolean membership(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.rID") Integer rid,
            @In("member.sensor") SensorySystem sensor,
            @In("coord.sensorNames") ArrayList<String> names,
            @In("coord.allInputs") ArrayList<ArrayList> allInputs,
            @In("coord.inputSent") Boolean[] sent
    ) {
        return true;
    }

    /**
     * Calls receiveInput() on robot's SensorySystem for each of existing sensors.
     */
    @KnowledgeExchange
    public static void map(
            @In("coord.phase") Coordinator.Phase phase,
            @In("member.rID") Integer rid,
            @InOut("member.sensor") ParamHolder<SensorySystem> sensor,
            @In("coord.sensorNames") ArrayList<String> names,
            @In("coord.allInputs") ArrayList<ArrayList> allInputs,
            @InOut("coord.inputSent") ParamHolder<Boolean[]> sent
    ) {
        if (phase.equals(Coordinator.Phase.SENDING)) {
            for (int i = 0; i < names.size(); i++) {
                sensor.value.receiveInput(names.get(i), allInputs.get(i).get(rid));
                sent.value[rid] = true;
            }
        }
    }
}
