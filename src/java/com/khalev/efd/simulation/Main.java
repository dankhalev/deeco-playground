package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;

/**
 * Main class of the program. Requires to receive a simulation properties file as its 1st parameter.
 */
public class Main {

    public static void main(String[] args) throws AnnotationProcessorException, SimulationParametersException, DEECoException {
        if (args.length < 1) {
            throw  new SimulationParametersException("Please provide a name of XML file with simulation properties");
        } else {
            Simulation simulation = new Simulation(args[0]);
            simulation.startSimulation();
        }
    }

}
