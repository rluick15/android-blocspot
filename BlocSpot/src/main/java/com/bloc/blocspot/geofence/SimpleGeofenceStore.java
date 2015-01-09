package com.bloc.blocspot.geofence;

import android.content.Context;
import android.content.SharedPreferences;

import com.bloc.blocspot.utils.Constants;

public class SimpleGeofenceStore {
    private final SharedPreferences mPrefs;

    public SimpleGeofenceStore(Context context) {
        mPrefs = context.getSharedPreferences(Constants.GEOFENCE_PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Returns a stored geofence by its id, or returns null
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     */
    public SimpleGeofence getGeofence(String id) {
        double lat = mPrefs.getFloat(getGeofenceFieldKey(id, Constants.KEY_LATITUDE),
                Constants.INVALID_FLOAT_VALUE);
        double lng = mPrefs.getFloat(getGeofenceFieldKey(id, Constants.KEY_LONGITUDE),
                Constants.INVALID_FLOAT_VALUE);
        float radius = mPrefs.getFloat(getGeofenceFieldKey(id, Constants.KEY_RADIUS),
                Constants.INVALID_FLOAT_VALUE);
        long expirationDuration = mPrefs.getLong(getGeofenceFieldKey(id, Constants.KEY_EXPIRATION_DURATION),
                Constants.INVALID_LONG_VALUE);
        int transitionType = mPrefs.getInt(getGeofenceFieldKey(id, Constants.KEY_TRANSITION_TYPE),
                Constants.INVALID_INT_VALUE);

        if (lat != Constants.INVALID_FLOAT_VALUE && lng != Constants.INVALID_FLOAT_VALUE &&
                radius != Constants.INVALID_FLOAT_VALUE &&
                expirationDuration != Constants.INVALID_LONG_VALUE &&
                transitionType != Constants.INVALID_INT_VALUE) {

            return new SimpleGeofence(id, lat, lng, radius, expirationDuration, transitionType);
        }
        else {
            return null;
        }
    }

    /**
     * Save a geofence.
     * @param geofence The SimpleGeofence containing the
     * values you want to save in SharedPreferences
     */
    public void setGeofence(String id, SimpleGeofence geofence) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(getGeofenceFieldKey(id, Constants.KEY_ID), id);
        editor.putFloat(getGeofenceFieldKey(id, Constants.KEY_LATITUDE), (float) geofence.getLatitude());
        editor.putFloat(getGeofenceFieldKey(id, Constants.KEY_LONGITUDE), (float) geofence.getLongitude());
        editor.putFloat(getGeofenceFieldKey(id, Constants.KEY_RADIUS), geofence.getRadius());
        editor.putLong(getGeofenceFieldKey(id, Constants.KEY_EXPIRATION_DURATION), geofence.getExpirationDuration());
        editor.putInt(getGeofenceFieldKey(id, Constants.KEY_TRANSITION_TYPE), geofence.getTransitionType());
        editor.commit();
    }

    public void removeGeofence(String id) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, Constants.KEY_TRANSITION_TYPE));
        editor.commit();
    }

    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id, String fieldName) {
        return Constants.KEY_PREFIX + "_" + id + "_" + fieldName;
    }
}