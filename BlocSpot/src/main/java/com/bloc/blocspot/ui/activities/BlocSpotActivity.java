package com.bloc.blocspot.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.bloc.blocspot.ui.fragments.ChangeCategoryFragment;
import com.bloc.blocspot.ui.fragments.EditNoteFragment;
import com.bloc.blocspot.ui.fragments.FilterDialogFragment;
import com.bloc.blocspot.ui.fragments.InfoWindowFragment;
import com.bloc.blocspot.utils.Constants;
import com.bloc.blocspot.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
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
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

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
    private PendingIntent mGeofenceRequestIntent;
    private boolean mInProgress;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.LIST_STATE, mListState);
        outState.putString(Constants.FILTER_TEXT, mFilter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBoolean(Constants.LIST_STATE);
            mFilter = savedInstanceState.getString(Constants.FILTER_TEXT);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mInProgress = false;

        Utils.setContext(this);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mPoiList = (ListView) findViewById(android.R.id.list);
        TextView emptyView = (TextView) findViewById(R.id.emptyListView);
        mPoiList.setEmptyView(emptyView); //set the empty listview

        checkCategoryPreference();

        initCompo();
        currentLocation();

        if(mListState) { //hide the map if the list state is selected
            getFragmentManager().beginTransaction().hide(mMapFragment).commit();
        }
        else { //hide the list if map is to be shown
            mPoiList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFilters(mFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setContext(null);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST:
//            /*
//             * If the result code is Activity.RESULT_OK, try
//             * to connect again
//             */
//                switch (resultCode) {
//                    case Activity.RESULT_OK :
//                    /*
//                     * Try the request again
//                     */
//                        break;
//                }
//        }
//    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Geofence Detection", "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        }
        else {
            // Get the error code
            int errorCode = connectionResult.getErrorCode();
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    errorCode,
                    this,
                    Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getFragmentManager(), "Geofence Detection");
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

    /**
     * This method is called onCreate of the Main Activity. It checks if a shared preference
     * file has been created for the Category list and if not creates one with an array list
     * that contains one default category "Uncategorized"     *
     */
    private void checkCategoryPreference() {
        SharedPreferences sharedPrefs = getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPrefs.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories = new Gson().fromJson(json, type);
        if(categories == null) {
            categories = new ArrayList<Category>();
            Category uncategorized = new Category(Constants.CATEGORY_UNCATEGORIZED, Constants.CYAN);
            categories.add(uncategorized);
            String jsonCat = new Gson().toJson(categories);
            SharedPreferences.Editor prefsEditor = sharedPrefs.edit();
            prefsEditor.putString(Constants.CATEGORY_ARRAY, jsonCat);
            prefsEditor.apply();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {} //Todo: Something goes here!

    @Override
    public void applyFilters(String name) {
        mFilter = name;
        new GetPlaces(BlocSpotActivity.this, name).execute();
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
                        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
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
                Log.e("ERROR", String.valueOf(visited));
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
    public void deletePoi(final String id) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mPoiTable.deletePoi(id);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlocSpotActivity.this, "POI Deleted!",
                                Toast.LENGTH_LONG).show();
                        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
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
        new GetPlaces(BlocSpotActivity.this, mFilter).execute();
        mInfoWindowFragment.refreshInfoWindow(id);
    }

    private class GetPlaces extends AsyncTask<Void, Void, Cursor> {

        private ProgressDialog dialog;
        private Context context;
        private String filter;
        private Exception ex;

        public GetPlaces(Context context, String filter) {
            this.context = context;
            this.filter = filter;
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
            catch (Exception e) {
                //dialog.dismiss();
                Log.e("ERROR_PRE", String.valueOf(e));
            }
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
            } catch (Exception e) {
                ex = e;
                Log.e("ERROR_DO", String.valueOf(ex));
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if(ex != null) {
                Log.e("ERROR_POST", String.valueOf(ex));
                dialog.dismiss();
            }

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
                        .position(new LatLng(c.getDouble(c.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE)),
                                c.getDouble(c.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE))))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(getMarkerColor(c))));
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(loc.getLatitude(), loc.getLongitude())) //current location
                    .zoom(14) // Sets the zoom
                    .tilt(0) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

        private float getMarkerColor(Cursor c) {
            float colorId = 0;
            String color = c.getString(c.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
            if(color.equals(Constants.CYAN)) {
                colorId = BitmapDescriptorFactory.HUE_CYAN;
            }
            else if(color.equals(Constants.BLUE)) {
                colorId = BitmapDescriptorFactory.HUE_BLUE;
            }
            else if(color.equals(Constants.GREEN)) {
                colorId = BitmapDescriptorFactory.HUE_GREEN;
            }
            else if(color.equals(Constants.MAGENTA)) {
                colorId = BitmapDescriptorFactory.HUE_MAGENTA;
            }
            else if(color.equals(Constants.ORANGE)) {
                colorId = BitmapDescriptorFactory.HUE_ORANGE;
            }
            else if(color.equals(Constants.RED)) {
                colorId = BitmapDescriptorFactory.HUE_RED;
            }
            else if(color.equals(Constants.ROSE)) {
                colorId = BitmapDescriptorFactory.HUE_ROSE;
            }
            else if(color.equals(Constants.VIOLET)) {
                colorId = BitmapDescriptorFactory.HUE_VIOLET;
            }
            else if(color.equals(Constants.YELLOW)) {
                colorId = BitmapDescriptorFactory.HUE_YELLOW;
            }
            return colorId;
        }
    }

    private void initCompo() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mInfoWindowFragment = new InfoWindowFragment(marker.getTitle(), BlocSpotActivity.this);
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
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        }
        else {
            loc = location;
            new GetPlaces(BlocSpotActivity.this, null).execute();
            Log.e(TAG, "location : " + location);
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

}