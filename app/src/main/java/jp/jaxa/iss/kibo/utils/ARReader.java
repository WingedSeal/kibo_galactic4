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
     * read AR from the Navcam type Mat
     * get ID from
     *
     *
     * @param api KiboRpcApi
     * @return int[]
     */


    public static int[] readAR(KiboRpcApi api){
        Mat matSrc = new Mat();
        Mat IDs = new Mat();
        List<Mat> Corners = new ArrayList<>();
        Dictionary Dict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        int[] AR_ID = new int[4];
        matSrc = api.getMatNavCam();

        Aruco.detectMarkers(matSrc, Dict, Corners, IDs);
        AR_ID = new int[]
                {
                        (int) IDs.get(0, 0)[0],
                        (int) IDs.get(1, 0)[0],
                        (int) IDs.get(2, 0)[0],
                        (int) IDs.get(3, 0)[0]
                };

        return AR_ID;

    }
}
