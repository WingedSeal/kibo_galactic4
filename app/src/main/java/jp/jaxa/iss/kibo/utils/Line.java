package jp.jaxa.iss.kibo.utils;

import gov.nasa.arc.astrobee.types.Point;

import javax.annotation.Nullable;

public class Line {

    /**
     * Find the last axis's value that causes approximately optimized path between 2 points.
     * One of 3 axis must be null, and it'll dictate the missing axis value the function needs to calculate.
     *
     * @param point1 point 1
     * @param point2 point 2
     * @param x      x value of interested point
     * @param y      y value of interested point
     * @param z      z value of interested point
     * @return x value that causes optimized path if x is null, y value if y is null, z value if z is null
     */
    public static double findOptimizedPosition(Point point1, Point point2, @Nullable Double x, @Nullable Double y, @Nullable Double z) {
        /*
         * x(t) = a*t + point1.x
         * y(t) = b*t + point1.y
         * z(t) = c*t + point1.z
         */
        final double a = point2.getX() - point1.getX();
        final double b = point2.getY() - point1.getY();
        final double c = point2.getZ() - point1.getZ();
        if (x == null && y != null && z != null) {
            final double t_y = y - point1.getY() / b;
            final double t_z = z - point1.getZ() / c;
            final double x_t_y = a * t_y + point1.getX(); // x(t_y)
            final double x_t_z = a * t_z + point1.getX(); // x(t_z)
            return (x_t_y + x_t_z) / 2;
        }
        else if (x != null && y == null && z != null) {
            final double t_x = x - point1.getX() / a;
            final double t_z = z - point1.getZ() / c;
            final double y_t_x = b * t_x + point1.getY(); // y(t_x)
            final double y_t_z = b * t_z + point1.getY(); // y(t_z)
            return (y_t_x + y_t_z) / 2;
        }
        else if (x != null && y != null && z == null) {
            final double t_x = x - point1.getX() / a;
            final double t_y = y - point1.getY() / b;
            final double z_t_x = c * t_x + point1.getZ(); // z(t_x)
            final double z_t_y = c * t_y + point1.getZ(); // z(t_x)
            return (z_t_x + z_t_y) / 2;
        }
        throw new IllegalArgumentException("Only one of x y z has to be null");
    }
}

