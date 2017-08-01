package com.example.wgu_companion.wgucompanion;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeAdapter extends ArrayAdapter<HomeItem>{

    Context context;
    int layoutResourceId;
    HomeItem data[] = null;

    public HomeAdapter(@NonNull Context context, @LayoutRes int layoutResourceId, HomeItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        HomeItemHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new HomeItemHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.home_list_icon);
            holder.txtTitle = (TextView)row.findViewById(R.id.home_list_text);

            row.setTag(holder);
        }
        else
        {
            holder = (HomeItemHolder)row.getTag();
        }

        HomeItem item = data[position];
        holder.txtTitle.setText(item.title);
        holder.imgIcon.setImageResource(item.icon);

        return row;
    }

    static class HomeItemHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
