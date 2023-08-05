package jp.jaxa.iss.kibo.rpc.thailand.utils;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

public class QuaternionCalculator {
    public static double extraX;
    public static final Quaternion UPWARD = new Quaternion(0f, 0.707f, 0f, 0.707f);
    public static final Quaternion DOWNWARD = new Quaternion(0f, -0.707f, 0f, 0.707f);

    /**
     * Calculate and return Quaternion of Astrobee
     * to the target from any position
     * <p>
     * <a href="https://math.stackexchange.com/questions/4520571/how-to-get-a-rotation-quaternion-from-two-vectors">...</a>
     *
     * <pre>
     * {@code w = cos(Θ/2)}
     * {@code x = i * sin(Θ/2)}
     * {@code y = j * sin(Θ/2)}
     * {@code z = k * sin(Θ/2)}
     * </pre>
     *
     * @param pos    coordinate(x,y,z) of Astrobee of the Astrobee (pivot at center point)
     * @param target coordinate(x,y,z) of Astrobee of the target
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

        // value of vector of laser shoot form <xi,0j,0k>
        double _cos = Math.cos(Math.acos(0.1302d / normLaser));
        extraX = calculateDistanceBetweenLaserToPoint(normLaser, normVec1, _cos);

        //vector between pivot of Astrobee to final laser pos
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

        //norm of Axis
        double normAxis = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2));


        //unit vector of Axis
        i /= normAxis;
        j /= normAxis;
        k /= normAxis;

        //return quaternion
        return new Quaternion((float) (i * Math.sin(theta / 2)), (float) (j * Math.sin(theta / 2)), (float) (k * Math.sin(theta / 2)), (float) Math.cos(theta / 2));
    }


    /**
     * Calculate x distance from laser position to desire position from <xi,0,0>
     * <p>
     * R3^2 = R1^2 + R2^2 + 2R1R2cos(theta)
     *
     * @param normR1 norm of vector R1 (normLaser)
     * @param normR3 norm of vector R3 (normVector1)
     * @param cos    cos of theta between vector R1 and R2
     * @return value of x type double
     */
    public static double calculateDistanceBetweenLaserToPoint(double normR1, double normR3, double cos) {
        return (-2 * normR1 * cos + Math.sqrt(Math.pow(2 * normR1 * cos, 2) - 4 * (Math.pow(normR1, 2) - Math.pow(normR3, 2)))) / 2;
    }

    /**
     * Calculate quaternion from NavCam vector to the target point
     *
     * @param pos    coordinate(x,y,z) of Astrobee of the Astrobee (pivot at center point)
     * @param target coordinate(x,y,z) of Astrobee of the target
     * @return quaternion to where NavCam point to target
     */
    public static Quaternion calculateNavCamQuaternion(Point pos, Point target) {
        //vector between point pos to target
        double x1 = target.getX() - pos.getX();
        double y1 = target.getY() - pos.getY();
        double z1 = target.getZ() - pos.getZ();

        //norm of <x1,y1,z1>
        double normVec1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2));

        //norm of vector from pivot of astrobee to laser pos
        double normNavCam = Math.sqrt(Math.pow(0.1177d, 2) + Math.pow(0.0422d, 2) + Math.pow(0.0826d, 2));

        // value of vector of laser shoot form <xi,0j,0k>
        double _cos = Math.cos(Math.acos(0.1177d / normNavCam));
        double extraX = calculateDistanceBetweenLaserToPoint(normNavCam, normVec1, _cos);

        //vector between pivot of Astrobee to final laser pos
        double x2 = 0.1177d + extraX;
        double y2 = -0.0422d;
        double z2 = -0.0826d;

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

        //norm of Axis
        double normAxis = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2));


        //unit vector of Axis
        i /= normAxis;
        j /= normAxis;
        k /= normAxis;

        //return quaternion
        return new Quaternion((float) (i * Math.sin(theta / 2)), (float) (j * Math.sin(theta / 2)), (float) (k * Math.sin(theta / 2)), (float) Math.cos(theta / 2));

    }

    /**
     * Calculate quaternion from DockCam vector to the target point
     *
     * @param pos    coordinate(x,y,z) of Astrobee of the Astrobee (pivot at center point)
     * @param target coordinate(x,y,z) of Astrobee of the target
     * @return quaternion to where DockCam point to target
     */
    public static Quaternion calculateDockCamQuaternion(Point pos, Point target) {
        //vector between point pos to target
        double x1 = target.getX() - pos.getX();
        double y1 = target.getY() - pos.getY();
        double z1 = target.getZ() - pos.getZ();

        //norm of <x1,y1,z1>
        double normVec1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2));

        //norm of vector from pivot of astrobee to laser pos
        double normDockCam = Math.sqrt(Math.pow(0.1061d, 2) + Math.pow(0.054d, 2) + Math.pow(0.0064d, 2));

        // value of vector of laser shoot form <xi,0j,0k>
        double _cos = Math.cos(Math.acos(0.1061d / normDockCam));
        double extraX = calculateDistanceBetweenLaserToPoint(normDockCam, normVec1, _cos);

        //vector between pivot of Astrobee to final laser pos
        double x2 = -0.1061d - extraX;
        double y2 = -0.054d;
        double z2 = -0.0064d;

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

        //norm of Axis
        double normAxis = Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2));


        //unit vector of Axis
        i /= normAxis;
        j /= normAxis;
        k /= normAxis;

        //return quaternion
        return new Quaternion((float) (i * Math.sin(theta / 2)), (float) (j * Math.sin(theta / 2)), (float) (k * Math.sin(theta / 2)), (float) Math.cos(theta / 2));

    }


    /**
     * Calculate between two Quaternion order not necessary
     * <p>
     * <a href="https://www.thepulsar.be/article/quaternion-based-rotations/">...</a>
     *
     * @param one Quaternion one
     * @param two Quaternion two
     * @return angle in radian between 2 quaternion
     */

    public static double calculateRadianBetweenQuaternion(Quaternion one, Quaternion two) {
        double[] normalVector = new double[]{1, 0, 0}; //(v)
        double[] axisOne = getInfoQuaternion(one);
        double[] axisTwo = getInfoQuaternion(two);
        // Find half theta from normal vector to that orientation
        double halfThetaOne = axisOne[3];
        double halfThetaTwo = axisTwo[3];

        // v x n
        double[] normalCrossAxisOne = new double[]{
                0,
                -axisOne[2],
                axisOne[1]
        };
        double[] normalCrossAxisTwo = new double[]{
                0,
                -axisTwo[2],
                axisTwo[1]
        };

        // n x v
        double[] axisOneCrossNormal = new double[]{
                0,
                axisOne[2],
                -axisOne[1]
        };
        double[] axisTwoCrossNormal = new double[]{
                0,
                axisTwo[2],
                -axisTwo[1]
        };

        // v . n
        double normalDotAxisOne = axisOne[0];
        double normalDotAxisTwo = axisTwo[0];

        // find both vector of quaternion
        double i1 = Math.pow(Math.cos(halfThetaOne), 2) - (Math.sin(halfThetaOne) * Math.cos(halfThetaOne) * (normalCrossAxisOne[0])) + (Math.sin(halfThetaOne) * Math.cos(halfThetaOne) * axisOneCrossNormal[0]) - (Math.pow(Math.sin(halfThetaOne), 2) * (normalVector[0] - 2 * normalDotAxisOne * axisOne[0]));
        double j1 = -(Math.sin(halfThetaOne) * Math.cos(halfThetaOne) * (normalCrossAxisOne[1])) + (Math.sin(halfThetaOne) * Math.cos(halfThetaOne) * axisOneCrossNormal[1]) - (Math.pow(Math.sin(halfThetaOne), 2) * (normalVector[1] - 2 * normalDotAxisOne * axisOne[1]));
        double k1 = -(Math.sin(halfThetaOne) * Math.cos(halfThetaOne) * (normalCrossAxisOne[2])) + (Math.sin(halfThetaOne) * Math.cos(halfThetaOne) * axisOneCrossNormal[2]) - (Math.pow(Math.sin(halfThetaOne), 2) * (normalVector[2] - 2 * normalDotAxisOne * axisOne[2]));
        double i2 = Math.pow(Math.cos(halfThetaTwo), 2) - (Math.sin(halfThetaTwo) * Math.cos(halfThetaTwo) * (normalCrossAxisTwo[0])) + (Math.sin(halfThetaTwo) * Math.cos(halfThetaTwo) * axisTwoCrossNormal[0]) - (Math.pow(Math.sin(halfThetaTwo), 2) * (normalVector[0] - 2 * normalDotAxisTwo * axisTwo[0]));
        double j2 = -(Math.sin(halfThetaTwo) * Math.cos(halfThetaTwo) * (normalCrossAxisTwo[1])) + (Math.sin(halfThetaTwo) * Math.cos(halfThetaTwo) * axisTwoCrossNormal[1]) - (Math.pow(Math.sin(halfThetaTwo), 2) * (normalVector[1] - 2 * normalDotAxisTwo * axisTwo[1]));
        double k2 = -(Math.sin(halfThetaTwo) * Math.cos(halfThetaTwo) * (normalCrossAxisTwo[2])) + (Math.sin(halfThetaTwo) * Math.cos(halfThetaTwo) * axisTwoCrossNormal[2]) - (Math.pow(Math.sin(halfThetaTwo), 2) * (normalVector[2] - 2 * normalDotAxisTwo * axisTwo[2]));

        // norm of vectors from Quaternion one and two
        double normVec1 = Math.sqrt(Math.pow(i1, 2) + Math.pow(j1, 2) + Math.pow(k1, 2));
        double normVec2 = Math.sqrt(Math.pow(i2, 2) + Math.pow(j2, 2) + Math.pow(k2, 2));

        // dot product between 2 vector
        double dotProductVec1Vec2 = i1 * i2 + j1 * j2 + k1 * k2;
        if (two.getW() == 1) {
            return Math.acos(i1 / normVec1);
        } else if (one.getW() == 1) {
            return Math.acos(i2 / normVec2);
        }
        return Math.acos(dotProductVec1Vec2 / (normVec1 * normVec2));
    }

    /**
     * get axis and radian of quaternion
     *
     * @param quaternion Quaternion
     * @return array of double [axis x, axis y, axis z, radian]
     */
    public static double[] getInfoQuaternion(Quaternion quaternion) {
        double halfThetaOfRotation = Math.acos(quaternion.getW());
        return new double[]{(quaternion.getX() / Math.sin(halfThetaOfRotation)), (quaternion.getY() / Math.sin(halfThetaOfRotation)), (quaternion.getZ() / Math.sin(halfThetaOfRotation)), halfThetaOfRotation};
    }

    /**
     * Calculate rotateVector by axis
     * <p>
     * <a href="https://www.youtube.com/watch?v=q-ESzg03mQc&t=134s/">...</a>
     *
     * @param axis             axis of rotation
     * @param vector           vector to calculate
     * @param radianOfRotation radian of rotation
     * @return calculated vector
     */
    public static Point rotateVector(double[] axis, Point vector, double radianOfRotation) {
        double crossX = axis[1] * vector.getZ() - axis[2] * vector.getY();
        double crossY = -(axis[0] * vector.getZ() - axis[2] * vector.getX());
        double crossZ = axis[0] * vector.getY() - axis[1] * vector.getX();

        double dotX = axis[0] * vector.getX();
        double dotY = axis[1] * vector.getY();
        double dotZ = axis[2] * vector.getZ();

        double x = Math.cos(radianOfRotation) * vector.getX() + Math.sin(radianOfRotation) * crossX + (1 - Math.cos(radianOfRotation)) * dotX * axis[0];
        double y = Math.cos(radianOfRotation) * vector.getY() + Math.sin(radianOfRotation) * crossY + (1 - Math.cos(radianOfRotation)) * dotY * axis[1];
        double z = Math.cos(radianOfRotation) * vector.getZ() + Math.sin(radianOfRotation) * crossZ + (1 - Math.cos(radianOfRotation)) * dotZ * axis[2];

        return new Point(x, y, z);
    }
}