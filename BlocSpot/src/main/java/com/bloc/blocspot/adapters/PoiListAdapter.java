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
    private Boolean mVisited;
    private String mLat;
    private String mLng;
    private String mCatName;
    private String mCatColor;

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
        holder.visited = (TextView) mView.findViewById(R.id.visitedHolder);
        holder.lat = (TextView) mView.findViewById(R.id.latHolder);
        holder.lng = (TextView) mView.findViewById(R.id.lngHolder);
        holder.catName = (TextView) mView.findViewById(R.id.catNameHolder);
        holder.catColor = (TextView) mView.findViewById(R.id.catColorHolder);
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
        String catName = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_NAME));

        holder.name.setText(name);
        if(note != null) {
            holder.note.setText(note);
        }
        holder.id.setText(id);
        holder.lat.setText(String.valueOf(lat));
        holder.lng.setText(String.valueOf(lng));
        holder.catName.setText(catName);
        holder.catColor.setText(color);

        Location placeLoc = new Location("");
        placeLoc.setLatitude(lat);
        placeLoc.setLongitude(lng);
        float dist = (float) (mLoc.distanceTo(placeLoc) / 1609.34); //in miles
        holder.dist.setText(String.format("%.2f", dist) + " mi");

        if(visited != null && visited == true) {
            holder.checkMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_on));
            holder.visited.setText(Constants.TRUE);
        }
        else if(visited != null && visited == false) {
            holder.checkMark.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_off));
            holder.visited.setText(Constants.FALSE);
        }

        if(color != null && holder.color != null) {
            Utils.setColorString(color, holder.color);
        }

        //setup popup menu
        holder.threeDots.setFocusable(false);
        mPopupMenu = new PopupMenu(mContext, holder.threeDots);
        mPopupMenu.getMenu().add(Menu.NONE, 0, Menu.NONE, context.getString(R.string.popup_edit_note));
        mPopupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, context.getString(R.string.popup_visited));
        mPopupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, context.getString(R.string.popup_category));
        mPopupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, context.getString(R.string.popup_view_map));
        mPopupMenu.getMenu().add(Menu.NONE, 4, Menu.NONE, context.getString(R.string.popup_share));
        mPopupMenu.getMenu().add(Menu.NONE, 5, Menu.NONE, context.getString(R.string.popup_delete));

        holder.threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNote = holder.note.getText().toString();
                mCatColor = holder.catColor.getText().toString();
                mCatName = holder.catName.getText().toString();
                mId = holder.id.getText().toString();
                mLat = holder.lat.getText().toString();
                mLng = holder.lng.getText().toString();
                String tf = holder.visited.getText().toString();
                if(tf.equals(Constants.TRUE)){
                    mVisited = true;
                }
                else if(tf.equals(Constants.FALSE)) {
                    mVisited = false;
                }
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
                    case 1:
                        ((BlocSpotActivity) mContext).editVisited(mId, !mVisited);
                        break;
                    case 2:
                        ((BlocSpotActivity) mContext).changeCategory(mId, mCatName, mCatColor);
                        break;
                    case 3:
                        ((BlocSpotActivity) mContext).viewOnMap(mLat, mLng);
                        break;
                    case 4:
                        break;
                    case 5:
                        ((BlocSpotActivity) mContext).deletePoi(mId);
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
        TextView visited;
        TextView lat;
        TextView lng;
        TextView catName;
        TextView catColor;
    }

    public interface OnPoiListAdapterListener {
        public void editNoteDialog(String id, String note);
        public void editVisited(String id, Boolean visited);
        public void viewOnMap(String lat, String lng);
        public void deletePoi(String id);
        public void changeCategory(String id, String category, String catColor);
    }
}
