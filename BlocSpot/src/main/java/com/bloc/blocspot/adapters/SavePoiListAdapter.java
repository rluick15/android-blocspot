package com.bloc.blocspot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.utils.Constants;

import java.util.ArrayList;

/**
 * This class is an adapter for displaying the category list in the save poi dialog fragment
 */
public class SavePoiListAdapter extends ArrayAdapter<Category> {

    private ArrayList<Category> mCategories;
    private Context mContext;

    public SavePoiListAdapter(Context context, ArrayList<Category> categories) {
        super(context, R.layout.adapter_save_poi, categories);

        this.mContext = context;
        this.mCategories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_poi, null);
            holder = new ViewHolder();
            holder.catText = (TextView) convertView.findViewById(R.id.catText);
            holder.background = (RelativeLayout) convertView.findViewById(R.id.layoutBackground);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.catText.setText(mCategories.get(position).getName());
        setColor(position, mCategories, holder.background);


        return convertView;
    }

    private void setColor(int position, ArrayList<Category> categories, RelativeLayout background) {
        String color = mCategories.get(position).getColor();

        if(color.equals(Constants.CYAN)) {
            background.setBackgroundColor(mContext.getResources().getColor(R.color.cyan));
        }
    }

    private static class ViewHolder {
        TextView catText;
        RelativeLayout background;
    }
}
