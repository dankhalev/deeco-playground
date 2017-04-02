package com.khalev.efd.simulation;

/**
 * Represents a single line segment on {@link EnvironmentMap}. Each segment can be either vertical or horizontal, has
 * start and end
 */
public class Line {

    double start;
    double end;
    boolean isVertical;
    int horizon;

    /**
     * @return for vertical line  its smaller Y-coordinate, for horizontal its smaller X-coordinate.
     */
    public double getStart() {
        return start;
    }

    /**
     * @return for vertical line  its bigger Y-coordinate, for horizontal its bigger X-coordinate.
     */
    public double getEnd() {
        return end;
    }

    /**
     * @return true if line segment is vertical, false otherwise.
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * @return for vertical line returns its X-coordinate, for horizontal - its Y-coordinate.
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
