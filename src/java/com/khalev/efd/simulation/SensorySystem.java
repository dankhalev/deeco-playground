package com.khalev.efd.simulation;

import java.util.HashMap;

/**
 * Provides access to all the sensors that are present on the robot. Initially contains only collision sensor, additional
 * sensors should be registered in robot's code. Each sensor is identified by its name and type of its input.
 */
public class SensorySystem {

    private HashMap<String, Sensor> sensors = new HashMap<>();

    void receiveInput(String name, Object input) {
        Sensor sensor = sensors.get(name);
        if (sensor != null) {
            sensor.receiveInput(input);
        }
    }

    /**
     * Accesses a specified sensor and returns its input value
     * @param name name of the sensor
     * @param cls expected class of value on input of this sensor
     * @return if the sensor with this name exists and if a type of its input value is cls than value on input, else null
     */
    public <T> T getInputFromSensor(String name, Class<T> cls) {
        Sensor sensor = sensors.get(name);
        if (sensor == null) {
            return null;
        }
        Object o = sensor.getInput();
        if (o != null && o.getClass().isAssignableFrom(cls)) {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            return t;
        } else {
            return null;
        }
    }

    /**
     * Creates a new sensor on robot. If a sensor with specified name already exists, replaces it.
     * @param name Name of added sensor.
     * @param cls Type of input value of the sensor. If null, sensor with that name will be removed (if present)
     */
    public <T> void registerSensor(String name, Class<T> cls) {
        if (cls != null) {
            sensors.put(name, new Sensor<>(cls));
        } else {
            sensors.remove(name);
        }
    }

}


