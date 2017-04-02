package com.khalev.efd.robots.basic;

import com.khalev.efd.simulation.DEECoRobot;
import com.khalev.efd.simulation.Wheels;
import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

@Component
public class DumbRobot extends DEECoRobot {

    public DumbRobot() {
        wheels = new SimpleWheels();
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decisionProcess(
            @InOut("wheels") ParamHolder<Wheels> wheels
    ) {
        wheels.value.setAction(1,0);
    }
}
