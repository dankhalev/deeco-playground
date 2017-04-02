package com.khalev.efd.visualization;

class ComponentParameters {
    float x, y, angle, size;
    String tag = "";

    ComponentParameters(double size) {
        this.size = (float) size;
    }

    ComponentParameters() {
        this.angle = 0f;
    }
}
