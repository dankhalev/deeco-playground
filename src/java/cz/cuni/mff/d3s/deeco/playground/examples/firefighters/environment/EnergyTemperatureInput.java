package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import java.util.ArrayList;
import java.util.List;

/**
 * Input that robots receive from {@link EnergyTemperatureProcessor} environment layer. Contains information about an
 * amount of energy that robot has; a damage it receives from high temperatures at the moment, and a {@link TemperatureData}
 * at its location. If extended data were requested, contains also a list of coordinates of all fires on the field.
 *
 * @author Danylo Khalyeyev
 */
public class EnergyTemperatureInput {
    public int energy;
    public int damage = 0;
    public TemperatureData data;
    public List<Coordinates> fires = new ArrayList<>();

    public EnergyTemperatureInput(int energy, TemperatureData data) {
        this.energy = energy;
        this.data = data;
    }

    public EnergyTemperatureInput(int energy) {
        this.energy = energy;
    }
}
