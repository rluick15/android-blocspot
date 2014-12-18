package com.bloc.blocspot.adapters;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.ui.activities.BlocSpotActivity;
import com.bloc.blocspot.utils.Constants;
import com.bloc.blocspot.utils.Utils;

public class PoiListAdapter extends CursorAdapter {

    private Context mContext;
    private Cursor mCursor;
    private View mView;
    private final LayoutInflater inflater;
    private Location mLoc;
    private PopupMenu mPopupMenu;
    private String mId;
    private String mNote;

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
        holder.id = (TextView) mView.findViewById(R.id.idHolder);
        mView.setTag(holder);

        return mView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        String name = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_POI_NAME));
        String id = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_ID));
        String note = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_NOTE));
        Boolean visited = cursor.getInt(cursor.getColumnIndex(Constants.TABLE_COLUMN_VISITED)) > 0;
        Double lat = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LATITUDE));
        Double lng = cursor.getDouble(cursor.getColumnIndex(Constants.TABLE_COLUMN_LONGITUDE));
        String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));

        holder.name.setText(name);
        if(note != null) {
            holder.note.setText(note);
        }
        holder.id.setText(id);

        Location placeLoc = new Location("");
        placeLoc.setLatitude(lat);
        placeLoc.setLongitude(lng);
        float dist = (float) (mLoc.distanceTo(placeLoc) / 1609.34); //in miles
        holder.dist.setText(String.format("%.2f", dist) + " mi");

        if(visited != null && visited == true) {
            holder.checkMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_on));
        }
        if(color != null && holder.color != null) {
            Utils.setColorString(color, holder.color);
        }

        //setup popup menu
        holder.threeDots.setFocusable(false);
        mPopupMenu = new PopupMenu(mContext, holder.threeDots);
        mPopupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, "Edit Note");
        mPopupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Mark as Visited");
        mPopupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "View on Map");
        mPopupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Share POI");
        mPopupMenu.getMenu().add(Menu.NONE, 4, Menu.NONE, "Delete Poi");

        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote = holder.note.getText().toString();
                mId = holder.id.getText().toString();
                mPopupMenu.show();
            }
        });

        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case 0:
                        ((BlocSpotActivity) mContext).editNoteDialog(mId, mNote);
                        break;
                }
                return false;
            }
        });
    }

    private static class ViewHolder {
        TextView name;
        TextView note;
        TextView dist;
        TextView color;
        ImageView checkMark;
        ImageButton threeDots;
        TextView id;
    }

    public interface OnPoiListAdapterListener {
        public void editNoteDialog(String id, String note);
    }
}
