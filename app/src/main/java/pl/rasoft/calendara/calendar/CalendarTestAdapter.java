package pl.rasoft.calendara.calendar;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Set;

import pl.rasoft.calendara.R;
import pl.rasoft.calendara.utils.EventInfo;
import pl.rasoft.calendara.utils.SETTINGS;

public class CalendarTestAdapter extends BaseAdapter {


    private Context _context;
    private ArrayList<EventInfo> _events;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CalendarTestAdapter(Context context)
    {
        _context = context;
        Set<String> tmp = CalendarContentResolver.getCalendars(context);
        _events = CalendarContentResolver.getEvents(context);
    }

    @Override
    public int getCount() {
        return _events.size();
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

        EventInfo event = _events.get(i);
        LayoutInflater inflater = (LayoutInflater)_context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        view = inflater.inflate(R.layout.calendar_widget_item, null);

        TextView itemDay = view.findViewById(R.id.item_day);
        TextView itemDayOfWeek = view.findViewById(R.id.item_dayofweek);
        TextView itemTime = view.findViewById(R.id.item_time);
        TextView itemTitle = view.findViewById(R.id.item_title);
        TextView itemDayDescription = view.findViewById(R.id.item_day_description);

        itemTime.setText(event.getTime());
        itemTitle.setText(event.title);

        if (event.first) {
            itemDayDescription.setVisibility(View.VISIBLE);
            itemDayDescription.setText(event.getDayDescription(_context));
            itemDay.setText(event.getDay());
            itemDayOfWeek.setText(event.getDayOfWeek(_context));
        }
        else {
            itemDayDescription.setVisibility(View.GONE);
            itemDayDescription.setText("");
            itemDay.setText("");
            itemDayOfWeek.setText("");
        }

        int h = (int) SETTINGS.convertDpToPixel(_context, 40f);
        if (event.last) {
            h = (int)SETTINGS.convertDpToPixel(_context, 50f);
        }
        LinearLayout layout = view.findViewById(R.id.item_main_layout);
        layout.setMinimumHeight(h);

//        setColor(event, views);

//             Ustawienie szczegółów przekazywanych przy kliknięciu.
//             Android nakazuje ustawić jeden event dla całego ListView w widgecie,
//             Dlatego do Extras należy dodać szczegóły przekazywane przy kliknięciu
//        setEventDetails(event, views);

        return view;
    }

//    View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Switch s = (Switch)v;
//            SETTINGS.setCalendar( (String)s.getText(), s.isChecked() );
//        }
//    };
}
