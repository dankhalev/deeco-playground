package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.List;

/**
 * Common ancestor for all input processors. Input processors are meant to generate inputs for corresponding sensors.
 * By default there is only a one type of sensor, called collision sensor, it is represented by a {@link SimulationEngine}
 * class. User is free to add any number of additional sensor types by extending this class.
 * @param <T> Type of input that this processor sends to robots' sensors
 *
 * @author Danylo Khalyeyev
 */
public abstract class SensoryInputsProcessor<T> {

    /**
     * A map of physical obstacles in the environment.
     */
    protected EnvironmentMap environmentMap;

    /**
     * Generates a list of inputs for robots.
     * @param robots unmodifiable list of robots in the simulation. Can be used to access their parameters. For each of
     *               those robots the return list should contain an input value at the same index.
     * @param objects unmodifiable list of objects in the simulation. Can be used to access their parameters.
     * @return ArrayList containing inputs for each robot. Size of this list must be equal to size of the list of robots.
     * Even if some particular robot doesn't have a sensor of this type, its input still should be generated.
     */
    protected abstract List<T> sendInputs(List<RobotPlacement> robots, List<ObjectPlacement> objects);

    void setEnvironmentMap(EnvironmentMap environmentMap) {
        this.environmentMap = environmentMap;
    }

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified in scenario XML file
     */
    protected void processArg(String arg) {

    }

}
