package cz.cuni.mff.d3s.deeco.playground.simulation;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides access to all the sensors that are present on the robot. Each sensor is identified by its name. Initially
 * contains only collision sensor with a name "collisions", additional sensors should be registered in robot's code.
 *
 * @author Danylo Khalyeyev
 */
public final class SensorySystem {

    private Set<String> sensors = new HashSet<>();
    private int rID;

    SensorySystem(int rID) {
        this.rID = rID;
    }

    /**
     * Accesses a specified sensor and returns its input value. If there is no sensor type with this name in simulation
     * or if the sensor of this type is not registered or if a value on its input does not have an expected type, returns
     * null.
     * @param name name of the sensor
     * @param cls expected class of value on input of this sensor
     * @param <T> type of return value
     * @return if the sensor with this name exists and if a type of its input value is cls than value on input, else null
     */
    public <T> T getInputFromSensor(String name, Class<T> cls) {
        try {
            if (sensors.contains(name)) {
                return Environment.getInstance().getInputFromSensor(rID, name, cls);
            }
        } catch (Exception e) {
            Environment.getInstance().exitWithException(e);
        }
        return null;
    }

    /**
     * Adds a new sensor to robot.
     * @param name name of added sensor.
     */
    public void registerSensor(String name) {
            sensors.add(name);
    }

    /**
     * Removes a sensor from robot.
     * @param name name of sensor to be removed.
     */
    public void unregisterSensor(String name) {
        sensors.remove(name);
    }

}

