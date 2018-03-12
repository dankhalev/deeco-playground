package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.Coordinates;
import cz.cuni.mff.d3s.deeco.playground.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.InOut;
import cz.cuni.mff.d3s.deeco.annotations.PeriodicScheduling;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Items that robots have to collect to receive points.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class CollectibleItem extends DEECoObject {

    public Boolean found = false;
    public Boolean waiting = false;
    public Integer timeToRespawn = (int)(Math.random() * 40) + 20;
    static Integer nextID = 0;
    public Integer itemID = nextID++;
    public Integer collectedByTeam1 = 0;
    public Integer collectedByTeam2 = 0;

    @Override
    protected void setParameters(Double x, Double y, String tag, Double size) {
        super.setParameters(0.0, 0.0, tag, 0.0);
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void respawn(
            @InOut("found") ParamHolder<Boolean> found,
            @InOut("waiting") ParamHolder<Boolean> waiting,
            @InOut("position") ParamHolder<Coordinates> position,
            @InOut("size") ParamHolder<Double> size,
            @InOut("timeToRespawn") ParamHolder<Integer> timeToRespawn
    ) {
        if (found.value) {
            size.value = 0.0;
            if (!waiting.value) {
                timeToRespawn.value = (int) (Math.random() * 40) + 20;
                waiting.value = true;
            }
        }
        --timeToRespawn.value;
        if (size.value == 0 && timeToRespawn.value <= 0) {
            waiting.value = false;
            found.value = false;
            double x = Math.random() * 80 + 10;
            double y = Math.random() * 80 + 10;
            position.value = new Coordinates(x, y, 0);
            size.value = 1.0;
        }
    }
}
