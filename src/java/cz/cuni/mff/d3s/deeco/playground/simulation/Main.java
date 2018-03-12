package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;

/**
 * This class contains a main() method that runs a simulation. It requires to receive at least one scenario file as its
 * parameter.
 *
 * @author Danylo Khalyeyev
 */
public class Main {

    public static void main(String[] args) throws AnnotationProcessorException, SimulationParametersException, DEECoException {
        if (args.length < 1) {
            System.out.println("You need to provide a path to at least one scenario file to run a simulation");
        } else {
            int numSuccessful = 0;
            try {
                for (String arg : args) {
                    Simulation simulation = new Simulation(arg);
                    simulation.startSimulation();
                    numSuccessful++;
                }
            } finally {
                if (args.length == 1 && numSuccessful == 1) {
                    System.out.println("The scenario was simulated successfully.");
                } else if (args.length == numSuccessful) {
                    System.out.println(numSuccessful + " scenarios were simulated successfully.");
                } else if (numSuccessful == 0) {
                    System.out.println("Simulation of the scenario failed.");
                } else if (numSuccessful == 1) {
                    System.out.println("The first scenario was simulated successfully. Simulation of the second " +
                            "scenario failed.");
                } else if (numSuccessful > 1) {
                    System.out.println(numSuccessful + " scenarios were simulated successfully. Simulation of the " +
                            "scenario #" + (numSuccessful+1) + " failed.");
                }
            }
        }
    }

}
