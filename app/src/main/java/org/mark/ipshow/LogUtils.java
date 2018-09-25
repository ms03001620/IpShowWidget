package org.mark.ipshow;

import android.util.Log;

public class LogUtils {

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            return Log.e(tag, msg, tr);
        }
        return 0;
    }
}
