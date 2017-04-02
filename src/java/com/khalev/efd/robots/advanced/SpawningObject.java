package com.khalev.efd.robots.advanced;

import com.khalev.efd.simulation.Coordinates;
import com.khalev.efd.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

import java.util.ArrayList;
import java.util.Collections;

@Component
public class SpawningObject extends DEECoObject {

    public ArrayList<Double> array = new ArrayList<>();

    public SpawningObject() {
        super();
        Double[] arr = new Double[] {
                74.95807262755191,
                47.697154802747875,
                69.50845140120856,
                67.15694036103737,
                87.34132446768281,
                47.49241468673472,
                74.95807262755191,
                47.697154802747875,
                69.50845140120856,
                67.15694036103737,
                74.95807262755191,
                47.697154802747875,
                69.50845140120856,
                67.15694036103737
        };
        Collections.addAll(array, arr);
    }

    public Boolean found = false;
    public Integer spawns = 0;

    @Process
    @PeriodicScheduling(period = 1)
    public static void respawn(
            @InOut("found") ParamHolder<Boolean> found,
            @InOut("spawns") ParamHolder<Integer> spawns,
            @InOut("position") ParamHolder<Coordinates> position,
            @InOut("array") ParamHolder<ArrayList<Double>> array
    ) {
        if (found.value) {
            //double x = Math.random() * 100;
            //double y = Math.random() * 100;
            double x = array.value.remove(0);
            double y = array.value.remove(0);
            position.value = new Coordinates(x, y, 0);
            found.value = false;
            spawns.value++;
        }
    }
}
