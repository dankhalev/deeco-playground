package cz.cuni.mff.d3s.deeco.playground.visualization;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains configurations for visualization of a component layer (either object layer or robot layer). Components can be
 * colored in a one of two ways: individually or based on tag. Individual coloring means that each individual component
 * has a particular coloring assigned to it, and it does not change throughout the visualization. Tag-based coloring
 * means that a coloring of the component is determined by its current tag, so it can be changed in the process of
 * visualization if component's tag changes. In both cases there is also a default coloring which is applied if there is
 * no coloring assigned to a particular individual or tag.
 *
 * @author Danylo Khalyeyev
 */
class ComponentConfigs {

    ColoringType type;
    List<Coloring> objects = new ArrayList<>();
    Map<String, Coloring> tags = new HashMap<>();
    Coloring def;
    Color fontColor;
    Boolean rotationEnabled = true;
    Boolean displayNumbers = false;
    Boolean displayTags = false;
    Boolean circularShape = false;

    enum ColoringType {
        INDIVIDUAL, TAG
    }

}
