package cz.cuni.mff.d3s.deeco.playground.examples.predator;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.ObjectPlacement;
import cz.cuni.mff.d3s.deeco.playground.simulation.RobotPlacement;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensoryInputsProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides robots with information about their positions.
 *
 * @author Danylo Khalyeyev
 */
public class CoordinatesProcessor extends SensoryInputsProcessor<Coordinates> {

    @Override
    protected List<Coordinates> sendInputs(List<RobotPlacement> robots, List<ObjectPlacement> objects) {
        List<Coordinates> coords = new ArrayList<>();
        for (RobotPlacement r : robots) {
            coords.add(new Coordinates(r.getX(), r.getY(), r.getAngle()));
        }
        return coords;
    }

}
