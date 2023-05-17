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

        // value of vector of laser shoot form <xi,0j,0k>
        double _cos = Math.cos(Math.acos(0.1302d / normLaser));
        double extraX = (-2 * normLaser * _cos + Math.sqrt(Math.pow(2 * normLaser * _cos, 2) - 4 * (Math.pow(normLaser, 2) - Math.pow(normVec1, 2)))) / 2;

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
     *
     * Calculate between two Quaternion order not neccessary
     *
     * @param one   Quaternion one
     * @param two   Quaternion two
     * @return      theta in radian between two quaternion type double
     */
    static public double calculateRadianBetweenQuaternion(Quaternion one, Quaternion two){
        double halfThetaOne = Math.acos(one.getW());
        double halfThetaTwo = Math.acos(two.getW());

        //(n1,n2)
        double[] axisOne = new double[]{(one.getX()/Math.sin(halfThetaOne)),(one.getY()/Math.sin(halfThetaOne)),(one.getZ()/Math.sin(halfThetaOne))};
        double[] axisTwo = new double[]{(two.getX()/Math.sin(halfThetaTwo)),(two.getY()/Math.sin(halfThetaTwo)),(two.getZ()/Math.sin(halfThetaTwo))};

        double[] vectorNormal = new double[] {1,0,0}; //(v)
        //v x n
        double[] normalCrossAxisOne = new double[] {
                0,
                -axisOne[2],
                axisOne[1]
        };
        double[] normalCrossAxisTwo = new double[] {
                0,
                -axisTwo[2],
                axisTwo[1]
        };

        //n x v
        double[] axisOneCrossNormal = new double[] {
                0,
                axisOne[2],
                -axisOne[1]
        };
        double[] axisTwoCrossNormal = new double[] {
                0,
                axisTwo[2],
                -axisTwo[1]
        };
        double normalDotAxisOne = axisOne[0];
        double normalDotAxisTwo = axisTwo[0];

        double i1 =Math.pow(Math.cos(halfThetaOne),2) - (Math.sin(halfThetaOne) * Math.sin(halfThetaOne) * (normalCrossAxisOne[0])) + (Math.sin(halfThetaOne)*Math.cos(halfThetaOne) * axisOneCrossNormal[0]-(Math.pow(Math.sin(halfThetaOne),2) * (vectorNormal[0]-2*normalDotAxisOne*axisOne[0])));
        double j1 =-(Math.sin(halfThetaOne) * Math.sin(halfThetaOne) * (normalCrossAxisOne[1])) + (Math.sin(halfThetaOne)*Math.cos(halfThetaOne) * axisOneCrossNormal[1]-(Math.pow(Math.sin(halfThetaOne),2) * (vectorNormal[1]-2*normalDotAxisOne*axisOne[1])));
        double k1 =-(Math.sin(halfThetaOne) * Math.sin(halfThetaOne) * (normalCrossAxisOne[2])) + (Math.sin(halfThetaOne)*Math.cos(halfThetaOne) * axisOneCrossNormal[2]-(Math.pow(Math.sin(halfThetaOne),2) * (vectorNormal[2]-2*normalDotAxisOne*axisOne[2])));
        double i2 =Math.pow(Math.cos(halfThetaTwo),2) - (Math.sin(halfThetaTwo) * Math.sin(halfThetaTwo) * (normalCrossAxisTwo[0])) + (Math.sin(halfThetaTwo)*Math.cos(halfThetaTwo) * axisTwoCrossNormal[0]-(Math.pow(Math.sin(halfThetaTwo),2) * (vectorNormal[0]-2*normalDotAxisTwo*axisOne[0])));
        double j2 =-(Math.sin(halfThetaTwo) * Math.sin(halfThetaTwo) * (normalCrossAxisTwo[1])) + (Math.sin(halfThetaTwo)*Math.cos(halfThetaTwo) * axisTwoCrossNormal[1]-(Math.pow(Math.sin(halfThetaTwo),2) * (vectorNormal[1]-2*normalDotAxisTwo*axisOne[1])));
        double k2 =-(Math.sin(halfThetaTwo) * Math.sin(halfThetaTwo) * (normalCrossAxisTwo[2])) + (Math.sin(halfThetaTwo)*Math.cos(halfThetaTwo) * axisTwoCrossNormal[2]-(Math.pow(Math.sin(halfThetaTwo),2) * (vectorNormal[2]-2*normalDotAxisTwo*axisOne[2])));

        double normVec1 = Math.sqrt(Math.pow(i1,2) + Math.pow(j1,2) + Math.pow(k1,2));
        double normVec2 = Math.sqrt(Math.pow(i2,2) + Math.pow(j2,2) + Math.pow(k2,2));

        double dotProductVec1Vec2 = i1 * i2 + j1 * j2 + k1 * k2;

        return Math.acos(dotProductVec1Vec2/(normVec1*normVec2));


    }
}