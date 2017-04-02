package com.khalev.efd.robots.firefighters;

import com.khalev.efd.simulation.Coordinates;

public class FirefighterOrder {

    Type type;
    Coordinates target;

    public FirefighterOrder(Type type, Coordinates target) {
        this.type = type;
        this.target = target;
    }

    enum Type {
        FOLLOW, RECHARGE, FIGHT, STAY
    }
}
