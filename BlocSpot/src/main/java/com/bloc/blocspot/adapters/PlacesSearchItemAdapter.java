package com.bloc.blocspot.adapters;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.places.Place;
import com.bloc.blocspot.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PlacesSearchItemAdapter extends ArrayAdapter<Place>  {

    private Context mContext;
    private ArrayList<Place> mPlaceList;
    private Location mLoc;
    private PoiTable mPoiTable = new PoiTable();

    public PlacesSearchItemAdapter(Context context, ArrayList<Place> places, Location loc) {
        super(context, R.layout.adapter_places_search_item, places);

        mContext = context;
        mPlaceList = places;
        mLoc = loc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_places_search_item, null);
            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.placeName);
            holder.typeLabel = (TextView) convertView.findViewById(R.id.placeType);
            holder.distanceLabel = (TextView) convertView.findViewById(R.id.placeDist);
            holder.colorLabel = (TextView) convertView.findViewById(R.id.colorArea);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONArray placeType = mPlaceList.get(position).getTypes();
        String type = null;
        try {
            type = placeType.getString(0).replace(" ", " ").replace("_", " ");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Location placeLoc = new Location("");
        placeLoc.setLatitude(mPlaceList.get(position).getLatitude());
        placeLoc.setLongitude(mPlaceList.get(position).getLongitude());
        float dist = (float) (mLoc.distanceTo(placeLoc) / 1609.34); //in miles

        String typeCap = CapitalizeString(type);

        String name = mPlaceList.get(position).getName();
        holder.nameLabel.setText(name);
        holder.typeLabel.setText(typeCap);
        holder.distanceLabel.setText(String.format("%.2f", dist) + " mi");

        Cursor cursor = mPoiTable.alreadyPoiCheck(name);
        if(cursor.moveToFirst() && cursor.getCount() >= 1) {
            String color = cursor.getString(cursor.getColumnIndex(Constants.TABLE_COLUMN_CAT_COLOR));
            holder.colorLabel.setVisibility(View.VISIBLE);
            setColorString(color, holder.colorLabel);
        }
        else {
            holder.colorLabel.setVisibility(View.INVISIBLE);
        }
        cursor.close();

        return convertView;
    }

    private static String CapitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    private void setColorString(String color, TextView colorLabel) {
        if(color.equals(Constants.CYAN)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.cyan));
        }
        else if(color.equals(Constants.BLUE)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        }
        else if(color.equals(Constants.GREEN)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        }
        else if(color.equals(Constants.MAGENTA)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.magenta));
        }
        else if(color.equals(Constants.ORANGE)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
        }
        else if(color.equals(Constants.RED)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        }
        else if(color.equals(Constants.ROSE)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.rose));
        }
        else if(color.equals(Constants.VIOLET)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.violet));
        }
        else if(color.equals(Constants.YELLOW)) {
            colorLabel.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
        }
    }



//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//
//                FilterResults results = new FilterResults();
//                ArrayList<Place> tempList = new ArrayList<Place>();
//                if(constraint != null && mPlaceList != null) {
//                    int length = mPlaceList.size();
//                    int i = 0;
//                    while (i < length) {
//                        Place item = mPlaceList.get(i);
//                        tempList.add(item);
//                        i++;
//                    }
//                    results.values = tempList;
//                    results.count = tempList.size();
//                }
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults results) {
//                mPlaceList = (ArrayList<Place>) results.values;
//                if (results.count > 0) {
//                    notifyDataSetChanged();
//                }
//                else {
//                    notifyDataSetInvalidated();
//                }
//            }
//        };
//    }

    private static class ViewHolder {
        TextView nameLabel;
        TextView typeLabel;
        TextView distanceLabel;
        TextView colorLabel;
    }
}
