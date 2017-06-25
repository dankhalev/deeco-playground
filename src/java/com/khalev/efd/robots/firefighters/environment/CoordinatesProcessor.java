package com.khalev.efd.robots.firefighters.environment;

import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.ObjectPlacement;
import com.khalev.efd.simulation.RobotPlacement;
import com.khalev.efd.simulation.SensoryInputsProcessor;

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
        List<Coordinates> coords = new ArrayList<>();
        for (RobotPlacement r : robots) {
            coords.add(new Coordinates(r.getX(), r.getY(), r.getAngle()));
        }
        return coords;
    }
}
