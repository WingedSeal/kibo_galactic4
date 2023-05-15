package jp.jaxa.iss.kibo.utils;

import gov.nasa.arc.astrobee.types.Point;

import javax.annotation.Nullable;

public class Line {

    /**
     * Find the last axis's value that causes optimized path between 2 points.
     * One of 3 axis must be null, and it'll dictate the missing axis value the function needs to calculate.
     *
     * @param point1 point 1
     * @param point2 point 2
     * @param x x value of interested point
     * @param y y value of interested point
     * @param z z value of interested point
     * @return x value that causes optimized path if x is null, y value if y is null, z value if z is null
     */
    private static double findOptimizedPosition(Point point1, Point point2, @Nullable Double x, @Nullable Double y, @Nullable Double z) {
        return 0;
    }
}

