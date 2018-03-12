package cz.cuni.mff.d3s.deeco.playground.visualization;

/**
 * Draws robots.
 * @see ComponentVisualizationLayer
 *
 * @author Danylo Khalyeyev
 */
class RobotVisualizationLayer extends ComponentVisualizationLayer {

    RobotVisualizationLayer(ComponentConfigs configs, LogfileReader reader) {
        super(configs, reader);
    }

    @Override
    protected void render(int cycle) {
        this.drawComponents(reader.getRobots(cycle));
    }

}
