package org.mark.ipshow;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;


import java.util.logging.LogRecord;


public class IpWidgetProvider extends AppWidgetProvider {
    private static final String MyOnClick = "myOnClickTag";
    private Task mTask;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews =  new RemoteViews(context.getPackageName(), R.layout.widget_main);
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
        Log.d("IpWidgetProvider", "onReceive");
        if (MyOnClick.equals(intent.getAction())) {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            remoteViews.setTextViewText(R.id.text_ip, context.getString(R.string.scan));
            ComponentName thisWidget = new ComponentName(context, IpWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);

            remoteViews.setTextViewText(R.id.text_ip, context.getString(R.string.scan));

            if (mTask == null) {
                Log.d("IpWidgetProvider", "create task");
                mTask = new Task(context);
            }

            Log.d("IpWidgetProvider", "postDelayed task");
            mHandler.removeCallbacks(mTask);
            mHandler.postDelayed(mTask, 1000);
        }
    }

    class Task implements Runnable {
        private Context mContext;

        public Task(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_main);
            remoteViews.setTextViewText(R.id.text_ip, getWiFiIpString(mContext));
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
            ComponentName thisWidget = new ComponentName(mContext, IpWidgetProvider.class);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }
    }

    public String getWiFiIpString(Context content) {
        String ipResult = null;
        try {
            WifiManager wm = (WifiManager) content.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null) {
                ipResult = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            } else {
                ipResult = content.getString(R.string.retry);
            }
        } catch (Exception e) {
            ipResult = e.toString();
            Log.e("IpWidgetProvider", "WifiManager", e);
        }
        return ipResult;
    }
}
