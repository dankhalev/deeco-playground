package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment;

/**
 * Temperature data that robots receive on their temperature sensors. Consists of the tamperature value that was detected
 * at the robot's location, and an angle the maximal temperature came from.
 *
 * @author Danylo Khalyeyev
 */
public class TemperatureData {

    public double temperatureVector;
    public double maxDetectedTemperature;

    TemperatureData(double maxDetectedTemperature, double temperatureVector) {
        this.temperatureVector = temperatureVector;
        this.maxDetectedTemperature = maxDetectedTemperature;
    }
}
