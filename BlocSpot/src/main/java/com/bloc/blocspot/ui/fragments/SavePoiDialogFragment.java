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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_poi_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_save_poi_dialog));

        //Todo: change background and color of title

        //get Category Array
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Constants.MAIN_PREFS, 0);
        String json = sharedPrefs.getString(Constants.CATEGORY_ARRAY, null);
        Type type = new TypeToken<ArrayList<Category>>(){}.getType();
        final ArrayList<Category> categories = new Gson().fromJson(json, type);

        mListView = (ListView) rootView.findViewById(R.id.categoryList);
        final SavePoiListAdapter adapter = new SavePoiListAdapter(mContext, categories);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_LONG).show();
                view.setSelected(true);
            }
        });

        Button newCatButton = (Button) rootView.findViewById(R.id.addButton);
        newCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCategoryDialogFragment dialogFragment =
                        new CreateCategoryDialogFragment(mPlace, categories, mContext);
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
