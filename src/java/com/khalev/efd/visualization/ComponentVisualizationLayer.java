package com.khalev.efd.visualization;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;

abstract class ComponentVisualizationLayer extends VisualizationLayer {

    ComponentConfigs configs;
    LogfileReader reader;


    ComponentVisualizationLayer(ComponentConfigs configs, LogfileReader reader) {
        this.reader = reader;
        this.configs = configs;
    }

    void drawComponents(ArrayList<ComponentParameters> parameters) {
        if (configs.type == ComponentConfigs.ColoringType.INDIVIDUAL) {
            for (int i = 0; i < parameters.size(); i++) {
                drawComponent(parameters.get(i), configs.objects.get(i), i);
            }
        } else {
            for (int i = 0; i < parameters.size(); i++) {
                ComponentParameters robot = parameters.get(i);
                Coloring coloring = configs.tags.getOrDefault(robot.tag, configs.def);
                drawComponent(robot, coloring, i);
            }
        }
    }

    private void drawComponent(ComponentParameters robot, Coloring coloring, int i) {
        if (coloring.type == Coloring.Type.COLOR) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(coloring.color);
            if (configs.circularShape) {
                shapeRenderer.circle(robot.x, robot.y, robot.size);
            } else {
                shapeRenderer.rect(robot.x - robot.size, robot.y - robot.size, 2*robot.size, 2*robot.size);
            }
            shapeRenderer.end();
        } else {
            spriteBatch.begin();
            Sprite sprite = new Sprite(coloring.texture);
            sprite.flip(false, true);
            sprite.setCenter(robot.x, robot.y);
            sprite.setScale(2 * robot.size / sprite.getWidth(), 2 * robot.size / sprite.getHeight());
            if (configs.rotationEnabled) {
                sprite.setRotation(180-robot.angle);
            }
            sprite.draw(spriteBatch);
            spriteBatch.end();
        }
        if (configs.displayTags) {
            font.getData().setScale(Visualizer.getZoom()/40f);
            font.setColor(configs.fontColor);
            drawText(robot.tag, robot.x, robot.y);
        } else if (configs.displayNumbers) {
            font.getData().setScale(Visualizer.getZoom()/20f);
            font.setColor(configs.fontColor);
            drawText(String.valueOf(i), robot.x, robot.y);
        }
    }
}
