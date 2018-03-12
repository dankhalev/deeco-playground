package cz.cuni.mff.d3s.deeco.playground.examples.competition;

import cz.cuni.mff.d3s.deeco.playground.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.annotations.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Invisible object, that counts the score of the game (updated by {@link ScoreUpdate}).
 *
 * @author Danylo Khalyeyev
 */
@Component
public class Counter extends DEECoObject {

    public Map<Integer, Integer> team1 = new HashMap<>();
    public Map<Integer, Integer> team2 = new HashMap<>();

    @Override
    protected void setParameters(Double x, Double y, String tag, Double size) {
        super.setParameters(0.0, 0.0, tag, 0.0);
    }
}
