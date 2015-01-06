package com.bloc.blocspot.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class GeofenceIntentService extends IntentService {

    public GeofenceIntentService() {
        super("GeofenceIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            int transition = geofencingEvent.getGeofenceTransition();
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
                Log.e("GEOFENCES", String.valueOf(geofenceList));
                String[] geofenceIds = new String[geofenceList.size()];

                for (int i = 0; i < geofenceIds.length; i++) {
                    geofenceIds[i] = geofenceList.get(i).getRequestId();
                }


                sendNotification("");
            }
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the main Activity.
     *
     */
    private void sendNotification(String ids) {
        Intent notificationIntent = new Intent(getApplicationContext(), BlocSpotActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(BlocSpotActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("dssasdasdas")
                .setContentText("YAYAYAYAYA")
                .setContentIntent(notificationPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
}
