package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages robots' energies.
 *
 * @author Danylo Khalyeyev
 */
public class EnergyProcessor extends SensoryInputsProcessor<EnergyInput> {

    List<EnergyInput> energies;
    int initialEnergy = 300;
    int movingCost = 1;
    int chargingValue = 30;

    @Override
    protected List<EnergyInput> sendInputs(List<RobotPlacement> robots, List<ObjectPlacement> objects) {
        if (energies == null) {
            energies = new ArrayList<>();
            for (int i = 0; i < robots.size(); i++) {
                energies.add(new EnergyInput(initialEnergy));
            }
        }
        //Calculating moving costs
        for (int i = 0; i < robots.size(); i++) {
            Action action = robots.get(i).currentAction;
            if (action.type != Action.Type.STAY) {
                energies.get(i).energy -= movingCost;
            }
        }
        //Charging robots
        for (ObjectPlacement object : objects) {
            if (object.getTag().equals("Power Station")) {
                for (int j = 0; j < robots.size(); j++) {
                    RobotPlacement robot = robots.get(j);
                    if (Geometry.distance(object.getX(), object.getY(), robot.getX(), robot.getY()) < Math.pow(object.getSize(), 2)) {
                        energies.get(j).energy = energies.get(j).energy < initialEnergy - chargingValue ?
                                energies.get(j).energy + chargingValue : initialEnergy;
                    }
                }
            }
        }

        return energies;
    }
}
