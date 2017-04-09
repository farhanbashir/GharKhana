package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.fragments.CategoriesFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.SignupFragment;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.Items;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;

/**
 * Created by muhammadfarhanbashir on 03/03/2017.
 */

public class ItemsAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    ArrayList<Items> mItems;

    public ItemsAdapter(Context context, ArrayList<Items> items)
    {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mItems = items;
    }

    public class ViewHolder {
        TextView chef_name_textview;
        TextView item_name_textview;
        TextView price_textview;
        Button order_button;
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
            view = inflater.inflate(R.layout.items_listview_layout, null);
            // Locate the TextViews in listview_item.xml
            holder.chef_name_textview = (TextView) view.findViewById(R.id.chef_name_textview);
            holder.item_name_textview = (TextView) view.findViewById(R.id.item_name_textview);
            holder.price_textview = (TextView) view.findViewById(R.id.price_textview);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.chef_name_textview.setText(mItems.get(position).getChef_first_name());
        holder.item_name_textview.setText(mItems.get(position).getItem_name());
        holder.price_textview.setText("PKR "+mItems.get(position).getPrice());

        Button order_button = (Button) view.findViewById(R.id.order_button);

        order_button.setOnClickListener(new View.OnClickListener() {
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

                                //check if user is not guest
                                if(SharedPreference.getInstance().getValue(mContext, "guest").equals("true"))
                                {
                                    //save order detail and show registration
                                    SharedPreference.getInstance().save(mContext, "selected_category_id", mItems.get(position).category_id);
                                    fragmentManager.beginTransaction().replace(R.id.content_frame, new SignupFragment()).addToBackStack(null).commit();
                                }
                                else
                                {
                                    makeOrder(position);
                                }

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

    public void makeOrder(final int position)
    {
        final MySpinner spinner = new MySpinner(mContext);
        spinner.getProgressDialog().show();


        String end_point = mContext.getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        String item_id = mItems.get(position).getItem_id();

        LoginBasicClass user = SharedPreference.getInstance().getUserObject(mContext);

        String chef_id = mItems.get(position).getChef_id();
        String quantity = "1";

        Call<JsonObject> call = service.saveOrder(user.user_id, chef_id, item_id, quantity);

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
                        //redirect here
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
