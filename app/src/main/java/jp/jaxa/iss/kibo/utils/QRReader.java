package jp.jaxa.iss.kibo.utils;

import android.graphics.Bitmap;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

import java.util.HashMap;

public class QRReader {


    private static final HashMap<String, String> MESSAGES = new HashMap<String, String>();

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
    public static String readQR(KiboRpcApi api) {
        return readQR(api, CameraMode.NAV);
    }

    /**
     * Read the QRCode from camera type bitmap, returns null if fails
     *
     * @param api KiboRpcApi
     * @param mode which camera to use
     * @return translated message that can be used in api.reportMissionComplete, null if fails
     */
    public static String readQR(KiboRpcApi api, CameraMode mode) {
        Bitmap bMap = null;
        switch (mode) {
            case NAV:
                bMap = api.getBitmapNavCam();
                break;
            case DOCK:
                bMap = api.getBitmapDockCam();

        }
        if (bMap == null) {
            throw new RuntimeException("bMap is null");
        }
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        //covert bitmap to BinaryBitmap(zxing) for reading QR
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            String contents = result.getText();
            return MESSAGES.get(contents);
        } catch (ReaderException e) {
            return null;
        }
    }

}
