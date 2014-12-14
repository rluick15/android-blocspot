package com.bloc.blocspot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bloc.blocspot.blocspot.R;
import com.bloc.blocspot.categories.Category;
import com.bloc.blocspot.utils.Constants;

import java.util.ArrayList;

/**
 * This class is an adapter for displaying the category list in the save poi dialog fragment
 */
public class SavePoiListAdapter extends ArrayAdapter<Category> implements Checkable {

    private ArrayList<Category> mCategories;
    private Context mContext;
    private ArrayList<Boolean> itemChecked;
    private ViewHolder mHolder;

    public SavePoiListAdapter(Context context, ArrayList<Category> categories) {
        super(context, R.layout.adapter_save_poi, categories);

        this.mContext = context;
        this.mCategories = categories;
        itemChecked = new ArrayList<Boolean>();
    }

    @Override
    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_save_poi, null);
            mHolder = new ViewHolder();
            mHolder.catText = (TextView) convertView.findViewById(R.id.catText);
            mHolder.background = (RelativeLayout) convertView.findViewById(R.id.layoutBackground);
            convertView.setTag(mHolder);
        }
        else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.catText.setText(mCategories.get(position).getName());
        setColor(position, mCategories, mHolder.background);

        return convertView;
    }

    private void setColor(int position, ArrayList<Category> categories, RelativeLayout background) {
        String color = mCategories.get(position).getColor();

        if(color.equals(Constants.CYAN)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_cyan));
        }
        else if(color.equals(Constants.BLUE)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_blue));
        }
        else if(color.equals(Constants.GREEN)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_green));
        }
        else if(color.equals(Constants.MAGENTA)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_magenta));
        }
        else if(color.equals(Constants.ORANGE)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_orange));
        }
        else if(color.equals(Constants.RED)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_red));
        }
        else if(color.equals(Constants.ROSE)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_rose));
        }
        else if(color.equals(Constants.VIOLET)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_violet));
        }
        else if(color.equals(Constants.YELLOW)) {
            background.setBackground(mContext.getResources().getDrawable(R.drawable.clicked_yellow));
        }
    }

    @Override
    public void setChecked(boolean b) {
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void toggle() {

    }

    private static class ViewHolder {
        TextView catText;
        RelativeLayout background;
    }
}
