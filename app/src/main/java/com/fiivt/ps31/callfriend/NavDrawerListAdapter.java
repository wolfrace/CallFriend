package com.fiivt.ps31.callfriend;

/**
 * Created by YGV on 11.05.2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import lombok.Data;

public class NavDrawerListAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<NavDrawerItem> mNavDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        super(context, R.layout.drawer_list_item, navDrawerItems);
        this.mContext = context;
        this.mNavDrawerItems = navDrawerItems;
    }

    @Data
    private static class DrawerItemViewHolder {
        private TextView title;
        private ImageView icon;
        private FrameLayout divider;

        public void setDrawerItemValues(NavDrawerItem item) {
            title.setText(item.getTitle());
            icon.setImageResource(item.getIcon());
            divider.setBackgroundResource(item.getColor());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = getViewWithHolder(convertView, parent);
        DrawerItemViewHolder viewHolder = (DrawerItemViewHolder) view.getTag();
        NavDrawerItem item = mNavDrawerItems.get(position);
        viewHolder.setDrawerItemValues(item);
        return view;
    }

    private View getViewWithHolder(View convertView, ViewGroup parent) {
        if (convertView == null) {
            View view = createNewView(parent);
            initializeHolder(view);
            return view;
        } else {
            return convertView;
        }
    }

    private View createNewView(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.drawer_list_item, parent, false);
    }

    private DrawerItemViewHolder initializeHolder(View view) {
        DrawerItemViewHolder holder = new DrawerItemViewHolder();
        holder.setTitle((TextView) view.findViewById(R.id.drawer_item_title));
        holder.setIcon((ImageView) view.findViewById(R.id.drawer_item_icon));
        holder.setDivider((FrameLayout) view.findViewById(R.id.drawer_item_divider));
        view.setTag(holder);
        return holder;
    }

    @Override
    public int getCount() {
        return mNavDrawerItems.size();
    }
}

