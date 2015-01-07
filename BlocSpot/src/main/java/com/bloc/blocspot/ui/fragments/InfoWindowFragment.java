package com.bloc.blocspot.ui.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.bloc.blocspot.utils.Constants;
import com.bloc.blocspot.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoWindowFragment extends DialogFragment {

    private String mId;
    private Context mContext;
    private PoiTable mPoiTable = new PoiTable();
    private TextView mNameField;
    private TextView mNoteField;
    private ImageButton mVisitedButton;
    private TextView mCatField;
    private Boolean mVisited;
    private String mName;
    private String mLat;
    private String mLng;
    private String mNote;
    private String mGeoId;

    public InfoWindowFragment() {} // Required empty public constructor

    public InfoWindowFragment(String id, String geoId, Context context) {
        this.mId = id;
        this.mContext = context;
        this.mGeoId = geoId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_window, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        mNameField = (TextView) rootView.findViewById(R.id.nameField);
        mNoteField = (TextView) rootView.findViewById(R.id.noteField);
        mVisitedButton = (ImageButton) rootView.findViewById(R.id.visitedButton);
        mCatField = (TextView) rootView.findViewById(R.id.categoryField);

        new GetPlaceInfo(mId).execute();

        mVisitedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BlocSpotActivity) mContext).editVisited(mId, !mVisited);
            }
        });

        ImageButton deletePoiButton = (ImageButton) rootView.findViewById(R.id.deleteButton);
        deletePoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BlocSpotActivity) mContext).deletePoi(mId, mGeoId);
                dismiss();
            }
        });

        ImageButton sharePoiButton = (ImageButton) rootView.findViewById(R.id.shareButton);
        sharePoiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BlocSpotActivity) mContext).shareLocation(mName, mLat, mLng);
            }
        });

        ImageButton editNoteButton = (ImageButton) rootView.findViewById(R.id.noteButton);
        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BlocSpotActivity) mContext).editNoteDialog(mId, mNote);
            }
        });

        TextView catTextView = (TextView) rootView.findViewById(R.id.categoryField);
        catTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BlocSpotActivity) mContext).changeCategory(mId);
            }
        });

        return rootView;
    }

    public void refreshInfoWindow(String id) {
        new GetPlaceInfo(id).execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class GetPlaceInfo extends AsyncTask<Void, Void, Cursor> {

        private String id;

        public GetPlaceInfo(String id) {
            this.id = id;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mPoiTable.poiSpecificQuery(id);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if(cursor.moveToFirst()) {
                mName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
                mNote = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_NOTE));
                Boolean visited = cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COLUMN_VISITED)) > 0;
                String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
                String catName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_NAME));
                mLat = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE));
                mLng = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE));

                mNameField.setText(mName);
                mNoteField.setText(mNote);
                mCatField.setText(catName);

                if(color != null && mCatField != null) {
                    Utils.setColorString(color, mCatField);
                }

                if(visited != null && visited) {
                    mVisitedButton.setImageDrawable(mContext.getResources()
                            .getDrawable(R.drawable.ic_check_on));
                    mVisited = true;
                }
                else if(visited != null && !visited) {
                    mVisitedButton.setImageDrawable(mContext.getResources()
                            .getDrawable(R.drawable.ic_check_off));
                    mVisited = false;
                }
            }
        }
    }

    public interface OnPoiListAdapterListener {
        public void editNoteDialog(String id, String note);
        public void editVisited(String id, Boolean visited);
        public void viewOnMap(String lat, String lng);
        public void deletePoi(String id);
        public void changeCategory(String id);
        public void shareLocation(String name, String lat, String lng);
    }

}
