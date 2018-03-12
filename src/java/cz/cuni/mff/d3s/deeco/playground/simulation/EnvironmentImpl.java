package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * {@inheritDoc}
 *
 * @author Danylo Khalyeyev
 */
class EnvironmentImpl extends Environment {


    private final int CYCLES;
    private EnvironmentMap environmentMap;
    private SimulationEngine simulationEngine;
    private List<RobotPlacement> robots;
    private List<RobotPlacement> previousPositions;
    private List<Action> actions = new ArrayList<>();
    private List<ObjectPlacement> objects = new ArrayList<>();
    private Map<String, List> inputs = new HashMap<>();
    private List<SensoryInputsProcessor> sensors;
    private List<String> sensorNames;
    private int cycle;
    private FileWriter logfile;
    private long startTime;
    private Logger logger;
    private String status = "";
    private boolean endSignal = false;
    private RuntimeFramework runtimeFramework;

    /**
     * Creates an Environment with specified parameters.
     * @param numCycles number of cycles to simulate
     * @param robots list of initial robot placements
     * @param logs a file where simulation logs will be written
     * @param map a map of physical obstacles in the environment
     * @param booleanMap a boolean representation of a map of physical obstacles in the environment
     * @param sensors list of {@link SensoryInputsProcessor}s
     * @param objects list of initial object placements
     * @param sensorNames list of sensor names (in the same order as in list of SIPs)
     * @param node a DEECoNode on which the simulation runs
     */
    EnvironmentImpl(int numCycles, List<RobotPlacement> robots, File logs, EnvironmentMap map, boolean[][] booleanMap,
                    List<SensoryInputsProcessor> sensors, List<ObjectPlacement> objects,
                    List<String> sensorNames, DEECoNode node) {
        try {
            CYCLES = numCycles;
            this.environmentMap = map;
            this.robots = robots;
            this.objects = objects;
            this.runtimeFramework = node.getRuntimeFramework();

            this.simulationEngine = new SimulationEngine(robots, map);
            this.sensorNames = new ArrayList<>();
            this.sensorNames.add(collisionSensorName);
            this.sensorNames.addAll(sensorNames);
            this.sensors = new ArrayList<>();
            this.sensors.add(this.simulationEngine);
            this.sensors.addAll(sensors);

            logs.getParentFile().mkdirs();
            this.logfile = new FileWriter(logs);
            writeHeader(booleanMap);
            configureLogger();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Logger getLogger() {
        return logger;
    }

    void updateRobots(RobotData[] data) {
        actions.clear();
        assert data.length == robots.size(): "Received RobotData[] array has wrong size";
        for (int i = 0; i < data.length; i++) {
            actions.add(data[i].action);
            robots.get(i).tag = data[i].tag;
            robots.get(i).currentAction = data[i].action;
        }
    }

    void updateObjects(ObjectData[] data) {
        assert data.length == objects.size(): "Received ObjectData[] array has wrong size";
        for (int i = 0; i < data.length; i++) {
            ObjectPlacement objectPlacement = objects.get(i);
            objectPlacement.x = data[i].coordinates.x;
            objectPlacement.y = data[i].coordinates.y;
            objectPlacement.size = data[i].size;
            objectPlacement.tag = data[i].tag;
        }
    }

    void updateStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }

    int computeNextCycleAndWriteLogs() {
        if (cycle == 0) {
            startTime = System.nanoTime();
        }
        try {
            if (cycle <= CYCLES) {
                writeSimulationLogs();
                logger.info("CYCLE " + cycle);
                //Compute new positions of robots
                previousPositions = robots;
                robots = simulationEngine.performActions(actions);
                //Go through a list of sensors and generate inputs for them
                for (int i = 0; i < sensors.size(); i++) {
                    SensoryInputsProcessor sip = sensors.get(i);
                    //SIP receives unmodifiable view on the list of robots and objects
                    List<RobotPlacement> robotPlacements = Collections.unmodifiableList(robots);
                    List<ObjectPlacement> objectPlacements = Collections.unmodifiableList(objects);
                    @SuppressWarnings("unchecked")
                    List list = sip.sendInputs(robotPlacements, objectPlacements);
                    //Each SIP has to generate an input for every robot, otherwise we have to throw exception
                    if (list != null && list.size() == robots.size()) {
                        inputs.put(sensorNames.get(i), list);
                    } else {
                        throw new RuntimeException("Number of inputs received from " + sip.getClass().getSimpleName() +
                                " does not equal to the number of robots");
                    }
                }
                if (cycle > 0) {
                    writeRobotLogs();
                }
                cycle++;
            }

            if (endCondition()) {
                writeTimerLogs();
                logfile.close();
                if (endSignal) {
                    runtimeFramework.getScheduler().setExecutor(new DoNothingExecutor());
                }
                return 1;
            }

            return 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    <T> T getInputFromSensor(int rID, String name, Class<T> cls) {
        List sensor = inputs.get(name);
        if (sensor == null) {
            return null;
        }
        Object o = sensor.get(rID);
        if (o != null && o.getClass().isAssignableFrom(cls)) {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            return t;
        } else {
            return null;
        }
    }

    void stopSimulation() {
        endSignal = true;
    }

    void exitWithException(Exception e) {
        this.getLogger().log(Level.SEVERE, e.getMessage(), e);
        try {
            logfile.close();
        } catch (IOException e1) {
            this.getLogger().log(Level.SEVERE, e1.getMessage(), e1);
        }
        System.exit(-1);
    }

    private void configureLogger() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        LogManager.getLogManager().reset();
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                String s = record.getMessage() + System.getProperty("line.separator");
                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    return sw.toString();
                }
                return s;
            }
        };

        Handler ch = new ConsoleHandler();
        ch.setFormatter(formatter);
        ch.setLevel(Level.INFO);
        logger.addHandler(ch);
        LogManager lm = LogManager.getLogManager();
        lm.addLogger(logger);
        logger.setLevel(Level.INFO);
    }

    private void writeTimerLogs() {
        long endTime = System.nanoTime();
        logger.info("Time elapsed: " + ((endTime - startTime) / 1000000) + " ms");
        if (this.cycle != 0) {
            logger.info("Approximately " + ((endTime - startTime) / this.cycle / 1000000) + " ms per cycle");
        }
    }

    private void writeRobotLogs() {
        StringBuilder s = new StringBuilder("\n");
        for (int i = 0; i < previousPositions.size(); i++) {
            RobotPlacement r = robots.get(i);
            RobotPlacement rp = previousPositions.get(i);
            Action act = actions.get(i);
            CollisionData in = (CollisionData) inputs.get("collisions").get(i);

            s.append("Robot #").append(rp.id).append(" had coordinates ").append(rp.x).append(", ").append(rp.y).append(";\n");
            if (act.type == Action.Type.MOVE) {
                s.append("Robot #").append(r.id).append(": MOVE, ").append((act.degreeOfRealization) * 100).append("%\n");
            } else if (act.type == Action.Type.ROTATE) {
                s.append("Robot #").append(r.id).append(": ROTATE, ").append(Math.toDegrees(act.angle)).append(" degrees\n");
            } else if(act.type == Action.Type.ROTATE_AND_MOVE) {
                s.append("Robot #").append(r.id).append(": ROTATE&MOVE, ").append(Math.toDegrees(act.angle)).append(" degrees, ").append((act.degreeOfRealization) * 100).append("%\n");
            } else {
                s.append("Robot #").append(r.id).append(": STAY\n");
            }
            if (in.collisionPoints.size() == 0) {
                s.append("Robot #").append(r.id).append(": no collisions\n");
            } else {
                s.append("Robot #").append(r.id).append(": COLLISIONS AT ");
                for (double point : in.collisionPoints) {
                    s.append(String.format("%.2f", Math.toDegrees(point)));
                    s.append("; ");
                }
                s.append("\n");
            }
            s.append("Robot #").append(r.id).append(" has final coordinates ").append(r.x).append(", ").append(r.y).append(";\n");
        }
        logger.finer(s.toString());
    }

    /**
     * Writes simulation logs for current cycle
     * @throws IOException if IOException occurred during writing to the logfile
     */
    private void writeSimulationLogs() throws IOException {
        logfile.write(prefixString(status));
        logfile.write("&&");
        for (RobotPlacement robot : robots) {
            logfile.write(robot.x.toString());
            logfile.write(",,");
            logfile.write(robot.y.toString());
            logfile.write(",,");
            logfile.write(robot.angle.toString());
            logfile.write(",,");
            logfile.write(prefixString(robot.tag));
            logfile.write(";;");
        }
        logfile.write("&&");
        for (ObjectPlacement object : objects) {
            logfile.write(object.x.toString());
            logfile.write(",,");
            logfile.write(object.y.toString());
            logfile.write(",,");
            logfile.write(object.size.toString());
            logfile.write(",,");
            logfile.write(prefixString(object.tag));
            logfile.write(";;");
        }
        logfile.write("\n");
    }

    /**
     * Prefixes special symbols with '\'. There are three special symbols in the logfile: ',' ';' '&amp;'. If a one of those
     * symbols appears in a tag or status string, it gets prefixed with '\'.
     * @param s string to be prefixed
     * @return a prefixed string
     */
    private String prefixString(String s) {
        if (s == null) {
            return "";
        }
        char[] array = s.toCharArray();
        StringBuilder builder = new StringBuilder("");
        for (char c : array) {
            if (c == '&' || c == ',' || c == ';') {
                builder.append('\\');
                builder.append(c);
            } else if (c != '\n') {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Writes a header of simulation logs.
     * @param booleanMap a boolean representation of a map of physical obstacles in the environment
     * @throws IOException if IOException occurred during writing to the logfile
     */
    private void writeHeader(boolean[][] booleanMap) throws IOException {
        if (booleanMap != null) {
            logfile.write(environmentMap.getSizeX()+ "\n");
            logfile.write(environmentMap.getSizeY()+ "\n");
            for (int i = 0; i < environmentMap.getSizeX(); i++) {
                for (int j = 0; j < environmentMap.getSizeY(); j++) {
                    if (booleanMap[i][j]) {
                        logfile.write('1');
                    } else {
                        logfile.write('0');
                    }
                }
            }
            logfile.write('\n');
        } else {
            logfile.write("null\n");
            logfile.write(environmentMap.getSizeX()+ "\n");
            logfile.write(environmentMap.getSizeY()+ "\n");
        }
        logfile.write("---\n");
        for (RobotPlacement r : robots) {
            logfile.write(r.robot.getClass().getName());
            logfile.write(",");
            logfile.write(r.size.toString());
            logfile.write("\n");
        }
        logfile.write("---\n");
        for (ObjectPlacement r : objects) {
            logfile.write(r.object.getClass().getName());
            logfile.write("\n");
        }
        logfile.write("---\n");
    }

    /**
     * Checks whether simulation has to stop
     * @return true if simulation has to stop, false otherwise
     */
    private boolean endCondition() {
        return cycle > CYCLES || endSignal;
    }

}
