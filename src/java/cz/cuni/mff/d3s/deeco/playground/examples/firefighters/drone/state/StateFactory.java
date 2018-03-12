package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;

import java.util.HashMap;
import java.util.Map;

/**
 * The factory that provides robots with requested states.
 *
 * @author Danylo Khalyeyev
 */
public class StateFactory {

    private Map<String, FirefighterState> stateMap;
    private static StateFactory instance;



    private StateFactory() {
        stateMap = new HashMap<>();
        stateMap.put("ROAM", new RoamState());
        stateMap.put("FIGHT", new FightState());
        stateMap.put("FOLLOW", new FollowState());
        stateMap.put("RECHARGE", new RechargeState());
    }

    public static StateFactory getInstance() {
        if (instance == null) {
            instance = new StateFactory();
        }
        return instance;
    }

    public FirefighterState getState(String name) {
        name = name.toUpperCase();
        return stateMap.get(name);
    }

    public FirefighterState getAvoidState(DroneContext context) {
        return new AvoidState(context.coordinates, context.collisionData);
    }

}
