package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.ArrayList;

//TODO: Remove received[] and sent[] controls
/**
 * DEECo Component that communicates information from robots to the environment and vice versa. It regulates the main
 * cycle of the simulation by going through 4 sequential phases:
 * 1. FETCHING. In this phase, Coordinator receives actions from robots via {@link ActionEnsemble} and current parameters
 * of objects via {@link ObjectEnsemble}
 * 2. PROCESSING. Coordinator transmits received information to the {@link Environment} and then calls Environment.cycle().
 * Environment computes positions of robots and inputs for all their sensors. Coordinator receives those inputs.
 * 3. SENDING. Inputs that were computed in previous phase are distributed to robots via {@link InputEnsemble}.
 * 4. WAITING. Coordinator waits so that components' processes and ensembles' knowledge exchanges could take place before
 * transitioning to the next cycle.
 * Any knowledge of this component except for {@link Coordinator#endSignal} and {@link Coordinator#status} must not be
 * changed by any of the external ensembles for it will break down the simulation.
 */
@Component
public class Coordinator {

    /**
     * This field is required for any DEECo component by JDEECo implementation
     */
    public String id;

    public Coordinator.Phase phase = Phase.FETCHING;

    /**
     * This array is filled with robots' actions and tags during FETCHING phase
     */
    public RobotData[] actions;
    public Boolean[] actionReceived;

    /**
     * This array is filled with objects' attributes during FETCHING phase
     */
    public ObjectData[] objects;
    public Boolean[] objectReceived;

    /**
     * This array is filled with inputs that will be distributed to robots during SENDING phase
     */
    public ArrayList<ArrayList> allInputs;
    public Boolean[] inputSent;

    /**
     * List of sensor names. Created during simulation setup from simulation parameters
     */
    public ArrayList<String> sensorNames;
    public final Integer numOfRobots;
    public final Integer numOfObjects;

    public Integer counter = 0;
    public Integer cycle = 0;

    /**
     * If this flag is true, simulation will stop after this cycle; it can be changed by other components.
     */
    public Boolean endSignal = false;

    /**
     * This string represents the status of the simulation; it can be changed by other components. This string is written
     * to the logs file in each cycle.
     */
    public String status;


    public Coordinator(int numOfRobots, int numOfObjects, ArrayList<String> sensorNames) {
        this.numOfRobots = numOfRobots;
        this.numOfObjects = numOfObjects;
        this.sensorNames = sensorNames;
        actions = new RobotData[numOfRobots];
        actionReceived = new Boolean[numOfRobots];
        objects = new ObjectData[numOfObjects];
        objectReceived = new Boolean[numOfObjects];
        inputSent = new Boolean[numOfRobots];
        for (int i = 0; i < numOfRobots; i++) {
            actionReceived[i] = false;
            inputSent[i] = false;
        }
        for (int i = 0; i < numOfObjects; i++) {
            objectReceived[i] = false;
        }
    }

    /**
     * This process is used to switch between phases in the simulation, regulating its flow
     */
    @Process
    @PeriodicScheduling(period = Environment.CYCLE)
    public static void nextCycle(
        @InOut("actions") ParamHolder<RobotData[]> actions,
        @InOut("actionReceived") ParamHolder<Boolean[]> actionReceived,
        @InOut("objects") ParamHolder<ObjectData[]> objects,
        @InOut("objectReceived") ParamHolder<Boolean[]> objectReceived,
        @InOut("allInputs") ParamHolder<ArrayList<ArrayList>> allInputs,
        @InOut("inputSent") ParamHolder<Boolean[]> sent,
        @InOut("phase") ParamHolder<Phase> phase,
        @In("numOfRobots") Integer numOfRobots,
        @In("numOfObjects") Integer numOfObjects,
        @InOut("cycle") ParamHolder<Integer> cycle,
        @InOut("counter") ParamHolder<Integer> counter,
        @In("endSignal") Boolean endSignal,
        @In("status") String status
    ) {
        if (phase.value == Phase.FETCHING && andAll(actionReceived.value) && andAll(objectReceived.value)) {
            //When all data is collected, we have to compute the next step
            phase.value = Phase.PROCESSING;
            Environment env = Environment.getInstance();
            //Sending all collected data to environment:
            env.updateActions(actions.value);
            env.updateObjects(objects.value);
            env.updateStatus(status);
            if (endSignal) {
                env.stopSimulation();
            }
            //Computing positions and inputs for the next cycle:
            env.cycle();
            env.logger.fine("Cycle: " + cycle.value);
            cycle.value++;
            allInputs.value = env.getAllInputs();
            //Emptying arrays for the next cycle:
            for (int i = 0; i < numOfRobots; i++) {
                sent.value[i] = false;
                actionReceived.value[i] = false;
                actions.value[i] = null;
            }
            for (int i = 0; i < numOfObjects; i++) {
                objectReceived.value[i] = false;
            }
            //And now, we can proceed to distributing those inputs:
            phase.value = Phase.SENDING;
            env.logger.fine("SENDING PHASE");
        } else if (phase.value == Phase.SENDING && andAll(sent.value)) {
            //If all inputs are sent than we have to wait for robots to make their decisions
            phase.value = Phase.WAITING;
            counter.value = 0;
            Environment.getInstance().logger.fine("WAITING PHASE");
        } else if (phase.value == Phase.WAITING) {
            //In WAITING phase we only have to wait a specified time
            if (counter.value < Simulation.getCYCLE()) {
                counter.value += Environment.CYCLE;
            } else {
                //After we have waited enough, we can start collecting robots' actions and other data from components
                phase.value = Phase.FETCHING;
                Environment.getInstance().logger.fine("FETCHING PHASE");
            }
        }
    }

    private static boolean andAll(Boolean[] f) {
        for (boolean b : f) {
            if (!b)
                return false;
        }
        return true;
    }

    public enum Phase {
        FETCHING, PROCESSING, SENDING, WAITING
    }

}
