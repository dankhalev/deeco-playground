package com.khalev.efd.visualization;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

class Coloring {

    Type type;
    Color color;
    Sprite texture;

    Coloring(Color color) {
        this.type = Type.COLOR;
        this.color = color;
    }
    Coloring(Sprite texture) {
        this.type = Type.TEXTURE;
        this.texture = texture;
    }

    enum Type {
        COLOR, TEXTURE;
    }
}
