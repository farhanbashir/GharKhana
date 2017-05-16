package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.Items;
import com.example.muhammadfarhanbashir.gharkhana.models.OrderHistory;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.R.id.delete_item;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ChefItemsAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<Items> mItems;

    public ChefItemsAdapter(Context context, ArrayList<Items> items)
    {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mItems = items;
    }

    public class ViewHolder {
        TextView item_name;
        TextView item_description;
        TextView item_price;
        TextView item_date;
        TextView item_serving;
        TextView item_additional;
        TextView item_time_taken;
    }

    @Override
    public Items getItem(int position)
    {
        return mItems.get(position);
    }

    @Override
    public int getCount() {
        return mItems.size();
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
            view = inflater.inflate(R.layout.chef_items_listview_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.item_name = (TextView) view.findViewById(R.id.item_name);
            holder.item_description = (TextView) view.findViewById(R.id.item_description);
            holder.item_price = (TextView) view.findViewById(R.id.item_price);
            holder.item_date = (TextView) view.findViewById(R.id.item_date);
            holder.item_serving = (TextView) view.findViewById(R.id.item_serving);
            holder.item_additional = (TextView) view.findViewById(R.id.item_additional);
            holder.item_time_taken = (TextView) view.findViewById(R.id.item_time_taken);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.item_name.setText(mItems.get(position).getItem_name());
        holder.item_description.setText(mItems.get(position).getDescription());
        holder.item_price.setText("PKR "+mItems.get(position).getPrice());
        holder.item_serving.setText("Serving: "+mItems.get(position).getServing());
        holder.item_additional.setText("Additional: "+mItems.get(position).additional);
        holder.item_time_taken.setText("Time: "+mItems.get(position).time_taken);
        holder.item_date.setText(MyUtils.formatMysqlDate(mItems.get(position).created));

        Button delete_item = (Button) view.findViewById(R.id.delete_item);

        delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext);

                // set title
                //alertDialogBuilder.setTitle(R.string.alert);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure?")
                        .setCancelable(true)
                        .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                dialog.cancel();
                                deleteItem(position);
                            }
                        })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

            });

        return view;
    }

    public void deleteItem(final int position)
    {
        final MySpinner spinner = new MySpinner(mContext);
        spinner.getProgressDialog().show();


        String end_point = mContext.getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        String item_id = mItems.get(position).getItem_id();

        Call<JsonObject> call = service.deleteItem(item_id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(spinner.getProgressDialog().isShowing())
                {
                    spinner.getProgressDialog().dismiss();
                }

                Gson gson = new Gson();
                HeaderClass header = gson.fromJson(response.body().get("header"), HeaderClass.class);

                if(response.code() == 200)
                {
                    if(header.error.equals("0"))
                    {
                        mItems.remove(position);
                        notifyDataSetChanged();
                        //Log.d("categories",categories.get(0).description.toString());
                    }
                    else
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
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(spinner.getProgressDialog().isShowing())
                {
                    spinner.getProgressDialog().dismiss();
                }

                MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
            }
        });
    }
}
