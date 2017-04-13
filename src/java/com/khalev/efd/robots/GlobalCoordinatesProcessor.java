package com.khalev.efd.robots;

import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.ObjectPlacement;
import com.khalev.efd.simulation.RobotPlacement;
import com.khalev.efd.simulation.SensoryInputsProcessor;

import java.util.ArrayList;
import java.util.List;

public class GlobalCoordinatesProcessor extends SensoryInputsProcessor<Coordinates> {

    @Override
    protected List<Coordinates> sendInputs(List<RobotPlacement> robots, List<ObjectPlacement> objects) {
        List<Coordinates> coords = new ArrayList<>();
        for (RobotPlacement r : robots) {
            coords.add(new Coordinates(r.getX(), r.getY(), r.getAngle()));
        }
        return coords;
    }

}
