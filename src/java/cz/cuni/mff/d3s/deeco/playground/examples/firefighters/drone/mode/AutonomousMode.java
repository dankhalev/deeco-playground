package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state.FirefighterState;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state.StateFactory;

import java.util.Objects;

/**
 * In this operating mode firefighter acts autonomously, based only on its sensory data, without coordination with other
 * firefighters. Its operation in this mode is given by its current {@link FirefighterState}.
 *
 * @author Danylo Khalyeyev
 */
public class AutonomousMode extends FirefighterMode {

    private FirefighterState state = StateFactory.getInstance().getState("ROAM");

    @Override
    public String execute(DroneContext context) {
        FirefighterState newState = state.pickAction(context);
        while (!Objects.equals(state, newState)) {
            state = newState;
            newState = state.pickAction(context);
        }
        return state.getName();
    }
}

