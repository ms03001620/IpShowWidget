package org.mark.ipshow;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtils {
    private static int index = 0;

    public static String mockIPAddress(Context context) {
        index++;
        if (index % 2 == 0) {
            return "10.10.10.10";
        } else {
            return "255.255.255.255";
        }
    }

    public static String getIPAddress(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return sss(context);
        } else {
            return ddd(context);
        }
    }

    public static String ddd(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return "manager null";
        }
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info.isConnected()) {
            // 当前使用2G/3G/4G网络
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    LogUtils.e("IpUtils", "SocketException", e);
                    return e.getMessage();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // 当前使用WIFI网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    return intIP2StringIP(wifiInfo.getIpAddress());
                }
                return "";
            }
        }
        return "no connected()";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String sss(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return "manager null";
        }
        Network[] networks = connectivityManager.getAllNetworks();
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    return intIP2StringIP(wifiInfo.getIpAddress());
                }
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    LogUtils.e("IpUtils", "SocketException", e);
                    return e.getMessage();
                }
            }
            return "transport none";
        }

        return networks == null ? "networks null" : "networks " + networks.length;
    }


    /**
     * 将得到的int类型的IP转换为String类型
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
