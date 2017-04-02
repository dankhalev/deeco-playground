package com.khalev.efd.visualization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * This class can be extended to create additional visualization layer.
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
     * Initializes this layer, gets references to ShapeRenderer and SpriteBatch.
     */
    protected void initialize(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.font = new BitmapFont(Gdx.files.internal("src\\resources\\defaultFont.fnt"),Gdx.files.internal("src\\resources\\defaultFont.png"),true);
        font.getData().setScale(Visualizer.getZoom()/2.0f);
        font.setColor(Color.BLACK);
    }

    /**
     * This method is called over and over again till the end of visualization. It should be redefined to draw all the
     * objects of this layer on the screen.
     * @param cycle Simulation cycle that is being currently visualized.
     */
    protected abstract void render(int cycle);

    /**
     * This method can be used to process an argument provided in simulation parameters. It is always called during
     * initialization of the simulation.
     * @param arg String argument specified in simulation parameters XML file
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

}
