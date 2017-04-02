package com.khalev.efd.simulation;

class Sensor<T> {

    private T input;
    private Class<T> cls;

    public Sensor(Class<T> cls) {
        this.cls = cls;
    }

    void receiveInput(Object input) {
        if (input != null && (input.getClass().isAssignableFrom(this.cls))) {
            @SuppressWarnings("unchecked")
            T inp = (T) input;
            this.input = inp;
        } else {
            this.input = null;
        }
    }

    T getInput() {
        return this.input;
    }

}
