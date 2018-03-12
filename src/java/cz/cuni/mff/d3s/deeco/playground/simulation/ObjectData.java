package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Represents data that {@link Coordinator} collects from each object through {@link ObjectEnsemble} and transmits to
 * the {@link Environment}.
 *
 * @author Danylo Khalyeyev
 */
class ObjectData {

    Coordinates coordinates;
    Double size;
    String tag;

    ObjectData(Coordinates coordinates, Double size, String tag) {
        this.coordinates = coordinates;
        this.size = size;
        this.tag = tag;
    }

}
