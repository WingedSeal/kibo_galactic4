package jp.jaxa.iss.kibo.utils;


import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

public class ARReader {

    /**
     * Mark that Numbers of TargetsID start from the upper right
     * and increase in a counterclockwise direction.
     *
     * read AR from the NavCam type Mat
     *
     *
     *
     * @param api KiboRpcApi
     * @return int[] as the TargetsID according to the rulebook
     */
    public static int[] readAR(KiboRpcApi api){
        Mat IDs = new Mat();
        List<Mat> Corners = new ArrayList<>();
        Dictionary Dict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Mat matSrc = api.getMatNavCam();

        Aruco.detectMarkers(matSrc, Dict, Corners, IDs);
        int[] AR_ID = new int[Corners.size()];
        for(int i=0; i<Corners.size(); i++) {
            AR_ID[i] = (int) IDs.get(i, 0)[0];
        }

        return AR_ID;

    }
}