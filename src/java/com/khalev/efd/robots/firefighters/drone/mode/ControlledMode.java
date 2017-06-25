package com.khalev.efd.robots.firefighters.drone.mode;

import com.khalev.efd.robots.firefighters.Algorithms;
import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.robots.firefighters.leader.FirefighterLeader;
import com.khalev.efd.robots.firefighters.leader.FirefighterOrder;
import com.khalev.efd.robots.firefighters.drone.state.FirefighterState;
import com.khalev.efd.robots.firefighters.drone.state.StateFactory;
import com.khalev.efd.simulation.Coordinates;
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
