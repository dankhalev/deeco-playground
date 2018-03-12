package cz.cuni.mff.d3s.deeco.playground.examples.firefighters.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import cz.cuni.mff.d3s.deeco.playground.visualization.Visualizer;
import cz.cuni.mff.d3s.deeco.playground.visualization.VisualizationLayer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Draws temperature map on the screen. Reads the map from the logs written by {@link TemperatureMap}.
 *
 * @author Danylo Khalyeyev
 */
public class TemperatureLayer extends VisualizationLayer {

    private int[][] values;
    private List<int[][]> map = new ArrayList<>();
    private int sizeX;
    private int sizeY;
    private Pixmap pixmap;
    private Sprite[] sprites;

    public TemperatureLayer() {

    }

    @Override
    protected void render(int cycle) {
        //Get the temperature data for this cycle.
        if (cycle >= map.size()) {
            cycle = map.size()-1;
        }
        values = map.get(cycle);
        int zoom = Visualizer.getZoom();

        //If this cycle was not visualized previously, compute a pixmap for it,
        // and store the sprite in cache
        if (cycle < sprites.length && sprites[cycle] == null) {
            pixmap = new Pixmap(sizeX*zoom, sizeY * zoom, Pixmap.Format.RGB888);
            //Compute the value of every pixel on the screen by
            //interpolating it from adjacent temperature values.
            for (int i = 0; i < sizeX * zoom; i++) {
                for (int j = 0; j < sizeY * zoom; j++) {
                    float v00 = values[i / zoom][j / zoom] / 256f;
                    float v01 = values[i / zoom][j / zoom + 1] / 256f;
                    float v10 = values[i / zoom + 1][j / zoom] / 256f;
                    float v11 = values[i / zoom + 1][j / zoom + 1] / 256f;
                    float offX = (i % zoom) / (float) (zoom);
                    float offY = (j % zoom) / (float) (zoom);
                    float v;
                    v = v00 * (1 - offX) * (1 - offY) +
                            v01 * offY * (1 - offX) +
                            v10 * offX * (1 - offY) +
                            v11 * offX * offY;
                    pixmap.setColor(new Color(v, v, v, 1f));
                    pixmap.drawPixel(i, j);
                }
            }
            Sprite sprite = new Sprite(new Texture(pixmap));
            sprite.setFlip(false, true);
            sprites[cycle] = sprite;
        }

        //Draw the map.
        spriteBatch.begin();
        spriteBatch.draw(sprites[cycle], 0, 0);
        spriteBatch.end();
    }

    @Override
    protected void processArg(String arg) {
        //Read the map of temperatures from the file.
        if (arg != null) {
            try {
                BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(new File(arg)));
                sizeX = fileInputStream.read();
                sizeY = fileInputStream.read();
                values = new int[sizeX+1][sizeY+1];


                boolean isNextCycleAvailable = fileInputStream.available() > 0;
                while (isNextCycleAvailable) {
                    int[][] cValues = new int[sizeX+1][sizeY+1];
                    for (int i = 0; i < sizeX; i++) {
                        for (int j = 0; j < sizeY; j++) {
                            cValues[i][j] = fileInputStream.read();
                        }
                    }
                    map.add(cValues);
                    isNextCycleAvailable = fileInputStream.available() > 0;
                }
                this.sprites = new Sprite[map.size()];
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("TemperatureLayer: input file was not specified");
        }
    }
}
