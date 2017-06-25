package com.khalev.efd.robots.firefighters.drone;

import com.khalev.efd.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.annotations.Component;

/**
 * An object that represents recharge station for robots. Always has tag "Charging Station".
 *
 * @author Danylo Khalyeyev
 */
@Component
public class ChargingStation extends DEECoObject {

    @Override
    protected void setParameters(Double x, Double y, String tag, Double size) {
        super.setParameters(x, y, tag, size);
        this.tag = "Charging Station";
    }
}
