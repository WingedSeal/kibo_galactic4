package jp.jaxa.iss.kibo.utils;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

public class QuaternionCalculator {
    /**
     * Calculate and return Quaternion of Astrobee
     * to the target from any position
     * <pre>
     * {@code w = cos(Θ/2)}
     * {@code x = i * sin(Θ/2)}
     * {@code y = j * sin(Θ/2)}
     * {@code z = k * sin(Θ/2)}
     * </pre>
     *
     * @param pos    position of the Astrobee (pivot at center point)
     * @param target position of the target
     * @return quaternion of astrobee that laser point to target
     */
    static public Quaternion calculateQuaternion(Point pos, Point target) {
        //vector between point pos to target
        double x1 = target.getX() - pos.getX();
        double y1 = target.getY() - pos.getY();
        double z1 = target.getZ() - pos.getZ();

        //norm of <x1,y1,z1>
        double normVec1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2));

        //norm of vector from pivot of astrobee to laser pos
        double normLaser = Math.sqrt(Math.pow(0.1302, 2) + Math.pow(0.0572, 2) + Math.pow(0.1111, 2));

        //vector of laser to desire pos <extraX,0,0>
        double extraX = Math.sqrt(Math.pow(normVec1, 2) + Math.pow(normLaser, 2) - 2 * normLaser * normVec1 * Math.cos(Math.acos(0.1302d / normLaser) / 2));

        //vector between pivot of astrobee to target pos
        double x2 = 0.1302d + extraX;
        double y2 = 0.0572d;
        double z2 = -0.1111d;

        //norm of <x2,y2,z2>
        double normVec2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2) + Math.pow(z2, 2));

        //make 2 vector a unit vector
        x1 /= normVec1;
        y1 /= normVec1;
        z1 /= normVec1;
        x2 /= normVec2;
        y2 /= normVec2;
        z2 /= normVec2;

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