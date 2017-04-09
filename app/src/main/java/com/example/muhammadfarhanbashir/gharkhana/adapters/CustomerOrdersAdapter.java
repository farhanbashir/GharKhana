package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.models.OrderHistory;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;

import java.util.ArrayList;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class CustomerOrdersAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<OrderHistory> mOrders;

    public CustomerOrdersAdapter(Context context, ArrayList<OrderHistory> orders)
    {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mOrders = orders;
    }

    public class ViewHolder {
        TextView item_name;
    }

    @Override
    public OrderHistory getItem(int position)
    {
        return mOrders.get(position);
    }

    @Override
    public int getCount() {
        return mOrders.size();
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
            view = inflater.inflate(R.layout.customer_orders_listview_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.item_name = (TextView) view.findViewById(R.id.item_name);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.item_name.setText(mOrders.get(position).getName());


        return view;
    }
}
