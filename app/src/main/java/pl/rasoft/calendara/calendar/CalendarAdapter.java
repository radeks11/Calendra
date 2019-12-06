package pl.rasoft.calendara.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

import java.util.Set;

import pl.rasoft.calendara.R;
import pl.rasoft.calendara.utils.SETTINGS;

public class CalendarAdapter extends BaseAdapter {


    Context _context;
    String[] _avalaibleCalendars;
    Set<String> _currentCalendars;

    public CalendarAdapter(Context context)
    {
        _context = context;
        Set<String> tmp = CalendarContentResolver.getCalendars(context);
        _avalaibleCalendars = tmp.toArray(new String[tmp.size()]);
        _currentCalendars = SETTINGS.getCalendars();
    }

    @Override
    public int getCount() {
        return _avalaibleCalendars.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String calendar = _avalaibleCalendars[i];
        LayoutInflater inflater = (LayoutInflater)_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate(R.layout.calendar_config_item, null);

        // view.setTag(event);
        Switch s = view.findViewById(R.id.calendar_item_switch);
        s.setText(calendar);
        s.setChecked( _currentCalendars.contains(calendar) );

        s.setOnClickListener(onClickListener);
        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Switch s = (Switch)v;
            SETTINGS.setCalendar( (String)s.getText(), s.isChecked() );
        }
    };
}
