package bpaulino.com.br.photogallery.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

import bpaulino.com.br.photogallery.R;
import bpaulino.com.br.photogallery.activity.PhotoGalleryActivity;
import bpaulino.com.br.photogallery.fragment.PhotoGalleryFragment;
import bpaulino.com.br.photogallery.model.GalleryItem;
import bpaulino.com.br.photogallery.util.QueryPreferences;

/**
 * Created by bruno on 12/14/15.
 */
public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final int POLL_INTERVAL = 1000 * 60; // 60 seconds

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if( !isNetworkAvailableAndConnected()) return;

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if(query == null) {
            items = new FlickrService().fetchRecentPhotos();
        }else {
            items = new FlickrService().searchPhotos(query);
        }

        if(items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();
        if(resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        }else {
            Log.i(TAG, "Got a new result: " + resultId);
            // Send a notification to user
            Resources resources = getResources();
            Intent it = PhotoGalleryActivity.newIntent(this);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, it, 0);

            Notification notification
                    = new NotificationCompat.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat noficationManager = NotificationManagerCompat.from(this);
            noficationManager.notify(0, notification);
        }

        QueryPreferences.setLastResultId(this, resultId);

    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent it = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, it, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(isOn) {
            alarmManager
                    .setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent it = PollService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, it, PendingIntent.FLAG_NO_CREATE);
        return  pendingIntent != null;
    }
}
