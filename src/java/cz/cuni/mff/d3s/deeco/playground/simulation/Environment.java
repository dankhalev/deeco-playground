package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.logging.Logger;

/**
 * This class represents the environment in which robots interact with each other. Before the start of the simulation an
 * instance of this class is created. Each cycle of simulation this instance receives information about current state of
 * components from {@link Coordinator}, moves robots according to their {@link Action}s using {@link SimulationEngine},
 * generates inputs for all types of sensors by going through the list of {@link SensoryInputsProcessor}s, and writes
 * simulation logs to the logfile. This instance is also used by robots' {@link SensorySystem}s to access the information
 * on their sensors.
 *
 * @author Danylo Khalyeyev
 */
abstract class Environment {

    static final String collisionSensorName = "collisions";
    static final int CYCLE = 1;
    private static int waitingTime = 1;
    private static Environment instance;

    static int getWaitingTime() {
        return waitingTime;
    }

    static boolean setWaitingTime(int i) {
        if (instance == null && i > 0) {
            waitingTime = i;
            return true;
        }
        return false;
    }

    static Environment getInstance() {
        return instance;
    }

    static boolean setInstance(Environment e) {
        if  (instance == null) {
            instance = e;
            return true;
        }
        return false;
    }

    static void reset() {
        instance = null;
        waitingTime = 1;
    }

    /**
     * Stores the data collected from robots.
     * @param data array with the data collected from robots. Its size should be equal to the number of robots in the
     *             simulation.
     */
    abstract void updateRobots(RobotData[] data);

    /**
     * Stores the data collected from objects.
     * @param data array with the data collected from objects. Its size should be equal to the number of objects in the
     *             simulation.
     */
    abstract void updateObjects(ObjectData[] data);

    /**
     * Stores a global status string.
     * @param status a status string to be stored.
     */
    abstract void updateStatus(String status);

    /**
     * Writes simulation logs to the logfile, moves robots according to their {@link Action}s using
     * {@link SimulationEngine}, and generates inputs for all types of sensors by going through the list of
     * {@link SensoryInputsProcessor}s.
     * @return 0 if simulation continues, 1 if it has ended.
     */
    abstract int computeNextCycleAndWriteLogs();

    /**
     * Through this method, {@link SensorySystem}s get access to inputs of their sensors.
     * @param rID {@link DEECoRobot#rID} of the robot
     * @param name name of the sensor
     * @param cls expected type of input
     * @param <T> expected type of input
     * @return the input of the specified sensor for the specified robot if this sensor exists and has an input type T,
     * null otherwise.
     */
    abstract <T> T getInputFromSensor(int rID, String name, Class<T> cls);

    /**
     * This method can be used to access the application's logger.
     * @return application's logger
     */
    abstract Logger getLogger();

    /**
     * After this method is called, simulation will stop with the next cycle.
     */
    abstract void stopSimulation();

    /**
     * This method is called when exception is handled by {@link Coordinator}, {@link RobotEnsemble} or
     * {@link ObjectEnsemble}. In DEECo, those exceptions would result in repeated call of the same process, and exception
     * would remain unnoticed. This method writes an exception to the logger and exits the program.
     * @param e exception that has occurred
     */
    abstract void exitWithException(Exception e);

}
