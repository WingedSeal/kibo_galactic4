package jp.jaxa.iss.kibo.logger;


import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss]");
    public static String logMessage = dateFormat.format(new Date()) + " Logger started";
    @SuppressLint("SimpleDateFormat")
    public static void log(String text) {
        Date date = new Date();
        logMessage += dateFormat.format(date) + " " + text + "\n";
    }
}
