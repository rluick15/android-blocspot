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

import com.bloc.blocspot.adapters.SavePoiListAdapter;
import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.bloc.blocspot.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This fragment allows the user to set the filters for displaying the poi on the list and map
 */
public class FilterDialogFragment extends DialogFragment {

    private Context mContext;
    private Category mCategory;

    private OnFilterListener mListener;

    public FilterDialogFragment() {} // Required empty public constructor

    public FilterDialogFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_filter));

        //set the filter button to disabled until a category is selected
        final Button filterButton = (Button) rootView.findViewById(R.id.filterButton);
        if(mCategory == null) {
            filterButton.setEnabled(false);
        }

        //get Category Array
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPrefs.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        final ArrayList<Category> categories = new Gson().fromJson(json, type);

        ListView listView = (ListView) rootView.findViewById(R.id.catList);
        final SavePoiListAdapter adapter = new SavePoiListAdapter(mContext, categories);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setSelected(true);
                mCategory = (Category) adapterView.getItemAtPosition(position);
                filterButton.setEnabled(true);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mCategory.getName();
                ((BlocSpotActivity) mContext).applyFilters(name);
                dismiss();
            }
        });

        Button resetButton = (Button) rootView.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BlocSpotActivity) mContext).applyFilters(null);
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFilterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFilterListener {
        public void applyFilters(String name);
    }

}
