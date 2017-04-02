package com.khalev.efd.simulation;

import java.util.ArrayList;

/**
 * Common ancestor for all input processors.
 * @param <T> Type of input that this processor sends to robots' sensors
 */
public abstract class SensoryInputsProcessor<T> {

    /**
     * Physical map of the environment.
     */
    protected EnvironmentMap environmentMap;

    /**
     * Creates array of inputs for robots.
     * @param robots List of robots in the simulation. Can be used to access their parameters. For each of this robot return
     *               list must contain an input value at the same index.
     * @param objects List of objects in the simulation. Can be used to access their parameters.
     * @return ArrayList containing inputs for each robot. Size of this list must be equal to size of the list of robots.
     * Even if some particular robot doesn't have a sensor of this type, its in
     */
    protected abstract ArrayList<T> sendInputs(ArrayList<RobotPlacement> robots, ArrayList<ObjectPlacement> objects);

    void setEnvironmentMap(EnvironmentMap environmentMap) {
        this.environmentMap = environmentMap;
    }

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified in simulation parameters XML file
     */
    protected void processArg(String arg) {

    }
}
