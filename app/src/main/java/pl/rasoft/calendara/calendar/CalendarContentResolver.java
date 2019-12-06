package pl.rasoft.calendara.calendar;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import pl.rasoft.calendara.R;
import pl.rasoft.calendara.utils.EventInfo;
import pl.rasoft.calendara.utils.SETTINGS;

public class CalendarContentResolver {

    // <editor-fold desc="Definicje statyczne">

    private static final Uri EVENTS_URI = Uri.parse("content://com.android.calendar/calendars/instances/whenbyday");

    private static final Uri CALENDARS_URI = Uri.parse("content://com.android.calendar/calendars");

    private static final String[] CALENDAR_FIELDS = {
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
    };

    private static final String[] EVENT_FIELDS = {
            CalendarContract.Instances.EVENT_ID,                    // 0
            CalendarContract.Instances.TITLE,                       // 1
            CalendarContract.Instances.DTSTART,                     // 2
            CalendarContract.Instances.BEGIN,                       // 3
            CalendarContract.Instances.DTEND,                       // 4
            CalendarContract.Instances.END,                         // 5
            CalendarContract.Instances.DURATION,                    // 6
            CalendarContract.Instances.RDATE,                       // 7
            CalendarContract.Instances.RRULE,                       // 8
            CalendarContract.Instances.EVENT_LOCATION,              // 9
            CalendarContract.Instances.CALENDAR_ID,                 // 10
            CalendarContract.Instances.CALENDAR_DISPLAY_NAME,       // 11
            CalendarContract.Instances.CALENDAR_COLOR,              // 12
            CalendarContract.Instances.EVENT_TIMEZONE,              // 13
            CalendarContract.Instances.EVENT_COLOR,                 // 14
            CalendarContract.Instances.ALL_DAY                      // 15
    };

    private static final int COL_EVENT_ID                   = 0;
    private static final int COL_TITLE                      = 1;
    private static final int COL_DTSTART                    = 2;
    private static final int COL_BEGIN                      = 3;
    private static final int COL_DTEND                      = 4;
    private static final int COL_END                        = 5;
    private static final int COL_DURATION                   = 6;
    private static final int COL_RDATE                      = 7;
    private static final int COL_RULE                       = 8;
    private static final int COL_EVENT_LOCATION             = 9;
    private static final int COL_CALENDAR_ID                = 10;
    private static final int COL_CALENDAR_DISPLAY_NAME      = 11;
    private static final int COL_CALENDAR_COLOR             = 12;
    private static final int COL_EVENT_TIMEZONE             = 13;
    private static final int COL_EVENT_COLOR                = 14;
    private static final int COL_ALL_DAY                    = 15;


    // </editor-fold>

    // <editor-fold desc="Narzędzia">

    /**
     * Pobiera listę kalendarzy dostępnych w systemie
     * @return Set z nazwami kalendarzy
     */
    public static Set<String> getCalendars(Context context) {

        Set<String> calendars = new HashSet<>();
        if (!SETTINGS.hasCalendarPermission(context))
        {
            return calendars;
        }

        ContentResolver contentResolver = context.getContentResolver();

        // Fetch a list of all calendars sync'd with the device and their display names
        Cursor cursor = contentResolver.query(CALENDARS_URI, CALENDAR_FIELDS, null, null, null);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    // This is actually a better pattern:
                    String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
                    Boolean selected = !cursor.getString(3).equals("0");
                    calendars.add(displayName);
                }
            }
        }
        catch (Exception ex) {
            Log.d(SETTINGS.TAG, ex.getMessage());
        }

        cursor.close();
        return calendars;
    }

    /**
     * Pobiera listę zdarzeń
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<EventInfo> getEvents(Context context) {

        ArrayList<EventInfo> events = new ArrayList<EventInfo>();
        if (!SETTINGS.hasCalendarPermission(context))
        {
            return events;
        }

        TimeZone tz = TimeZone.getDefault();
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar searchFrom = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        searchFrom.set(Calendar.HOUR_OF_DAY, 0);
        searchFrom.set(Calendar.MINUTE, 0);
        searchFrom.set(Calendar.SECOND, 0);
        searchFrom.set(Calendar.MILLISECOND, 0);
        Calendar searchTo = (Calendar)searchFrom.clone();
        searchTo.add(Calendar.DATE, 2);

        String query = CalendarContract.Instances.VISIBLE + " = 1";
        String sort = CalendarContract.Instances.DTSTART + " ASC";

        Uri.Builder uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(uriBuilder, searchFrom.getTimeInMillis());
        ContentUris.appendId(uriBuilder, searchTo.getTimeInMillis());

        Set<String> calendars = SETTINGS.getCalendars();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uriBuilder.build(), EVENT_FIELDS, query, null, sort);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                EventInfo ci = new EventInfo();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Dane podstawowe
                ci.id = cursor.getString(COL_EVENT_ID);
                // Log.d(SETTINGS.TAG, ci.id);
                ci.title = cursor.getString(COL_TITLE);
                ci.location = cursor.getString(COL_EVENT_LOCATION);
                ci.calendarName = cursor.getString(COL_CALENDAR_DISPLAY_NAME);

                // timezone
                if (!cursor.isNull(COL_EVENT_TIMEZONE)){
                    ci.timeZone = TimeZone.getTimeZone(cursor.getString(COL_EVENT_TIMEZONE));
                }
                else {
                    ci.start.setTimeZone(TimeZone.getDefault());
                }

                // Data i godzina rozpoczęcia
                ci.start = Calendar.getInstance();
                ci.start.setTimeInMillis(cursor.getLong(COL_BEGIN));
                ci.start.setTimeZone(ci.timeZone);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cursor.getLong(9));

                // Data i godzina zakończenia
                if (!cursor.isNull(COL_END))
                {
                    ci.end = Calendar.getInstance();
                    ci.end.setTimeInMillis((cursor.getLong(COL_END)));
                    ci.end.setTimeZone(ci.timeZone);
                }

                ci.allDay = cursor.getInt(COL_ALL_DAY) != 0;

                // kolor. Najpierw event, potem kalendarz
                if (!cursor.isNull(COL_EVENT_COLOR)) {
                    ci.color = cursor.getInt(COL_EVENT_COLOR);
                }
                else if (!cursor.isNull(COL_CALENDAR_COLOR)) {
                    ci.color = cursor.getInt(COL_CALENDAR_COLOR);
                }
                else {
                    ci.color = context.getResources().getColor(R.color.widgetCalendarColor, null);
                }

                boolean flag = true;
                if (!SETTINGS.getWidgetShowPastEvents() && !ci.allDay && now.after(ci.end)) {
                    flag = false;
                }

                // Dodanie do listy jeżeli kalendarz jest na liście obsługiwanych
                if (calendars.contains(ci.calendarName) && flag) {
                    // Log.d(SETTINGS.TAG, ci.title + ": " + format.format(ci.start.getTime()) + ", " + format.format(ci.end.getTime())); // + ", (" + ci.duration.getSeconds() + " seconds)");
                    events.add(ci);
                }
            }
        }

        events.sort(EventInfo.BeginComparator);
        setFirstLast(events);
        return events;
    }

    /**
     * Ustawia flagę pierwszy i ostatni event danego dnia
     *
     * @param events
     */
    protected static void setFirstLast(ArrayList<EventInfo> events)
    {
        Calendar d = Calendar.getInstance();
        d.set(Calendar.YEAR, 2000);
        d.set(Calendar.HOUR_OF_DAY, 0);
        d.set(Calendar.MINUTE, 0);
        d.set(Calendar.SECOND, 0);
        d.set(Calendar.MILLISECOND, 0);
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();

        for (int i = 0; i < events.size(); i++) {
            EventInfo event = events.get(i);
            Calendar currentDate = (Calendar)event.start.clone();

            // Log.d(SETTINGS.TAG, "before: " + format.format(currentDate.getTime()) + " - " + Long.toString(currentDate.getTimeInMillis()));
            currentDate.setTimeZone(tz);
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            // Log.d(SETTINGS.TAG, "after: " + format.format(currentDate.getTime()) + " - " + Long.toString(currentDate.getTimeInMillis()));
            event.first = !currentDate.equals(d);
            event.last = false;

            if (event.first && i > 0) {
                EventInfo prev = events.get(i - 1);
                prev.last = true;
            }

            d = currentDate;
        }
    }

    // </editor-fold>

}
