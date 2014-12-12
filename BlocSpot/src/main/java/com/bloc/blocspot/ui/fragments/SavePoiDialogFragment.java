package com.bloc.blocspot.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bloc.blocspot.adapters.SavePoiListAdapter;
import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SavePoiDialogFragment extends DialogFragment {

    private Place mPlace;
    private ListView mListView;
    private Context mContext;

    public SavePoiDialogFragment() {} // Required empty public constructor

    public SavePoiDialogFragment(Context context, Place place) {

        this.mPlace = place;
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_poi_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_save_poi_dialog));

        //get Category Array
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPrefs.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        ArrayList<Category> categories = new Gson().fromJson(json, type);

        mListView = (ListView) rootView.findViewById(R.id.categoryList);
        SavePoiListAdapter adapter = new SavePoiListAdapter(mContext, categories);
        mListView.setAdapter(adapter);

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
    public interface OnFragmentInteractionListener {
    }

}
