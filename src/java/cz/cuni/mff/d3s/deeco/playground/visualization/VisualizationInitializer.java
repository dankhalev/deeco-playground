package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.graphics.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Reads the configuration file (if present) and the header of the simulation logs file and constructs a list of
 * {@link VisualizationLayer}s from them. Creates a {@link LogfileReader} and passes it to component layers. If
 * configuration file is not present, creates list of layers with default configuration.
 *
 * @author Danylo Khalyeyev
 */
class VisualizationInitializer {

    private final String XMLSchema = "src/resources/Visualization.xsd";
    private List<String> robotClasses = new ArrayList<>();
    private List<String> objectClasses = new ArrayList<>();
    private Color statusColor = Color.BLACK;
    private ComponentConfigs robotConfigs;
    private ComponentConfigs objectConfigs;
    private Coloring mapColoring;
    private LogfileReader logReader;
    private List<VisualizationLayer> layers = new ArrayList<>();
    private boolean isRobotLayerInitialized, isObjectLayerInitialized, isMapLayerInitialized;
    private EnvironmentMap map;

    /**
     * Reads the configuration file (if present) and the header of the simulation logs file and constructs a list of
     * {@link VisualizationLayer}s from them. Creates a {@link LogfileReader} and passes it to component layers. If
     * configuration file is not present, creates list of layers with default configuration.
     * @param logfile a simulation logs file
     * @param config a configuration file
     * @return a list of {@link VisualizationLayer}s constructed from provided files
     * @throws IOException if IOException occurs during reading any of those files
     * @throws VisualizationParametersException if configuration file is not correct or if simulation logs file contains
     * errors (it can happen if it was modified after it was generated)
     */
    List<VisualizationLayer> init(File logfile, File config) throws IOException, VisualizationParametersException {
        //create and validate configuration document
        Document doc = null;
        if (config != null) {
            boolean valid = validateAgainstXSD(config, XMLSchema);
            if (valid) {
                doc = this.initializeXMLAndSetZoom(config);
            } else {
                throw new VisualizationParametersException("Provided config file is not correct");
            }
        }

        //read an encoded bitmap from simulation logs
        BufferedReader reader = new BufferedReader(new FileReader(logfile));
        String firstLine = reader.readLine();
        if (!Objects.equals(firstLine, "null")) {
                int sizeX = Integer.parseInt(firstLine);
                int sizeY = Integer.parseInt(reader.readLine());
                map = readBitmap(sizeX, sizeY, reader);
        } else {
            try {
                int sizeX = Integer.parseInt(reader.readLine());
                int sizeY = Integer.parseInt(reader.readLine());
                map = new EnvironmentMap(sizeX, sizeY);
            } catch (NumberFormatException ex) {
                throw new VisualizationParametersException("Simulation logs file is not correct");
            }
        }
        Visualizer.sizeX = map.getWidth()*Visualizer.getZoom();
        Visualizer.sizeY = map.getHeight()*Visualizer.getZoom();

        //read component classes and sizes of robots
        List<ComponentParameters> robots = new ArrayList<>();
        List<ComponentParameters> objects = new ArrayList<>();
        String s;
        s = reader.readLine();
        if (!"---".equals(s)) {
            throw new VisualizationParametersException("Simulation logs file is not correct");
        }
        s = reader.readLine();
        while (!Objects.equals(s, "---")) {
            if (s == null) {
                throw new VisualizationParametersException("Simulation logs file is not correct");
            }
            String[] ss = s.split(",");
            robotClasses.add(ss[0]);
            robots.add(new ComponentParameters(Double.parseDouble(ss[1])* Visualizer.getZoom()));
            s = reader.readLine();
        }
        s = reader.readLine();
        while (!Objects.equals(s, "---")) {
            if (s == null) {
                throw new VisualizationParametersException("Simulation logs file is not correct");
            }
            objectClasses.add(s);
            objects.add(new ComponentParameters());
            s = reader.readLine();
        }

        //create default configs and a logreader:
        mapColoring = defaultMapColoring();
        robotConfigs = defaultRobotConfigs(robotClasses.size());
        objectConfigs = defaultObjectConfigs(objectClasses.size());
        logReader = new LogfileReader(reader, robots, objects);

        //change configs according to configuration file
        if (doc != null) {
            this.parseConfigsAndInitializeLayers(doc);
        }

        //if some of the main layers were not present, add them in default order
        if (!isMapLayerInitialized) {
            MapVisualizationLayer mapLayer = new MapVisualizationLayer(mapColoring, map);
            layers.add(mapLayer);
        }
        if(!isObjectLayerInitialized) {
            ObjectVisualizationLayer objectLayer = new ObjectVisualizationLayer(objectConfigs, logReader, statusColor);
            layers.add(objectLayer);
        }
        if (!isRobotLayerInitialized) {
            RobotVisualizationLayer robotLayer = new RobotVisualizationLayer(robotConfigs, logReader);
            layers.add(robotLayer);
        }

        objectConfigs.rotationEnabled = false;

        return layers;
    }

    /**
     * Returns an {@link EnvironmentMap} that was read from the logfile
     * @return an {@link EnvironmentMap} that was read from the logfile
     */
    EnvironmentMap getMap() {
        return this.map;
    }

    /**
     * Reads an encoded bitmap that is written in the header of simulation logs file.
     * @param sizeX width of the encoded bitmap
     * @param sizeY height of the encoded bitmap
     * @param reader a Reader from which the bitmap will be read
     * @return an {@link EnvironmentMap} that was decoded from the simulation logs file
     * @throws IOException if IOException has occurred during reading a logfile
     */
    private EnvironmentMap readBitmap(int sizeX, int sizeY, Reader reader) throws IOException {
        EnvironmentMap map = new EnvironmentMap(sizeX, sizeY);
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                int b = reader.read();
                if ('1' == b) {
                    map.addObstacle(x, y);
                }
            }
        }
        reader.read();
        return map;
    }

    /**
     * Creates an XML Document from a given file; parses zoom attribute if present and sets zoom on {@link Visualizer}.
     * It is important to set zoom right in the beginning, because it determines a many other visualization parameters.
     * @param config an XML configuration file
     * @return XML Document object
     * @throws VisualizationParametersException if config file is not correct
     */
    private Document initializeXMLAndSetZoom(File config) throws VisualizationParametersException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(config);

            Node zoomAttribute = doc.getDocumentElement().getAttributes().getNamedItem("zoom");
            if (zoomAttribute != null) {
                Visualizer.setZoom(Integer.parseInt(zoomAttribute.getTextContent()));
            }
            return doc;
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new VisualizationParametersException(e);
        }
    }

    /**
     * Returns a default visualization configuration for objects. By default, objects are displayed as yellow squares,
     * without numbers or tags on them. Default font color is black.
     * @param number a number of objects that should be visualized
     * @return a default visualization configuration for objects
     */
    private ComponentConfigs defaultObjectConfigs(int number) {
        ComponentConfigs configs = new ComponentConfigs();
        configs.type = ComponentConfigs.ColoringType.INDIVIDUAL;
        Coloring def = new Coloring(Color.YELLOW);
        configs.def = def;
        configs.fontColor = Color.BLACK;
        configs.objects = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            configs.objects.add(def);
        }
        return configs;
    }

    /**
     * Returns a default visualization configuration for robots. By default, robots are displayed as red circles, without
     * numbers or tags on them. Default font color is black.
     * @param number a number of robots that should be visualized
     * @return a default visualization configuration for robots
     */
    private ComponentConfigs defaultRobotConfigs(int number) {
        ComponentConfigs configs = new ComponentConfigs();
        configs.circularShape = true;
        configs.type = ComponentConfigs.ColoringType.INDIVIDUAL;
        Coloring def = new Coloring(Color.RED);
        configs.fontColor = Color.BLACK;
        configs.def = def;
        configs.objects = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            configs.objects.add(def);
        }
        return configs;
    }

    /**
     * Returns a default coloring for obstacles (black color).
     * @return a default coloring for obstacles
     */
    private Coloring defaultMapColoring() {
        return new Coloring(Color.BLACK);
    }

    /**
     * Goes through configuration file and initializes all the layers described in it.
     * @param doc XML document representing configuration file
     * @throws IOException if IOException occurs while reading configuration file
     * @throws VisualizationParametersException if configuration file is not correct
     */
    private void parseConfigsAndInitializeLayers(Document doc) throws IOException, VisualizationParametersException {
        //as zoom was set previously, the only two global attributes that remain are maxCPS and fontColor
        Node cpsAttribute = doc.getDocumentElement().getAttributes().getNamedItem("maxCPS");
        if (cpsAttribute != null) {
            Visualizer.maxCPS = Integer.parseInt(cpsAttribute.getTextContent());
        }
        Node rewindAttribute = doc.getDocumentElement().getAttributes().getNamedItem("rewindSpeed");
        if (rewindAttribute != null) {
            Visualizer.setRewindSpeed(Integer.parseInt(rewindAttribute.getTextContent()));
        }
        Node fontAttribute = doc.getDocumentElement().getAttributes().getNamedItem("fontColor");
        if (fontAttribute != null) {
            Color color = parseColor(fontAttribute.getTextContent());
            if (color != null) {
                statusColor = color;
            } else {
                wrongColor();
            }
        }

        //Go through the list of layers sequentially, initialize them and and to the resulting list one by one
        NodeList topLevelList = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < topLevelList.getLength(); i++) {
            Node e = topLevelList.item(i);

            String s = e.getNodeName();
            Coloring coloring;
            switch (s) {
                case "robots":
                    if (!isRobotLayerInitialized) {
                        robotConfigs = parseConfigs(e, robotConfigs, robotClasses);
                        layers.add(new RobotVisualizationLayer(robotConfigs, logReader));
                        isRobotLayerInitialized = true;
                    } else {
                        throw new VisualizationParametersException("You cannot have more than one robot layer");
                    }
                    break;
                case "objects":
                    if (!isObjectLayerInitialized) {
                        objectConfigs = parseConfigs(e, objectConfigs, objectClasses);
                        layers.add(new ObjectVisualizationLayer(objectConfigs, logReader, statusColor));
                        isObjectLayerInitialized = true;
                    } else {
                        throw new VisualizationParametersException("You cannot have more than one object layer");
                    }
                    break;
                case "map":
                    if (!isMapLayerInitialized) {
                        coloring = getColoringFromNode(e);
                        if (coloring != null) {
                            mapColoring = coloring;
                        }
                        layers.add(new MapVisualizationLayer(mapColoring, map));
                        isMapLayerInitialized = true;
                    } else {
                        throw new VisualizationParametersException("You cannot have more than one map layer");
                    }
                    break;
                case "background":
                    coloring = getColoringFromNode(e);
                    checkNullColoring(coloring);
                    layers.add(new BackgroundVisualizationLayer(coloring));
                    break;
                case "additional":
                    VisualizationLayer layer = parseCustomLayer(e);
                    layers.add(layer);
                    break;
            }
        }
    }

    /**
     * Constructs {@link ComponentConfigs} from a given node. If some properties are not present in the node, leaves them
     * with default values.
     * @param node an XML Node to extract configs from
     * @param configs a default configs that will be altered
     * @param classes list of classes for each component that will be visualized
     * @return constructed {@link ComponentConfigs}
     * @throws VisualizationParametersException if node contains errors
     */
    private ComponentConfigs parseConfigs(Node node, ComponentConfigs configs, List<String> classes)
            throws VisualizationParametersException {
        //process global attributes
        Node attribute = node.getAttributes().getNamedItem("displayNumbers");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.displayNumbers = true;
        }
        attribute = node.getAttributes().getNamedItem("displayTags");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.displayTags = true;
        }
        attribute = node.getAttributes().getNamedItem("enableRotation");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.rotationEnabled = true;
        }
        attribute = node.getAttributes().getNamedItem("circularShape");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.circularShape = true;
        }
        attribute = node.getAttributes().getNamedItem("fontColor");
        if (attribute != null) {
            Color color = parseColor(attribute.getTextContent());
            if (color != null) {
                configs.fontColor = color;
            } else {
                wrongColor();
            }
        }
        //determine which visualisation option is used
        Element element = (Element)node;
        NodeList nodeList = element.getElementsByTagName("tag-based");
        if (nodeList.getLength() == 0) {
            nodeList = element.getElementsByTagName("class-based");
        }
        if (nodeList.getLength() == 0) {
            nodeList = element.getElementsByTagName("number-based");
        }
        if (nodeList.getLength() == 0) {
            return configs;
        }
        //read default element if present
        Element base = (Element) nodeList.item(0);
        NodeList defList = base.getElementsByTagName("default");
        if (defList.getLength() != 0) {
            Coloring def = getColoringFromNode(defList.item(0));
            checkNullColoring(def);
            configs.def = def;
            int size = configs.objects.size();
            configs.objects.clear();
            for (int i = 0; i < size; i++) {
                configs.objects.add(def);
            }
        }
        //add colorings for each component depending on which option is used
        if (base.getNodeName().equals("number-based")) {
            configs.type = ComponentConfigs.ColoringType.INDIVIDUAL;
            NodeList numberList = base.getElementsByTagName("number");
            for (int i = 0; i < numberList.getLength(); i++) {
                Node n = numberList.item(i);
                int number = Integer.parseInt(n.getAttributes().getNamedItem("number").getTextContent());
                Coloring coloring = getColoringFromNode(n);
                checkNullColoring(coloring);
                if (number < configs.objects.size() && number >= 0) {
                    configs.objects.remove(number);
                    configs.objects.add(number, coloring);
                }
            }
        } else if (base.getNodeName().equals("class-based")) {
            configs.type = ComponentConfigs.ColoringType.INDIVIDUAL;
            NodeList classList = base.getElementsByTagName("class");
            for (int i = 0; i < classList.getLength(); i++) {
                Node n = classList.item(i);
                String classname = n.getAttributes().getNamedItem("name").getTextContent();
                Coloring coloring = getColoringFromNode(n);
                checkNullColoring(coloring);
                for (int j = 0; j < classes.size(); j++) {
                    if (classes.get(j).equals(classname)) {
                        configs.objects.remove(j);
                        configs.objects.add(j, coloring);
                    }
                }
            }
        } else if (base.getNodeName().equals("tag-based")) {
            configs.type = ComponentConfigs.ColoringType.TAG;
            NodeList tagList = base.getElementsByTagName("tag");
            for (int i = 0; i < tagList.getLength(); i++) {
                Node n = tagList.item(i);
                Coloring coloring = getColoringFromNode(n);
                checkNullColoring(coloring);
                String tagName = n.getAttributes().getNamedItem("name").getTextContent();
                configs.tags.put(tagName, coloring);
            }
        }
        return configs;
    }

    private void checkNullColoring(Coloring coloring) throws VisualizationParametersException {
        if (coloring == null) {
            throw new VisualizationParametersException("One of the elements doesn't have any color or texture specified");
        }
    }

    /**
     * Creates {@link Coloring} based on node's content. If node has a color attribute, creates a Coloring of type COLOR,
     * if it contains texture attribute, creates a Coloring of type TEXTURE (without loading its texture). If none of
     * those attributes is present, returns null, if both are, throws an exception.
     * @param coloredNode a node to read coloring from
     * @return if a color or texture attribute is present than a {@link Coloring} obtained from node, else null
     * @throws VisualizationParametersException if both texture and color attributes are present
     */
    private Coloring getColoringFromNode(Node coloredNode) throws VisualizationParametersException {
        Coloring coloring = null;
        Node colorNode = coloredNode.getAttributes().getNamedItem("color");
        Node textureNode = coloredNode.getAttributes().getNamedItem("texture");
        if (textureNode != null && colorNode != null) {
            throw new VisualizationParametersException("One of the elements in configuration file has both " +
                    "color and texture attributes");
        }
        if (textureNode != null) {
            coloring = new Coloring(textureNode.getTextContent());
        } else if (colorNode != null) {
            Color color = parseColor(colorNode.getTextContent());
            if (color != null) {
                coloring = new Coloring(color);
            } else {
                wrongColor();
            }
        }
        return coloring;
    }

    private void wrongColor() throws VisualizationParametersException {
        throw new VisualizationParametersException("A value of a color attribute in the configuration file does not " +
                "represent a color");
    }

    /**
     * Tries to get a color from its string representation. A color can be represented either as a one of keywords or as
     * a list of 3 or 4 float values (RGB or RGBA representations). If a string does not represent a color, returns null.
     * @param text string to parse
     * @return a color obtained from that string, null if string does not represent a color
     */
    private Color parseColor(String text) {
        String color = text.toUpperCase();
        switch (color) {
            case "TRANSPARENT":
                return Color.CLEAR;
            case "CLEAR":
                return Color.CLEAR;
            case "WHITE":
                return Color.WHITE;
            case "GRAY":
                return Color.GRAY;
            case "BLACK":
                return Color.BLACK;
            case "RED":
                return Color.RED;
            case "GREEN":
                return Color.GREEN;
            case "BLUE":
                return Color.BLUE;
            case "YELLOW":
                return Color.YELLOW;
            case "CYAN":
                return Color.CYAN;
            case "MAGENTA":
                return Color.MAGENTA;
            case "ORANGE":
                return Color.ORANGE;
            case "BROWN":
                return Color.BROWN;
            default:
                String[] rgba = color.split(",");
                try {
                    if (rgba.length == 3) {
                        float r = Float.parseFloat(rgba[0]);
                        float g = Float.parseFloat(rgba[1]);
                        float b = Float.parseFloat(rgba[2]);
                        return new Color(r, g, b, 0);
                    } else if (rgba.length == 4) {
                        float r = Float.parseFloat(rgba[0]);
                        float g = Float.parseFloat(rgba[1]);
                        float b = Float.parseFloat(rgba[2]);
                        float a = Float.parseFloat(rgba[3]);
                        return new Color(r, g, b, a);
                    }
                } catch (NumberFormatException ex) {
                    return null;
                }
                return null;
        }
    }

    /**
     * Creates a new visualization layer of a specified class. Checks whether this class exists and extends
     * {@link VisualizationLayer}.
     * @param layerNode an XML node describing additional visualization layer
     * @return a created instance
     * @throws VisualizationParametersException if this class does not exist or does not extend {@link VisualizationLayer}
     * class.
     */
    private VisualizationLayer parseCustomLayer(Node layerNode) throws VisualizationParametersException {
        String classname = layerNode.getAttributes().getNamedItem("class").getTextContent();
        Node argNode = layerNode.getAttributes().getNamedItem("arg");
        String arg = argNode != null ? argNode.getTextContent() : null;
        try {
            Class cls = Class.forName(classname);
            if (!VisualizationLayer.class.isAssignableFrom(cls)) {
                throw new VisualizationParametersException("Class " + classname + " does not extend class VisualizationLayer.");
            }
            VisualizationLayer layer = (VisualizationLayer) cls.newInstance();
            layer.arg = arg;
            return layer;
        } catch (ClassNotFoundException ex) {
            throw new VisualizationParametersException("Class " + classname + " does not exist.");
        } catch (InstantiationException | IllegalAccessException  ex) {
            throw new VisualizationParametersException("Class " + classname + " is not valid class.");
        }
    }

    /**
     * @param xml A path to XML file to validate
     * @param xsd A path to XML Schema to validate against
     * @return true if file exists and valid, false otherwise
     * @throws VisualizationParametersException if provided XML Schema does not exist
     */
    private boolean validateAgainstXSD(File xml, String xsd) throws VisualizationParametersException {
        try {
            File schemaFile = new File(xsd);
            if (!schemaFile.exists()) {
                throw new VisualizationParametersException("Visualization.xsd is not present at " + xsd);
            }
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(schemaFile));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (IOException | SAXException e) {
            return false;
        }
    }

}
