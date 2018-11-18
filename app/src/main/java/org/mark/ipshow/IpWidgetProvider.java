package org.mark.ipshow;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.RemoteViews;


/**
 * android 7 正常
 * android 8 无法获取到wifi变化
 * android 9 无法工作
 */
public class IpWidgetProvider extends AppWidgetProvider {
    private static final String USER_CLICK_BUTTON = "user.click.button";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            updateTextView(context, appWidgetIds[i], getIpString(context));
        }

        saveTitlePref(context, appWidgetIds);
    }

    static void saveTitlePref(Context context, int[] appWidgetId) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < appWidgetId.length; i++) {
            if (i > 0) {
                string.append(",");
            }
            string.append(appWidgetId[i]);
        }

        SharedPreferences.Editor prefs = context.getSharedPreferences(USER_CLICK_BUTTON, Context.MODE_PRIVATE).edit();
        prefs.putString(USER_CLICK_BUTTON, string.toString());
        prefs.apply();
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        try {
            final int[] appWidgetIds = getWidgetIds(context);

            LogUtils.d("IpWidgetProvider", "onReceive:" + (intent == null ? "null" : intent.getAction()) + ", ss:" + appWidgetIds.length);


            for (int i = 0; i < appWidgetIds.length; i++) {
                final int index = i;
                LogUtils.d("IpWidgetProvider", "onReceive:" + i);

                updateTextView(context, appWidgetIds[i], context.getString(R.string.scan));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateTextView(context, appWidgetIds[index], getIpString(context));
                    }
                }, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTextView(Context context, int widgetId, String text) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        views.setTextViewText(R.id.text_ip, text);
        views.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, USER_CLICK_BUTTON));
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
    }


    private int[] getWidgetIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_CLICK_BUTTON, Context.MODE_PRIVATE);
        String string = prefs.getString(USER_CLICK_BUTTON, "");

        if (string.contains(",")) {
            String[] ss = string.split(",");
            int[] ids = new int[ss.length];
            for (int i = 0; i < ss.length; i++) {
                String s = ss[i];
                ids[i] = Integer.valueOf(s);
            }

            return ids;

        } else {
            return new int[]{Integer.valueOf(string)};
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