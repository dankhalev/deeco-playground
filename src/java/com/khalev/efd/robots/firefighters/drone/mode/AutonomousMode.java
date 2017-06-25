package com.khalev.efd.robots.firefighters.drone.mode;

import com.khalev.efd.robots.firefighters.drone.DroneContext;
import com.khalev.efd.robots.firefighters.drone.state.FirefighterState;
import com.khalev.efd.robots.firefighters.drone.state.StateFactory;

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

