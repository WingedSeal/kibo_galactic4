package jp.jaxa.iss.kibo.rpc.thailand.utils;

import gov.nasa.arc.astrobee.types.Point;

import javax.annotation.Nullable;

public class Line {

    public static double distanceBetweenPoints(Point point1, Point point2) {
        return Math.sqrt(
                Math.pow(point1.getX() - point2.getX(), 2) +
                        Math.pow(point1.getY() - point2.getY(), 2)
                        +
                        Math.pow(point1.getZ() - point2.getZ(), 2)
        );
    }

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
            final double t_y = (y - point1.getY()) / b;
            final double t_z = (z - point1.getZ()) / c;
            final double x_t_y = a * t_y + point1.getX(); // x(t_y)
            final double x_t_z = a * t_z + point1.getX(); // x(t_z)
            if (0 <= t_y && t_y <= 1 && 0 <= t_z && t_z <= 1)
                return (x_t_y + x_t_z) / 2;
            if (0 <= t_y && t_y <= 1)
                return x_t_y;
            if (0 <= t_z && t_z <= 1)
                return x_t_z;
            return (point1.getX() + point2.getX()) / 2;
        } else if (x != null && y == null && z != null) {
            final double t_x = (x - point1.getX()) / a;
            final double t_z = (z - point1.getZ()) / c;
            final double y_t_x = b * t_x + point1.getY(); // y(t_x)
            final double y_t_z = b * t_z + point1.getY(); // y(t_z)
            if (0 <= t_x && t_x <= 1 && 0 <= t_z && t_z <= 1)
                return (y_t_x + y_t_z) / 2;
            if (0 <= t_x && t_x <= 1)
                return y_t_x;
            if (0 <= t_z && t_z <= 1)
                return y_t_z;
            return (point1.getX() + point2.getX()) / 2;

        } else if (x != null && y != null && z == null) {
            final double t_x = (x - point1.getX()) / a;
            final double t_y = (y - point1.getY()) / b;
            final double z_t_x = c * t_x + point1.getZ(); // z(t_x)
            final double z_t_y = c * t_y + point1.getZ(); // z(t_x)
            if (0 <= t_x && t_x <= 1 && 0 <= t_y && t_y <= 1)
                return (z_t_x + z_t_y) / 2;
            if (0 <= t_x && t_x <= 1)
                return z_t_x;
            if (0 <= t_y && t_y <= 1)
                return z_t_y;
            return (point1.getX() + point2.getX()) / 2;

        }
        throw new IllegalArgumentException("Only one of x y z has to be null");
    }
}

