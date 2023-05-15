package jp.jaxa.iss.kibo.utils;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

public class QuaternionCalculator {
    Point target1 = new Point(11.2625d, -10.58d, 5.3625d);
    Point target2 = new Point(10.513384d, -9.085172d, 3.76203d);
    Point target3 = new Point(10.6031d, -7.71007d, 3.76093);
    Point target4 = new Point(9.866984d, -6.673972d, 5.09531);
    Point target5 = new Point(11.102d, -8.0304d, 5.9076);
    Point target6 = new Point(12.023d, -8.989d, 4.8305);

    /**
     * Calculate and return Quaternion of Astrobee
     * to the target from any position
     * <p>
     * w = cos(Θ/2)
     * x = i * sin(Θ/2)
     * y = j * sin(Θ/2)
     * z = k * sin(Θ/2)
     *
     * @param pos    position of the Astrobee (pivot at center point)
     * @param target position of the target
     * @return quaternion of astrobee that laser point to target
     */
    public Quaternion calculateQuaternion(Point pos, Point target) {
        //vector between point pos to target
        double x1 = target.getX() - pos.getX();
        double y1 = target.getY() - pos.getY();
        double z1 = target.getZ() - pos.getZ();

        //norm of <x1,y1,z1>
        double normvec1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2));

        //norm of vector from pivot of astrobee to laser pos
        double normlaser = Math.sqrt(Math.pow(0.1302d, 2) + Math.pow(0.0572d, 2) + Math.pow(0.1111d, 2));

        //vector of laser to desire pos <extrax,0,0>
        double extrax = Math.sqrt(Math.pow(normvec1, 2) + Math.pow(normlaser, 2) - 2 * normlaser * normvec1 * Math.cos(Math.acos(0.1302d / normlaser) / 2));

        //vector between pivot of astrobee to targer pos
        double x2 = 0.1302d + extrax;
        double y2 = 0.0572d;
        double z2 = -0.1111d;

        //norm of <x2,y2,z2>
        double normvec2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2) + Math.pow(z2, 2));

        //make 2 vector a unit vector
        x1 /= normvec1;
        y1 /= normvec1;
        z1 /= normvec1;
        x2 /= normvec2;
        y2 /= normvec2;
        z2 /= normvec2;

        //find Θ from dot product
        double theta = Math.acos(x1 * x2 + y1 * y2 + z1 * z2);

        //find axis of rotation from cross product
        double i = y2 * z1 - y1 * z2;
        double j = -(x2 * z1 - x1 * z2);
        double k = x2 * y1 - x1 * y2;

        //return quaternion
        return new Quaternion((float) (i * Math.sin(theta / 2)), (float) (j * Math.sin(theta / 2)), (float) (k * Math.sin(theta / 2)), (float) Math.cos(theta / 2));
    }
}