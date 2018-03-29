package org.mark.ipshow;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;


public class IpWidgetProvider extends AppWidgetProvider {
    private static final String MyOnClick = "user.click.button";
    private static Task sTask = new Task();
    private static Handler sHandler = new Handler();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        ComponentName thisWidget = new ComponentName(context, IpWidgetProvider.class);
        remoteViews.setTextViewText(R.id.text_ip, getWiFiIpString(context));
        remoteViews.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, MyOnClick));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("IpWidgetProvider", "onReceive:" + (intent == null ? "null" : intent.getAction()));
        if (intent == null) {
            return;
        }
        if (MyOnClick.equals(intent.getAction()) || "android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            remoteViews.setTextViewText(R.id.text_ip, context.getString(R.string.scan));
            appWidgetManager.updateAppWidget(new ComponentName(context, IpWidgetProvider.class), remoteViews);

            sTask.setContext(context);
            sHandler.removeCallbacks(sTask);
            sHandler.postDelayed(sTask, 1000);
        }
    }

    static class Task implements Runnable {
        private Context mContext;

        public void setContext(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            Log.d("IpWidgetProvider", "Task running");
            if (mContext != null) {
                final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_main);
                remoteViews.setTextViewText(R.id.text_ip, getWiFiIpString(mContext));
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                ComponentName thisWidget = new ComponentName(mContext, IpWidgetProvider.class);
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            }
        }
    }

    public static String getWiFiIpString(Context context) {
        String ipResult = null;
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                if (WifiManager.WIFI_STATE_ENABLED == wm.getWifiState()) {
                    ipResult = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                } else {
                    ipResult = context.getString(R.string.wifi_disable);
                }

            } else {
                ipResult = context.getString(R.string.retry);
            }
        } catch (Exception e) {
            ipResult = e.toString();
            Log.e("IpWidgetProvider", "WifiManager", e);
        }
        return ipResult;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("IpWidgetProvider", "onDeleted");
        sTask.setContext(null);
        sTask = null;
        sHandler = null;
    }
}