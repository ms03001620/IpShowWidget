package org.mark.ipshow;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.RemoteViews;

// 当wifi变化后需要更新widget的数据

// 问题：可能静态注册失效导致无法收到wifi变化
public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d("IpWidgetProvider", "WifiBroadcastReceiver action:" + intent.getAction());
        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            // save the connected state to get in onUpdate
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, IpWidgetProvider.class));
            updateAppWidgetFromService(context, appWidgetManager);
            //onUpdate(context, appWidgetManager, appWidgetIds);


        }
    }

    public static void updateAppWidgetFromService(Context context, AppWidgetManager appWidgetManager) {
        appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, IpWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, IpWidgetProvider.class);

            intent.putExtra("appWidgetID", appWidgetId);

            intent.setAction(USER_CLICK_BUTTON);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);

            views.setOnClickPendingIntent(R.id.btn_refresh, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static final String USER_CLICK_BUTTON = "user.click.button";

}
