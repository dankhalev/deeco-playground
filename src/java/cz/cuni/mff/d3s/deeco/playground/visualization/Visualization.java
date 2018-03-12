package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The main class of the visualization part of the DEECo Playground project. To run the visualization, an instance of
 * this class has to be initialized with a simulation logs file in its constructor (and optionally with configuration
 * file, though it can be null), and then started by calling {@link Visualization#startVisualization()} method.
 *
 * @author Danylo Khalyeyev
 */
public final class Visualization {

    private int windowWidth;
    private int windowHeight;
    private List<VisualizationLayer> layers;

    /**
     * Initializes a new visualization from provided simulation logs file and optional configuration file. If
     * configuration file is not null, it should be valid against Visualization.xsd.
     * @param logs a path to a simulation logs file
     * @param configs a path to a valid configuration file, if null than visualization will have default configuration
     * @throws VisualizationParametersException if a logfile or configuration file is not correct
     * @throws IOException if an IOException occurs during reading any of those files
     */
    public Visualization(String logs, String configs) throws VisualizationParametersException, IOException {
        File logfile = new File(logs);
        File configFile = null;
        if (!logfile.exists()) {
            throw new VisualizationParametersException("Provided simulation logs file does not exist");
        }
        if (configs != null) {
            configFile = new File(configs);
            if (!configFile.exists()) {
                throw new VisualizationParametersException("Provided configuration file does not exist");
            }
        }
        VisualizationInitializer initializer = new VisualizationInitializer();
        this.layers = initializer.init(logfile, configFile);
        this.windowHeight = initializer.getMap().getHeight() * Visualizer.getZoom();
        this.windowWidth = initializer.getMap().getWidth() * Visualizer.getZoom();
    }

    /**
     * Creates a visualization window and runs a visualization in it.
     */
    public void startVisualization() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = windowWidth;
        config.height = windowHeight;
        new LwjglApplication(new Visualizer(layers), config);
    }

}
