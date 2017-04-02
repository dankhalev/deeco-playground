package com.khalev.efd.visualization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;

import javax.imageio.IIOException;
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
import java.util.Objects;

class VisualizationInitializer {

    private final String XMLSchema = "src\\resources\\Visualization.xsd";
    private ArrayList<String> robotClasses = new ArrayList<>();
    private ArrayList<String> objectClasses = new ArrayList<>();
    private Color statusColor = Color.BLACK;
    private ComponentConfigs robotConfigs;
    private ComponentConfigs objectConfigs;
    private Coloring mapColoring;
    private Coloring background;
    private LogfileReader logReader;
    private ArrayList<VisualizationLayer> layers = new ArrayList<>();
    private boolean isRobotLayerInitialized, isObjectLayerInitialized, isMapLayerInitialized;
    private EnvironmentMap map;

    ArrayList<VisualizationLayer> init(File logfile, File config, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) throws IOException, VisualizationParametersException {
        Document doc = null;
        if (config != null) {
            if (this.validateAgainstXSD(config, XMLSchema)) {
                doc = this.initializeXMLAndSetZoom(config);
            } else {
                throw new VisualizationParametersException("Provided config file is not correct");
            }
        }

        BufferedReader reader = new BufferedReader(new FileReader(logfile));
        String bitmapPath = reader.readLine();
        if (!Objects.equals(bitmapPath, "null")) {
            try {
                File bitmap = new File(bitmapPath);
                map = (new BitmapProcessor()).readBitmap(bitmap);
            } catch (IIOException ex) {
                throw new RuntimeException("Bitmap file does not exist: " + bitmapPath);
            }
        } else {
            try {
                int sizeX = Integer.parseInt(reader.readLine());
                int sizeY = Integer.parseInt(reader.readLine());
                map = new EnvironmentMap(sizeX, sizeY);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Simulation logs file is not correct");
            }
        }
        Visualizer.sizeX = map.getWidth()*Visualizer.getZoom();
        Visualizer.sizeY = map.getHeight()*Visualizer.getZoom();
        ArrayList<ComponentParameters> robots = new ArrayList<>();
        ArrayList<ComponentParameters> objects = new ArrayList<>();
        String s;
        s = reader.readLine();
        if (!"---".equals(s)) {
            throw new RuntimeException("Simulation logs file is not correct");
        }
        s = reader.readLine();
        while (!Objects.equals(s, "---")) {
            if (s == null) {
                throw new RuntimeException("Simulation logs file is not correct");
            }
            String[] ss = s.split(",");
            robotClasses.add(ss[0]);
            robots.add(new ComponentParameters(Double.parseDouble(ss[1])* Visualizer.getZoom()));
            s = reader.readLine();
        }
        s = reader.readLine();
        while (!Objects.equals(s, "---")) {
            if (s == null) {
                throw new RuntimeException("Simulation logs file is not correct");
            }
            objectClasses.add(s);
            objects.add(new ComponentParameters());
            s = reader.readLine();
        }


        mapColoring = defaultMapColoring();
        robotConfigs = defaultRobotConfigs(robotClasses.size());
        objectConfigs = defaultObjectConfigs(objectClasses.size());


        logReader = new LogfileReader(reader, robots, objects);
        if (doc != null) {
            this.parseConfigsAndInitializeLayers(doc);
        }
        if (!isMapLayerInitialized) {
            MapVisualizationLayer mapLayer = new MapVisualizationLayer(mapColoring, map);
            layers.add(mapLayer);
        }
        if (!isRobotLayerInitialized) {
            RobotVisualizationLayer robotLayer = new RobotVisualizationLayer(robotConfigs, logReader);
            layers.add(robotLayer);
        }
        if(!isObjectLayerInitialized) {
            ObjectVisualizationLayer objectLayer = new ObjectVisualizationLayer(objectConfigs, logReader, statusColor);
            layers.add(objectLayer);
        }
        objectConfigs.rotationEnabled = false;

        for (VisualizationLayer layer : layers) {
            layer.initialize(shapeRenderer, spriteBatch);
        }

        roundRobotTextures();
        return layers;
    }

    private Document initializeXMLAndSetZoom(File config) {
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
            throw new RuntimeException(e);
        }
    }

    private void roundRobotTextures() {
        roundTexture(robotConfigs.def);
        robotConfigs.objects.forEach(this::roundTexture);
        robotConfigs.tags.values().forEach(this::roundTexture);
        if (objectConfigs.circularShape) {
            roundTexture(objectConfigs.def);
            objectConfigs.objects.forEach(this::roundTexture);
            objectConfigs.tags.values().forEach(this::roundTexture);
        }
    }

    private void roundTexture(Coloring coloring) {
        if (coloring.type == Coloring.Type.TEXTURE) {
            coloring.texture = roundTexture(coloring.texture);
        }
    }

    private static Sprite roundTexture(Sprite texture) {
        if (!texture.getTexture().getTextureData().isPrepared())
            texture.getTexture().getTextureData().prepare();
        Pixmap pixmap = texture.getTexture().getTextureData().consumePixmap();
        if (pixmap.getHeight() != pixmap.getWidth()) {
            int size;
            if (pixmap.getHeight() > pixmap.getWidth()) {
                size = pixmap.getWidth();
            } else {
                size = pixmap.getHeight();
            }
            Pixmap partTexture = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            partTexture.drawPixmap(pixmap, 0,0,0,0, size,size);
            pixmap = partTexture;
        }
        Pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(Color.CLEAR);
        float r = pixmap.getHeight() / 2f;
        for (int i = 0; i < pixmap.getHeight(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                double distance = Math.pow(r - i, 2) +  Math.pow(r - j, 2);
                if (distance > Math.pow(r, 2)) {
                    pixmap.drawPixel(i,j);
                }
            }
        }

        return new Sprite(new Texture(pixmap));
    }

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

    private Coloring defaultMapColoring() {
        return new Coloring(Color.BLACK);
    }

    private void parseConfigsAndInitializeLayers(Document doc) throws IOException, VisualizationParametersException {
        Node cpsAttribute = doc.getDocumentElement().getAttributes().getNamedItem("maxCPS");
        if (cpsAttribute != null) {
            Visualizer.maxCPS = Integer.parseInt(cpsAttribute.getTextContent());
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

        NodeList toplevel = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < toplevel.getLength(); i++) {
            Node e = toplevel.item(i);

            String s = e.getNodeName();
            Coloring coloring;
            switch (s) {
                case "robots":
                    if (!isRobotLayerInitialized) {
                        robotConfigs = parseConfigs(e, robotConfigs, robotClasses);
                        layers.add(new RobotVisualizationLayer(robotConfigs, logReader));
                        isRobotLayerInitialized = true;
                    } else {
                        throw new VisualizationParametersException("You cannot have 2 robot layers");
                    }
                    break;
                case "objects":
                    if (!isObjectLayerInitialized) {
                        objectConfigs = parseConfigs(e, objectConfigs, objectClasses);
                        layers.add(new ObjectVisualizationLayer(objectConfigs, logReader, statusColor));
                        isObjectLayerInitialized = true;
                    } else {
                        throw new VisualizationParametersException("You cannot have 2 object layers");
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
                        throw new VisualizationParametersException("You cannot have 2 map layers");
                    }
                    break;
                case "background":
                    coloring = getColoringFromNode(e);
                    if (coloring != null) {
                        background = coloring;
                    }
                    layers.add(new BackgroundVisualizationLayer(background));
                    break;
                case "additional":
                    VisualizationLayer layer = parseCustomLayer(e);
                    layers.add(layer);
                    break;
            }
        }
    }

    private ComponentConfigs parseConfigs(Node e, ComponentConfigs configs, ArrayList<String> classes)
            throws VisualizationParametersException {
        Node attribute = e.getAttributes().getNamedItem("displayNumbers");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.displayNumbers = true;
        }
        attribute = e.getAttributes().getNamedItem("displayTags");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.displayTags = true;
        }
        attribute = e.getAttributes().getNamedItem("enableRotation");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.rotationEnabled = true;
        }
        attribute = e.getAttributes().getNamedItem("circularShape");
        if (attribute != null && attribute.getTextContent().equals("true")) {
            configs.circularShape = true;
        }
        attribute = e.getAttributes().getNamedItem("fontColor");
        if (attribute != null) {
            Color color = parseColor(attribute.getTextContent());
            if (color != null) {
                configs.fontColor = color;
            } else {
                wrongColor();
            }
        }
        Element element = (Element)e;
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

    private Coloring getColoringFromNode(Node e) throws VisualizationParametersException {
        Coloring coloring = null;
        Node colorNode = e.getAttributes().getNamedItem("color");
        Node textureNode = e.getAttributes().getNamedItem("texture");
        if (textureNode != null && colorNode != null) {
            throw new VisualizationParametersException("One of the elements in visualization parameters file has both " +
                    "color and texture attributes");
        }
        if (textureNode != null) {
            Texture tx = new Texture(Gdx.files.absolute(textureNode.getTextContent()));
            Sprite sprite = new Sprite(tx);
            sprite.flip(false, true);
            coloring = new Coloring(sprite);
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
        throw new VisualizationParametersException("Wrong color!");
    }

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

    private VisualizationLayer parseCustomLayer(Node e) throws VisualizationParametersException {
        String classname = e.getAttributes().getNamedItem("class").getTextContent();
        Node argNode = e.getAttributes().getNamedItem("arg");
        String arg = argNode != null ? argNode.getTextContent() : null;
        try {
            Class cls = Class.forName(classname);
            if (!VisualizationLayer.class.isAssignableFrom(cls)) {
                throw new VisualizationParametersException("Class " + classname + " does not extend class SensoryInputsProcessor.");
            }
            VisualizationLayer layer = (VisualizationLayer) cls.newInstance();
            layer.processArg(arg);
            return layer;
        } catch (ClassNotFoundException ex) {
            throw new VisualizationParametersException("Class " + classname + " does not exist.");
        } catch (InstantiationException | IllegalAccessException  ex) {
            throw new VisualizationParametersException("Class " + classname + " is not valid class.");
        }
    }

    private boolean validateAgainstXSD(File xml, String xsd) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new File(xsd)));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
}
