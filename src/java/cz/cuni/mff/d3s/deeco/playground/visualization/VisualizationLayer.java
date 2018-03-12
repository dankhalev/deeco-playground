package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Common ancestor for all visualization layers. User can extend this class to create their own additional layers.
 *
 * @author Danylo Khalyeyev
 */
public abstract class VisualizationLayer {

    /**
     * Can be used to draw simple shapes on screen
     */
    protected ShapeRenderer shapeRenderer;

    /**
     * Can be used to draw textures on screen
     */
    protected SpriteBatch spriteBatch;

    /**
     * Can be used to draw text on screen
     */
    protected BitmapFont font;

    /**
     * This field just carries an argument provided to this layer till the moment it will be used in processArg() call
     */
    String arg;

    /**
     * Initializes this layer, gets references to ShapeRenderer and SpriteBatch, creates a default font. In
     * visualization layers that contain textures this method is overridden to load those textures. User can override
     * this method to add their own initialization code.
     * @param shapeRenderer an object that can be used to draw simple shapes on screen
     * @param spriteBatch an object that can be used to draw textures on screen
     */
    protected void initialize(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.font = new BitmapFont(Gdx.files.internal("src/resources/defaultFont.fnt"),Gdx.files.internal("src/resources/defaultFont.png"),true);
        font.getData().setScale(Visualizer.getZoom()/2.0f);
        font.setColor(Color.BLACK);
    }

    /**
     * This method is called over and over again till the end of the visualization. It should be redefined to draw all
     * the objects of this layer to the screen.
     * @param cycle Simulation cycle that is being currently visualized.
     */
    protected abstract void render(int cycle);

    /**
     * This method can be used to process an argument provided to this layer in configuration file. It is always called
     * during initialization of the visualization.
     * @param arg String argument specified in configuration file
     */
    protected void processArg(String arg) {

    }

    /**
     * Draws text on the screen. Does not change current size and color.
     * @param text Text to be drawn.
     * @param centerX X-coordinate of the center of drawn text.
     * @param centerY Y-coordinate of the center of drawn text.
     */
    protected void drawText(String text, float centerX, float centerY) {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, text);

        float fontX = centerX - glyphLayout.width/2;
        float fontY = centerY - glyphLayout.height/2;
        spriteBatch.begin();
        font.draw(spriteBatch, glyphLayout, fontX, fontY);
        spriteBatch.end();
    }

    /**
     * Loads a texture from a file
     * @param path a path to the texture file
     * @return com.badlogic.gdx.graphics.g2d.Sprite object created from loaded texture
     * @throws com.badlogic.gdx.utils.GdxRuntimeException if a given file does not exist
     */
    protected final Sprite loadTexture(String path) {
        Texture tx = new Texture(Gdx.files.absolute(path));
        Sprite sprite = new Sprite(tx);
        sprite.flip(false, true);
        return sprite;
    }

}
