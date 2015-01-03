package com.bloc.blocspot.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bloc.blocspot.adapters.SavePoiListAdapter;
import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.geofence.SimpleGeofence;
import com.bloc.blocspot.geofence.SimpleGeofenceStore;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.ui.activities.SearchActivity;
import com.bloc.blocspot.utils.Constants;
import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

public class SavePoiDialogFragment extends DialogFragment {

    private Place mPlace;
    private Context mContext;
    private Category mCategory;
    private PoiTable mPoiTable = new PoiTable();
    private SimpleGeofenceStore mGeofenceStorage;
    private SimpleGeofence mGeofence;

    public SavePoiDialogFragment() {} // Required empty public constructor

    public SavePoiDialogFragment(Context context, Place place) {
        this.mPlace = place;
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mGeofenceStorage = new SimpleGeofenceStore(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pick_category_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_save_poi_dialog));
        getDialog().setCanceledOnTouchOutside(true);

        //Todo: change background and color of title

        //set the save button to disabled until a category is selected
        final Button savePoiButton = (Button) rootView.findViewById(R.id.saveButton);
        savePoiButton.setText(R.string.button_save_poi);
        if(mCategory == null) {
            savePoiButton.setEnabled(false);
        }

        //get Category Array
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPrefs.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        final ArrayList<Category> categories = new Gson().fromJson(json, type);

        ListView listView = (ListView) rootView.findViewById(R.id.categoryList);
        final SavePoiListAdapter adapter = new SavePoiListAdapter(mContext, categories);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setSelected(true);
                mCategory = (Category) adapterView.getItemAtPosition(position);
                savePoiButton.setEnabled(true);
            }
        });

        Button newCatButton = (Button) rootView.findViewById(R.id.addButton);
        newCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCategoryDialogFragment dialogFragment =
                        new CreateCategoryDialogFragment(mPlace, categories, mContext, null);
                dialogFragment.show(getFragmentManager(), "dialog");
                dismiss();
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        savePoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = mPlace.getName();
                final Double lat = mPlace.getLatitude();
                final Double lng = mPlace.getLongitude();
                final String catName = mCategory.getName();
                final String catColor = mCategory.getColor();
                final String id = UUID.randomUUID().toString();
                mGeofence = new SimpleGeofence(id, lat, lng, Constants.GEOFENCE_RADIUS,
                        Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER);
                mGeofenceStorage.setGeofence(id, mGeofence);
                //Todo:how to attach the place name to the Geofence
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        mPoiTable.addNewPoi(name, lat, lng, catName, catColor, id);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, mContext.getString(R.string.toast_poi_saved),
                                        Toast.LENGTH_LONG).show();
                                ((SearchActivity) mContext).returnToMain();
                            }
                        });
                    }
                }.start();

                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSavePoiInteractionListener {
        public void returnToMain();
    }

}
