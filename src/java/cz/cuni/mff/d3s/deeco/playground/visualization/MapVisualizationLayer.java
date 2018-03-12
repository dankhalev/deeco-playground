package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Draws a map of physical obstacles on the screen.
 *
 * @author Danylo Khalyeyev
 */
class MapVisualizationLayer extends VisualizationLayer {

    private final Coloring coloring;
    private final EnvironmentMap map;

    MapVisualizationLayer(Coloring mapColoring, EnvironmentMap map) {
        this.map = map;
        this.coloring = mapColoring;
    }

    @Override
    protected void initialize(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        super.initialize(shapeRenderer, spriteBatch);
        if (coloring.type == Coloring.Type.TEXTURE) {
            coloring.texture = loadTexture(coloring.texturePath);
        }
    }

    @Override
    protected void render(int cycle) {
        int ZOOM = Visualizer.getZoom();
        if (coloring.type == Coloring.Type.COLOR) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(coloring.color);
            for (int k = 0; k < map.getWidth(); k++) {
                for (int j = 0; j < map.getHeight(); j++) {
                    if (map.isOccupied(k, j)) {
                        shapeRenderer.rect(k*ZOOM, j*ZOOM, ZOOM, ZOOM);
                    }
                }
            }
            shapeRenderer.end();
        } else {
            spriteBatch.begin();
            for (int k = 0; k < map.getWidth(); k++) {
                for (int j = 0; j < map.getHeight(); j++) {
                    if (map.isOccupied(k, j)) {
                        spriteBatch.draw(coloring.texture, k*ZOOM, j*ZOOM, ZOOM, ZOOM);
                    }
                }
            }
            spriteBatch.end();
        }
    }

}
