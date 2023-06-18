package jp.jaxa.iss.kibo.utils;


import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.logger.Logger;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.defaultapk.Astrobee;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class ARReader {
    private static int count = 0;
    private static int[] AR_ID = new int[0];
    private static final Point TOP_LEFT_TO_CENTER = new Point(0.1d, -0.0375d, 0);
    private static final Point TOP_RIGHT_TO_CENTER = new Point(-0.1d, -0.0375d, 0);
    private static final Point BOT_LEFT_TO_CENTER = new Point(0.1, 0.0375, 0);
    private static final Point BOT_RIGHT_TO_CENTER = new Point(-0.1, 0.0375, 0);


    /*
     * Read AR from the NavCam type Mat and mark that Numbers of TargetsID start from the upper right
     * and increase in a counterclockwise direction.
     *
     * @param astrobee KiboRpcApi
     * @return Corners(List < Mat >) of each AR that can detect from NavCam image type Mat
     *
    public static List<Mat> readAR(Astrobee astrobee) {
        double[][] navCamInstinct = astrobee.getNavCamIntrinsics();
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat dstMatrix = new Mat(1, 5, CvType.CV_32FC1);
        cameraMatrix.put(0, 0, navCamInstinct[0]);
        dstMatrix.put(0, 0, navCamInstinct[1]);
        Mat idMatrix = new Mat();
        List<Mat> corners = new ArrayList<>();
        Dictionary dict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Mat matSrc = astrobee.api.getMatNavCam();
        Mat undistorted = new Mat();


        astrobee.api.saveMatImage(matSrc,"image" + count );
        Aruco.detectMarkers(matSrc, dict, corners, idMatrix);
        AR_ID = new int[corners.size()];
        for (int i = 0; i < corners.size(); i++) {
            AR_ID[i] = (int) idMatrix.get(i, 0)[0];
        }
        count++;

        return corners;

    }

    **
     * Calculate error distance between laser point and target. Use result in api.RelativeMoveto()
     *
     * @param astrobee           Astrobee
     * @param quaternion    current Quaternion of Astrobee
     * @return              Point, use in relativeMoveTo
     *
    public static Point calculateErrorCoordinate(Astrobee astrobee, Quaternion quaternion) {
        Mat rVec = new Mat();
        Mat tVec = new Mat();

        double[][] navCamInstinct = astrobee.getNavCamIntrinsics();
        Logger.__log("camera matrix :" + navCamInstinct[0][0] + " " + navCamInstinct[0][4]);
        Logger.__log("dst matrix :" + navCamInstinct[1][0] + " " + navCamInstinct[1][4]);
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat dstMatrix = new Mat(1, 5, CvType.CV_32FC1);
        cameraMatrix.put(0, 0, navCamInstinct[0]);
        dstMatrix.put(0, 0, navCamInstinct[1]);
        final float ARUCO_SIZE_CM = 0.05f; //meter
        List<Mat> corners = readAR(astrobee);
        Aruco.estimatePoseSingleMarkers(corners, ARUCO_SIZE_CM, cameraMatrix, dstMatrix, rVec, tVec);

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

        Logger.__log("AR ID :" +(AR_ID[0]%4));
        Logger.__log("pointToTarget x:" + pointToTarget.getX() + " y:" + pointToTarget.getY() + " z:" + pointToTarget.getZ() );
        double[] rotationVector = new double[]{rVec.get(0, 0)[0], rVec.get(0, 0)[1], -rVec.get(0, 0)[2]};
        Logger.__log("rotationVector = x:" + rotationVector[0] + " y:" + rotationVector[1] + " z:" + rotationVector[2]);
        double radianOfRotation = Math.sqrt(Math.pow(rotationVector[0], 2) + Math.pow(rotationVector[1], 2) + Math.pow(rotationVector[2], 2));
        Logger.__log("radianOfRotation :"+ radianOfRotation );
        rotationVector[0] /= radianOfRotation;
        rotationVector[1] /= radianOfRotation;
        rotationVector[2] /= radianOfRotation;
        radianOfRotation = Math.PI - radianOfRotation;
        Logger.__log("rotationVector after divide = x:" + rotationVector[0] + " y:" + rotationVector[1] + " z:" + rotationVector[2]);
        Point vectorToTarget = QuaternionCalculator.rotateVector(rotationVector, pointToTarget, radianOfRotation);
        Logger.__log("vectorToTarget = x:" + vectorToTarget.getX() + " y:" + vectorToTarget.getY() + " z:" + vectorToTarget.getZ());
        Point idealTargetPoint = new Point(
                tVec.get(0,0)[0] + vectorToTarget.getX(),
                -tVec.get(0,0)[1] + vectorToTarget.getY(),
                tVec.get(0,0)[2] + vectorToTarget.getZ()
        );
        Logger.__log("tVec = x:" + tVec.get(0,0)[0] + " y:" + tVec.get(0,0)[1] + " z:" + tVec.get(0,0)[2]);
        try{
            Logger.__log("AR ID :" +(AR_ID[1]%4));
            Logger.__log("tVec = x:" + tVec.get(1,0)[0] + " y:" + tVec.get(1,0)[1] + " z:" + tVec.get(1,0)[2]);
        }catch(Exception e){

        }
        try{
            Logger.__log("AR ID :" +(AR_ID[2]%4));
            Logger.__log("tVec = x:" + tVec.get(2,0)[0] + " y:" + tVec.get(2,0)[1] + " z:" + tVec.get(2,0)[2]);
        }catch(Exception e){

        }
        Logger.__log("idealTargetPoint = x:" + idealTargetPoint.getX() + " y:" + idealTargetPoint.getY() + " z:" + idealTargetPoint.getZ());
        Point LASER_POINT_COORDINATE = new Point(0.0994d, 0.0285d, 0.0125 + QuaternionCalculator.extraX);
        Point moveTo = new Point(
//                idealTargetPoint.getX() - LASER_POINT_COORDINATE.getX(),
//                idealTargetPoint.getY() - LASER_POINT_COORDINATE.getY(),
//                idealTargetPoint.getZ() - LASER_POINT_COORDINATE.getZ()
                idealTargetPoint.getZ() - LASER_POINT_COORDINATE.getZ(),
                idealTargetPoint.getX() - LASER_POINT_COORDINATE.getX(),
                -(idealTargetPoint.getY() - LASER_POINT_COORDINATE.getY())

        );
        Logger.__log("moveTo = x:" + moveTo.getX() + " y:" + moveTo.getY() + " z:" + moveTo.getZ());
        double[] currentQuaternionInfo = QuaternionCalculator.getInfoQuaternion(quaternion);
//        Point unfixedMoveTo = QuaternionCalculator.rotateVector(currentQuaternionInfo, moveTo, currentQuaternionInfo[3]);
//        return new Point(-unfixedMoveTo.getZ(), unfixedMoveTo.getY(), -unfixedMoveTo.getY());
        Logger.__log("extrax : " +QuaternionCalculator.extraX );
        Logger.__log("quaternion = x:" + currentQuaternionInfo[0] + " y:" + currentQuaternionInfo[1] + " z:" + currentQuaternionInfo[2] + " w:"+currentQuaternionInfo[3]);
        return QuaternionCalculator.rotateVector(currentQuaternionInfo,moveTo,2 * currentQuaternionInfo[3]);
    }
    */


}
