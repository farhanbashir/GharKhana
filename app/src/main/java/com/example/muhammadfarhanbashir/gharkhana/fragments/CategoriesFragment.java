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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadfarhanbashir.gharkhana.HomeActivity;
import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.adapters.CategoriesAdapter;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MySpinner;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.muhammadfarhanbashir.gharkhana.HomeActivity.fragmentManager;


public class CategoriesFragment extends Fragment {
    View myView;
    TextView title_textview;
    ImageView toolbar_image;
    AppBarLayout main_header;
    Button updateLoc;
    Call<JsonObject> call;
    boolean canceled;
    SimpleLocation location;
    private static final int PERMISSION_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_categories, container, false);
        title_textview = (TextView) getActivity().findViewById(R.id.toolbar_title);
        updateLoc = (Button) myView.findViewById(R.id.updateLocation);
        main_header = (AppBarLayout) getActivity().findViewById(R.id.main_header);
        title_textview.setText("AJJ KYA KHANA HAI?");
        main_header.setVisibility(View.VISIBLE);
        location = new SimpleLocation(getActivity());

        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);
        if (SharedPreference.getInstance().getLoggedIn(getContext())) {
            toolbar_image.setImageResource(R.drawable.icon_menu_white);
        } else {
            toolbar_image.setVisibility(View.INVISIBLE);
        }

        updateLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                            Toast.makeText(getActivity(), "Location updated!", Toast.LENGTH_SHORT).show();

                        }

                }

            }
        });

        getCategoriesData();

        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        toolbar_image = (ImageView) getActivity().findViewById(R.id.toolbar_image);

        toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) getActivity()).openDrawer();

            }
        });
    }

    public void getCategoriesData() {

        final Gson gson = new Gson();
//        String categories_string = SharedPreference.getInstance().getValue(getContext(), "categories");
//
//        if (!categories_string.equals(""))
//        {
//            ArrayList<CategoriesBasicClass> categories = gson.fromJson(categories_string, new TypeToken<ArrayList<CategoriesBasicClass>>() {
//            }.getType());
//            setCategories(categories);
//        }
//        else
//        {
            final MySpinner spinner = new MySpinner(getContext());
            spinner.getProgressDialog().show();
//            spinner.getProgressDialog().setCanceledOnTouchOutside(false);
//            spinner.getProgressDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialogInterface) {
//                    getFragmentManager().popBackStack();
//                }
//            });

            String end_point = getResources().getString(R.string.end_point);

            RestClient rest_client = new RestClient(end_point);
            MyApi service = rest_client.getService().create(MyApi.class);

            call = service.getCategories(SharedPreference.getInstance().getValue(getContext(), "lat"), SharedPreference.getInstance().getValue(getContext(), "lng"));

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (spinner.getProgressDialog().isShowing()) {
                        spinner.getProgressDialog().dismiss();
                    }

                    HeaderClass header = gson.fromJson(response.body().get("header"), HeaderClass.class);

                    if (response.code() == 200) {
                        if (header.error.equals("0")) {
                            JsonObject body = response.body().getAsJsonObject("body");
                            ArrayList<CategoriesBasicClass> categories = gson.fromJson(body.get("info"), new TypeToken<ArrayList<CategoriesBasicClass>>() {
                            }.getType());
                            //SharedPreference.getInstance().save(getContext(), "categories", gson.toJson(categories));
                            setCategories(categories);
                            //Log.d("categories",categories.get(0).description.toString());
                        } else {
                            MyUtils.showAlert(getContext(), header.message);
                        }
                    } else {
                        MyUtils.showAlert(getContext(), getResources().getString(R.string.error_text));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    if (!call.isCanceled()){
                        if (spinner.getProgressDialog().isShowing()) {
                            spinner.getProgressDialog().dismiss();
                        }
                        MyUtils.showAlert(getContext(), getResources().getString(R.string.error_text));
                }else {
                        canceled=true;
                    }
                }
            });
        //}


    }

    public void setCategories(final ArrayList<CategoriesBasicClass> categories) {
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), categories);
        ListView listview = (ListView) myView.findViewById(R.id.categories_list);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("selected_category_id", categories.get(i).category_id);
                bundle.putString("selected_category", categories.get(i).getName());
                ItemsFragment items = new ItemsFragment();
                items.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_frame, items).addToBackStack(null).commit();
                //Log.d("name",categories.get(i).category_id);
            }
        });

        listview.setAdapter(categoriesAdapter);
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
