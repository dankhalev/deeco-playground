package cz.cuni.mff.d3s.deeco.playground.simulation;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.timer.DiscreteEventTimer;
import cz.cuni.mff.d3s.deeco.timer.SimulationTimer;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


/**
 * The main class of the simulation part of the DEECo Playground project. To run the simulation, an instance of this
 * class has to be initialized with a valid scenario file in its constructor, and then started by calling
 * {@link Simulation#startSimulation} method.
 *
 * @author Danylo Khalyeyev
 */
public final class Simulation {

    private final String SIMULATION_SCHEMA = "src/resources/Simulation.xsd";
    private final int DEFAULT_WAITING_TIME = 1;
    private final double DEFAULT_ROBOT_SIZE = 3.0;
    private final double MINIMAL_ROBOT_SIZE = 1.0;

    private List<Object> componentsAndEnsembles = new ArrayList<>();
    private List<RobotPlacement> robots = new ArrayList<>();
    private List<ObjectPlacement> objects = new ArrayList<>();
    private List<SensoryInputsProcessor> sensors = new ArrayList<>();
    private List<String> sensorNames = new ArrayList<>();
    private File logfile;
    private EnvironmentMap map;
    private boolean[][] booleanMap;
    private int cycles;


    /**
     * Initializes a new simulation from provided scenario XML file. This file must be valid against Simulation.xsd.
     * @param scenarioFile A path to scenario file.
     * @throws SimulationParametersException if provided file does not exist, is not valid against Simulation.xsd or
     * contains mistakes (like collisions between robots or wrong attribute values)
     */
    public Simulation(String scenarioFile) throws SimulationParametersException {
        boolean exists = new File(scenarioFile).exists();
        boolean valid = validateAgainstXSD(scenarioFile, SIMULATION_SCHEMA);
        if (!exists) {
            throw  new SimulationParametersException("Specified file does not exist");
        } else if (!valid) {
            throw  new SimulationParametersException("Specified file does not represent a valid simulation properties file");
        }

        resetEnvironment();
        initializeFromXML(scenarioFile);
    }

    /**
     * Creates an Environment and launches DEECo runtime with all entities (robots, objects, sensors, ensembles) created
     * during initialization.
     * @throws AnnotationProcessorException for reasons specified in JDEECo
     * @throws DEECoException for reasons specified in JDEECo
     */
    public void startSimulation() throws AnnotationProcessorException, DEECoException {
        //Initialize DEECo runtime
        SimulationTimer timer = new DiscreteEventTimer();
        DEECoNode deecoNode = new DEECoNode(0, timer);
        for (Object o : componentsAndEnsembles) {
            if (o instanceof Class) {
                deecoNode.deployEnsemble((Class)o);
            } else {
                deecoNode.deployComponent(o);
            }
        }
        //Create environment. This should be done between initializing runtime and starting it.
        Environment env = new EnvironmentImpl(cycles, robots, logfile, map, booleanMap, sensors, objects, sensorNames,
                deecoNode);
        boolean canStart = Environment.setInstance(env);
        if (!canStart) {
            throw new RuntimeException("Cannot start simulation because another one is running");
        }
        //Start runtime
        int timeToSimulate = (cycles+1)*(Environment.CYCLE*Environment.getWaitingTime()+Environment.CYCLE);
        timer.start(timeToSimulate);
        resetEnvironment();
    }

    private void resetEnvironment() {
        Environment.reset();
        DEECoRobot.resetCounter();
        DEECoObject.resetCounter();
    }

    /**
     * @param xml A path to XML file to validate
     * @param xsd A path to XML Schema to validate against
     * @return true if file exists and valid, false otherwise
     * @throws SimulationParametersException if provided XML Schema does not exist
     */
    private boolean validateAgainstXSD(String xml, String xsd) throws SimulationParametersException {
        try {
            File schemaFile = new File(xsd);
            if (!schemaFile.exists()) {
                throw  new SimulationParametersException("Simulation.xsd is not present at " + xsd);
            }
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(schemaFile));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xml)));
            return true;
        } catch (IOException | SAXException e) {
            return false;
        }
    }

    /**
     * Reads provided XML file and extracts all entities and parameters from it. Initializes entities and prepares all
     * the data that is needed for simulation.
     * @param scenarioFile  A path to XML file with scenario description.
     * @throws SimulationParametersException if provided scenario is inconsistent.
     */
    private void initializeFromXML(String scenarioFile) throws SimulationParametersException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(scenarioFile));
            //Read simulation parameters and all the entities specified in the scenario file
            processSimulationParameters(doc);
            processRobots(doc);
            processEnsembles(doc);
            processSensors(doc);
            processObjects(doc);
            //Add Coordinator and system ensembles
            componentsAndEnsembles.add(new Coordinator(robots.size(), objects.size()));
            if (robots.size() > 0) {
                componentsAndEnsembles.add(RobotEnsemble.class);
            }
            if (objects.size() > 0) {
                componentsAndEnsembles.add(ObjectEnsemble.class);
            }

            checkParametersForConsistency(robots, map);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSimulationParameters(Document doc) throws IOException, SimulationParametersException {
        NamedNodeMap attributes = doc.getDocumentElement().getAttributes();
        Node cyclesNode = attributes.getNamedItem("cycles");
        Node processingTimeNode = attributes.getNamedItem("processingTime");
        Node sizeXNode = attributes.getNamedItem("sizeX");
        Node sizeYNode = attributes.getNamedItem("sizeY");
        Node bitmapNode = attributes.getNamedItem("bitmap");
        Node logfileNode = attributes.getNamedItem("logfile");

        cycles = Integer.parseInt(cyclesNode.getTextContent());
        logfile = new File(logfileNode.getTextContent());

        if (processingTimeNode != null) {
            Environment.setWaitingTime(Integer.parseInt(processingTimeNode.getTextContent()));
        } else {
            Environment.setWaitingTime(DEFAULT_WAITING_TIME);
        }

        if (bitmapNode != null) {
            try {
                File bitmap = new File(bitmapNode.getTextContent());
                if (!bitmap.exists()) {
                    throw new SimulationParametersException("Bitmap file does not exist");
                }
                BitmapReader bp = new BitmapReader(bitmap);
                map = bp.readBitmap();
                booleanMap = bp.getBooleanRepresentation();
            } catch (IOException e) {
                throw new SimulationParametersException("Cannot read bitmap file", e);
            }
        } else if (sizeXNode != null && sizeYNode != null){
            map = BitmapReader.createEmptyMap(Integer.parseInt(sizeXNode.getTextContent()),
                    Integer.parseInt(sizeYNode.getTextContent()));
            booleanMap = null;
        } else {
            throw new SimulationParametersException("You must specify either bitmap or size of the field");
        }
    }

    private void processRobots(Document doc) throws SimulationParametersException {
        NodeList robotList = doc.getElementsByTagName("robot");
        for (int i = 0; i < robotList.getLength(); i++) {
            Element robotElement = (Element) robotList.item(i);
            //Process robot's parameters:
            Node classNode = robotElement.getAttributes().getNamedItem("class");
            Node posXNode = robotElement.getAttributes().getNamedItem("posX");
            Node posYNode = robotElement.getAttributes().getNamedItem("posY");
            Node angleNode = robotElement.getAttributes().getNamedItem("angle");
            Node sizeNode = robotElement.getAttributes().getNamedItem("size");
            Node tagNode = robotElement.getAttributes().getNamedItem("tag");
            Node argNode = robotElement.getAttributes().getNamedItem("arg");

            String classname = classNode.getTextContent();
            double posX = Double.parseDouble(posXNode.getTextContent());
            double posY = Double.parseDouble(posYNode.getTextContent());
            double angle = Math.toRadians(Double.parseDouble(angleNode.getTextContent()));
            String tag = tagNode != null ? tagNode.getTextContent() : "";
            String arg = argNode != null ? argNode.getTextContent() : "";
            double size = sizeNode != null ? Double.parseDouble(sizeNode.getTextContent()) : DEFAULT_ROBOT_SIZE;
            if (size < MINIMAL_ROBOT_SIZE) {
                throw new SimulationParametersException("Robot's size cannot be less than " + MINIMAL_ROBOT_SIZE);
            }
            //Create new robot and add it to the list
            DEECoRobot r = createNewRobot(classname, tag, arg);
            componentsAndEnsembles.add(r);
            robots.add(new RobotPlacement(r, posX, posY, angle, size, tag));
        }
    }

    private void processObjects(Document doc) throws SimulationParametersException {
        NodeList objectList = doc.getElementsByTagName("object");
        for (int i = 0; i < objectList.getLength(); i++) {
            Element objectElement = (Element) objectList.item(i);
            //Process object's parameters:
            Node classNode = objectElement.getAttributes().getNamedItem("class");
            Node posXNode = objectElement.getAttributes().getNamedItem("posX");
            Node posYNode = objectElement.getAttributes().getNamedItem("posY");
            Node sizeNode = objectElement.getAttributes().getNamedItem("size");
            Node tagNode = objectElement.getAttributes().getNamedItem("tag");
            Node argNode = objectElement.getAttributes().getNamedItem("arg");

            String classname = classNode.getTextContent();
            double posX = Double.parseDouble(posXNode.getTextContent());
            double posY = Double.parseDouble(posYNode.getTextContent());
            double size = Double.parseDouble(sizeNode.getTextContent());
            String tag = tagNode != null ? tagNode.getTextContent() : "";
            String arg = argNode != null ? argNode.getTextContent() : "";
            //Create new object and add it to the list
            DEECoObject o = createNewObject(classname, posX, posY, size, tag, arg);
            componentsAndEnsembles.add(o);
            objects.add(new ObjectPlacement(o, posX, posY, size, tag));
        }
    }

    private void processEnsembles(Document doc) throws SimulationParametersException {
        NodeList ensembles = doc.getElementsByTagName("ensemble");
        for (int i = 0; i < ensembles.getLength(); i++) {
            Element e = (Element) ensembles.item(i);

            Node classNode = e.getAttributes().getNamedItem("class");
            String classname = classNode.getTextContent();

            Class ensemble = loadEnsemble(classname);
            componentsAndEnsembles.add(ensemble);
        }
    }

    private void processSensors(Document doc) throws SimulationParametersException {
        NodeList sensors = doc.getElementsByTagName("sensor");
        for (int i = 0; i < sensors.getLength(); i++) {
            Element sensorElement = (Element) sensors.item(i);
            //Process sensor's parameters
            Node nameNode = sensorElement.getAttributes().getNamedItem("name");
            Node classNode = sensorElement.getAttributes().getNamedItem("processor");
            Node argNode = sensorElement.getAttributes().getNamedItem("arg");

            String classname = classNode.getTextContent();
            String sensorName = nameNode.getTextContent();
            String arg = argNode != null ? argNode.getTextContent() : "";
            //Check whether sensor with this name already exists
            for (String s : sensorNames) {
                if (s.equals(sensorName)) {
                    throw new SimulationParametersException("You cannot add two sensors with the same name: " + s);
                }
            }
            //Create new sensor and add it to the list
            SensoryInputsProcessor sip = createDataProcessor(classname, arg);
            this.sensors.add(sip);
            sensorNames.add(sensorName);
        }
    }

    /**
     * Creates a new instance of a robot of a specified class. Checks if this class exists, contains @Component
     * annotation, and extends {@link DEECoRobot}. Calls initialization methods {@link DEECoRobot#setParameters}
     * and {@link DEECoRobot#processArg} on this new instance.
     * @param classname fully qualified name of the robot's class
     * @param tag initial tag of the robot
     * @param arg argument that will be passed to the robot in initialization
     * @return a created instance
     * @throws SimulationParametersException if this class does not exist, does not contain @Component annotation, or
     * does not extend {@link DEECoRobot} class.
     */
    private DEECoRobot createNewRobot(String classname, String tag, String arg) throws SimulationParametersException {
        try {
            Class<?> clazz = Class.forName(classname);
            Annotation annotation = clazz.getAnnotation(Component.class);
            if (annotation == null) {
                throw new SimulationParametersException("Class " + classname + " is not a DEECo @Component");
            }
            if (!DEECoRobot.class.isAssignableFrom(clazz)) {
                throw new SimulationParametersException("Class " + classname + " does not extend class DEECoRobot.");
            }
            DEECoRobot robot = (DEECoRobot) clazz.newInstance();
            robot.setParameters(tag);
            robot.processArg(arg);
            return robot;
        } catch (ClassNotFoundException e) {
            throw new SimulationParametersException("Class " + classname + " does not exist.");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SimulationParametersException("Class " + classname + " is not a valid class.");
        }
    }

    /**
     * Creates a new instance of an object of a specified class. Checks if this class exists, contains @Component
     * annotation, and extends {@link DEECoObject}. Calls initialization methods {@link DEECoObject#setParameters} and
     * {@link DEECoObject#processArg} on this new instance.
     * @param classname classname fully qualified name of the object's class
     * @param x initial X-coordinate of the object
     * @param y initial Y-coordinate of the object
     * @param size initial size of the object
     * @param tag initial tag of the object
     * @param arg argument that will be passed to the object in initialization
     * @return a created instance
     * @throws SimulationParametersException if this class does not exist, does not contain @Component annotation, or
     * does not extend {@link DEECoObject} class.
     */
    private DEECoObject createNewObject(String classname, double x, double y, double size, String tag, String arg) throws SimulationParametersException {
        try {
            Class<?> clazz = Class.forName(classname);
            Annotation annotation = clazz.getAnnotation(Component.class);
            if (annotation == null) {
                throw new SimulationParametersException("Class " + classname + " is not a DEECo @Component");
            }
            if (!DEECoObject.class.isAssignableFrom(clazz)) {
                throw new SimulationParametersException("Class " + classname + " does not extend class DEECoObject.");
            }
            DEECoObject object = (DEECoObject) clazz.newInstance();
            object.setParameters(x, y, tag, size);
            object.processArg(arg);
            return object;
        } catch (ClassNotFoundException e) {
            throw new SimulationParametersException("Class " + classname + " does not exist.");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SimulationParametersException("Class " + classname + " is not valid class.");
        }
    }

    /**
     * Creates a new instance of a sensory inputs processor of a specified class. Checks whether this class exists, and
     * extends {@link SensoryInputsProcessor}. Calls initialization method {@link SensoryInputsProcessor#processArg}
     * on this new instance.
     * @param classname fully qualified name of the SIP's class
     * @param arg argument that will be passed to the SIP in initialization
     * @return a created instance
     * @throws SimulationParametersException if this class does not exist or does not extend {@link SensoryInputsProcessor}
     * class.
     */
    private SensoryInputsProcessor createDataProcessor(String classname, String arg) throws SimulationParametersException {
        try {
            Class<?> cls = Class.forName(classname);
            if (!SensoryInputsProcessor.class.isAssignableFrom(cls)) {
                throw new SimulationParametersException("Class " + classname + " does not extend class SensoryInputsProcessor.");
            }
            SensoryInputsProcessor sip = (SensoryInputsProcessor) cls.newInstance();
            sip.setEnvironmentMap(map);
            sip.processArg(arg);
            return sip;
        } catch (ClassNotFoundException e) {
            throw new SimulationParametersException("Class " + classname + " does not exist.");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SimulationParametersException("Class " + classname + " is not valid class.");
        }
    }

    /**
     * Loads an ensemble of a specified class. Checks whether this class exists and has @Ensemble annotation.
     * @param classname fully qualified name of the ensemble's class
     * @return ensemble's class
     * @throws SimulationParametersException if this class does not exist or does not contain @Ensemble annotation
     */
    private Class loadEnsemble(String classname) throws SimulationParametersException {
        try {
            Class<?> cls = Class.forName(classname);
            Annotation annotation = cls.getAnnotation(Ensemble.class);
            if (annotation == null) {
                throw new SimulationParametersException("Class " + classname + " is not a DEECo @Ensemble");
            }
            return cls;
        } catch (ClassNotFoundException e) {
            throw new SimulationParametersException("Class " + classname + " does not exist.");
        }
    }

    /**
     * Checks whether initial positions of robots contain collisions. There are two possible collision types: one that
     * occurs between two robots and the one that occurs between a robot and a physical obstacle.
     * @param robots list of all robots that are present in the simulation
     * @param map map of obstacles in the environment
     * @throws SimulationParametersException if there is at least one collision in initial positions
     */
    private void checkParametersForConsistency(List<RobotPlacement> robots, EnvironmentMap map)
            throws SimulationParametersException {
        Collision collision = SimulationEngine.checkMapConsistency(robots, map, booleanMap);
        if (collision.type == Collision.Type.WALL) {
            throw new SimulationParametersException("There was found a collision between robot #" +
                    (collision.robot1 +1) + " and a physical obstacle. To be consistent, initial parameters of " +
                    "simulation should not contain collisions.");
        } else if (collision.type == Collision.Type.ROBOT) {
            throw new SimulationParametersException("There was found a collision between robot #" +
                    (collision.robot1 +1) + " and robot #" + (collision.robot2 +1) + ". To be consistent, initial " +
                    "parameters of simulation should not contain collisions.");
        }

    }

}
