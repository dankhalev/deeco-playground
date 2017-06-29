package com.khalev.efd.robots.firefighters.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import com.khalev.efd.visualization.Visualizer;
import com.khalev.efd.visualization.VisualizationLayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Draws temperature map on the screen. Reads the map from the logs written by {@link TemperatureMap}.
 *
 * @author Danylo Khalyeyev
 */
public class TemperatureLayer extends VisualizationLayer {

    private FileInputStream fileInputStream;
    private int[][] values;
    private int cycle = -1;
    private int sizeX;
    private int sizeY;
    private Pixmap pixmap;
    private boolean isNextCycleAvailable = true;

    public TemperatureLayer() {

    }

    @Override
    protected void render(int cycle) {
        //If the cycle number was incremented, we need to read the temperature data for the next cycle.
        if (this.cycle != cycle) {
            try {
                for (int i = 0; i < sizeX; i++) {
                    for (int j = 0; j < sizeY; j++) {
                        values[i][j] = fileInputStream.read();
                    }
                }
                isNextCycleAvailable = fileInputStream.available() > 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.cycle++;
        }

        int zoom = Visualizer.getZoom();
        //Initialize pixmap at the first call of this method.
        if (pixmap == null) {
            pixmap = new Pixmap(sizeX*zoom, sizeY * zoom, Pixmap.Format.RGB888);
        }
        if (isNextCycleAvailable) {
            //Compute the value of every pixel on the screen by interpolating it from adjacent temperature values.
            for (int i = 0; i < sizeX * zoom; i++) {
                for (int j = 0; j < sizeY * zoom; j++) {
                    float v00 = values[i / zoom][j / zoom] / 256f;
                    float v01 = values[i / zoom][j / zoom + 1] / 256f;
                    float v10 = values[i / zoom + 1][j / zoom] / 256f;
                    float v11 = values[i / zoom + 1][j / zoom + 1] / 256f;
                    float offX = (i % zoom) / (float) (zoom);
                    float offY = (j % zoom) / (float) (zoom);
                    float v;
                    v = v00 * (1 - offX) * (1 - offY) + v01 * offY * (1 - offX) + v10 * offX * (1 - offY) + v11 * offX * offY;
                    pixmap.setColor(new Color(v, v, v, 1f));
                    pixmap.drawPixel(i, j);
                }
            }
        }
        //Draw the map.
        spriteBatch.begin();
        Sprite sprite = new Sprite(new Texture(pixmap));
        sprite.setFlip(false, true);
        spriteBatch.draw(sprite, 0, 0);
        spriteBatch.end();
    }

    @Override
    protected void processArg(String arg) {
        if (arg != null) {
            try {
                fileInputStream = new FileInputStream(new File(arg));
                sizeX = fileInputStream.read();
                sizeY = fileInputStream.read();
                values = new int[sizeX+1][sizeY+1];
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new RuntimeException("TemperatureLayer: input file was not specified");
        }
    }
}
