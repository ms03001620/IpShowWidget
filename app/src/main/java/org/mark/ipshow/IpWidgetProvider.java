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

import java.util.Arrays;


/**
 * android 7 正常
 * android 8 无法获取到wifi变化
 * android 9 无法工作
 */
public class IpWidgetProvider extends AppWidgetProvider {
    private static final String USER_CLICK_BUTTON = "user.click.button";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LogUtils.d("IpWidgetProvider", "onUpdate appWidgetIds:" + Arrays.toString(appWidgetIds));
        for (int appWidgetId : appWidgetIds) {
            updateTextView(context, appWidgetId, makeDisplayText(context));
        }
        saveTitlePref(context, appWidgetIds);
    }

    static void saveTitlePref(Context context, int[] appWidgetId) {
        LogUtils.d("IpWidgetProvider", "saveTitlePref:" + Arrays.toString(appWidgetId));
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

    private int[] loadWidgetIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_CLICK_BUTTON, Context.MODE_PRIVATE);
        String string = prefs.getString(USER_CLICK_BUTTON, null);
        if (string != null) {
            if (string.contains(",")) {
                String[] idsString = string.split(",");
                int[] ids = new int[idsString.length];
                for (int i = 0; i < idsString.length; i++) {
                    String s = idsString[i];
                    ids[i] = Integer.parseInt(s);
                }
                return ids;
            } else {
                return new int[]{Integer.parseInt(string)};
            }
        }
        return null;
    }


    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        LogUtils.d("IpWidgetProvider", "onReceive action:" + intent.getAction());
        if (!USER_CLICK_BUTTON.equals(intent.getAction())) {
            return;
        }

        final int[] appWidgetIds = loadWidgetIds(context);
        if (appWidgetIds != null) {
            for (final int id : appWidgetIds) {
                updateTextView(context, id, context.getString(R.string.scan));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateTextView(context, id, makeDisplayText(context));
                    }
                }, 1000);
            }
        }
    }

    private void updateTextView(Context context, int widgetId, String text) {
        LogUtils.d("IpWidgetProvider", "updateTextView widgetId:" + widgetId + ", text:" + text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
        views.setTextViewText(R.id.text_ip, text);
        views.setOnClickPendingIntent(R.id.btn_refresh, getPendingSelfIntent(context, USER_CLICK_BUTTON));
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
    }


    public static String makeDisplayText(Context context) {
        String address = IpUtils.getIPAddress(context);
        if (TextUtils.isEmpty(address)) {
            address = context.getString(R.string.retry);
        }
        return address;
    }
}