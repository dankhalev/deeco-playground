package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.state;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.Algorithms;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.DroneContext;
import cz.cuni.mff.d3s.deeco.playground.simulation.CollisionData;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.Geometry;

import java.util.List;

/**
 * In this state, robot tries to escape from occured collision.
 *
 * @author Danylo Khalyeyev
 */
class AvoidState extends FirefighterState {

    private List<Coordinates> trajectory;
    private int type;

    AvoidState(Coordinates coordinates, CollisionData collisionData) {
        this.type = 0;
        this.trajectory = Algorithms.constructAvoidanceTrajectory(coordinates, collisionData, type);
        name = "AVOID";
    }

    @Override
    FirefighterState makeDecision(DroneContext context) {
        if (trajectory.size() == 0) {
            return StateFactory.getInstance().getState("ROAM");
        } else {
            Coordinates point = trajectory.get(0);
            Coordinates position = context.coordinates;
            context.wheels.setAction(1,
                    Geometry.subjectiveAngleBetween(position.x, position.y, point.x, point.y, position.angle));
            if (Geometry.distance(position.x, position.y, point.x, point.y) < 4) {
                trajectory.remove(0);
            }
        }
        return this;
    }

    @Override
    FirefighterState checkEnergy(DroneContext context) {
        return null;
    }

    @Override
    FirefighterState checkCollision(DroneContext context) {
        if (Algorithms.detectFrontalCollision(context.collisionData)) {
            type++;
            this.trajectory = Algorithms.constructAvoidanceTrajectory(context.coordinates, context.collisionData, type);
            this.trajectory.remove(this.trajectory.size() - 1);
        }
        return null;
    }

}
