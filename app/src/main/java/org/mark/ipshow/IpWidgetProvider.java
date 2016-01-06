package org.mark.ipshow;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Mark on 2016/1/4.
 */
public class IpWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "IpWidgetProvider";
    private static final String MyOnClick = "myOnClickTag";

    private Context mContext;
    Handler mHandler;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mContext = context;
        RemoteViews remoteViews =  new RemoteViews(context.getPackageName(), R.layout.widget_main);
        ComponentName thisWidget = new ComponentName(context, IpWidgetProvider.class);
        remoteViews.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, MyOnClick));
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        mHandler = new Handler(context.getMainLooper(), new Handler.Callback() {
            public boolean handleMessage(Message message) {
                RemoteViews remoteViews =  new RemoteViews(mContext.getPackageName(), R.layout.widget_main);
                remoteViews.setTextViewText(R.id.text_ip, String.valueOf(message.obj));
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                ComponentName thisWidget = new ComponentName(mContext, IpWidgetProvider.class);
                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                return false;
            }
        });

        Log.d(TAG, "onUpdate");
        new Thread(networkTask).start();
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {
        if (MyOnClick.equals(intent.getAction())){
            Log.d(TAG, "MyOnClick");
            mContext = context;
            new Thread(networkTask).start();
        }
        super.onReceive(context, intent);
    };

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            String ip = IpUtils.getLocalIpAddress();
            Log.d(TAG, ip);
            msg.obj = ip;
            mHandler.sendMessage(msg);
        }
    };

}
