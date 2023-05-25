package jp.jaxa.iss.kibo.utils;


import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ARReader {
    private static int[] AR_ID = new int[0];
    private static final Point TOP_LEFT_TO_CENTER = new Point(0.1d, -0.0375d, 0);
    private static final Point TOP_RIGHT_TO_CENTER = new Point(-0.1d, -0.0375d, 0);
    private static final Point BOT_LEFT_TO_CENTER = new Point(0.1, 0.0375, 0);
    private static final Point BOT_RIGHT_TO_CENTER = new Point(-0.1, 0.0375, 0);
    private static final Point LASER_POINT_COORDINATE = new Point(0.0994d, 0.0285d, QuaternionCalculator.extraX);

    /**
     * Read AR from the NavCam type Mat and mark that Numbers of TargetsID start from the upper right
     * and increase in a counterclockwise direction.
     *
     * @param api KiboRpcApi
     * @return Corners(List < Mat >) of each AR that can detect from NavCam image type Mat
     */
    public static List<Mat> readAR(KiboRpcApi api) {
        Mat idMatrix = new Mat();
        List<Mat> corners = new ArrayList<>();
        Dictionary dict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Mat matSrc = api.getMatNavCam();

        Aruco.detectMarkers(matSrc, dict, corners, idMatrix);
        AR_ID = new int[corners.size()];
        for (int i = 0; i < corners.size(); i++) {
            AR_ID[i] = (int) idMatrix.get(i, 0)[0];
        }

        return corners;

    }

    /**
     * Calculate error distance between laser point and target. Use result in api.RelativeMoveto()
     *
     * @param api           KiboRpcApi
     * @param quaternion    current Quaternion of Astrobee
     * @return              Point, use in relativeMoveTo
     */
    public static Point calculateErrorCoordinate(KiboRpcApi api, Quaternion quaternion) {
        Mat rVec = new Mat();
        Mat tVec = new Mat();
        double[][] navCamInstinct = api.getNavCamIntrinsics();
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat dstMatrix = new Mat(1, 5, CvType.CV_32FC1);
        Mat objPoints = new Mat();
        cameraMatrix.put(0, 0, navCamInstinct[0]);
        dstMatrix.put(0, 0, navCamInstinct[1]);
        final float ARUCO_SIZE_CM = 5f;
        List<Mat> corners = readAR(api);
        Aruco.estimatePoseSingleMarkers(corners, ARUCO_SIZE_CM, cameraMatrix, dstMatrix, rVec, tVec, objPoints);
        if (AR_ID.length == 0) return null;

        Point pointToTarget;
        switch (AR_ID[0] % 4) {
            case 1:
                pointToTarget = TOP_RIGHT_TO_CENTER;
                break;
            case 2:
                pointToTarget = TOP_LEFT_TO_CENTER;
                break;
            case 3:
                pointToTarget = BOT_LEFT_TO_CENTER;
                break;
            case 0:
                pointToTarget = BOT_RIGHT_TO_CENTER;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + AR_ID[0] % 4);
        }
        double[] rotationVector = new double[]{rVec.get(0, 0)[0], rVec.get(0, 0)[1], rVec.get(0, 0)[2]};
        double radianOfRotation = Math.sqrt(Math.pow(rotationVector[0], 2) + Math.pow(rotationVector[1], 2) + Math.pow(rotationVector[2], 2));
        rotationVector[0] /= radianOfRotation;
        rotationVector[1] /= radianOfRotation;
        rotationVector[2] /= radianOfRotation;

        Point vectorToTarget = QuaternionCalculator.rotateVector(rotationVector, pointToTarget, radianOfRotation);

        Point idealTargetPoint = new Point(
                pointToTarget.getX() + vectorToTarget.getX(),
                pointToTarget.getY() + vectorToTarget.getY(),
                pointToTarget.getZ() + vectorToTarget.getZ()
        );
        Point moveTo = new Point(
                idealTargetPoint.getX() - LASER_POINT_COORDINATE.getX(),
                idealTargetPoint.getY() - LASER_POINT_COORDINATE.getY(),
                idealTargetPoint.getZ() - LASER_POINT_COORDINATE.getZ()
        );
        double[] currentQuaternionInfo = QuaternionCalculator.getInfoQuaternion(quaternion);
        Point unfixedMoveTo = QuaternionCalculator.rotateVector(currentQuaternionInfo, moveTo, currentQuaternionInfo[3]);
        return new Point(-unfixedMoveTo.getZ(), unfixedMoveTo.getY(), -unfixedMoveTo.getY());
    }


}
