package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.annotations.Component;

import java.util.HashMap;

@Component
public class Counter extends DEECoObject {

    public HashMap<Integer, Integer> team1 = new HashMap<>();
    public HashMap<Integer, Integer> team2 = new HashMap<>();

    @Override
    protected void setParameters(Double x, Double y, String tag, Double size) {
        super.setParameters(0.0, 0.0, tag, 0.0);
    }
}
