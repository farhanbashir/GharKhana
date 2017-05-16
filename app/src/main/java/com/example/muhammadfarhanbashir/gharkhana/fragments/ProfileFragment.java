package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.signup_button;

/**
 * Created by muhammadfarhanbashir on 01/03/2017.
 */

public class ProfileFragment extends Fragment {
    View myView;
    TextView title_textview;
    AppBarLayout main_header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_profile, container, false);

        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("PROFILE");
        main_header.setVisibility(View.VISIBLE);


        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Button update_button = (Button) myView.findViewById(R.id.update_profile_button);

        EditText first_name_textbox = (EditText) myView.findViewById(R.id.fname_textbox);
        EditText last_name_textbox = (EditText) myView.findViewById(R.id.lname_textbox);
        EditText phone_textbox = (EditText) myView.findViewById(R.id.number_textbox);
        EditText address_textbox = (EditText) myView.findViewById(R.id.address_textbox);
        EditText email_textbox = (EditText) myView.findViewById(R.id.email_textbox);

        TextView profile_notification_bar = (TextView) myView.findViewById(R.id.profile_notification_bar);

        Gson gson = new Gson();
        String user_string = SharedPreference.getInstance().getValue(getContext(), "user");
        LoginBasicClass user_details = gson.fromJson(user_string, LoginBasicClass.class);

        first_name_textbox.setText(user_details.first_name);
        last_name_textbox.setText(user_details.last_name);
        phone_textbox.setText(user_details.contact_number);
        address_textbox.setText(user_details.address);
        email_textbox.setText(user_details.email);

        if(user_details.role_id.equals(getResources().getString(R.string.consumer_roleid)))
        {
            profile_notification_bar.setText("You have registered as CONSUMER");
        }
        else
        {
            profile_notification_bar.setText("You have registered as CHEF");
        }

        //update work
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int error = 0;
                String fname, lname, phone, address, email;

                EditText first_name_textbox = (EditText) myView.findViewById(R.id.fname_textbox);
                EditText last_name_textbox = (EditText) myView.findViewById(R.id.lname_textbox);
                EditText phone_textbox = (EditText) myView.findViewById(R.id.number_textbox);
                EditText address_textbox = (EditText) myView.findViewById(R.id.address_textbox);
                EditText email_textbox = (EditText) myView.findViewById(R.id.email_textbox);

                fname = first_name_textbox.getText().toString();
                lname = last_name_textbox.getText().toString();
                phone = phone_textbox.getText().toString();
                address = address_textbox.getText().toString();
                email = email_textbox.getText().toString();

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    error++;
                    email_textbox.setError("Email is not valid");
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


                if(error == 0)
                {
                    final MySpinner spinner = new MySpinner(getContext());
                    spinner.getProgressDialog().show();


                    String end_point = getResources().getString(R.string.end_point);

                    RestClient rest_client = new RestClient(end_point);
                    MyApi service = rest_client.getService().create(MyApi.class);

                    LoginBasicClass user = SharedPreference.getInstance().getUserObject(getContext());
                    String user_id = user.user_id;

                    String device_id = SharedPreference.getInstance().getValue(getContext(), "device_token");
                    if(device_id.equals(""))
                    {
                        device_id = FirebaseInstanceId.getInstance().getToken();
                        SharedPreference.getInstance().save(getContext(), "device_token", device_id);
                    }
                    Call<JsonObject> call = service.editUser(user_id, fname, lname, phone, email, address, device_id);

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
                                    MyUtils.showAlert(getContext(), header.message);
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
