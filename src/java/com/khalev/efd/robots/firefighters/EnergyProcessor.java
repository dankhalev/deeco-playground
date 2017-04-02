package com.khalev.efd.robots.firefighters;


import com.khalev.efd.simulation.*;

import java.util.ArrayList;

public class EnergyProcessor extends SensoryInputsProcessor<EnergyInput> {

    ArrayList<EnergyInput> energies;
    TemperatureMap temperatureMap;
    int initialEnergy = 1500;
    int movingCost = 1;
    int coolingCost = 5;
    int knowledgeCost = 50;
    int chargingValue = 100;

    @Override
    protected ArrayList<EnergyInput> sendInputs(ArrayList<RobotPlacement> robots, ArrayList<ObjectPlacement> objects) {
        if (energies == null) {
            energies = new ArrayList<>();
            for (int i = 0; i < robots.size(); i++) {
                energies.add(new EnergyInput(initialEnergy));
            }
        }
        ArrayList<Coordinates> coolers = new ArrayList<>();
        for (int i = 0; i < robots.size(); i++) {
            RobotPlacement r = robots.get(i);
            Action action = r.currentAction;
            if (action instanceof EnergyAction) {
                if (((EnergyAction) action).isCoolerActivated) {
                    coolers.add(new Coordinates((int) r.getX(), (int) r.getY(), 0));
                    energies.get(i).energy -= coolingCost;
                }
                if (((EnergyAction) action).coordinatorDataRequest) {
                    energies.get(i).fires = temperatureMap.seeds;
                    energies.get(i).energy -= knowledgeCost;
                }
            }
        }
        temperatureMap.cycle(coolers);
        ArrayList<TemperatureData> temperatures = temperatureMap.getTemperatureData(robots);
        for (int i = 0; i < energies.size(); i++) {
            energies.get(i).data = temperatures.get(i);
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
        //Calculating high temperature damage
        for (EnergyInput energyInput : energies) {
            if (energyInput.data.maxDetectedTemperature > temperatureMap.threshold) {
                int damage = ((int)energyInput.data.maxDetectedTemperature - temperatureMap.threshold) / 10;
                energyInput.energy -= damage;
                energyInput.damage = damage;
            }
        }

        return energies;
    }

    @Override
    protected void processArg(String params) {
        if (params != null) {
            temperatureMap = new TemperatureMap(params);
        } else {
            throw new RuntimeException("TemperatureMap: output file was not specified");
        }
    }
}
