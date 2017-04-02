package com.khalev.efd.simulation;

import cz.cuni.mff.d3s.deeco.annotations.Component;
import cz.cuni.mff.d3s.deeco.annotations.Ensemble;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessorException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoException;
import cz.cuni.mff.d3s.deeco.runtime.DEECoNode;
import cz.cuni.mff.d3s.deeco.timer.WallTimeTimer;
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

//TODO: disable RobotEnsemble or ObjectEnsemble if no robots/objects are in simulation

/**
 * The main class of this package. To run the simulation:
 *      1. Write a correct simulation parameters file (scenario)
 *      2. Instantiate this class with that file's name in constructor
 *      3. Call startSimulation();
 */
public class Simulation {

    private String schema = "src\\resources\\Simulation.xsd";
    private List<Object> components = new ArrayList<>();
    private ArrayList<RobotPlacement> robots = new ArrayList<>();
    private ArrayList<ObjectPlacement> objects = new ArrayList<>();
    private ArrayList<SensoryInputsProcessor> sensors = new ArrayList<>();

    private File logfile;
    private File bitmap;
    private EnvironmentMap map;
    private int cycles;

    private static int CYCLE = 2;

    static int getCYCLE() {
        return CYCLE;
    }

    public Simulation(String parametersFile) throws SimulationParametersException {
        if (!validateAgainstXSD(parametersFile, schema)) {
            throw  new SimulationParametersException("Specified file does not represent a valid simulation properties file");
        }
        initializeFromXML(parametersFile);
    }

    public void startSimulation() throws AnnotationProcessorException, DEECoException {
        Environment env = new Environment(cycles, robots, logfile, map,
                (bitmap != null ? bitmap.getAbsolutePath() : null), sensors, objects);
        Environment.setInstance(env);
        startRuntime();
    }

    private boolean validateAgainstXSD(String xml, String xsd) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new File(xsd)));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xml)));
            return true;
        } catch(Exception ex) {
            return false;
        }
    }



    private void startRuntime() throws AnnotationProcessorException, DEECoException {
        WallTimeTimer wallTimeTimer = new WallTimeTimer();
        DEECoNode deecoNode = new DEECoNode(0, wallTimeTimer);
        for (Object o : components) {
            if (o instanceof Class) {
                deecoNode.deployEnsemble((Class)o);
            } else {
                deecoNode.deployComponent(o);
            }
        }
        Environment.getInstance().setRuntime(deecoNode.getRuntimeFramework());
        wallTimeTimer.start();
    }

    private void initializeFromXML(String XMLFile) throws SimulationParametersException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(XMLFile));

            //Processing attributes
            NamedNodeMap nnm = doc.getDocumentElement().getAttributes();
            cycles = Integer.parseInt(nnm.getNamedItem("cycles").getTextContent());
            if (nnm.getNamedItem("processingTime") != null) {
                CYCLE = Integer.parseInt(nnm.getNamedItem("processingTime").getTextContent());
            }
            logfile = new File(nnm.getNamedItem("logfile").getTextContent());
            Node sizeXNode = nnm.getNamedItem("sizeX");
            Node sizeYNode = nnm.getNamedItem("sizeY");
            Node bitmapNode = nnm.getNamedItem("bitmap");
            if (bitmapNode == null && (sizeXNode == null || sizeYNode == null)) {
                throw new SimulationParametersException("You must specify either bitmap or size of the field");
            } else if (bitmapNode != null) {
                bitmap = new File(bitmapNode.getTextContent());
                BitmapProcessor bp = new BitmapProcessor(bitmap);
                map = bp.readBitmap();
            } else {
                map = BitmapProcessor.createEmptyMap(Integer.parseInt(sizeXNode.getTextContent()),
                        Integer.parseInt(sizeYNode.getTextContent()));
            }


            //Processing robots
            NodeList robotList = doc.getElementsByTagName("robot");
            for (int i = 0; i < robotList.getLength(); i++) {
                Element e = (Element) robotList.item(i);
                String classname = e.getAttributes().getNamedItem("class").getTextContent();
                double posX = Double.parseDouble(e.getAttributes().getNamedItem("posX").getTextContent());
                double posY = Double.parseDouble(e.getAttributes().getNamedItem("posY").getTextContent());
                double angle = Math.toRadians(Double.parseDouble(e.getAttributes().getNamedItem("angle").getTextContent()));
                String tag = (e.getAttributes().getNamedItem("tag") != null ?
                        e.getAttributes().getNamedItem("tag").getTextContent() : "");
                Node argNode = e.getAttributes().getNamedItem("arg");
                String arg = argNode != null ? argNode.getTextContent() : null;
                Node sizeNode = e.getAttributes().getNamedItem("size");
                double size = sizeNode != null ? Double.parseDouble(sizeNode.getTextContent()) : 3.0;
                if (size < 1) {
                    throw new SimulationParametersException("Robot's size cannot be less than 1.0");
                }
                DEECoRobot r = createNewRobot(classname, tag, arg);
                components.add(r);
                robots.add(new RobotPlacement(r, posX, posY, angle, size, tag));
            }
            //Processing ensembles
            NodeList ensembles = doc.getElementsByTagName("ensemble");
            for (int i = 0; i < ensembles.getLength(); i++) {
                Element e = (Element) ensembles.item(i);
                String classname = e.getAttributes().getNamedItem("class").getTextContent();
                Class ensemble = loadEnsemble(classname);
                components.add(ensemble);
            }
            //Processing sensors
            ArrayList<String> sensorNames = new ArrayList<>();
            sensorNames.add("collisions");

            NodeList sensors = doc.getElementsByTagName("sensor");
            for (int i = 0; i < sensors.getLength(); i++) {
                Element e = (Element) sensors.item(i);
                String sensorName = e.getAttributes().getNamedItem("name").getTextContent();
                for (String s : sensorNames) {
                    if (s.equals(sensorName)) {
                        throw new SimulationParametersException("You cannot add two sensors with the same name: " + s);
                    }
                }
                String classname = e.getAttributes().getNamedItem("processor").getTextContent();
                Node argNode = e.getAttributes().getNamedItem("arg");
                String arg = argNode != null ? argNode.getTextContent() : null;
                SensoryInputsProcessor sip = createDataProcessor(classname, arg);
                this.sensors.add(sip);
                sensorNames.add(sensorName);
            }
            //Processing objects
            NodeList objectList = doc.getElementsByTagName("object");
            for (int i = 0; i < objectList.getLength(); i++) {
                Element e = (Element) objectList.item(i);
                String classname = e.getAttributes().getNamedItem("class").getTextContent();
                double posX = Double.parseDouble(e.getAttributes().getNamedItem("posX").getTextContent());
                double posY = Double.parseDouble(e.getAttributes().getNamedItem("posY").getTextContent());
                double size = Double.parseDouble(e.getAttributes().getNamedItem("size").getTextContent());
                String tag = (e.getAttributes().getNamedItem("tag") != null ?
                        e.getAttributes().getNamedItem("tag").getTextContent() : "");
                Node argNode = e.getAttributes().getNamedItem("arg");
                String arg = argNode != null ? argNode.getTextContent() : null;
                DEECoObject o = createNewObject(classname, posX, posY, size, tag, arg);
                components.add(o);
                objects.add(new ObjectPlacement(o, posX, posY, size, tag));
            }
            components.add(new Coordinator(robots.size(), objects.size(), sensorNames));
            components.add(ActionEnsemble.class);
            components.add(InputEnsemble.class);
            components.add(ObjectEnsemble.class);

            checkParametersForConsistency(robots, map, cycles);
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private DEECoRobot createNewRobot(String classname, String tag, String arg) throws SimulationParametersException {
        try {
            Class clazz = Class.forName(classname);
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
            throw new SimulationParametersException("Class " + classname + " is not valid class.");
        }
    }

    private DEECoObject createNewObject(String classname, double x, double y, double size, String tag, String arg) throws SimulationParametersException {
        try {
            Class clazz = Class.forName(classname);
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

    private SensoryInputsProcessor createDataProcessor(String classname, String arg) throws SimulationParametersException {
        try {
            Class cls = Class.forName(classname);
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

    private Class loadEnsemble(String classname) throws SimulationParametersException {
        try {
            Class cls = Class.forName(classname);
            Annotation annotation = cls.getAnnotation(Ensemble.class);
            if (annotation == null) {
                throw new SimulationParametersException("Class " + classname + " is not a DEECo @Ensemble");
            }
            return cls;
        } catch (ClassNotFoundException e) {
            throw new SimulationParametersException("Class " + classname + " does not exist.");
        }
    }

    private void checkParametersForConsistency(ArrayList<RobotPlacement> robots, EnvironmentMap map, int cycles) throws SimulationParametersException {
        Collision collision = SimulationEngine.checkMapConsistency(robots, map);
        if (collision.type == Collision.Type.WALL) {
            throw new SimulationParametersException("There was found a collision between the robot #" + (collision.num1+1) +
                    " and the wall. To be consistent, initial parameters of simulation should not contain collisions.");
        } else if (collision.type == Collision.Type.ROBOT) {
            throw new SimulationParametersException("There was found a collision between robot #" + (collision.num1+1) +
                    " and robot #" + (collision.num2+1) + ". To be consistent, initial parameters of simulation should not contain collisions.");
        }

    }
}
