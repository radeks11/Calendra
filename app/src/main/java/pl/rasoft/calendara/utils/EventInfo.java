package pl.rasoft.calendara.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

public class EventInfo {
    public String id;
    public String title;
    public String calendarName;
    public String location;
    public Calendar start;
    public Calendar end;
    public Duration duration;
    public TimeZone timeZone;
    public int color;
    public boolean first;
    public boolean last;
    public boolean allDay;
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dayFormat = new SimpleDateFormat("d MMM");

    public String getDay() {
        return dayFormat.format(start.getTime());
    }

    public String getDayOfWeek(Context context) {
        return start.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, context.getResources().getConfiguration().locale);
        // return String.format("%d", start.get(Calendar.DA));
    }

    public String getTime() {

        String s = "";

        if (allDay) {
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