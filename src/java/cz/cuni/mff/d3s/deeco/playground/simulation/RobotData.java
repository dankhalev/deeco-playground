package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Represents data that {@link Coordinator} collects from each robot through {@link RobotEnsemble} and transmits to
 * the {@link Environment}.
 *
 * @author Danylo Khalyeyev
 */
class RobotData {

    Action action;
    String tag;

    RobotData(Action action, String tag) {
        this.action = action;
        this.tag = tag;
    }

}
