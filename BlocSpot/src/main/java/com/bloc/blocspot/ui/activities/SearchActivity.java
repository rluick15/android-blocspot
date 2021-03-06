package com.bloc.blocspot.ui.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bloc.blocspot.adapters.PlacesSearchItemAdapter;
import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.places.PlacesService;
import com.bloc.blocspot.ui.fragments.SavePoiDialogFragment;
import com.bloc.blocspot.utils.Constants;
import com.bloc.blocspot.utils.Utils;

import java.util.ArrayList;

public class SearchActivity extends FragmentActivity implements SavePoiDialogFragment.OnSavePoiInteractionListener {

    private final String TAG = getClass().getSimpleName();

    private LocationManager locationManager;
    private Location loc;
    private ListView mSearchList;
    private String mQuery;
    PlacesSearchItemAdapter mAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.QUERY_TEXT, mQuery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setContext(this);
        setContentView(R.layout.activity_search);

        //check if the device is connected to the network and terminate the app if not
        Utils.checkIfConnected();

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(Constants.QUERY_TEXT);
        }

        //get the search view query string
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            mQuery = getIntent().getStringExtra(SearchManager.QUERY);
        }

        mSearchList = (ListView) findViewById(R.id.searchList);

        if(Utils.haveNetworkConnection()) {
            if (mQuery != null) {
                currentLocation(mQuery.toLowerCase().replace("-", "_").replace(" ", "_"));
            } else {
                currentLocation(Constants.EMPTY_STRING);
            }
        }

        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place place = (Place) adapterView.getItemAtPosition(position);
                SavePoiDialogFragment poiDialog = new SavePoiDialogFragment(SearchActivity.this, place);
                poiDialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setContext(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        //automatically expand and focus on the search view
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.requestFocusFromTouch();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        searchView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//                if(mAdapter != null) {
//                    mAdapter.getFilter().filter(charSequence);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });

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

    private void currentLocation(String query) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            Toast.makeText(this, getString(R.string.toast_no_gps), Toast.LENGTH_SHORT).show();
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        }
        else {
            loc = location;
            new GetPlaces(SearchActivity.this, query).execute();
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

    @Override
    public void returnToMain() {
        Intent intent = new Intent(this, BlocSpotActivity.class);
        startActivity(intent);
    }

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

            mAdapter = new PlacesSearchItemAdapter(context, result, loc);
            mSearchList.setTextFilterEnabled(true);
            mSearchList.setAdapter(mAdapter);
        }
    }
}
