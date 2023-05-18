package jp.jaxa.iss.kibo.utils;


import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

public class ARReader {

    /**
     * read AR on the target
     *
     *
     *
     * Numbers of TargetsID start from the upper right
     * and increase in a counterclockwise direction.
     *
     *
     *
     *
     *
     */

    static Mat matSrc;						// Variable store matrix of image.
    static Mat IDs; 						// Variable store ID each of AR code. : Matrix format

    static List<Mat> Corners = new ArrayList<>(); 	// Variable store four corner each of AR Code.
    static Dictionary Dict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250); // Standard AR code format according rule book.
    static int[] AR_ID = new int[]{0, 0, 0, 0};		// Variable store ID each of AR code. : Array of Int format

    /* Target tag variables */
    public int[]   ArID1_CornerRT = new int[]{0, 0};	// Container the specified corner of the ar tag.
    public int[]   ArID2_CornerLT = new int[]{0, 0};
    public int[]   ArID3_CornerLB = new int[]{0, 0};
    public int[]   ArID4_CornerRB = new int[]{0, 0};
    int ID_1 = 1;
    int ID_2 = 2;
    int ID_3 = 3;
    int ID_4 = 4;


    public static void readAR(KiboRpcApi api){
        matSrc = api.getMatNavCam();

        Aruco.detectMarkers(matSrc, Dict, Corners, IDs); 	// AR tag detector
        AR_ID = new int[] 									// Put ID of AR Code to Array of int.
                {
                        (int) IDs.get(0, 0)[0],
                        (int) IDs.get(1, 0)[0],
                        (int) IDs.get(2, 0)[0],
                        (int) IDs.get(3, 0)[0]
                };
    }

}
