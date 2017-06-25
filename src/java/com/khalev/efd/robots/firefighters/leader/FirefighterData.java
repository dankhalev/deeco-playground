package com.khalev.efd.robots.firefighters.leader;

import com.khalev.efd.robots.firefighters.environment.EnergyTemperatureInput;
import com.khalev.efd.simulation.CollisionData;
import com.khalev.efd.simulation.Coordinates;

/**
 * Encapsulates all the data on sensors of a single robot, so that these data could be easily transferred to the leader.
 *
 * @author Danylo Khalyeyev
 */
class FirefighterData {

    Coordinates position;
    EnergyTemperatureInput energyTemperatureInput;
    CollisionData collisionData;

    FirefighterData(Coordinates position, EnergyTemperatureInput energyTemperatureInput, CollisionData collisionData) {
        this.position = position;
        this.energyTemperatureInput = energyTemperatureInput;
        this.collisionData = collisionData;
    }
}
