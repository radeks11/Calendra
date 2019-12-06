package pl.rasoft.calendara.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import pl.rasoft.calendara.R;
import pl.rasoft.calendara.calendar.CalendarJobService;
import pl.rasoft.calendara.utils.SETTINGS;

public class CalendarWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        SETTINGS.Log("CalendarWidget.updateAppWidget");

        // Wczytaj aktualne ustawienia
        SETTINGS.readPreferences(context);

        Intent serviceIntent = new Intent(context, CalendarWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);
        views.setRemoteAdapter(R.id.widget_listview, serviceIntent);

         // Dodanie obsługi click dla ListView
        setListViewOnClick(context, views);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public static void updateAllWidgets(Context context)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());

        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), CalendarWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            for (int appWidgetId : appWidgetIds) {
                CalendarWidget.updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    /**
     *  Dodanie obsługi click.
     *  Android blokuje dopinanie osobnych PendingIntent do poszczególnych elementów ListView
     *  więc w tym miejscu podpięty jest ogólny intent dla całej listy
     */
    private static void setListViewOnClick(Context context, RemoteViews views)
    {
        SETTINGS.Log("CalendarWidget.setListViewOnClick");
        Intent launchIntent = new Intent(context, CalendarWidgetReceiver.class);
        launchIntent.setAction(CalendarWidgetReceiver.ACTION_CLICK);
        PendingIntent launchPendingIntent = PendingIntent.getBroadcast(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_listview, launchPendingIntent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(SETTINGS.TAG, "CalendarWidget.onUpdate");

        CalendarJobService.schedule(context);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        SETTINGS.Log("CalendarWidget.onAppWidgetOptionsChanged");
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        SETTINGS.Log("CalendarWidget.onReceive, action: " + action);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());

        ComponentName thisWidget = new ComponentName(context.getApplicationContext(), CalendarWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
        if (appWidgetIds != null && appWidgetIds.length > 0) {
            onUpdate(context, appWidgetManager, appWidgetIds);
        }

    }

}

