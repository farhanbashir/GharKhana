package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.add;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.lname_textbox;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.main_header;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.toolbar_image;
import static com.example.muhammadfarhanbashir.gharkhana.R.string.address;
import static com.example.muhammadfarhanbashir.gharkhana.R.string.email;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class SignupFragment extends Fragment {
    View myView;
    TextView title_textview;
    ImageView toolbar_image;
    AppBarLayout main_header;
    String role_id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_signup, container, false);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title_textview.setText("REGISTER");
        main_header.setVisibility(View.VISIBLE);
        //if(SharedPreference.getInstance().getLoggedIn(getContext()))
        {
            toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

            toolbar_image.setImageResource(R.drawable.icon_back_white);
            toolbar_image.setVisibility(View.VISIBLE);
        }


        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).addToBackStack(null).commit();

            }
        });

        //default value for radio
        role_id = getResources().getString(R.string.consumer_roleid);


        Button signup_button = (Button) myView.findViewById(R.id.signup_button);

        RadioButton consumer_radio =(RadioButton) myView.findViewById(R.id.consumer_radio);
        RadioButton chef_radio =(RadioButton) myView.findViewById(R.id.chef_radio);

        consumer_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                role_id = getResources().getString(R.string.consumer_roleid);
            }
        });

        chef_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                role_id = getResources().getString(R.string.chef_roleid);
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int error = 0;
                String fname, lname, phone, address, email, password;
                String latitude = SharedPreference.getInstance().getValue(getContext(), "lat");
                String longiitude = SharedPreference.getInstance().getValue(getContext(), "lng");

                EditText first_name_textbox = (EditText) myView.findViewById(R.id.fname_textbox);
                EditText last_name_textbox = (EditText) myView.findViewById(R.id.lname_textbox);
                EditText phone_textbox = (EditText) myView.findViewById(R.id.number_textbox);
                EditText address_textbox = (EditText) myView.findViewById(R.id.address_textbox);
                EditText email_textbox = (EditText) myView.findViewById(R.id.email_textbox);
                EditText password_textbox = (EditText) myView.findViewById(R.id.password_textbox);
                RadioButton role_radio = (RadioButton) myView.findViewById(R.id.consumer_radio);

                fname = first_name_textbox.getText().toString();
                lname = last_name_textbox.getText().toString();
                phone = phone_textbox.getText().toString();
                address = address_textbox.getText().toString();
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

                if(phone.equals(""))
                {
                    error++;
                    phone_textbox.setError("Please enter valid phone number");
                }

                if(address.equals(""))
                {
                    error++;
                    address_textbox.setError("Please enter address");
                }

                if(fname.equals(""))
                {
                    error++;
                    first_name_textbox.setError("Please enter first name");
                }

                if(lname.equals(""))
                {
                    error++;
                    last_name_textbox.setError("Please enter last name");
                }

                if(role_id.equals(""))
                {
                    error++;
                    role_radio.setError("Select Role");
                }

                if(error == 0)
                {
                    final MySpinner spinner = new MySpinner(getContext());
                    spinner.getProgressDialog().show();


                    String end_point = getResources().getString(R.string.end_point);

                    RestClient rest_client = new RestClient(end_point);
                    MyApi service = rest_client.getService().create(MyApi.class);

                    Call<JsonObject> call = service.signup(fname, lname, phone, email, password, latitude, longiitude, role_id);

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
                                    SharedPreference.getInstance().save(getContext(), "user", gson.toJson(user));
                                    SharedPreference.getInstance().save(getContext(), "categories", gson.toJson(categories));
                                    SharedPreference.getInstance().setLoggedIn(getContext(), true);

                                    //clear the history and start new home activity
                                    Intent intent = new Intent(getContext(), HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    getContext().startActivity(intent);

                                    //fragmentManager.beginTransaction().replace(R.id.content_frame, new CategoriesFragment()).commit();
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
