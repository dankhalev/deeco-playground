package com.khalev.efd.robots.firefighters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.khalev.efd.visualization.Visualizer;
import com.khalev.efd.visualization.VisualizationLayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TemperatureLayer extends VisualizationLayer {

    private FileInputStream fileInputStream;
    private int[][] values;
    private int cycle = -1;
    private int sizeX;
    private int sizeY;
    private static boolean interpolationEnabled = true;

    public TemperatureLayer() {
    }

    @Override
    protected void render(int cycle) {
        if (this.cycle != cycle) {
            try {
                for (int i = 0; i < sizeX; i++) {
                    for (int j = 0; j < sizeY; j++) {
                        values[i][j] = fileInputStream.read();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.cycle++;
        }
        int zoom = Visualizer.getZoom();
        if (!interpolationEnabled) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    float v = (values[i][j] / 256f);
                    shapeRenderer.setColor(new Color(v, v, v, 1f));
                    shapeRenderer.rect(i * zoom, j * zoom, zoom, zoom);
                }
            }
            shapeRenderer.end();
        } else {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
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

                    shapeRenderer.setColor(new Color(v, v, v, 1f));
                    shapeRenderer.rect(i, j, 1, 1);
                }
            }
            shapeRenderer.end();
        }
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
