package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.Categories;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ChefAddItemFragment extends Fragment {
    View myView;
    TextView title_textview;
    AppBarLayout main_header;
    String category_id, parent_category_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_chef_add_item, container, false);

        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("ADD ITEM");
        main_header.setVisibility(View.VISIBLE);

        Bundle args = getArguments();
        parent_category_id = args.get("selected_category_id").toString();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Button add_button = (Button) myView.findViewById(R.id.add_item);

        Spinner dynamicSpinner = (Spinner) myView.findViewById(R.id.category_dropdown);

//        final Gson gson = new Gson();
//        String categories_string = SharedPreference.getInstance().getValue(getContext(), "categories");
//        final ArrayList<CategoriesBasicClass> categories = gson.fromJson(categories_string, new TypeToken<ArrayList<CategoriesBasicClass>>(){}.getType());


        Categories categories = new Categories();
        final ArrayList<CategoriesBasicClass> children = categories.getAllChildren(parent_category_id);

        String[] items = new String[children.size()] ;

        for(int i=0; i<children.size(); i++)
        {
            items[i] = children.get(i).getName();
        }



//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_spinner_item, items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item_layout, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_layout);
        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category_id = children.get(i).category_id;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int error = 0;
                String item_name, item_description, item_price, item_serving, chef_id, additional, time_taken;

                EditText item_name_textbox = (EditText) myView.findViewById(R.id.item_name);
                EditText item_description_textbox = (EditText) myView.findViewById(R.id.item_description);
                EditText item_price_textbox = (EditText) myView.findViewById(R.id.item_price);
                EditText item_serving_textbox = (EditText) myView.findViewById(R.id.item_serving);
                EditText additional_textbox = (EditText) myView.findViewById(R.id.additional);
                EditText time_taken_textbox = (EditText) myView.findViewById(R.id.time_taken);

                item_name = item_name_textbox.getText().toString();
                item_description = item_description_textbox.getText().toString();
                item_price = item_price_textbox.getText().toString();
                item_serving = item_serving_textbox.getText().toString();
                additional = additional_textbox.getText().toString();
                time_taken = time_taken_textbox.getText().toString();

                LoginBasicClass user = SharedPreference.getInstance().getUserObject(getContext());
                chef_id = user.user_id;



                if(error == 0)
                {
                    final MySpinner spinner = new MySpinner(getContext());
                    spinner.getProgressDialog().show();


                    String end_point = getResources().getString(R.string.end_point);

                    RestClient rest_client = new RestClient(end_point);
                    MyApi service = rest_client.getService().create(MyApi.class);

                    Call<JsonObject> call = service.saveItem(category_id, parent_category_id, chef_id, item_name, item_price, item_description, item_serving, additional, time_taken);

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
                                    fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).commit();
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


            }
        });
    }
}
