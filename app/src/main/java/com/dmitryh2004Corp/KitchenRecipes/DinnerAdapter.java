package com.dmitryh2004Corp.KitchenRecipes;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

public class DinnerAdapter extends BaseExpandableListAdapter {
    Context context;
    HashMap<String, List<Dinner>> dinners;
    List<String> categories;


    public interface OnChildItemClickListener {
        void onClick(Dinner clickedDinner);
    }

    OnChildItemClickListener listener;

    public void setListener(OnChildItemClickListener listener) {
        this.listener = listener;
    }

    public DinnerAdapter(Context context, List<String> categories, HashMap<String, List<Dinner>> dinners) {
        this.context = context;
        this.categories = categories;
        this.dinners = dinners;
    }

    @Override
    public int getGroupCount() {
        return dinners.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dinners.get(categories.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Dinner getChild(int groupPosition, int childPosition) {
        return dinners.get(categories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        String category = categories.get(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dinner_list_group, null);
        }

        ((TextView) view.findViewById(R.id.dinnerCategory)).setText(category);

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        String category = categories.get(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dinner_list_child, null);
        }

        String name = getChild(groupPosition, childPosition).getName();
        Bitmap image = getChild(groupPosition, childPosition).getImage();

        ((TextView) view.findViewById(R.id.dinnerName)).setText(name);
        if (image == null) {
            ((ImageView) view.findViewById(R.id.dinnerImage)).setImageResource(R.drawable.image_placeholder);
        }
        else {
            ((ImageView) view.findViewById(R.id.dinnerImage)).setImageBitmap(image);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(getChild(groupPosition, childPosition));
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
