package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone;

import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment.EnergyTemperatureInput;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.wheels.FirefighterWheels;
import cz.cuni.mff.d3s.deeco.annotations.*;
import cz.cuni.mff.d3s.deeco.annotations.Process;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.ControlledMode;
import cz.cuni.mff.d3s.deeco.playground.examples.firefighters.drone.mode.FirefighterMode;
import cz.cuni.mff.d3s.deeco.playground.simulation.*;
import cz.cuni.mff.d3s.deeco.task.ParamHolder;

/**
 * Basic Firefighter Drone. Contains a single process in which it makes decision about its next movement. Has two modes
 * in which it can function: autonomous and controlled. In autonomous mode, it decides on its own, based on data on its
 * sensors. In the controlled one, it executes orders received from the leader of firefighter team.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class FirefighterDrone extends DEECoRobot {

    public Coordinates powerStation = null;
    public FirefighterMode mode = new ControlledMode();
    static Integer nextID = 0;
    public Integer firefighterID = nextID++;

    public FirefighterDrone() {
        wheels = new FirefighterWheels();
        sensor.registerSensor("coords");
        sensor.registerSensor("energy");
    }

    @Process
    @PeriodicScheduling(period = 1)
    public static void decision(
            @InOut("wheels") ParamHolder<Wheels> wheels,
            @In("sensor") SensorySystem sensor,
            @InOut("powerStation") ParamHolder<Coordinates> charger,
            @InOut("tag") ParamHolder<String> tag,
            @InOut("mode") ParamHolder<FirefighterMode> mode
    ) {
        //Getting sensory data
        CollisionData collisionData = sensor.getInputFromSensor("collisions", CollisionData.class);
        Coordinates coordinates = sensor.getInputFromSensor("coords", Coordinates.class);
        EnergyTemperatureInput energy = sensor.getInputFromSensor("energy", EnergyTemperatureInput.class);
        if (energy == null || coordinates == null || collisionData == null) {
            return;
        }
        //Preparing wheels
        FirefighterWheels pw = (FirefighterWheels) wheels.value;
        pw.provideEnergy(sensor);

        //Decision-making
        DroneContext context = new DroneContext(energy, coordinates, collisionData, pw, charger.value);
        tag.value = mode.value.execute(context);
        if (energy.energy <= 0) {
            tag.value = "BROKEN";
        }
    }
}
