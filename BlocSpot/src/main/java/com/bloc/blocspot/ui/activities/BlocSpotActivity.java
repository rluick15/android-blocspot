package com.bloc.blocspot.ui.activities;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.places.PlacesService;
import com.bloc.blocspot.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 *  This class is used to search places using Places API using keywords like police,hospital etc.
 *
 * @author Karn Shah
 * @Date   10/3/2013
 *
 */
public class BlocSpotActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private String[] places;
    private LocationManager locationManager;
    private Location loc;
    private boolean mListState = true;
    private MapFragment mMapFragment;
    private ListView mPoiList;
    private TextView mEmptyView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.LIST_STATE, mListState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBoolean(Constants.LIST_STATE);
        }

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mPoiList = (ListView) findViewById(android.R.id.list);
        mEmptyView = (TextView) findViewById(R.id.emptyListView);
        mPoiList.setEmptyView(mEmptyView); //set the empty listview

        initCompo();
        places = getResources().getStringArray(R.array.places);
        currentLocation();

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
                this, R.array.places, android.R.layout.simple_list_item_1),
                new ActionBar.OnNavigationListener() {
                    @Override
                    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                        Log.e(TAG, places[itemPosition].toLowerCase().replace("-", "_"));
                        if (loc != null) {
                            mMap.clear();
                            new GetPlaces(BlocSpotActivity.this,
                                    places[itemPosition].toLowerCase().replace(
                                    "-", "_").replace(" ", "_")).execute();
                        }
                        return true;
                    }
                });

        if(mListState == true) { //hide the map if the list state is selected
            getFragmentManager().beginTransaction().hide(mMapFragment).commit();
        }
        else if(mListState == false) { //hide the list if map is to be shown
            mPoiList.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlaces(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    "AIzaSyCdMYv2IzTm331hPXmgfUJCvvZmw9C2ZxI");
            ArrayList<Place> findPlaces = service.findPlaces(loc.getLatitude(), // 28.632808
                    loc.getLongitude(), places); // 77.218276

            for (int i = 0; i < findPlaces.size(); i++) {
                Place placeDetail = findPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);

            ArrayList<String> resultName = new ArrayList<String>();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            for (int i = 0; i < result.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName())
                        .position(new LatLng(result.get(i).getLatitude(),
                                result.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin))
                        .snippet(result.get(i).getVicinity()));

                resultName.add(i, result.get(i).getName());
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                            .get(0).getLongitude())) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_expandable_list_item_1,
                    android.R.id.text1, resultName);
            mPoiList.setAdapter(adapter);
        }
    }

    private void initCompo() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mListState == true) {
            getMenuInflater().inflate(R.menu.list_menu, menu);
        }
        if(mListState == false) {
            getMenuInflater().inflate(R.menu.map_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            if(mListState == true) {
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
            new GetPlaces(BlocSpotActivity.this, places[0].toLowerCase().replace(
                    "-", "_")).execute();
            Log.e(TAG, "location : " + location);
        }
    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            loc = location;
            locationManager.removeUpdates(listener);
        }
    };

}