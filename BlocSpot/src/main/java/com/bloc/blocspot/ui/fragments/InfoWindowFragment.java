package com.bloc.blocspot.ui.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoWindowFragment extends DialogFragment {

    private String mId;
    private Context mContext;
    private PoiTable mPoiTable = new PoiTable();
    private TextView mNameField;
    private TextView mNoteField;

    public InfoWindowFragment() {} // Required empty public constructor

    public InfoWindowFragment(String id, Context context) {
        this.mId = id;
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_window, container, false);

        mNameField = (TextView) rootView.findViewById(R.id.nameField);
        mNoteField = (TextView) rootView.findViewById(R.id.noteField);
        Toast.makeText(mContext, mId, Toast.LENGTH_LONG).show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                final Cursor cursor = mPoiTable.poiSpecificQuery(mId);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(cursor.moveToFirst()) {
                            String name = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
                            String note = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_NOTE));
                            Boolean visited = cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COLUMN_VISITED)) > 0;
                            Double lat = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE));
                            Double lng = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE));
                            String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));

                            mNameField.setText(name);
                            mNoteField.setText(note);
                        }
                    }
                });
            }
        }.start();

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

}
