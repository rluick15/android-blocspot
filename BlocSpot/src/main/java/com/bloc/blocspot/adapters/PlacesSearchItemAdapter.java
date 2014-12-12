package com.bloc.blocspot.adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.places.Place;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PlacesSearchItemAdapter extends ArrayAdapter<Place> implements Filterable {

    private Context mContext;
    private ArrayList<Place> mPlaceList;
    private Location mLoc;

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

        holder.nameLabel.setText(mPlaceList.get(position).getName());
        holder.typeLabel.setText(typeCap);
        holder.distanceLabel.setText(String.format("%.2f", dist) + " mi");

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
    }
}
