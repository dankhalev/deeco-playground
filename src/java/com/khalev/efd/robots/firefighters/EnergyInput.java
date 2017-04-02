package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;

import java.util.ArrayList;

public class EnergyInput {
    public int energy;
    public int damage = 0;
    public TemperatureData data;
    public ArrayList<Coordinates> fires = new ArrayList<>();

    public EnergyInput(int energy, TemperatureData data) {
        this.energy = energy;
        this.data = data;
    }

    public EnergyInput(int energy) {
        this.energy = energy;
    }
}
