package com.khalev.efd.visualization;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;

class ComponentConfigs {

    ColoringType type;

    ArrayList<Coloring> objects = new ArrayList<>();
    HashMap<String, Coloring> tags = new HashMap<>();
    Coloring def;
    Color fontColor;

    Boolean rotationEnabled = false, displayNumbers = false, displayTags = false, circularShape = false;

    enum ColoringType {
        INDIVIDUAL, TAG;
    }
}
