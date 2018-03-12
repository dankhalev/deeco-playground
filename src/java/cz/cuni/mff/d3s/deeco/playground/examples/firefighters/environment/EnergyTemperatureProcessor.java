package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment;


import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.wheels.EnergyTemperatureAction;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer of environment that manages energy and temperature data.
 *
 * @author Danylo Khalyeyev
 */
public class EnergyTemperatureProcessor extends SensoryInputsProcessor<EnergyTemperatureInput> {

    private List<EnergyTemperatureInput> energies;
    private TemperatureMap temperatureMap;
    private final int maxEnergy = 1500;
    private final int movingCost = 1;
    private final int coolingCost = 5;
    private final int knowledgeCost = 50;
    private final int chargingValue = 100;

    @Override
    protected List<EnergyTemperatureInput> sendInputs(List<RobotPlacement> robots, List<ObjectPlacement> objects) {
        //Creating list of energies for all robots, initializing each with maximal energy
        if (energies == null) {
            energies = new ArrayList<>();
            for (int i = 0; i < robots.size(); i++) {
                energies.add(new EnergyTemperatureInput(maxEnergy));
            }
        }

        //Interpreting robots' actions. If a robot has its fire extinguisher activated, add it to the list of extinguishers
        //that will be provided to temperature map. Also, reduce energy for those robots that activated their extinguishers
        //and for those that request coordinator data.
        List<Coordinates> extinguishers = new ArrayList<>();
        for (int i = 0; i < robots.size(); i++) {
            RobotPlacement r = robots.get(i);
            Action action = r.currentAction;
            if (action instanceof EnergyTemperatureAction) {
                if (((EnergyTemperatureAction) action).isExtinguisherActivated) {
                    extinguishers.add(new Coordinates((int) r.getX(), (int) r.getY(), 0));
                    energies.get(i).energy -= coolingCost;
                }
                if (((EnergyTemperatureAction) action).extendedDataRequest) {
                    energies.get(i).fires = temperatureMap.getFireCoordinates();
                    energies.get(i).energy -= knowledgeCost;
                }
            }
        }

        //Compute temperature map for the next cycle with respect to extinguishers activated on the map.
        temperatureMap.computeNextCycle(extinguishers);
        //Get temperature inputs for each robot.
        List<TemperatureData> temperatures = temperatureMap.getTemperatureData(robots);
        for (int i = 0; i < energies.size(); i++) {
            energies.get(i).data = temperatures.get(i);
        }

        //Subtract moving costs from energy of those robots that move in this cycle.
        for (int i = 0; i < robots.size(); i++) {
            Action action = robots.get(i).currentAction;
            if (action.type != Action.Type.STAY) {
                energies.get(i).energy -= movingCost;
            }
        }
        //Charge robots that are located near recharge station
        for (ObjectPlacement object : objects) {
            if (object.getTag().equals("Charging Station")) {
                for (int j = 0; j < robots.size(); j++) {
                    RobotPlacement robot = robots.get(j);
                    if (Geometry.distance(object.getX(), object.getY(), robot.getX(), robot.getY()) < Math.pow(object.getSize(), 2)) {
                        energies.get(j).energy = energies.get(j).energy < maxEnergy - chargingValue ?
                                energies.get(j).energy + chargingValue : maxEnergy;
                    }
                }
            }
        }
        //Subtract damage points from the energies of the robots that are affected by high temperatures
        for (EnergyTemperatureInput energyTemperatureInput : energies) {
            if (energyTemperatureInput.data.maxDetectedTemperature > temperatureMap.getThreshold()) {
                int damage = ((int) energyTemperatureInput.data.maxDetectedTemperature - temperatureMap.getThreshold()) / 10;
                energyTemperatureInput.energy -= damage;
                energyTemperatureInput.damage = damage;
            }
        }

        return energies;
    }

    @Override
    protected void processArg(String arg) {
        if (arg != null) {
            temperatureMap = new TemperatureMap(arg, environmentMap.getSizeX(), environmentMap.getSizeY());
        } else {
            throw new RuntimeException("TemperatureMap: output file was not specified");
        }
    }
}
