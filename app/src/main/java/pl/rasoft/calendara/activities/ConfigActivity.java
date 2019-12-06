package pl.rasoft.calendara.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import pl.rasoft.calendara.R;
import pl.rasoft.calendara.calendar.CalendarAdapter;
import pl.rasoft.calendara.utils.OnClickListenerA;
import pl.rasoft.calendara.utils.SETTINGS;
import pl.rasoft.calendara.widget.CalendarWidget;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class ConfigActivity extends AppCompatActivity {

    ImageButton _saveButton;
    ListView _calendarsListView;
    EditText _daysCount;
    TextView _pastColor;
    TextView _activeColor;
    TextView _defaultColor;
    Switch _showPastEvents;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_config);
        SETTINGS.readPreferences(this);
        checkPermissionCalendar();
        initFields();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     *
     */
    protected void initFields()
    {
        // Calendars
        _calendarsListView = findViewById(R.id.config_calendars);
        CalendarAdapter adapter = new CalendarAdapter(this);
        _calendarsListView.setAdapter(adapter);

        // Ilość dni
        _daysCount = findViewById(R.id.config_days);
        _daysCount.setText(  String.format("%d", SETTINGS.getWidgetDaysCount() ) );

        // Wybór koloru wydarzeń minionych
        _pastColor = findViewById(R.id.config_past_color);
        _pastColor.setBackgroundColor(SETTINGS.getWidgetPastColor());
        _pastColor.setOnClickListener(new OnClickListenerA(this, _pastColor));

        // Wybór koloru wydarzeń aktywnych
        _activeColor = findViewById(R.id.config_active_color);
        _activeColor.setBackgroundColor(SETTINGS.getWidgetActiveColor());
        _activeColor.setOnClickListener(new OnClickListenerA(this, _activeColor));

        // Wybór domyślnego koloru wydarzeń
        _defaultColor = findViewById(R.id.config_default_color);
        _defaultColor.setBackgroundColor(SETTINGS.getWidgetDefaultColor());
        _defaultColor.setOnClickListener(new OnClickListenerA(this, _defaultColor));

        _showPastEvents = findViewById(R.id.config_show_past_events);
        _showPastEvents.setChecked(SETTINGS.getWidgetShowPastEvents());

        // Save
        _saveButton = findViewById(R.id.config_save);
        _saveButton.setOnClickListener(saveButtonOnClickListener);
    }

    /**
     *
     */
    View.OnClickListener saveButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SETTINGS.setWidgetDaysCount(Integer.parseInt(_daysCount.getText().toString()) );
            SETTINGS.setWidgetShowPastEvents(_showPastEvents.isChecked());
            SETTINGS.writePreferences(getBaseContext());
            CalendarWidget.updateAllWidgets(getBaseContext());
            Intent resultValue = new Intent();
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregisterReceiver(_calendarEventReceiver);
    }

    /**
     * Sprawdzenie uprawnień do kalendarza.
     * Jeżeli nie ma to zapytanie się o ich włączenie
     */
    public void checkPermissionCalendar()
    {
        if (!SETTINGS.hasCalendarPermission(getApplicationContext())) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, SETTINGS.READ_CALENDAR_REQUEST_ID);
        }
    }

    /**
     * Wywoływane przez dialog. Ustawia kolor pola i w ustawieniach
     *
     * @param field_id
     * @param color
     */
    public void setColor(int field_id, int color)
    {
        switch (field_id) {
            case R.id.config_past_color:
                _pastColor.setBackgroundColor(color);
                SETTINGS.setWidgetPastColor(color);
                break;
            case R.id.config_active_color:
                _activeColor.setBackgroundColor(color);
                SETTINGS.setWidgetActiveColor(color);
                break;
            case R.id.config_default_color:
                _defaultColor.setBackgroundColor(color);
                SETTINGS.setWidgetDefaultColor(color);
                break;
        }
    }

    // <editor-fold desc="Receivers & listeners, ActivityResult">

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == SETTINGS.READ_CALENDAR_REQUEST_ID) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals("android.permission.READ_CALENDAR") && grantResults[i] == PERMISSION_GRANTED) {
                    initFields();
                    break;
                }
            }
        }
    }

    // </editor-fold>
}
