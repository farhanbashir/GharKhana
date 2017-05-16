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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.JsonObject;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.bbq_button;
import static com.example.muhammadfarhanbashir.gharkhana.R.id.chinese_khaney_button;

/**
 * Created by muhammadfarhanbashir on 26/04/2017.
 */

public class TestFragment extends Fragment {

    View myView;
    TextView title_textview;
    ImageView toolbar_image;
    AppBarLayout main_header;
    ToggleButton updateLoc;
    Call<JsonObject> call;
    boolean canceled;
    SimpleLocation location;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_test, container, false);
        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        //updateLoc = (Button) myView.findViewById(R.id.updateLocation);
        updateLoc = (ToggleButton) myView.findViewById(R.id.updateLocation);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("MAIN MENU");
        main_header.setVisibility(View.VISIBLE);
        location = new SimpleLocation(getActivity());

        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);
        if (SharedPreference.getInstance().getLoggedIn(getContext())) {
            toolbar_image.setImageResource(R.drawable.icon_menu_white);
            RelativeLayout update_location_layout = (RelativeLayout) myView.findViewById(R.id.update_location_layout);

            LoginBasicClass user = SharedPreference.getInstance().getUserObject(getContext());

            if(user.role_id.equals(getResources().getString(R.string.chef_roleid)))
            {
                update_location_layout.setVisibility(View.GONE);
            }

        } else {
            toolbar_image.setVisibility(View.INVISIBLE);
        }

        updateLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
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

                            }else {
                                SharedPreference.getInstance().save(getActivity(), "lat", String.valueOf(location.getLatitude()));
                                SharedPreference.getInstance().save(getActivity(), "lng", String.valueOf(location.getLongitude()));

                                updateLoc.setEnabled(false);
                                Toast.makeText(getActivity(), "Location updated!", Toast.LENGTH_SHORT).show();

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
                        else {
                            SharedPreference.getInstance().save(getActivity(), "lat", String.valueOf(location.getLatitude()));
                            SharedPreference.getInstance().save(getActivity(), "lng", String.valueOf(location.getLongitude()));

                            updateLoc.setEnabled(false);
                            Toast.makeText(getActivity(), "Location updated!", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }
        });


        return myView;
    }

    private void redirectUserToItemsPage(LoginBasicClass user, Bundle bundle)
    {
        if(user.user_id == null)
        {
            ItemsFragment items = new ItemsFragment();
            items.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, items).addToBackStack(null).commit();
        }
        else if (user.role_id.contentEquals(getResources().getString(R.string.consumer_roleid)))
        {
            ItemsFragment items = new ItemsFragment();
            items.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, items).addToBackStack(null).commit();
        }
        else
        {
            ChefAddItemFragment items = new ChefAddItemFragment();
            items.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, items).addToBackStack(null).commit();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).openDrawer();

            }
        });


        Button pakistani_khaney_button = (Button) myView.findViewById(R.id.pakistani_khaney_button);
        Button chinese_khaney_button = (Button) myView.findViewById(R.id.chinese_khaney_button);
        Button bbq_button = (Button) myView.findViewById(R.id.bbq_button);
        Button seafood_button = (Button) myView.findViewById(R.id.seafood_button);
        Button desserts_button = (Button) myView.findViewById(R.id.desserts_button);
        Button others_button = (Button) myView.findViewById(R.id.others_button);

        final LoginBasicClass user = SharedPreference.getInstance().getUserObject(getContext());
        final Bundle bundle = new Bundle();

        pakistani_khaney_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String category_id = "1";
                String category_name = "Pakistani Khaney";
                bundle.putString("selected_category_id", category_id);
                bundle.putString("selected_category", category_name);

                redirectUserToItemsPage(user, bundle);

            }
        });

        chinese_khaney_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_id = "12";
                String category_name = "Chinese Khaney";
                bundle.putString("selected_category_id", category_id);
                bundle.putString("selected_category", category_name);

                redirectUserToItemsPage(user, bundle);
            }
        });

        bbq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_id = "22";
                String category_name = "B.B.Q";
                bundle.putString("selected_category_id", category_id);
                bundle.putString("selected_category", category_name);

                redirectUserToItemsPage(user, bundle);
            }
        });

        seafood_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_id = "32";
                String category_name = "Seafood";
                bundle.putString("selected_category_id", category_id);
                bundle.putString("selected_category", category_name);

                redirectUserToItemsPage(user, bundle);
            }
        });

        desserts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_id = "40";
                String category_name = "Desserts";
                bundle.putString("selected_category_id", category_id);
                bundle.putString("selected_category", category_name);

                redirectUserToItemsPage(user, bundle);
            }
        });

        others_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_id = "47";
                String category_name = "Others";
                bundle.putString("selected_category_id", category_id);
                bundle.putString("selected_category", category_name);

                redirectUserToItemsPage(user, bundle);
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

                }else {

                    SharedPreference.getInstance().save(getActivity(), "lat", String.valueOf(location.getLatitude()));
                    SharedPreference.getInstance().save(getActivity(), "lng", String.valueOf(location.getLongitude()));

                    Toast.makeText(getActivity(), "Location updated!", Toast.LENGTH_SHORT).show();


                }

            }
        }

    }

}
