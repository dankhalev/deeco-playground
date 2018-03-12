package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.List;

/**
 * A common ancestor of {@link RobotVisualizationLayer} and {@link ObjectVisualizationLayer}. Defines methods that draw
 * components according to their coloring configurations and that load and manipulate their textures in initialization.
 *
 * @author Danylo Khalyeyev
 */
abstract class ComponentVisualizationLayer extends VisualizationLayer {

    ComponentConfigs configs;
    LogfileReader reader;

    ComponentVisualizationLayer(ComponentConfigs configs, LogfileReader reader) {
        this.reader = reader;
        this.configs = configs;
    }

    @Override
    protected void initialize(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        super.initialize(shapeRenderer, spriteBatch);
        loadAndRoundComponentTextures();
    }

    /**
     * Draws all components based on their drawing configuration.
     * @param parameters list of component parameters
     */
    void drawComponents(List<ComponentParameters> parameters) {
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

    /**
     * Draws a single component and its number or tag if that option is turned on.
     * @param robot current parameters of a component that is being visualized
     * @param coloring a coloring that should be used to draw this component
     * @param i a number of this component
     */
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

    /**
     * Loads the textures for all colorings in {@link ComponentVisualizationLayer#configs}. If textures should have
     * circular shape, rounds them.
     */
    private void loadAndRoundComponentTextures() {
        loadAndRoundTexture(configs.def);
        configs.objects.forEach(this::loadAndRoundTexture);
        configs.tags.values().forEach(this::loadAndRoundTexture);
    }

    /**
     * If this coloring has a TEXTURE type, loads its texture. If circularShape parameter is turned on, makes a texture
     * circular.
     * @param coloring a coloring to load and round
     */
    private void loadAndRoundTexture(Coloring coloring) {
        if (coloring.type == Coloring.Type.TEXTURE) {
            coloring.texture = loadTexture(coloring.texturePath);
            if (configs.circularShape) {
                coloring.texture = roundTexture(coloring.texture);
            }
        }
    }

    /**
     * Makes a texture circular by setting transparent color to certain bits of this texture.
     * @param texture a texture to round
     * @return rounded texture
     */
    private static Sprite roundTexture(Sprite texture) {
        if (!texture.getTexture().getTextureData().isPrepared())
            texture.getTexture().getTextureData().prepare();
        Pixmap pixmap = texture.getTexture().getTextureData().consumePixmap();
        if (pixmap.getHeight() != pixmap.getWidth()) {
            int size;
            if (pixmap.getHeight() > pixmap.getWidth()) {
                size = pixmap.getWidth();
            } else {
                size = pixmap.getHeight();
            }
            Pixmap partTexture = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            partTexture.drawPixmap(pixmap, 0,0,0,0, size,size);
            pixmap = partTexture;
        }
        Pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(Color.CLEAR);
        float r = pixmap.getHeight() / 2f;
        for (int i = 0; i < pixmap.getHeight(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                double distance = Math.pow(r - i, 2) +  Math.pow(r - j, 2);
                if (distance > Math.pow(r, 2)) {
                    pixmap.drawPixel(i,j);
                }
            }
        }

        return new Sprite(new Texture(pixmap));
    }
}
