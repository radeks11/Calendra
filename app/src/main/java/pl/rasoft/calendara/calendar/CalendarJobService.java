package pl.rasoft.calendara.calendar;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract;

import pl.rasoft.calendara.utils.SETTINGS;
import pl.rasoft.calendara.widget.CalendarWidget;

public class CalendarJobService extends JobService {
    private static final int iJobId = 1005; //Job Id
    private static final int ME_MYSELF_AND_I=3493;
    final Handler mHandler = new Handler(); //Just to display Toasts

    public static void schedule(Context oContext) {
        SETTINGS.Log("CalendarJobService.schedule");
        ComponentName oComponentName = new ComponentName(oContext, CalendarJobService.class);
        JobInfo.Builder oJobInfoBuilder = new JobInfo.Builder(ME_MYSELF_AND_I, oComponentName);
        final Uri CALENDAR_URI = Uri.parse("content://" + CalendarContract.AUTHORITY + "/");
        oJobInfoBuilder.addTriggerContentUri(new JobInfo.TriggerContentUri(CalendarContract.CONTENT_URI, JobInfo.TriggerContentUri.FLAG_NOTIFY_FOR_DESCENDANTS));
        oJobInfoBuilder.addTriggerContentUri(new JobInfo.TriggerContentUri(CALENDAR_URI, 0));
        JobScheduler jobScheduler = (JobScheduler) oContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(oJobInfoBuilder.build());
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        SETTINGS.Log("CalendarJobService.onStartJob");
        // updateWidget(this);
        CalendarWidget.updateAllWidgets(this);
        schedule(this); //Reschedule to receive future changes
        return (false);
    }

    @Override
    synchronized public boolean onStopJob(JobParameters params) {
        return (false);
    }



}
