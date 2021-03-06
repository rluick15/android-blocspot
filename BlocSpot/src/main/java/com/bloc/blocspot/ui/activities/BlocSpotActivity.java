package com.bloc.blocspot.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocspot.adapters.PoiListAdapter;
import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.geofence.GeofenceIntentService;
import com.bloc.blocspot.geofence.SimpleGeofenceStore;
import com.bloc.blocspot.ui.fragments.ChangeCategoryFragment;
import com.bloc.blocspot.ui.fragments.EditNoteFragment;
import com.bloc.blocspot.ui.fragments.FilterDialogFragment;
import com.bloc.blocspot.ui.fragments.InfoWindowFragment;
import com.bloc.blocspot.utils.Constants;
import com.bloc.blocspot.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 *
 *
 */
public class BlocSpotActivity extends FragmentActivity
        implements OnMapReadyCallback, FilterDialogFragment.OnFilterListener,
        EditNoteFragment.OnNoteUpdateListener, PoiListAdapter.OnPoiListAdapterListener,
        ChangeCategoryFragment.OnChangeCategoryListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location loc;
    private boolean mListState = true;
    private ListView mPoiList;
    private PoiTable mPoiTable = new PoiTable();
    private MapFragment mMapFragment;
    private String mFilter;
    private InfoWindowFragment mInfoWindowFragment;
    private boolean mInProgress;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;
    private ArrayList<Geofence> mCurrentGeofences;
    private ArrayList<String> mGeoIds;
    private SimpleGeofenceStore mGeofenceStorage;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.LIST_STATE, mListState);
        outState.putString(Constants.FILTER_TEXT, mFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setContext(this);
        setContentView(R.layout.activity_main);

        //check if the device is connected to the network and terminate the app if not
        Utils.checkIfConnected();

        //restore saved instances
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBoolean(Constants.LIST_STATE);
            mFilter = savedInstanceState.getString(Constants.FILTER_TEXT);
        }

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mPoiList = (ListView) findViewById(android.R.id.list);
        TextView emptyView = (TextView) findViewById(R.id.emptyListView);
        mPoiList.setEmptyView(emptyView); //set the empty listview

        checkCategoryPreference();

        //Initialize Geofencing Objects
        mGoogleApiClient = null;
        mGeofencePendingIntent = null;
        mInProgress = false;
        mGeoIds = new ArrayList<>();
        mGeofenceStorage = new SimpleGeofenceStore(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if(mListState) { //hide the map if the list state is selected
            getFragmentManager().beginTransaction().hide(mMapFragment).commit();
        }
        else { //hide the list if map is to be shown
            mPoiList.setVisibility(View.INVISIBLE);
        }
    }

    /*
    * Check if the network if connected to wifi or the mobile network
    *
    * return: Boolean true if connected, false if not
    */
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onStart() {
        super.onStart();
        initCompo();
        currentLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilters(mFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /*
    * Handle results returned to this Activity by other Activities started with
    * startActivityForResult(). In particular, the method onConnectionFailed() in
    * GeofenceRemover and GeofenceRequester may call startResolutionForResult() to
    * start an Activity that handles Google Play services problems. The result of this
    * call returns here, to onActivityResult.
    * calls
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        mInProgress = false;
                        beginAddGeofences(mGeoIds);
                        break;
                }
        }
    }

    /**
     * Start adding geofences. Save the geofences, then start adding them by requesting a
     * connection
     */
    private void beginAddGeofences(ArrayList<String> geoIds) {
        mCurrentGeofences = new ArrayList<>();
        if(geoIds.size() > 0) {
            for(String id : geoIds) {
               mCurrentGeofences.add(mGeofenceStorage.getGeofence(id).toGeofence());
            }

            if (!servicesConnected()) {
                return;
            }

            if (!mInProgress) {
                mInProgress = true;
                mGoogleApiClient.connect();
            }
            else { //retry
                mInProgress = false;
                beginAddGeofences(geoIds);
            }
        }
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getSupportFragmentManager(), Constants.APPTAG);
            }
            return false;
        }
    }

    /*
    * Create a PendingIntent that triggers an IntentService in your
    * app when a geofence transition occurs.
    */
    private PendingIntent getTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mGeofencePendingIntent = getTransitionPendingIntent();
        LocationServices.GeofencingApi
                .addGeofences(mGoogleApiClient, mCurrentGeofences, mGeofencePendingIntent)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if(!status.isSuccess()) {
                            Toast.makeText(BlocSpotActivity.this,
                                    getString(R.string.toast_geofences_failed), Toast.LENGTH_SHORT).show();
                        }
                        mInProgress = false;
                        mGoogleApiClient.disconnect();
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        mInProgress = false;
        mGoogleApiClient = null;
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mInProgress = false;
        if(connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException ignored) {}
        }
        else {
            int errorCode = connectionResult.getErrorCode();
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode, this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Geofence Detection");
            }
        }
    }

    /**
     * This method is called onCreate of the Main Activity. It checks if a shared preference
     * file has been created for the Category list and if not creates one with an array list
     * that contains one default category "Uncategorized"     *
     */
    private void checkCategoryPreference() {
        SharedPreferences sharedPrefs = getSharedPreferences(Constants.MAIN_PREFS, Context.MODE_PRIVATE);
        String json = sharedPrefs.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories = new Gson().fromJson(json, type);
        if(categories == null) {
            categories = new ArrayList<>();
            Category uncategorized = new Category(Constants.CATEGORY_UNCATEGORIZED, Constants.CYAN);
            categories.add(uncategorized);
            String jsonCat = new Gson().toJson(categories);
            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            prefsEditor.putString(Constants.CATEGORY_ARRAY, jsonCat);
            prefsEditor.apply();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {}

    @Override
    public void applyFilters(String name) {
        mFilter = name;
        currentLocation();
    }

    @Override
    public void updateNoteDb(final String id, final String note) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.updateNote(id, note);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_poi_updated),
                            Toast.LENGTH_LONG).show();
                        refreshList(id);
                    }
                });
            }
        }.start();
    }

    @Override
    public void editNoteDialog(String id, String note) {
        EditNoteFragment dialog = new EditNoteFragment(id, this, note);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void editVisited(final String id, final Boolean visited) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.updateVisited(id, visited);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_poi_updated),
                                Toast.LENGTH_LONG).show();
                        refreshList(id);
                    }
                });
            }
        }.start();
    }

    @Override
    public void viewOnMap(String lat, String lng) {
        getFragmentManager().beginTransaction().show(mMapFragment).commit();
        mPoiList.setVisibility(View.INVISIBLE);
        mListState = false;
        this.invalidateOptionsMenu();

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)) //current location
                .zoom(20) // Sets the zoom
                .tilt(0) // Sets the tilt of the camera to 30 degrees
                .build(); // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void deletePoi(final String id, final String geoId) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.deletePoi(id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, getString(R.string.toast_delete_poi),
                                Toast.LENGTH_LONG).show();
                        mGeofenceStorage.removeGeofence(geoId);
                        refreshList(id);
                    }
                });
            }
        }.start();
    }

    @Override
    public void changeCategory(String id) {
        ChangeCategoryFragment dialog = new ChangeCategoryFragment(id, this);
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void shareLocation(String name, String lat, String lng) {
        String newName = name.replace(" ", "+");
        String shareUrl = "https://www.google.com/maps/place/" + newName + "/@" + lat + "," + lng;
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType(Constants.INTENT_TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_SUBJECT, name);
        intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(intent, getString(R.string.intent_share_poi)));
    }

    @Override
    public void refreshList(String id) {
        currentLocation();
        if(mInfoWindowFragment != null) {
            mInfoWindowFragment.refreshInfoWindow(id);
        }
    }

    private class GetPlaces extends AsyncTask<Void, Void, Cursor> {

        private ProgressDialog dialog;
        private Context context;
        private String filter;

        public GetPlaces(Context context, String filter) {
            this.context = context;
            this.filter = filter;
            mGeoIds.clear();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage(getString(R.string.loading_message));
                dialog.isIndeterminate();
                dialog.show();
            }
            catch (Exception ignored) {}
        }

        @Override
        protected Cursor doInBackground(Void... arg0) {
            Cursor cursor = null;
            try {
                //check for a filter and if none exists run a regular query
                if(filter != null) {
                    cursor = mPoiTable.filterQuery(filter);
                }
                else {
                    cursor = mPoiTable.poiQuery();
                }
            } catch (Exception ignored) {}
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if (dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException ignored){}
            }

            PoiListAdapter adapter = new PoiListAdapter(BlocSpotActivity.this, cursor, loc);
            mPoiList.setAdapter(adapter);

            Cursor c;
            mMap.clear();
            for (int i = 0; i < cursor.getCount(); i++) {
                c = ((Cursor) adapter.getItem(i));
                mMap.addMarker(new MarkerOptions()
                        .title(c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_ID)))
                        .snippet(c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_GEO_ID)))
                        .position(new LatLng(c.getDouble(c.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE)),
                                c.getDouble(c.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE))))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(getMarkerColor(c))));
                mGeoIds.add(c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_GEO_ID)));
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(loc.getLatitude(), loc.getLongitude())) //current location
                    .zoom(14) // Sets the zoom
                    .tilt(0) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            beginAddGeofences(mGeoIds);
        }

        private float getMarkerColor(Cursor c) {
            float colorId = 0;
            String color = c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
            switch (color) {
                case Constants.CYAN:
                    colorId = BitmapDescriptorFactory.HUE_CYAN;
                    break;
                case Constants.BLUE:
                    colorId = BitmapDescriptorFactory.HUE_BLUE;
                    break;
                case Constants.GREEN:
                    colorId = BitmapDescriptorFactory.HUE_GREEN;
                    break;
                case Constants.MAGENTA:
                    colorId = BitmapDescriptorFactory.HUE_MAGENTA;
                    break;
                case Constants.ORANGE:
                    colorId = BitmapDescriptorFactory.HUE_ORANGE;
                    break;
                case Constants.RED:
                    colorId = BitmapDescriptorFactory.HUE_RED;
                    break;
                case Constants.ROSE:
                    colorId = BitmapDescriptorFactory.HUE_ROSE;
                    break;
                case Constants.VIOLET:
                    colorId = BitmapDescriptorFactory.HUE_VIOLET;
                    break;
                case Constants.YELLOW:
                    colorId = BitmapDescriptorFactory.HUE_YELLOW;
                    break;
            }
            return colorId;
        }
    }

    private void initCompo() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mInfoWindowFragment = new InfoWindowFragment(marker.getTitle(),
                        marker.getSnippet(), BlocSpotActivity.this);
                mInfoWindowFragment.show(getSupportFragmentManager(), "dialog");

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mListState) {
            getMenuInflater().inflate(R.menu.list_menu, menu);
        }
        if(!mListState) {
            getMenuInflater().inflate(R.menu.map_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_switch) {
            if (mListState) {
                getFragmentManager().beginTransaction().show(mMapFragment).commit();
                mPoiList.setVisibility(View.INVISIBLE);
                mListState = false;
            }
            else {
                getFragmentManager().beginTransaction().hide(mMapFragment).commit();
                mPoiList.setVisibility(View.VISIBLE);
                mListState = true;
            }
            this.invalidateOptionsMenu();
        }
        else if(id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.action_filter){
            FilterDialogFragment dialog = new FilterDialogFragment(this);
            dialog.show(getSupportFragmentManager(), "dialog");
        }

        return super.onOptionsItemSelected(item);
    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = locationManager.getBestProvider(new Criteria(), true);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            Toast.makeText(this, getString(R.string.toast_no_gps), Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        }
        else {
            loc = location;
            new GetPlaces(BlocSpotActivity.this, mFilter).execute();
        }
    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            loc = location;
            locationManager.removeUpdates(listener);
        }
    };

    /*
    * Define a DialogFragment to display the error dialog generated in
    * showErrorDialog.
    */
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}