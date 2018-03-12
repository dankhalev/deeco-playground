package cz.cuni.mff.d3s.deeco.playground.simulation;

/**
 * Represents a single line segment on {@link EnvironmentMap}. Each segment can be either vertical or horizontal, has a
 * start, an end, and a horizon on which it lies.
 *
 * @author Danylo Khalyeyev
 */
public final class Line {

    final double start;
    final double end;
    final boolean isVertical;
    final int horizon;

    /**
     * For a vertical line returns its smaller Y-coordinate, for a horizontal line its smaller X-coordinate.
     * @return for vertical line its smaller Y-coordinate, for horizontal its smaller X-coordinate.
     */
    public double getStart() {
        return start;
    }

    /**
     * For a vertical line returns its bigger Y-coordinate, for a horizontal line its bigger X-coordinate.
     * @return for vertical line its bigger Y-coordinate, for horizontal its bigger X-coordinate.
     */
    public double getEnd() {
        return end;
    }

    /**
     * Returns true if line segment is vertical, false otherwise.
     * @return true if line segment is vertical, false otherwise.
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * For a vertical line returns its X-coordinate, for a horizontal - its Y-coordinate.
     * @return for vertical line its X-coordinate, for horizontal - its Y-coordinate.
     */
    public int getHorizon() {
        return horizon;
    }

    Line(double start, double end, int horizon, boolean isVertical) {
        this.start = start;
        this.end = end;
        this.isVertical = isVertical;
        this.horizon = horizon;
    }

}
