package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.DEECoObject;
import cz.cuni.mff.d3s.deeco.annotations.Component;

/**
 * Power station for Team Blue. If a robot is near this station, it gets recharged.
 *
 * @author Danylo Khalyeyev
 */
@Component
public class PowerStation extends DEECoObject{

    public String teamID = "T2";

}
