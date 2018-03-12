package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.ObjectPlacement;
import cz.cuni.mff.d3s.deeco.playground.simulation.RobotPlacement;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensoryInputsProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * This sensor informs robots about their current positions.
 *
 * @author Danylo Khalyeyev
 */
public class CoordinatesProcessor
        extends SensoryInputsProcessor<Coordinates> {

    @Override
    protected List<Coordinates> sendInputs(
            List<RobotPlacement> robots,
            List<ObjectPlacement> objects
    ) {
        List<Coordinates> coordinates = new ArrayList<>();
        for (RobotPlacement r : robots) {
            coordinates.add(new Coordinates(r.getX(),
                    r.getY(), r.getAngle()));
        }
        return coordinates;
    }
}
