package com.bloc.blocspot.geofence;

import android.app.PendingIntent;
import android.content.Context;

import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class EditGeofences implements GeofencingApi {

    private Context mContext;
    private Boolean mInProgress;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;

    public EditGeofences(Context context) {
        // Save the context
        mContext = context;
        mGeofencePendingIntent = null;
        mGoogleApiClient = null;
        mInProgress = false;
    }

    /**
     * Set the "in progress" flag from a caller. This allows callers to re-set a
     * request that failed but was later fixed.
     *
     * @param flag Turn the in progress flag on or off.
     */
    public void setInProgressFlag(boolean flag) {
        mInProgress = flag;
    }

    /**
     * Get the current in progress status.
     *
     * @return The current value of the in progress flag.
     */
    public boolean getInProgressFlag() {
        return mInProgress;
    }

    @Override
    public PendingResult<Status> addGeofences(GoogleApiClient googleApiClient, List<Geofence> geofences, PendingIntent pendingIntent) {
        return null;
    }

    @Override
    public PendingResult<Status> addGeofences(GoogleApiClient googleApiClient, GeofencingRequest geofencingRequest, PendingIntent pendingIntent) {
        if (!((BlocSpotActivity) mContext).servicesConnected()) {
            return null;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) mContext)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) mContext)
                .build();

        if (!mInProgress) {
            mInProgress = true;
            mGoogleApiClient.connect();
        }
        else {
            //Todo:handle if request is underway
        }

        return null;
    }

    @Override
    public PendingResult<Status> removeGeofences(GoogleApiClient googleApiClient, PendingIntent pendingIntent) {
        return null;
    }

    @Override
    public PendingResult<Status> removeGeofences(GoogleApiClient googleApiClient, List<String> strings) {
        return null;
    }
}
