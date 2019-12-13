package pl.rasoft.calendara.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

import pl.rasoft.calendara.R;

public class EventInfo {
    public String id = "";
    public String title = "";
    public String calendarName = "";
    public String location = "";
    public Calendar start;
    public Calendar end;
    public TimeZone timeZone;
    public int color = 0;
    public boolean first = false;
    public boolean last = false;
    public boolean allDay = false;
    public boolean empty = true;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM");

    public String getDay() {
        return dayFormat.format(start.getTime());
    }

    public String getDayDescription(Context context) {
        Calendar calendar = SETTINGS.getCalendarDateInstance();
        if (SETTINGS.compareCalendars(calendar, start) == 0) {
            return context.getResources().getString(R.string.today);
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        if (SETTINGS.compareCalendars(calendar, start) == 0) {
            return context.getResources().getString(R.string.tomorrow);
        }
        return "";
    }

    public String getDayOfWeek(Context context) {
        return start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, context.getResources().getConfiguration().locale);
    }

    public String getTime() {

        String s;

        if (empty) {
            s = "";
        }
        else if (allDay) {
            s = "Wydarzenie całodniowe";
        }
        else {
            s = timeFormat.format(start.getTime()) + " - " + timeFormat.format(end.getTime()) ;
        }

        if (location != null && !location.isEmpty()) {
            s += " | " + location;
        }

        return s;
    };


    public static Comparator<EventInfo> BeginComparator = new Comparator<EventInfo>() {

        public int compare(EventInfo event1, EventInfo event2) {

            if (event1.id == event2.id && event1.start == event2.start) {
                return 0;
            }

            return (int) (event1.start.getTimeInMillis() - event2.start.getTimeInMillis());
        }
    };

}