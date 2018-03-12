package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.FirefighterDrone;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;

/**
 * Represents an order issued by {@link FirefighterLeader} to {@link FirefighterDrone}. Can be executed automatically on
 * provided {@link DroneContext}.
 *
 * @author Danylo Khalyeyev
 */
public abstract class FirefighterOrder {

    public abstract String execute(DroneContext context);

    public Coordinates getDestination() {
        return null;
    }
}
