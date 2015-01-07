package com.bloc.blocspot.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.bloc.blocspot.blocspot.BlocSpotApplication;
import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.bloc.blocspot.utils.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * This class receives geofence transition events from Location Services, in the
 * form of an Intent containing the transition type and geofence id(s) that triggered
 * the event.
 */
public class GeofenceIntentService extends IntentService {

    private PoiTable mPoiTable = new PoiTable();
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    public GeofenceIntentService() {
        super("GeofenceIntentService");
        mPrefs = BlocSpotApplication.get().getSharedPreferences(Constants.NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            int transition = geofencingEvent.getGeofenceTransition();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
                String[] geofenceIds = new String[geofenceList.size()];

                for (int i = 0; i < geofenceIds.length; i++) {
                    geofenceIds[i] = geofenceList.get(i).getRequestId();
                }

                String queryString = makePlaceholders(geofenceIds.length);
                new GetPlaceName(queryString, geofenceIds).execute();
            }
        }
    }

    /**
     * Creates a query string of question marks based upon the number of items in the geofenceId
     * array.
     */
    String makePlaceholders(int len) {
        if (len < 1) {
            return Constants.EMPTY_STRING;
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the main Activity.
     * The notifications will be sent once every 20 minutes if the user is within the geofence
     * and the app is reopened
     */
    private void sendNotification(String geoName, int i) {
        //Todo change back to 20 minutes
        if(System.currentTimeMillis() - mPrefs.getLong(geoName, 0) > 10
                || mPrefs.getLong(geoName, 0) == 0) {
            mEditor.putLong(geoName, System.currentTimeMillis());
            mEditor.commit();

            Intent notificationIntent = new Intent(getApplicationContext(), BlocSpotActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(BlocSpotActivity.class);
            stackBuilder.addNextIntent(notificationIntent);

            PendingIntent notificationPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(geoName)
                    .setAutoCancel(true)
                    .setContentText(getString(R.string.notification_poi))
                    .setContentIntent(notificationPendingIntent);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(i, builder.build());
        }
    }

    private class GetPlaceName extends AsyncTask<Void, Void, Cursor> {

        private String queryString;
        private String[] geofenceIds;

        public GetPlaceName(String queryString, String[] geofenceIds) {
            this.queryString = queryString;
            this.geofenceIds = geofenceIds;
        }

        @Override
        protected Cursor doInBackground(Void... params) {
            return mPoiTable.notificationQuery(queryString, geofenceIds);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            String geoName;
            int i = 0; //notification counter

            while (cursor.moveToNext()) {
                geoName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
                sendNotification(geoName, i);
                i++;
            }
        }
    }
}
