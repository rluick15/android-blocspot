package com.bloc.blocspot.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.places.Place;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateCategoryDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CreateCategoryDialogFragment extends DialogFragment {

    private ArrayList<Category> mCategories;
    private Place mPlace;
    private String mCatName;
    private Context mContext;

    public CreateCategoryDialogFragment() {} // Required empty public constructor

    public CreateCategoryDialogFragment(Place place, ArrayList<Category> categories, Context context) {
        this.mPlace = place;
        this.mCategories = categories;
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_category_dialog, container, false);
        getDialog().setTitle(getString(R.string.title_create_category));

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelCatButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavePoiDialogFragment poiDialog = new SavePoiDialogFragment(mContext, mPlace);
                poiDialog.show(getFragmentManager(), "dialog");
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
