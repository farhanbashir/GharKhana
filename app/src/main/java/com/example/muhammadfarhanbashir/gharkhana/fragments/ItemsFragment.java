package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.adapters.ItemsAdapter;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.Categories;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.Items;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
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

public class ItemsFragment extends Fragment {
    View myView;
    TextView title_textview;
    ImageView toolbar_image;
    AppBarLayout main_header;
    String selected_category_id, selected_category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_items, container, false);

        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);

        Bundle args = getArguments();

        selected_category_id = args.get("selected_category_id").toString();
        Categories categories = new Categories();
        selected_category = categories.getCategoryName(selected_category_id);

        if(!SharedPreference.getInstance().getLoggedIn(getContext()))
        {
            SharedPreference.getInstance().save(getContext(), "selected_category_id", selected_category_id);
        }
        //selected_category = args.get("selected_category").toString();

        title_textview.setText(selected_category);



        main_header.setVisibility(View.VISIBLE);

        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

        toolbar_image.setImageResource(R.drawable.icon_back_white);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentManager.beginTransaction().replace(R.id.content_frame, new TestFragment()).addToBackStack(null).commit();

            }
        });

        toolbar_image.setVisibility(View.VISIBLE);

        getItems();

        return myView;
    }

    public void getItems()
    {
        final MySpinner spinner = new MySpinner(getContext());
        spinner.getProgressDialog().show();

        String end_point = getResources().getString(R.string.end_point);

        RestClient rest_client = new RestClient(end_point);
        MyApi service = rest_client.getService().create(MyApi.class);

        Call<JsonObject> call = service.getMenu(selected_category_id,
                                                SharedPreference.getInstance().getValue(getContext(), "lat"),
                                                SharedPreference.getInstance().getValue(getContext(), "lng"));

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
                        setItems(items);
                        Log.d("categories",items.get(0).item_name.toString());
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

    public void setItems(final ArrayList<Items> items)
    {
        ItemsAdapter itemsAdapter = new ItemsAdapter(getActivity(), items);
        ListView listview = (ListView) myView.findViewById(R.id.items_list);

        listview.setAdapter(itemsAdapter);
    }
}
