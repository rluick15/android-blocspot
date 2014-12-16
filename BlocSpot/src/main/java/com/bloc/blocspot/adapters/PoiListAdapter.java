package com.bloc.blocspot.adapters;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.utils.Constants;
import com.bloc.blocspot.utils.Utils;

public class PoiListAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private View mView;
    private final LayoutInflater inflater;
    private Location mLoc;

    public PoiListAdapter(Context context, Cursor c, Location loc) {
        super(context, c);
        this.mContext = context;
        this.mCursor = c;
        this.inflater = LayoutInflater.from(context);
        this.mLoc = loc;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        mView = inflater.inflate(R.layout.adapter_poi_list, null);

        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) mView.findViewById(R.id.placeName);
        holder.note = (TextView) mView.findViewById(R.id.noteText);
        holder.checkMark = (ImageView) mView.findViewById(R.id.checkImage);
        holder.dist = (TextView) mView.findViewById(R.id.placeDist);
        holder.threeDots = (ImageButton) mView.findViewById(R.id.threeDots);
        holder.color = (TextView) mView.findViewById(R.id.colorArea);
        mView.setTag(holder);

        return mView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        String name = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
        String note = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_NOTE));
        Boolean visited = cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COLUMN_VISITED)) > 0;
        Double lat = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE));
        Double lng = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE));
        String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));

        holder.name.setText(name);
        if(note != null) {
            holder.note.setText(note);
        }

        Location placeLoc = new Location("");
        placeLoc.setLatitude(lat);
        placeLoc.setLongitude(lng);
        float dist = (float) (mLoc.distanceTo(placeLoc) / 1609.34); //in miles
        holder.dist.setText(String.format("%.2f", dist) + " mi");

        if(visited != null && visited == true) {
            holder.checkMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_on));
        }

        Utils.setColorString(color, holder.color);
    }

    private static class ViewHolder {
        TextView name;
        TextView note;
        TextView dist;
        TextView color;
        ImageView checkMark;
        ImageButton threeDots;
    }
}
