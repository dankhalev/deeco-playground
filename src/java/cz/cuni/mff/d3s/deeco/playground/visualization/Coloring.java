package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A coloring of an object specifies how this object has to be drawn. It can have two types: COLOR and TEXTURE.
 *
 * @author Danylo Khalyeyev
 */
class Coloring {

    Type type;
    Color color;
    Sprite texture;
    String texturePath;

    /**
     * Creates a coloring of COLOR type.
     * @param color a color for this coloring
     */
    Coloring(Color color) {
        this.type = Type.COLOR;
        this.color = color;
    }

    /**
     * Creates a coloring of TEXTURE type. Initializes it with a path to a texture, that should be loaded, but does
     * not load the texture.
     * @param texturePath a path to the texture
     */
    Coloring(String texturePath) {
        this.type = Type.TEXTURE;
        this.texturePath = texturePath;
    }


    enum Type {
        COLOR, TEXTURE
    }

}
