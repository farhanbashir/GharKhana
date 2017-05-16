package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.adapters.ChefItemsAdapter;
import com.example.muhammadfarhanbashir.gharkhana.adapters.ChefOrdersAdapter;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ChefItemsFragment extends Fragment {
    View myView;
    TextView title_textview;
    AppBarLayout main_header;
    ImageView toolbar_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_chef_items, container, false);

        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("ITEMS");
        main_header.setVisibility(View.VISIBLE);

        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);
        if(SharedPreference.getInstance().getLoggedIn(getContext()))
        {
            toolbar_image.setImageResource(R.drawable.icon_menu_white);
        }

        getChefItems();

        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity)getActivity()).openDrawer();

            }
        });
    }

    public void getChefItems()
    {
        final MySpinner spinner = new MySpinner(getContext());
        spinner.getProgressDialog().show();


        String end_point = getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        LoginBasicClass user_detail = SharedPreference.getInstance().getUserObject(getContext());

        Call<JsonObject> call = service.getItems(user_detail.user_id);

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
                        ArrayList<Items> items = gson.fromJson(body.get("info"), new TypeToken<ArrayList<Items>>(){}.getType());
                        setChefItems(items);
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

    public void setChefItems(final ArrayList<Items> items)
    {
        ChefItemsAdapter chefItemsAdapter = new ChefItemsAdapter(getContext(), items);

        ListView listView = (ListView) myView.findViewById(R.id.chef_items_list);

        listView.setAdapter(chefItemsAdapter);
    }
}
