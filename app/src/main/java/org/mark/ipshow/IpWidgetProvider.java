package org.mark.ipshow;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Mark on 2016/1/4.
 */
public class IpWidgetProvider extends AppWidgetProvider {
    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;
    private static final String MyOnClick = "myOnClickTag";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        remoteViews =  new RemoteViews(context.getPackageName(), R.layout.widget_main);
        thisWidget = new ComponentName(context, IpWidgetProvider.class);

        remoteViews.setTextViewText(R.id.text_ip, IpUtils.getIpV4String(context));

        remoteViews.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, MyOnClick));

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {

        if (MyOnClick.equals(intent.getAction())){
            RemoteViews remoteViews =  new RemoteViews(context.getPackageName(), R.layout.widget_main);
            remoteViews.setTextViewText(R.id.text_ip, IpUtils.getIpV4String(context));
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, IpWidgetProvider.class);
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

        super.onReceive(context, intent);
    };
}
