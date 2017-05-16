package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.OrderHistory;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        //TextView item_address;
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
            //holder.item_address = (TextView) view.findViewById(R.id.item_address);
            holder.item_price = (TextView) view.findViewById(R.id.item_price);
            holder.item_customer = (TextView) view.findViewById(R.id.item_customer);
            holder.item_date = (TextView) view.findViewById(R.id.item_date);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.item_name.setText(mOrders.get(position).getName());
        //holder.item_address.setText("Address: "+mOrders.get(position).address);
        holder.item_price.setText("PKR "+mOrders.get(position).getPrice());
        holder.item_customer.setText("Customer: "+mOrders.get(position).getCustomerName());

        holder.item_date.setText(MyUtils.formatMysqlDate(mOrders.get(position).created));

        final ToggleButton intimate_order_button = (ToggleButton) view.findViewById(R.id.intimate_button);

        if(mOrders.get(position).intimate_order.equals("1"))
        {
            intimate_order_button.setVisibility(View.INVISIBLE);
            //intimate_order_button.setChecked(true);
            //intimate_order_button.setEnabled(false);
        }

        intimate_order_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled, call here

                    final Gson gson = new Gson();
                    final MySpinner spinner = new MySpinner(mContext);
                    spinner.getProgressDialog().show();

                    String end_point = mContext.getResources().getString(R.string.end_point);

                    RestClient rest_client = new RestClient(end_point);
                    MyApi service = rest_client.getService().create(MyApi.class);

                    String order_id = mOrders.get(position).order_id;
                    Call<JsonObject> call = service.intimateOrder(order_id);

                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (spinner.getProgressDialog().isShowing()) {
                                spinner.getProgressDialog().dismiss();
                            }

                            HeaderClass header = gson.fromJson(response.body().get("header"), HeaderClass.class);

                            if (response.code() == 200)
                            {
                                if (header.error.equals("0"))
                                {
                                    intimate_order_button.setEnabled(false);
                                } else
                                {
                                    MyUtils.showAlert(mContext, header.message);
                                }
                            }
                            else
                            {
                                MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t)
                        {
                                if (spinner.getProgressDialog().isShowing())
                                {
                                    spinner.getProgressDialog().dismiss();
                                }
                                MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
                            }
                    });

                } else {
                    // The toggle is disabled

                }
            }
        });

        return view;
    }
}
