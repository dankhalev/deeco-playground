package cz.cuni.mff.d3s.deeco.playground.visualization;

/**
 * Any exception that is caused by inconsistency of provided configuration file or simulation logs file.
 *
 * @author Danylo Khalyeyev
 */
class VisualizationParametersException extends Exception {

    VisualizationParametersException(String s) {
        super(s);
    }

    VisualizationParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    VisualizationParametersException(Throwable cause) {
        super(cause);
    }
}
