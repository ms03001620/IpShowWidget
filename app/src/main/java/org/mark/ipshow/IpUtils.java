package org.mark.ipshow;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Mark on 2016/1/4.
 */
public class IpUtils {

    public static String getWiFiIpString(Context content){
        WifiManager wm = (WifiManager) content.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public static String getLocalIpAddressv2(){
        try{
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.d("IpUtils", inetAddress.getClass().getSimpleName()+":"+inetAddress.getHostAddress());
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress()+"";
                    }
                }
            }
        }catch (SocketException e) {
            return e.getMessage();
        }

        return "No connection";
    }

}
