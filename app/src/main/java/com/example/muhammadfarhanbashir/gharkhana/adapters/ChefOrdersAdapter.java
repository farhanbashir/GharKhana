package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.models.OrderHistory;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ChefOrdersAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<OrderHistory> mOrders;

    public ChefOrdersAdapter(Context context, ArrayList<OrderHistory> orders)
    {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mOrders = orders;
    }

    public class ViewHolder {
        TextView item_name;
        TextView item_date;
        //TextView item_quantity;
        TextView item_price;
        TextView item_customer;
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
            view = inflater.inflate(R.layout.chef_orders_listview_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.item_name = (TextView) view.findViewById(R.id.item_name);
            //holder.item_quantity = (TextView) view.findViewById(R.id.item_quantity);
            holder.item_price = (TextView) view.findViewById(R.id.item_price);
            holder.item_customer = (TextView) view.findViewById(R.id.item_customer);
            holder.item_date = (TextView) view.findViewById(R.id.item_date);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.item_name.setText(mOrders.get(position).getName());
        //holder.item_quantity.setText(mOrders.get(position).getQuantity());
        holder.item_price.setText("PKR "+mOrders.get(position).getPrice());
        holder.item_customer.setText(mOrders.get(position).getCustomerName());

        holder.item_date.setText(MyUtils.formatMysqlDate(mOrders.get(position).created));

        return view;
    }
}
