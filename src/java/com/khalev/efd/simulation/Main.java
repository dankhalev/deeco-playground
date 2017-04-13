package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;

/**
 * This class contains a main() method that runs a simulation. It requires to receive a scenario file as its 1st parameter.
 *
 * @author Danylo Khalyeyev
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
