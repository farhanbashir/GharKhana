package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.adapters.ChefOrdersAdapter;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.OrderHistory;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.email_textbox;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.login_button;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.password_textbox;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ChefOrdersFragment extends Fragment {
    View myView;
    TextView title_textview;
    AppBarLayout main_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_chef_orders, container, false);

        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("ORDERS");
        main_header.setVisibility(View.VISIBLE);

        getChefOrders();

        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

    }

    public void getChefOrders()
    {
        final MySpinner spinner = new MySpinner(getContext());
        spinner.getProgressDialog().show();


        String end_point = getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        LoginBasicClass user_detail = SharedPreference.getInstance().getUserObject(getContext());

        Call<JsonObject> call = service.chefHistory(user_detail.user_id);

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
                        JsonObject body = response.body().getAsJsonObject("body");
                        ArrayList<OrderHistory> orders = gson.fromJson(body.get("info"), new TypeToken<ArrayList<OrderHistory>>(){}.getType());
                        setChefOrders(orders);
                        //Log.d("categories",categories.get(0).description.toString());
                    }
                    else
                    {
                        MyUtils.showAlert(getContext(), header.message);
                    }
                }
                else
                {
                    MyUtils.showAlert(getContext(), getResources().getString(R.string.error_text));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(spinner.getProgressDialog().isShowing())
                {
                    spinner.getProgressDialog().dismiss();
                }

                MyUtils.showAlert(getContext(), getResources().getString(R.string.error_text));
            }
        });
    }

    public void setChefOrders(final ArrayList<OrderHistory> orders)
    {
        ChefOrdersAdapter chefOrdersAdapter = new ChefOrdersAdapter(getContext(), orders);

        ListView listView = (ListView) myView.findViewById(R.id.chef_orders_list);

        listView.setAdapter(chefOrdersAdapter);
    }
}
