package com.example.muhammadfarhanbashir.gharkhana.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.fragments.SignupFragment;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RangeTimePickerDialog;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.Items;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;

/**
 * Created by muhammadfarhanbashir on 03/03/2017.
 */

public class ItemsAdapter extends BaseAdapter {
    Activity mContext;
    LayoutInflater inflater;
    ArrayList<Items> mItems;
    MaterialRatingBar ratingBar;
    String delivery_time_string;

    public ItemsAdapter(Activity context, ArrayList<Items> items) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mItems = items;
    }

    public class ViewHolder {
        TextView chef_name_textview;
        TextView item_name_textview;
        TextView price_textview;
        RatingBar ratingBar;
        Button order_button;
        TextView item_description;
        //TextView chef_contact;
        TextView item_serving;
        TextView item_time_taken;
        TextView order_count;
        TextView additional;
    }

    @Override
    public Items getItem(int position) {
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
            holder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            holder.item_name_textview = (TextView) view.findViewById(R.id.item_name_textview);
            holder.price_textview = (TextView) view.findViewById(R.id.price_textview);
            holder.item_description = (TextView) view.findViewById(R.id.item_description);
            //holder.chef_contact = (TextView) view.findViewById(R.id.chef_contact);
            holder.item_serving = (TextView) view.findViewById(R.id.item_serving);
            holder.item_time_taken = (TextView) view.findViewById(R.id.item_time_taken);
            holder.additional = (TextView) view.findViewById(R.id.additional);


            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Set the results into TextViews
        holder.chef_name_textview.setText("Chef: " + mItems.get(position).getChef_last_name());
        holder.item_name_textview.setText(mItems.get(position).getItem_name());
        holder.price_textview.setText("PKR " + mItems.get(position).getPriceForConsumer());
        holder.item_description.setText(mItems.get(position).description);
        //holder.chef_contact.setText("Contact#: " + mItems.get(position).chef_contact_number);
        holder.item_time_taken.setText("Preparation Time: " + mItems.get(position).time_taken);
        holder.item_serving.setText("Serving: " + mItems.get(position).serving);
        holder.additional.setText("Additional: " + mItems.get(position).additional);

        if(mItems.get(position).getChefReview() == null)
        {
            holder.ratingBar.setRating(0);
        }
        else
        {
            holder.ratingBar.setRating(Float.parseFloat(mItems.get(position).getChefReview()));
        }

        final Button order_button = (Button) view.findViewById(R.id.order_button);

        if(mItems.get(position).order_count.equals("0"))
        {
            order_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //check if customer has any pending review


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            mContext);

                    if (SharedPreference.getInstance().getValue(mContext, "guest").equals("true"))
                    {
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Please register yourself.")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();

                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new SignupFragment()).addToBackStack(null).commit();

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                });

                    }
                    else
                    {
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Are you sure you want to place order?")
                                .setCancelable(true)
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();

                                        deliveryTimeDialog(position, order_button);
                                        //makeOrder(position);

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                });
                    }






                    // set title
                    //alertDialogBuilder.setTitle(R.string.alert);

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    Button positive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    positive.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 25);
                    negative.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) 25);

                }
            });
        }
        else
        {
            order_button.setText("Sold");
            order_button.setClickable(false);
        }


        return view;
    }

    private void submitReview(int position, String custome_id) {

        final MySpinner spinner = new MySpinner(mContext);
        spinner.getProgressDialog().show();


        String end_point = mContext.getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        LoginBasicClass user = SharedPreference.getInstance().getUserObject(mContext);

        String chef_id = mItems.get(position).getChef_id();

        Call<JsonObject> call = service.saveReview(chef_id, custome_id, String.valueOf(ratingBar.getRating()));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (spinner.getProgressDialog().isShowing()) {
                    spinner.getProgressDialog().dismiss();
                }

                Gson gson = new Gson();
                HeaderClass header = gson.fromJson(response.body().get("header"), HeaderClass.class);

                if (response.code() == 200) {
                    if (header.error.equals("0")) {

                        Toast.makeText(mContext, "Review submitted!", Toast.LENGTH_SHORT).show();

                    } else {
                        MyUtils.showAlert(mContext, header.message);
                    }
                } else {
                    MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (spinner.getProgressDialog().isShowing()) {
                    spinner.getProgressDialog().dismiss();
                }

                MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
            }
        });

    }

    /*private void deliveryTimeDialog(final int position, final Button order_button)
    {
        final android.support.v7.app.AlertDialog.Builder deliveryDialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = mContext.getLayoutInflater();
        View deliveryDialogView = inflater.inflate(R.layout.deliverytime, null);

        final TimePicker deliveryTimePicker = (TimePicker) deliveryDialogView.findViewById(R.id.deliveryTimePicker);
        deliveryTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // display a toast with changed values of time picker
                String AM_PM ;
                if(hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }

                delivery_time_string = hourOfDay+":"+minute;
                //Toast.makeText(mContext, delivery_time_string, Toast.LENGTH_SHORT).show();

            }
        });


        deliveryDialogBuilder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                makeOrder(position, order_button);

            }
        });

        deliveryDialogBuilder.setView(deliveryDialogView);
        deliveryDialogBuilder.setCancelable(false);
        deliveryDialogBuilder.create().show();



    }*/

    private void deliveryTimeDialog(final int position, final Button order_button)
    {
        final Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        final RangeTimePickerDialog mTimePicker;
        mTimePicker = new RangeTimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String AM_PM ;
                if(selectedHour < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }

                delivery_time_string = selectedHour+":"+minute;

            }
        }, hour, minute, true);//true = 24 hour time
        mTimePicker.setButton(RangeTimePickerDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(mContext, "Cancel: " + delivery_time_string, Toast.LENGTH_LONG).show();
                mTimePicker.dismiss();
            }
        });
        mTimePicker.setButton(RangeTimePickerDialog.BUTTON_POSITIVE, "Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTimePicker.dismiss();
                makeOrder(position, order_button);
                //Toast.makeText(mContext, "Submit: " + delivery_time_string, Toast.LENGTH_LONG).show();
            }
        });
        mTimePicker.setTitle("Select Time");
        mTimePicker.setMin(hour, minute);
        mTimePicker.setCancelable(false);
        mTimePicker.show();
    }

    private void rateDialog(final int position, final String customer_id) {

        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(mContext);

        LayoutInflater inflater = mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rate_dialog, null);
        ratingBar = (MaterialRatingBar) dialogView.findViewById(R.id.rating);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                submitReview(position, customer_id);

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.create().show();


    }

    public void makeOrder(final int position, final Button order_button) {
        final MySpinner spinner = new MySpinner(mContext);
        spinner.getProgressDialog().show();


        String end_point = mContext.getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        String item_id = mItems.get(position).getItem_id();

        final LoginBasicClass user = SharedPreference.getInstance().getUserObject(mContext);
        final String customer_id = user.user_id;

        String chef_id = mItems.get(position).getChef_id();
        String lat = SharedPreference.getInstance().getValue(mContext, "lat");
        String lng = SharedPreference.getInstance().getValue(mContext, "lng");
        Call<JsonObject> call = service.saveOrder(customer_id, chef_id, item_id, lat, lng, delivery_time_string);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (spinner.getProgressDialog().isShowing()) {
                    spinner.getProgressDialog().dismiss();
                }

                Gson gson = new Gson();
                HeaderClass header = gson.fromJson(response.body().get("header"), HeaderClass.class);

                if (response.code() == 200) {
                    if (header.error.equals("0")) {
                        //rateDialog(position, customer_id);
                        MyUtils.showAlert(mContext, "Order saved successfully");
                        order_button.setText("Sold");
                        order_button.setClickable(false);
                    } else {
                        MyUtils.showAlert(mContext, header.message);
                    }
                } else {
                    MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (spinner.getProgressDialog().isShowing()) {
                    spinner.getProgressDialog().dismiss();
                }

                MyUtils.showAlert(mContext, mContext.getResources().getString(R.string.error_text));
            }
        });
    }
}
