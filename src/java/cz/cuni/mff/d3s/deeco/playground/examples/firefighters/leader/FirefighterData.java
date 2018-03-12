package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.leader;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureInput;
import cz.cuni.mff.d3s.deeco.playground.simulation.CollisionData;
import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;

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
