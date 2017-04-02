package com.khalev.efd.robots.competition;

import com.khalev.efd.simulation.Coordinates;

public class RobotOrder {

    Type type;
    Coordinates target;

    public RobotOrder(Type type, Coordinates target) {
        this.type = type;
        this.target = target;
    }

    enum Type {
        FOLLOW, RECHARGE, STAY
    }
}
