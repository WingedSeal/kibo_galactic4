package jp.jaxa.iss.kibo.rpc.thailand.utils;

import android.graphics.Bitmap;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import jp.jaxa.iss.kibo.rpc.thailand.Astrobee;

import java.util.HashMap;

public class QRReader {
    private static int imageNumber = 0;
    private static final boolean saveImages = true;

    private static final HashMap<String, String> MESSAGES = new HashMap<>();

    static {
        MESSAGES.put("COLUMBUS", "GO_TO_COLUMBUS");
        MESSAGES.put("JEM", "STAY_AT_JEM");
        MESSAGES.put("RACK1", "CHECK_RACK_1");
        MESSAGES.put("ASTROBEE", "I_AM_HERE");
        MESSAGES.put("INTBALL", "LOOKING_FORWARD_TO_SEE_YOU");
        MESSAGES.put("BLANK", "NO_PROBLEM");
    }
    /**
     * Read the QRCode from NavCam type bitmap, returns null if fails
     *
     * @param api KiboRpcApi
     * @return translated message that can be used in api.reportMissionComplete, null if fails
     */
    public static String readQR(Astrobee api) {
        return readQR(api, CameraMode.NAV);
    }

    /**
     * Read the QRCode from camera type bitmap, returns null if fails
     *
     * @param api KiboRpcApi
     * @param mode which camera to use
     * @return translated message that can be used in api.reportMissionComplete, null if fails
     */
    public static String readQR(Astrobee api, CameraMode mode) {
        Mat distoredQRImg = null;
        switch (mode) {
            case NAV:
                distoredQRImg = api.api.getMatNavCam();
                break;
            case DOCK:
                distoredQRImg = api.api.getMatDockCam();
        }
        if (distoredQRImg == null) {
            throw new RuntimeException("bMap is null");
        }
        Bitmap bMap = api.undistoredMatImage(distoredQRImg,mode);
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        //covert bitmap to BinaryBitmap(zxing) for reading QR
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            String contents = result.getText();
            String message = MESSAGES.get(contents);
            if (saveImages){
                api.api.saveBitmapImage(bMap, "qrcode" + imageNumber + "_" + message + ".bmp");
                imageNumber++;
            }
            return message;
        } catch (ReaderException e) {
            if (saveImages) {
                api.api.saveBitmapImage(bMap, "[FAILED] qrcode" + imageNumber + ".bmp");
                imageNumber++;
            }
            Bitmap distBitmap = Bitmap.createBitmap(distoredQRImg.cols(),distoredQRImg.rows(), Bitmap.Config.ARGB_4444);
            try{
                Utils.matToBitmap(distoredQRImg,distBitmap);
                //copy pixel data from the Bitmap into the 'intArray' array
                distBitmap.getPixels(intArray, 0, distBitmap.getWidth(), 0, 0, distBitmap.getWidth(), distBitmap.getHeight());
                source = new RGBLuminanceSource(distBitmap.getWidth(), distBitmap.getHeight(), intArray);
                //covert bitmap to BinaryBitmap(zxing) for reading QR
                bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result = reader.decode(bitmap);
                String contents = result.getText();
                String message = MESSAGES.get(contents);
                //api.api.saveBitmapImage(distBitmap, "[FAILED] qrcode" + imageNumber + ".bmp");
                //imageNumber++;
                return message;
            }
            catch (ReaderException ef){
                if (saveImages) {
                    api.api.saveBitmapImage(distBitmap, "[FAILED] qrcodebefore " + (imageNumber -1) + ".bmp");
                }
                return null;
            }
        }
    }

}
