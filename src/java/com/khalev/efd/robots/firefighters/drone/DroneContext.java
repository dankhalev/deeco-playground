package com.khalev.efd.robots.firefighters.drone;

import com.khalev.efd.robots.firefighters.environment.EnergyTemperatureInput;
import com.khalev.efd.robots.firefighters.wheels.FirefighterWheels;
import com.khalev.efd.simulation.CollisionData;
import com.khalev.efd.simulation.Coordinates;

/**
 * Combines firefighter's sensory data, coordinates of power station, and a link to robot's wheels, thus representing a
 * context in which a robot operates at the moment
 *
 * @author Danylo Khalyeyev
 */
public class DroneContext {

    public EnergyTemperatureInput energyTemperatureInput;
    public Coordinates coordinates;
    public CollisionData collisionData;
    public FirefighterWheels wheels;
    public Coordinates charger;

    public DroneContext(EnergyTemperatureInput energyTemperatureInput, Coordinates coordinates, CollisionData collisionData, FirefighterWheels wheels, Coordinates charger) {
        this.energyTemperatureInput = energyTemperatureInput;
        this.coordinates = coordinates;
        this.collisionData = collisionData;
        this.wheels = wheels;
        this.charger = charger;
    }

}
