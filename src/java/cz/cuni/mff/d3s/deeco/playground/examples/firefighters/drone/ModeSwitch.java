package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.AutonomousMode;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.ControlledMode;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.FirefighterMode;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureInput;
import cz.cuni.mff.d3s.deeco.playground.simulation.SensorySystem;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;
import java.util.Map;

/**
 * Ensemble that regulates in which mode firefighters operate. If for some reason, the leader of the team does not issue
 * orders anymore (e.g. in does not have enough energy or it is broken, or there is no leader in the simulation at all),
 * other firefighters switch to autonomous mode. If the leader appears again, robots return to controlled mode.
 *
 * @author Danylo Khalyeyev
 */
@Ensemble
@PeriodicScheduling(period = 1)
public class ModeSwitch {

    @Membership
    public static boolean membership(
            @In("coord.teamOrders") Map teamOrders,
            @In("coord.teamPlacements") Map teamPlacements,
            @In("member.mode") FirefighterMode mode
    ) {
        return true;
    }

    @KnowledgeExchange
    public static void map(
            @In("coord.sensor") SensorySystem sensor,
            @InOut("member.mode") ParamHolder<FirefighterMode> mode,
            @In("coord.dataAvailable") Boolean dataAvailable

    ) {
        EnergyTemperatureInput energy = sensor.getInputFromSensor("energy", EnergyTemperatureInput.class);
        if (!dataAvailable && mode.value instanceof ControlledMode) {
            mode.value = new AutonomousMode();
        }
        if (dataAvailable && mode.value instanceof AutonomousMode) {
            mode.value = new ControlledMode();
        }

    }
}
