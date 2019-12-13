package pl.rasoft.calendara.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;

import pl.rasoft.calendara.R;
import pl.rasoft.calendara.calendar.CalendarContentResolver;
import pl.rasoft.calendara.utils.EventInfo;
import pl.rasoft.calendara.utils.SETTINGS;

public class CalendarWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CalendarWidgetItemFactory(getApplicationContext(), intent);
    }

    /**
     * Factory
     */
    class CalendarWidgetItemFactory implements RemoteViewsFactory {

        private Context _context;
        ArrayList<EventInfo> _events = new ArrayList<>();

        CalendarWidgetItemFactory(Context context, Intent intent) {
            _context = context;

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCreate() {
            _events = CalendarContentResolver.getEvents(_context);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDataSetChanged() {
            Log.d(SETTINGS.TAG, "onDataSetChanged: ");
            _events = CalendarContentResolver.getEvents(_context);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return _events.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(_context.getPackageName(), R.layout.calendar_widget_item);
            EventInfo event = _events.get(i);

            views.setTextViewText(R.id.item_day,  event.getDay());
            views.setTextViewText(R.id.item_dayofweek,  event.getDayOfWeek(getApplicationContext()));
            views.setTextViewText(R.id.item_time,  event.getTime());
            views.setTextViewText(R.id.item_title,  event.title);

            if (event.first) {
                views.setTextViewText(R.id.item_day_description, event.getDayDescription(getBaseContext()));
                views.setInt(R.id.item_day_description, "setVisibility", View.VISIBLE);
                views.setTextViewText(R.id.item_day,  event.getDay());
                views.setTextViewText(R.id.item_dayofweek,  event.getDayOfWeek(getApplicationContext()));
            }
            else {
                views.setTextViewText(R.id.item_day_description, "");
                views.setInt(R.id.item_day_description, "setVisibility", View.GONE);
                views.setTextViewText(R.id.item_day, "");
                views.setTextViewText(R.id.item_dayofweek,  "");
            }

//            int h = (int)SETTINGS.convertDpToPixel(_context, 40f);
//            if (event.last) {
//                h = (int)SETTINGS.convertDpToPixel(_context, 50f);
//            }
//            views.setInt(R.id.item_main_layout, "setMinimumHeight", h);

             setColor(event, views);

//             Ustawienie szczegółów przekazywanych przy kliknięciu.
//             Android nakazuje ustawić jeden event dla całego ListView w widgecie,
//             Dlatego do Extras należy dodać szczegóły przekazywane przy kliknięciu
             setEventDetails(event, views);

            return views;
        }

        protected void setColor(EventInfo event, RemoteViews views) {
            Calendar now = Calendar.getInstance();

            if (event.empty) {
                views.setInt(R.id.item_title, "setTextColor", SETTINGS.getWidgetPastColor());
                views.setInt(R.id.item_time, "setTextColor", SETTINGS.getWidgetPastColor());
                views.setInt(R.id.item_day, "setTextColor", SETTINGS.getWidgetPastColor());
                views.setInt(R.id.item_dayofweek, "setTextColor", SETTINGS.getWidgetPastColor());
                views.setInt(R.id.item_color, "setBackgroundColor", SETTINGS.getWidgetPastColor());
            }
            else {
                if (now.after(event.end)) {
                    views.setInt(R.id.item_title, "setTextColor", SETTINGS.getWidgetPastColor());
                    views.setInt(R.id.item_time, "setTextColor", SETTINGS.getWidgetPastColor());
                }
                else if (now.after(event.start)) {
                    views.setInt(R.id.item_title, "setTextColor", SETTINGS.getWidgetActiveColor());
                    views.setInt(R.id.item_time, "setTextColor", SETTINGS.getWidgetActiveColor());
                }
                else {
                    views.setInt(R.id.item_title, "setTextColor", SETTINGS.getWidgetDefaultColor());
                    views.setInt(R.id.item_time, "setTextColor", SETTINGS.getWidgetDefaultColor());
                }

                views.setInt(R.id.item_color, "setBackgroundColor", event.color);
            }
        }

        /**
         * Ustawienie szczegółów przekazywanych przy kliknięciu.
         *
         * @param event
         * @param views
         */
        protected void setEventDetails(EventInfo event, RemoteViews views) {

            Intent fillInIntent = new Intent();

            if (!event.empty) {
                Bundle extras = new Bundle();
                extras.putString("id", event.id);
                extras.putLong("beginTime", event.start.getTimeInMillis());
                extras.putLong("endTime", event.end.getTimeInMillis());
                fillInIntent.putExtras(extras);
            }

            // Podpięcie szczegółów do całego rekordu
            views.setOnClickFillInIntent(R.id.item_main_layout, fillInIntent);
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }


    }
}
