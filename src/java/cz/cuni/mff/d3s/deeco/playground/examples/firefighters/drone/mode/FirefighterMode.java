package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader.FirefighterOrder;

/**
 * Represents a firefighter robot's operating mode. Can be executed on provided {@link DroneContext}.
 *
 * @author Danylo Khalyeyev
 */
public abstract class FirefighterMode {

    private FirefighterOrder order;

    public abstract String execute(DroneContext context);

    public void receiveOrder(FirefighterOrder order) {
        this.order = order;
    }

}
