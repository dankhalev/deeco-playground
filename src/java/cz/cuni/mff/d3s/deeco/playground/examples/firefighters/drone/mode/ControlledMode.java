package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.Algorithms;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader.FirefighterLeader;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader.FirefighterOrder;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state.FirefighterState;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state.StateFactory;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;

import java.util.Objects;

/**
 * In this operating mode firefighter executes {@link FirefighterOrder}s issued by {@link FirefighterLeader}. Yet, if it
 * encounters frontal collision, it resolves it by itself, because leader cannot help robot in this case.
 *
 * @author Danylo Khalyeyev
 */
public class ControlledMode extends FirefighterMode {

    FirefighterOrder order;
    FirefighterState state = null;
    Coordinates destination = null;

    @Override
    public String execute(DroneContext context) {
        if (order == null) {
            return "ROAM";
        }
        if (Algorithms.detectFrontalCollision(context.collisionData)) {
            destination = order.getDestination();
            state = StateFactory.getInstance().getAvoidState(context);
        }
        if (!Objects.equals(destination, order.getDestination())) {
            state = null;
        }
        if (state != null) {
            FirefighterState newState = state.pickAction(context);
            if (!Objects.equals(state, newState)) {
                state = null;
            } else {
                return state.getName();
            }
        }

        return order.execute(context);
    }

    @Override
    public void receiveOrder(FirefighterOrder order) {
        this.order = order;
    }
}
