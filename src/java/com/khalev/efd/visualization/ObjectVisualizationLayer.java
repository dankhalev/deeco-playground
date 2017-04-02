package com.khalev.efd.visualization;

import com.badlogic.gdx.graphics.Color;

class ObjectVisualizationLayer extends ComponentVisualizationLayer {

    private Color statusColor;

    ObjectVisualizationLayer(ComponentConfigs configs, LogfileReader reader, Color statusColor) {
        super(configs, reader);
        this.statusColor = statusColor;
    }

    @Override
    protected void render(int cycle) {
        this.drawComponents(reader.getObjects(cycle));
        this.font.getData().setScale(Visualizer.getZoom()/20f);
        this.font.setColor(statusColor);
        this.drawText(reader.getStatus(), Visualizer.sizeX/2f, Visualizer.sizeY/10f);
    }

}
