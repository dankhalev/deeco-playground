package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Fills the visualization window with a specified color or draws a specified texture stretching it to the size of the
 * window.
 *
 * @author Danylo Khalyeyev
 */
class BackgroundVisualizationLayer extends VisualizationLayer {

    private Coloring coloring;

    BackgroundVisualizationLayer(Coloring background) {
        this.coloring = background;
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
