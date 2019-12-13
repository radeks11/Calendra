package pl.rasoft.calendara.utils;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import pl.rasoft.calendara.BuildConfig;
import pl.rasoft.calendara.R;

public class SETTINGS {

    // <editor-fold desc="Zmienne i właściwości">

    private static Set<String> _calendars;
    private static int _widgetDefaultColor;
    private static int _widgetPastColor;
    private static int _widgetActiveColor;
    private static int _widgetDaysCount;
    private static boolean _widgetShowPastEvents;
    private static Set<String> _widgets;

    public static final String TAG = "CalendaRA";
    public static final int READ_CALENDAR_REQUEST_ID = 202;

    public static Set<String> getCalendars() { return _calendars; }
    public static int getWidgetDefaultColor() { return _widgetDefaultColor; }
    public static void setWidgetDefaultColor(int color) { _widgetDefaultColor = color; }
    public static int getWidgetPastColor() { return _widgetPastColor; }
    public static void setWidgetPastColor(int color) { _widgetPastColor = color; }
    public static int getWidgetActiveColor() { return _widgetActiveColor; }
    public static void setWidgetActiveColor(int color) { _widgetActiveColor = color; }
    public static int getWidgetDaysCount() { return _widgetDaysCount; }
    public static void setWidgetDaysCount(int days) { _widgetDaysCount = days; }
    public static boolean getWidgetShowPastEvents() { return _widgetShowPastEvents; }
    public static void setWidgetShowPastEvents(boolean showPastEvents) { _widgetShowPastEvents = showPastEvents; }

    // </editor-fold>

    // <editor-fold desc="Preferences">

    /**
     * Wczytuje preferencje do zmiennych
     *
     * @param context
     */
    public static void readPreferences(Context context)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        _calendars = sharedPreferences.getStringSet("calendars", new HashSet<String>());
        // _calendars = new HashSet<>(Arrays.asList("r.siebula@rasoft.pl", "Familijne", "janekplaskacz@gmail.com"));
        // _widgets = sharedPreferences.getStringSet("widgets", new HashSet<String>());
        _widgetDefaultColor = sharedPreferences.getInt("widgetDefaultColor", context.getResources().getColor(R.color.widgetDefaultColor, null));
        _widgetPastColor = sharedPreferences.getInt("widgetPastColor", context.getResources().getColor(R.color.widgetPastColor, null));
        _widgetActiveColor = sharedPreferences.getInt("widgetActiveColor", context.getResources().getColor(R.color.widgetActiveColor, null));
        _widgetDaysCount = sharedPreferences.getInt("widgetDaysCount", 2);
        _widgetShowPastEvents = sharedPreferences.getBoolean("widgetShowPastEvents", true);
    }

    /**
     * Zapisuje preferencje
     *
     * @param context
     */
    public static void writePreferences(Context context)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putStringSet("calendars", _calendars);
        // editor.putStringSet("widgets", _widgets);
        editor.putInt("widgetDefaultColor", _widgetDefaultColor);
        editor.putInt("widgetPastColor", _widgetPastColor);
        editor.putInt("widgetActiveColor", _widgetActiveColor);
        editor.putInt("widgetDaysCount", _widgetDaysCount);
        editor.putBoolean("widgetShowPastEvents", _widgetShowPastEvents);
        editor.apply();
    }

    /**
     * Czy to nowa instancja widgeta
     *
     * @param instanceId
     * @return
     */
    public static boolean isNewWidgetInstance(int instanceId) {
        return !_widgets.contains(instanceId);
    }

    /**
     * Ustawia kalendarz
     *
     * @param calendar
     * @param status
     */
    public static void setCalendar(String calendar, boolean status)
    {
        if (_calendars.contains(calendar)) {
            if (!status) {
                _calendars.remove(calendar);
            }
        }
        else if (status) {
            _calendars.add(calendar);
        }
    }

    // </editor-fold>

    // <editor-fold desc="Permissions">

    /**
     *
     * @param context
     * @return
     */
    public static boolean hasCalendarPermission(Context context)
    {
        boolean b = context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
        return b;
    }

    // </editor-fold>

    // <editor-fold desc="Log">

    /**
     * Zapisuje do Log.d datę i czas z klasy Calendar. Można użyć szablonu dla dodania tekstu opisowego
     * @param template Szablon tekstu, gdzie data jest parametrem tekstowym, np.: Data rozpoczącia: %s
     * @param calendar Obiekt klasy Calendar
     */
    public static void Log(String template, Calendar calendar) {
        if (BuildConfig.DEBUG) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (template != null && !template.isEmpty()) {
                String s = String.format(template, format.format(calendar.getTime()));
                Log.d(TAG, s);
            } else {
                Log.d(TAG, format.format(calendar.getTime()));
            }
        }
    }

    public static void Log(String s) {
        Log.d(TAG, s);
    }

    public static void Log(String template, Object ...params) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format(template, params));
        }
    }

    public static void Log(Exception ex) {
        Log.d(TAG, ex.getMessage());
    }

    // </editor-fold>

    // <editor-fold desc="Konwersja dp to pixel ">

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(Context context, float dp){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(Context context, float px){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    // </editor-fold>

    /**
     *
     * @return
     */
    public static Calendar getCalendarDateInstance()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Porównuje dwa kalendarze
     * @param calendar1
     * @param calendar2
     * @return
     */
    public static int compareCalendars(Calendar calendar1, Calendar calendar2) {
        int result = Integer.compare(calendar1.get(Calendar.YEAR), calendar2.get(Calendar.YEAR));
        if (result != 0) {
            return result;
        }

        result = Integer.compare(calendar1.get(Calendar.MONTH), calendar2.get(Calendar.MONTH));
        if (result != 0) {
            return result;
        }

        return Integer.compare(calendar1.get(Calendar.DAY_OF_MONTH), calendar2.get(Calendar.DAY_OF_MONTH));
    }

}
