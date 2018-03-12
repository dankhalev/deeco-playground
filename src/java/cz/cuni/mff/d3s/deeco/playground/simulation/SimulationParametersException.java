package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Any exception that is caused by inconsistency of simulation's initial parameters provided in a scenario file.
 *
 * @author Danylo Khalyeyev
 */
class SimulationParametersException extends Exception {

    SimulationParametersException(String s) {
        super(s);
    }

    SimulationParametersException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
