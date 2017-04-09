package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class LoginFragment extends Fragment {
    View myView;
    TextView title_textview;
    EditText email_textbox;
    EditText password_textbox;
    Button login_button;
    AppBarLayout main_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_login, container, false);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        main_header.setVisibility(View.GONE);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        login_button = (Button) getActivity().findViewById(R.id.login_button);


        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email;
                String password;
                int error = 0;

                email_textbox = (EditText) myView.findViewById(R.id.email_textbox);
                password_textbox = (EditText) myView.findViewById(R.id.password_textbox);

                email = email_textbox.getText().toString();
                password = password_textbox.getText().toString();

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    error++;
                    email_textbox.setError("Email is not valid");
                }

                if(password.equals(""))
                {
                    error++;
                    password_textbox.setError("Please enter password");
                }

                if(error == 0)
                {
                    final MySpinner spinner = new MySpinner(getContext());
                    spinner.getProgressDialog().show();


                    String end_point = getResources().getString(R.string.end_point);

                    RestClient rest_client = new RestClient(end_point);
                    MyApi service = rest_client.getService().create(MyApi.class);

                    Call<JsonObject> call = service.login(email, password);
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
                                    JsonObject info = body.getAsJsonObject("info");
                                    LoginBasicClass user = gson.fromJson(info.get("user"), LoginBasicClass.class);
                                    ArrayList<CategoriesBasicClass> categories = gson.fromJson(info.get("categories"), new TypeToken<ArrayList<CategoriesBasicClass>>(){}.getType());

                                    //saving data in share preference
                                    //SharedPreference.getInstance().clearSharedPreference(getContext());
                                    SharedPreference.getInstance().removeValue(getContext(), "guest");
                                    SharedPreference.getInstance().save(getContext(), "user", gson.toJson(user));
                                    SharedPreference.getInstance().save(getContext(), "categories", gson.toJson(categories));
                                    SharedPreference.getInstance().setLoggedIn(getContext(), true);

                                    //clear the history and start new home activity
                                    Intent intent = new Intent(getContext(), HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getContext().startActivity(intent);

                                    //show hide menu
//                                    NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
//
//                                    Menu menu = navigationView.getMenu();
//
//                                    if(user.role_id.contentEquals(getResources().getString(R.string.consumer_roleid)))
//                                    {
//                                        MenuItem nav_search_dish = menu.findItem(R.id.nav_search_dish);
//                                        MenuItem nav_previous_orders = menu.findItem(R.id.nav_previous_orders);
//
//                                        nav_search_dish.setVisible(true);
//                                        nav_previous_orders.setVisible(true);
//
//                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new CategoriesFragment()).commit();
//                                    }
//                                    else
//                                    {
//                                        MenuItem nav_add_dish = menu.findItem(R.id.nav_add_dish);
//                                        MenuItem nav_my_dishes = menu.findItem(R.id.nav_my_dishes);
//                                        MenuItem nav_my_orders = menu.findItem(R.id.nav_my_orders);
//
//                                        nav_add_dish.setVisible(true);
//                                        nav_my_dishes.setVisible(true);
//                                        nav_my_orders.setVisible(true);
//
//                                        fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).commit();
//                                    }


                                    //MyUtils.redirectIfLoggedIn(getContext());
                                    //ArrayList<CategoriesBasicClass> categories = gson.fromJson(body.get("info"), new TypeToken<ArrayList<CategoriesBasicClass>>(){}.getType());

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
            }
        });



    }
}
