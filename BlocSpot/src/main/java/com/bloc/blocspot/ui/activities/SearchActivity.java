package com.bloc.blocspot.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.places.PlacesService;
import com.bloc.blocspot.utils.Constants;

import java.util.ArrayList;

public class SearchActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    private LocationManager locationManager;
    private Location loc;
    private String[] places;
    private ListView mSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        places = getResources().getStringArray(R.array.places);
        currentLocation();

        mSearchList = (ListView) findViewById(R.id.searchList);

//        if (loc != null) {
//            new GetPlaces(SearchActivity.this,
//                    places[0].toLowerCase().replace("-", "_").replace(" ", "_")).execute();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        //automatically expand and focus on the search view
        MenuItem searchViewItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.requestFocusFromTouch();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
            new GetPlaces(SearchActivity.this, "").execute();
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

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String searchText;

        public GetPlaces(Context context, String searchText) {
            this.context = context;
            this.searchText = searchText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.loading_message));
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    Constants.API_KEY);
            ArrayList<Place> findPlaces = service.findPlaces(loc.getLatitude(),
                    loc.getLongitude(), searchText);

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
                resultName.add(i, result.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_expandable_list_item_1,
                    android.R.id.text1, resultName);
            mSearchList.setAdapter(adapter);
        }
    }
}
