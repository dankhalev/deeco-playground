package com.khalev.efd.robots.firefighters;

public class TemperatureData {

    double temperatureVector;
    double maxDetectedTemperature;

    public TemperatureData(double maxDetectedTemperature, double temperatureVector) {
        this.temperatureVector = temperatureVector;
        this.maxDetectedTemperature = maxDetectedTemperature;
    }
}
