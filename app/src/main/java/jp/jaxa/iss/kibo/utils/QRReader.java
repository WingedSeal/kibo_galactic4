package jp.jaxa.iss.kibo.utils;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;

public class QRReader {

    /**
     * Read the QRcode from NavCam type bitmap
     *
     * @param api api KiboRpcApi
     * @return String: translated message that can simply use in api.reportMissionComplete
     */
    public static String readQR(KiboRpcApi api){

        //get bitmap from api
        Bitmap bMap = api.getBitmapNavCam();
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        //covert bitmap to BinaryBitmap(zxing) for reading QR
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try
        {
            Result result = reader.decode(bitmap);
            contents = result.getText();
            Log.d("[Content]: "," "+contents);
        }
        catch (ReaderException e)
        {
            Log.d("[error]", " "+e);
        }

        //translate message (implementable)
        if(contents == "JEM"){
            contents = "STAY_AT_JEM";
        }
        else if(contents == "COLUMBUS"){
            contents ="GO_TO_COLUMBUS";
        }
        else if(contents == "RACK1"){
            contents = "CHECK_RACK_1";
        }
        else if(contents == "ASTROBEE"){
            contents = "I_AM_HERE";
        }
        else if(contents == "INTBALL"){
            contents = "LOOKING_FORWARD_TO_SEE_YOU";
        }
        else{
            contents = "NO_PROBLEM";
        }

        return contents;

    }

}
