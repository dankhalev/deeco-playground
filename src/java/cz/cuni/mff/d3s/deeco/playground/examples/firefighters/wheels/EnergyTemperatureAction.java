package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.wheels;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureProcessor;
import cz.cuni.mff.d3s.deeco.playground.simulation.Action;

/**
 * Extension of {@link Action} that gives robot an ability to act upon {@link EnergyTemperatureProcessor} layer of the
 * environment. Allows to turn on fire extinguisher to lower temperatures around the robot. Also, allows to request data
 * about the positions of fires on the map (warning, this consumes a lot of energy).
 *
 * @author Danylo Khalyeyev
 */
public class EnergyTemperatureAction extends Action {

    public boolean isExtinguisherActivated = false;
    public boolean extendedDataRequest = false;

    public EnergyTemperatureAction(double velocity, double rotation) {
        super(velocity, rotation);
    }
}
