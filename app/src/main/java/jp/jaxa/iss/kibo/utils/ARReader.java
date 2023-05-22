package jp.jaxa.iss.kibo.utils;


import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import gov.nasa.arc.astrobee.types.Vec3d;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ARReader {
    private static int[] AR_ID = new int[0];
    private static Mat rVec = new Mat();
    private static Mat tVec = new Mat();
    private static final Point TopLeftToCenter = new Point(0.1d,-0.0375d,0);
    private static final Point TopRightToCenter = new Point(-0.1d,-0.0375d,0);
    private static final Point BotLeftToCenter = new Point(0.1,0.0375,0);
    private static final Point BotRightToCenter = new Point(-0.1,0.0375,0);
    /**
     * Read AR from the NavCam type Mat and mark that Numbers of TargetsID start from the upper right
     * and increase in a counterclockwise direction.
     *
     * @param api   KiboRpcApi
     * @return      Corners(List<Mat>) of each AR that can detect from NavCam image type Mat
     */
    public static List<Mat> readAR(KiboRpcApi api) {
        Mat IDs = new Mat();
        List<Mat> Corners = new ArrayList<>();
        Dictionary Dict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Mat matSrc = api.getMatNavCam();

        Aruco.detectMarkers(matSrc, Dict, Corners, IDs);
        AR_ID = new int[Corners.size()];
        for (int i = 0; i < Corners.size(); i++) {
            AR_ID[i] = (int) IDs.get(i, 0)[0];
        }

        return Corners;

    }


    public static Point calculateErrorCoordinate(KiboRpcApi api, Quaternion quaternion){
        Point laserCoordinate = laserPointCoordinate();
        double[][] navCamInstinct = api.getNavCamIntrinsics();
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat dstMatrix = new Mat(1, 5, CvType.CV_32FC1);
        Mat objPoints =new Mat();
        cameraMatrix.put(0, 0, navCamInstinct[0]);
        dstMatrix.put(0, 0, navCamInstinct[1]);
        final float ARUCOSIZE = 5f; //cm
        List<Mat> corner = readAR(api);
        Aruco.estimatePoseSingleMarkers(corner,ARUCOSIZE,cameraMatrix,dstMatrix,rVec,tVec,objPoints);
        if(AR_ID.length != 0){
            Point PointToTarget = null;
            switch(AR_ID[0]%4) {
                case 1:
                    PointToTarget = TopRightToCenter;
                    break;
                case 2:
                    PointToTarget = TopLeftToCenter;
                    break;
                case 3:
                    PointToTarget = BotLeftToCenter;
                    break;
                case 0:
                    PointToTarget = BotRightToCenter;
                    break;
                default:
                    break;
            }
            double[] rotationVector = new double[]{rVec.get(0,0)[0],rVec.get(0,0)[1],rVec.get(0,0)[2]};
            double radianOfRotation = Math.sqrt(Math.pow(rotationVector[0],2)+Math.pow(rotationVector[1],2)+ Math.pow(rotationVector[2],2));
            rotationVector[0]/= radianOfRotation;
            rotationVector[1]/= radianOfRotation;
            rotationVector[2]/= radianOfRotation;

            Point vectorToTarget = QuaternionCalculator.rotateVector(rotationVector,PointToTarget,radianOfRotation);

            Point idealTargetPoint = new Point(PointToTarget.getX() + vectorToTarget.getX() , PointToTarget.getY() + vectorToTarget.getY() , PointToTarget.getZ() + vectorToTarget.getZ());
            Point moveTo = new Point(idealTargetPoint.getX() - laserCoordinate.getX() ,idealTargetPoint.getY() - laserCoordinate.getY() ,idealTargetPoint.getZ() - laserCoordinate.getZ() );
            double[] currentQuaternionInfo = QuaternionCalculator.getInfoQuaternion(quaternion);
            Point unfixedMoveTo = QuaternionCalculator.rotateVector(currentQuaternionInfo,moveTo,currentQuaternionInfo[3]);
            Point fixedMoveTo =  new Point(-unfixedMoveTo.getZ(),unfixedMoveTo.getY(),-unfixedMoveTo.getY());
            return fixedMoveTo;
        }

//        Point[] coordinateAR = new Point[corner.size()];
//        for(int i=0; i<corner.size();++i){
//            coordinateAR[i] = new Point(tVec.get(i,0)[0],tVec.get(i,0)[1],tVec.get(i,0)[2]);// [x,y,z]
//        }
//        return coordinateAR;
        return null;
    }
    private static Point laserPointCoordinate(){
        return new Point(0.0994d,0.0285d,QuaternionCalculator.extraX);
    }

}
