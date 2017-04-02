package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.*;

/**
 * This class is used for computing cycles of the simulation and for writing logs.
 */
class Environment {

    public static final int CYCLE = 1;
    private final int STEPS;

    private boolean oldMode = false;

    private RuntimeFramework runtime;
    private EnvironmentMap environmentMap;
    private SimulationEngine computer;
    private ArrayList<RobotPlacement> robots;
    private ArrayList<RobotPlacement> previousPositions;
    private ArrayList<Action> actions = new ArrayList<>();
    private ArrayList<ObjectPlacement> objects = new ArrayList<>();

    private ArrayList<ArrayList> allInputs;
    private ArrayList<SensoryInputsProcessor> sensors;

    private int step;
    private int previousStep = -1;
    private FileWriter logfile;
    private long startTime;
    Logger logger;
    private String status = "";

    private boolean endSignal = false;
    void stopSimulation() {
        endSignal = true;
    }

    private static Environment instance;
    public static Environment getInstance() {
        return instance;
    }
    static void setInstance(Environment e) {
        instance = e;
    }

    Environment(int numOfSteps, ArrayList<RobotPlacement> robots, File logs, EnvironmentMap map, String bitmap,
                ArrayList<SensoryInputsProcessor> sensors, ArrayList<ObjectPlacement> objects) {
        try {
            STEPS = numOfSteps;
            this.environmentMap = map;
            this.robots = robots;
            this.objects = objects;
            this.computer = new SimulationEngine(robots, map);

            this.sensors = new ArrayList<>();
            this.sensors.add(this.computer);
            this.sensors.addAll(sensors);
            this.allInputs = new ArrayList<>(sensors.size());
            logs.getParentFile().mkdirs();
            this.logfile = new FileWriter(logs);
            writeHeader(bitmap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void setRuntime(RuntimeFramework runtime) {
        configureLogger();
        this.runtime = runtime;
    }

    private void configureLogger() {
        logger = Logger.getLogger(this.getClass().getPackage().getName());
        LogManager.getLogManager().reset();
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord arg0) {
                StringBuilder b = new StringBuilder();
                b.append(arg0.getSourceMethodName()).append("(): ");
                b.append(arg0.getMessage());
                b.append(System.getProperty("line.separator"));
                return b.toString();
            }
        };

        Handler ch = new ConsoleHandler();
        ch.setFormatter(formatter);
        ch.setLevel(Level.INFO);
        logger.addHandler(ch);
        LogManager lm = LogManager.getLogManager();
        lm.addLogger(logger);
        logger.setLevel(Level.INFO);

        startTime = System.nanoTime();
    }

    void updateActions(RobotData[] acts) {
        actions.clear();
        assert acts.length == robots.size(): "Received RobotData[] array has wrong size";
        for (int i = 0; i < acts.length; i++) {
            actions.add(acts[i].action);
            robots.get(i).tag = acts[i].tag;
            robots.get(i).currentAction = acts[i].action;
        }
    }

    void updateObjects(ObjectData[] objs) {
        assert objs.length == objects.size(): "Received ObjectData[] array has wrong size";
        for (int i = 0; i < objs.length; i++) {
            ObjectPlacement objectPlacement = objects.get(i);
            objectPlacement.x = objs[i].coordinates.x;
            objectPlacement.y = objs[i].coordinates.y;
            objectPlacement.size = objs[i].size;
            objectPlacement.tag = objs[i].tag;
        }
    }

    void updateStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }

    ArrayList<ArrayList> getAllInputs() {
        return allInputs;
    }

    int cycle() {
        try {
            writeLogs();
            logger.info("CYCLE " + step);
            assert step == previousStep + 1: "Unknown exception occurred in computations";
            previousStep++;

            if (!endCondition()) {
                previousPositions = robots;
                robots = computer.performActions(actions);
                allInputs.clear();
                for (SensoryInputsProcessor sip : sensors) {
                    ArrayList<RobotPlacement> robotPlacements = new ArrayList<>();
                    ArrayList<ObjectPlacement> objectPlacements = new ArrayList<>();
                    robotPlacements.addAll(robots);
                    objectPlacements.addAll(objects);
                    ArrayList list = sip.sendInputs(robotPlacements, objectPlacements);
                    if (list.size() != robots.size()) {
                        throw new RuntimeException("Number of inputs received from " + sip.getClass().getSimpleName() +
                                " does not equal to the number of robots");
                    } else {
                        allInputs.add(list);
                    }
                }
                if (step > 0)
                    writeRobotLogs();
                step++;
                return 0;
            } else {
                writeTimerLogs();
                logfile.close();
                System.exit(0);
                return 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTimerLogs() {
        long endTime = System.nanoTime();
        logger.info("Time elapsed: " + ((endTime - startTime) / 1000000) + " ms");
        logger.info("Approximately " + ((endTime - startTime) / this.STEPS / 1000000) + " ms per cycle");
    }

    private void writeRobotLogs() {
        String s = "\n";
        for (int i = 0; i < previousPositions.size(); i++) {
            RobotPlacement r = robots.get(i);
            RobotPlacement rp = previousPositions.get(i);
            Action act = actions.get(i);
            CollisionData in = (CollisionData) allInputs.get(0).get(i);

            s += ("Robot #" + rp.id + " had coordinates " + rp.x + ", " + rp.y + ";\n");
            if (act.type == Action.Type.MOVE) {
                s += ("Robot #" + r.id + ": MOVE, " + ((act.degreeOfRealization) * 100) + "%\n");
            } else if (act.type == Action.Type.ROTATE) {
                s += ("Robot #" + r.id + ": ROTATE, " + (Math.toDegrees(act.angle)) + " degrees\n");
            } else if(act.type == Action.Type.ROTATE_AND_MOVE) {
                s += ("Robot #" + r.id + ": ROTATE&MOVE, " + (Math.toDegrees(act.angle)) + " degrees, "
                        + ((act.degreeOfRealization) * 100) + "%\n");
            } else {
                s += ("Robot #" + r.id + ": STAY\n");
            }
            if (in.collisionPoints.size() == 0) {
                s += ("Robot #" + r.id + ": no collisions\n");
            } else {
                s += ("Robot #" + r.id + ": COLLISIONS AT ");
                for (double point : in.collisionPoints) {
                    s += String.format("%.2f", Math.toDegrees(point));
                    s += ("; ");
                }
                s += "\n";
            }
            s += ("Robot #" + r.id + " has final coordinates " + r.x + ", " + r.y + ";\n");
        }
        logger.finer(s);
    }

    private void writeLogs() throws IOException {
        if (oldMode) {
            for (RobotPlacement robot : robots) {
                logfile.write(robot.x.toString());
                logfile.write(",,");
                logfile.write(robot.y.toString());
                logfile.write(";;");
            }
            logfile.write("\n");
        } else {
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
    }

    private String prefixString(String s) {
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

    private void writeHeader(String bitmap) throws IOException {
        if (bitmap != null) {
            logfile.write(bitmap + "\n");
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

    private boolean endCondition() {
        return step >= STEPS || endSignal;
    }


}
