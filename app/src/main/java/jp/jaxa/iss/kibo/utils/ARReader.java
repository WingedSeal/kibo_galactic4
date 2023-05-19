package jp.jaxa.iss.kibo.utils;


import gov.nasa.arc.astrobee.types.Point;
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
        int[] AR_ID = new int[Corners.size()];
        for (int i = 0; i < Corners.size(); i++) {
            AR_ID[i] = (int) IDs.get(i, 0)[0];
        }

        return Corners;

    }

    /**
     *  Calculate the coordinate(relative to NavCam) of ARMakers (Aruco) in the image
     *
     *
     * @param api       KiboRpcApi
     * @return          coordinate of Aruco in image relative to NavCam type Array of Point
     */
    public static Point[] calculatePosAruco(KiboRpcApi api){
        double[][] navCamInstinct = api.getNavCamIntrinsics();
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
        Mat dstMatrix = new Mat(1, 5, CvType.CV_32FC1);
        Mat rVec =new Mat();
        Mat tVec = new Mat();
        Mat objPoints =new Mat();
        cameraMatrix.put(0, 0, navCamInstinct[0]);
        dstMatrix.put(0, 0, navCamInstinct[1]);
        final float ARUCOSIZE = 5f; //cm
        List<Mat> corner = readAR(api);
        Aruco.estimatePoseSingleMarkers(corner,ARUCOSIZE,cameraMatrix,dstMatrix,rVec,tVec,objPoints);
        Point[] coordinateAR = new Point[corner.size()];
        for(int i=0; i<corner.size();++i){
            coordinateAR[0] = new Point(tVec.get(i,0)[0],tVec.get(i,0)[1],tVec.get(i,0)[2]);// [x,y,z]
        }
        return coordinateAR;
    }
}
