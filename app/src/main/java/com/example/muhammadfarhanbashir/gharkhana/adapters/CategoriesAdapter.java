package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;

import java.util.ArrayList;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class CategoriesAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<CategoriesBasicClass> mCategories;

    public CategoriesAdapter(Context context, ArrayList<CategoriesBasicClass> categories)
    {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mCategories = categories;
    }

    public class ViewHolder {
        TextView category_name;
    }

    @Override
    public CategoriesBasicClass getItem(int position)
    {
        return mCategories.get(position);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.categories_listview_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.category_name = (TextView) view.findViewById(R.id.category_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.category_name.setText(mCategories.get(position).getName());


        return view;
    }
}
