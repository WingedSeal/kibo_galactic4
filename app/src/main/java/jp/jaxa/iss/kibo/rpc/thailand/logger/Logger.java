package jp.jaxa.iss.kibo.rpc.thailand.logger;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
    public static String logMessage = dateFormat.format(new Date()) + " Logger started";

    public static void __log(String text) {
        Date date = new Date();
        logMessage += dateFormat.format(date) + " " + text + "\n";
    }
}
