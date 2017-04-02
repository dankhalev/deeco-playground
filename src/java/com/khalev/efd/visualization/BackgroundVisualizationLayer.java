package com.khalev.efd.visualization;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

class BackgroundVisualizationLayer extends VisualizationLayer {

    private Coloring coloring;

    BackgroundVisualizationLayer(Coloring background) {
        this.coloring = background;
    }

    @Override
    protected void render(int cycle) {
        if (coloring.type == Coloring.Type.COLOR) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(coloring.color);
            shapeRenderer.rect(0,0,Visualizer.sizeX,Visualizer.sizeY);
            shapeRenderer.end();
        } else {
            spriteBatch.begin();
            spriteBatch.draw(coloring.texture, 0,0, Visualizer.sizeX, Visualizer.sizeY);
            spriteBatch.end();
        }
    }
}
