package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;

/**
 * Order to stay without moving.
 *
 * @author Danylo Khalyeyev
 */
class StayOrder extends FirefighterOrder {

    @Override
    public String execute(DroneContext context) {
        context.wheels.setAction(0,0);
        return "ROAM";
    }

}
