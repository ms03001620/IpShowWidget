package org.mark.ipshow;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.RemoteViews;


public class IpWidgetProvider extends AppWidgetProvider {
    private static final String USER_CLICK_BUTTON = "user.click.button";
    private static long sTime;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        ComponentName thisWidget = new ComponentName(context, IpWidgetProvider.class);
        remoteViews.setTextViewText(R.id.text_ip, getIpString(context));
        remoteViews.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, USER_CLICK_BUTTON));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        LogUtils.d("IpWidgetProvider", "onReceive:" + (intent == null ? "null" : intent.getAction()));
        if (intent == null) {
            return;
        }
        if (System.currentTimeMillis() - sTime < 1000) {
            // ignore fast click by user
            return;
        }

        sTime = System.currentTimeMillis();
        if (USER_CLICK_BUTTON.equals(intent.getAction()) || "android.net.wifi.STATE_CHANGE".equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_main);
            remoteViews.setTextViewText(R.id.text_ip, context.getString(R.string.scan));
            appWidgetManager.updateAppWidget(new ComponentName(context, IpWidgetProvider.class), remoteViews);

            Task task = new Task(context);
            new Handler().postDelayed(task, 1000);
        }
    }

    static class Task implements Runnable {
        private Context mContext;

        Task(Context context) {
            mContext = context;
        }

        @Override
        public void run() {
            LogUtils.d("IpWidgetProvider", "Task running");
            if (mContext != null) {
                final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_main);
                remoteViews.setTextViewText(R.id.text_ip, getIpString(mContext));
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                ComponentName thisWidget = new ComponentName(mContext, IpWidgetProvider.class);
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            }
        }
    }

    public static String getIpString(Context context) {
        String address = IpUtils.getIPAddress(context);
        if (TextUtils.isEmpty(address)) {
            address = context.getString(R.string.retry);
        }
        return address;
    }
}