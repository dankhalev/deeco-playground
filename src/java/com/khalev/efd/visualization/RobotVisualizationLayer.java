package com.khalev.efd.visualization;

class RobotVisualizationLayer extends ComponentVisualizationLayer {


    RobotVisualizationLayer(ComponentConfigs configs, LogfileReader reader) {
        super(configs, reader);
    }

    @Override
    protected void render(int cycle) {
        this.drawComponents(reader.getRobots(cycle));
    }

}
