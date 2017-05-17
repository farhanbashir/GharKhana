package com.example.muhammadfarhanbashir.gharkhana.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.add;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.consumer_radio;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.lname_textbox;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.main_header;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.role_radio;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.toolbar_image;
import static com.example.muhammadfarhanbashir.gharkhana.R.string.address;
import static com.example.muhammadfarhanbashir.gharkhana.R.string.email;


public class SignupFragment extends Fragment {
    View myView;
    TextView title_textview;
    ImageView toolbar_image;
    AppBarLayout main_header;
    String role_id = "";
    SimpleLocation location;
    TextView last_name_heading;
    EditText last_name_textbox;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_signup, container, false);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        title_textview.setText("REGISTER");
        main_header.setVisibility(View.VISIBLE);
        location = new SimpleLocation(getActivity());
        //if(SharedPreference.getInstance().getLoggedIn(getContext()))
        {
            toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

            toolbar_image.setImageResource(R.drawable.icon_back_white);
            toolbar_image.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST);
            } else {
                if (!location.hasLocationEnabled()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            getFragmentManager().popBackStack();
                        }
                    });
                    builder.setMessage("Ghar Khana wants your location to proceed, you need to turn on your GPS.");
                    builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    builder.show();

                }
            }
        } else {
            if (!location.hasLocationEnabled()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        getFragmentManager().popBackStack();
                    }
                });
                builder.setMessage("Ghar Khana wants your location to proceed, you need to turn on your GPS.");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.show();

            }
        }


        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        last_name_textbox = (EditText) myView.findViewById(R.id.lname_textbox);
        last_name_heading = (TextView) myView.findViewById(R.id.lname_heading);

        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyUtils.hideSoftKeyboard(getActivity());
                fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).addToBackStack(null).commit();

            }
        });

        //default value for radio
        role_id = getResources().getString(R.string.consumer_roleid);


        Button signup_button = (Button) myView.findViewById(R.id.signup_button);

        if (SharedPreference.getInstance().getValue(getContext(), "guest").equals("true"))
        {
            role_id = getResources().getString(R.string.consumer_roleid);
            RadioGroup role_radio_group = (RadioGroup) myView.findViewById(R.id.role_radio);
            role_radio_group.setVisibility(View.GONE);

            TextView role_name_heading = (TextView) myView.findViewById(R.id.role_name_heading);
            role_name_heading.setVisibility(View.GONE);
        }
        else
        {
            RadioButton consumer_radio =(RadioButton) myView.findViewById(R.id.consumer_radio);
            RadioButton chef_radio =(RadioButton) myView.findViewById(R.id.chef_radio);

            consumer_radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    role_id = getResources().getString(R.string.consumer_roleid);
                    last_name_heading.setVisibility(View.GONE);
                    last_name_textbox.setVisibility(View.GONE);
                }
            });

            chef_radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    role_id = getResources().getString(R.string.chef_roleid);
                    last_name_heading.setVisibility(View.VISIBLE);
                    last_name_textbox.setVisibility(View.VISIBLE);
                }
            });
        }

        final TextView show_hide_password = (TextView) myView.findViewById(R.id.show_hide_password);
        final EditText password_textbox = (EditText) myView.findViewById(R.id.password_textbox);

        show_hide_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(view.getTag().toString().equals("show")) {
                    password_textbox.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    show_hide_password.setTag("hide");
                    show_hide_password.setText("(Hide Password)");
                } else {
                    password_textbox.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    show_hide_password.setTag("show");
                    show_hide_password.setText("(Show Password)");
                }


                password_textbox.setSelection(password_textbox.getText().length());

            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreference.getInstance().save(getActivity(), "lat", String.valueOf(location.getLatitude()));
                SharedPreference.getInstance().save(getActivity(), "lng", String.valueOf(location.getLongitude()));

                Log.e("LAT", String.valueOf(location.getLatitude()));
                Log.e("LNG", String.valueOf(location.getLongitude()));

                int error = 0;
                String fname, lname, phone, address, email, password;
                String latitude = SharedPreference.getInstance().getValue(getContext(), "lat");
                String longiitude = SharedPreference.getInstance().getValue(getContext(), "lng");

                EditText first_name_textbox = (EditText) myView.findViewById(R.id.fname_textbox);
                EditText phone_textbox = (EditText) myView.findViewById(R.id.number_textbox);
                EditText address_textbox = (EditText) myView.findViewById(R.id.address_textbox);
                EditText email_textbox = (EditText) myView.findViewById(R.id.email_textbox);

                //RadioButton role_radio = (RadioButton) myView.findViewById(R.id.consumer_radio);

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

                if(lname.equals("") && role_id.equals(getResources().getString(R.string.chef_roleid)))
                {
                    error++;
                    last_name_textbox.setError("Please enter nick name");
                }

//                if(role_id.equals(""))
//                {
//                    error++;
//                    role_radio.setError("Select Role");
//                }

                if(error == 0)
                {
                    final MySpinner spinner = new MySpinner(getContext());
                    spinner.getProgressDialog().show();


                    String end_point = getResources().getString(R.string.end_point);

                    RestClient rest_client = new RestClient(end_point);
                    MyApi service = rest_client.getService().create(MyApi.class);
                    String device_id = SharedPreference.getInstance().getValue(getContext(), "device_token");
                    if(device_id.equals(""))
                    {
                        device_id = FirebaseInstanceId.getInstance().getToken();
                        SharedPreference.getInstance().save(getContext(), "device_token", device_id);
                    }
                    Call<JsonObject> call = service.signup(fname, lname, phone, email, password, latitude, longiitude, address, role_id, device_id);

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
                                    SharedPreference.getInstance().save(getContext(), "user", gson.toJson(user));
                                    SharedPreference.getInstance().setLoggedIn(getContext(), true);
                                    SharedPreference.getInstance().removeValue(getContext(), "guest");

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (!location.hasLocationEnabled()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            getFragmentManager().popBackStack();
                        }
                    });
                    builder.setMessage("Ghar Khana wants your location to proceed, you need to turn on your GPS.");
                    builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    builder.show();

                }

            }
        }

    }

}
