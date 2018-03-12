package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * DEECo Component that regulates the process of simulation by collecting information from robots and objects,
 * transmitting it to the environment, and invoking the computation of the next cycle in the {@link Environment}. It
 * regulates the main cycle of the simulation by going through 3 sequential phases:
 * 1. WAITING. Coordinator waits so that components' processes and ensembles' knowledge exchanges could take place before
 * it can start to collect their information.
 * 2. FETCHING. In this phase, Coordinator receives actions from robots via {@link RobotEnsemble} and current parameters
 * of objects via {@link ObjectEnsemble}.
 * 3. PROCESSING. Coordinator transmits received information to the {@link Environment} and then calls
 * {@link Environment#computeNextCycleAndWriteLogs()}. Environment computes positions of robots and inputs for all their
 * sensors.
 * Any knowledge of this component except for {@link Coordinator#endSignal} and {@link Coordinator#status} must not be
 * changed by any of the external ensembles because this will result in breaking the simulation apart.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class Coordinator {

    /**
     * This field is required for any DEECo component by JDEECo implementation
     */
    public String id;

    /**
     * Current phase.
     */
    public Coordinator.Phase phase = Phase.WAITING;

    /**
     * This array is filled with robots' actions and tags during FETCHING phase
     */
    public RobotData[] robotData;

    /**
     * This array is filled with objects' attributes during FETCHING phase
     */
    public ObjectData[] objectData;

    /**
     * Number of robots in the simulation.
     */
    public final Integer numOfRobots;

    /**
     * Number of objects in the simulation.
     */
    public final Integer numOfObjects;

    /**
     * Counts till the Coordinator can switch to the FETCHING phase.
     */
    public Integer counter = 0;

    /**
     * Number of the current cycle.
     */
    public Integer cycle = 0;

    /**
     * If this flag is true, simulation will stop after this cycle; it can be changed by other components.
     */
    public Boolean endSignal = false;

    /**
     * String that represents the status of the simulation; it can be changed by other components. This string is written
     * to the logs file in each cycle.
     */
    public String status;

    public Coordinator(int numOfRobots, int numOfObjects) {
        this.numOfRobots = numOfRobots;
        this.numOfObjects = numOfObjects;
        robotData = new RobotData[numOfRobots];
        objectData = new ObjectData[numOfObjects];
    }

    @Process
    @PeriodicScheduling(period = Environment.CYCLE)
    public static void cycle(
        @InOut("robotData") ParamHolder<RobotData[]> robotData,
        @InOut("objectData") ParamHolder<ObjectData[]> objectData,
        @InOut("phase") ParamHolder<Phase> phase,
        @In("numOfRobots") Integer numOfRobots,
        @In("numOfObjects") Integer numOfObjects,
        @InOut("cycle") ParamHolder<Integer> cycle,
        @InOut("counter") ParamHolder<Integer> counter,
        @In("endSignal") Boolean endSignal,
        @In("status") String status
    ) {
        try {
            if (phase.value == Phase.FETCHING) {
                //When all data is collected, we have to compute the next step
                phase.value = Phase.PROCESSING;
                Environment.getInstance().getLogger().fine("PROCESSING PHASE");
                Environment env = Environment.getInstance();
                //Sending all collected data to the environment:
                env.updateRobots(robotData.value);
                env.updateObjects(objectData.value);
                env.updateStatus(status);
                if (endSignal) {
                    env.stopSimulation();
                }
                //Computing positions and inputs for the next cycle:
                env.computeNextCycleAndWriteLogs();
                env.getLogger().fine("Cycle: " + cycle.value);
                cycle.value++;
                //Emptying arrays for the next cycle:
                for (int i = 0; i < numOfRobots; i++) {
                    robotData.value[i] = null;
                }
                for (int i = 0; i < numOfObjects; i++) {
                    objectData.value[i] = null;
                }

                phase.value = Phase.WAITING;
                Environment.getInstance().getLogger().fine("WAITING PHASE");
                counter.value = 0;
            } else if (phase.value == Phase.WAITING) {
                //In WAITING phase we only have to wait a specified time
                counter.value += Environment.CYCLE;
                if (counter.value >= Environment.getWaitingTime()) {
                    //After we have waited enough, we can start collecting robots' actions and other data from components
                    phase.value = Phase.FETCHING;
                    Environment.getInstance().getLogger().fine("FETCHING PHASE");
                }
            }
        } catch (Exception e) {
            Environment.getInstance().exitWithException(e);
        }
    }

    public enum Phase {
        FETCHING, PROCESSING, WAITING
    }

}
