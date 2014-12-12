package com.bloc.blocspot.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.utils.Constants;
import com.google.gson.Gson;

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
    private EditText mNameField;
    private RadioGroup mRadioGroup;
    private String mColorString;

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

        mNameField = (EditText) rootView.findViewById(R.id.newCatName);
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.colorSelect);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                mColorString = setColorString(id);
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.cancelCatButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavePoiDialogFragment poiDialog = new SavePoiDialogFragment(mContext, mPlace);
                poiDialog.show(getFragmentManager(), "dialog");
                dismiss();
            }
        });

        Button createButton = (Button) rootView.findViewById(R.id.addCatButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCatName = mNameField.getText().toString();

                if(mCatName.equals("")) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_no_category), Toast.LENGTH_LONG).show();
                }
                if(mColorString == null) {
                    Toast.makeText(mContext, mContext.getString(R.string.toast_select_color), Toast.LENGTH_LONG).show();
                }
                else {
                    mCategories.add(new Category(mCatName, mColorString));
                    String jsonCat = new Gson().toJson(mCategories);
                    SharedPreferences.Editor prefsEditor =
                            mContext.getSharedPreferences(Constants.MAIN_PREFS, 0).edit();
                    prefsEditor.putString(Constants.CATEGORY_ARRAY, jsonCat);
                    prefsEditor.commit();

                    SavePoiDialogFragment poiDialog = new SavePoiDialogFragment(mContext, mPlace);
                    poiDialog.show(getFragmentManager(), "dialog");
                    dismiss();
                }
            }
        });

        return rootView;
    }

    private String setColorString(int id) {
        if (id == R.id.blue){
            return Constants.BLUE;
        }
        else  if(id == R.id.green) {
            return Constants.GREEN;
        }
        else if(id == R.id.magenta) {
            return Constants.MAGENTA;
        }
        else if(id == R.id.orange) {
            return Constants.ORANGE;
        }
        else if(id == R.id.red) {
            return Constants.RED;
        }
        else if(id == R.id.rose) {
            return Constants.ROSE;
        }
        else if(id == R.id.violet) {
            return Constants.VIOLET;
        }
        else if(id == R.id.yellow) {
            return Constants.YELLOW;
        }
        return null;
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
