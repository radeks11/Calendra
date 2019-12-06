package pl.rasoft.calendara.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;

public class CalendarWidgetReceiver extends BroadcastReceiver {

    public static String ACTION_CLICK = "pl.rasoft.calendara.CLICK";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action != null && !action.isEmpty()) {
            Bundle extras = intent.getExtras();

            Intent showEventIntent = new Intent(Intent.ACTION_VIEW);
            Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon();
            uri.appendPath(extras.getString("id"));
            showEventIntent.setData(uri.build());
            showEventIntent.putExtra("beginTime", extras.getLong("beginTime"));
            showEventIntent.putExtra("endTime", extras.getLong("endTime"));
            context.startActivity(showEventIntent);
        }

    }
}
