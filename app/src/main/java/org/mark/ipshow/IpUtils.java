package org.mark.ipshow;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * Created by Mark on 2016/1/4.
 */
public class IpUtils {

    public static String getIpV4String(Context content){
        WifiManager wm = (WifiManager) content.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
