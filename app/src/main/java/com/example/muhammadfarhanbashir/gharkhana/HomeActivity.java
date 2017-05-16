package com.example.muhammadfarhanbashir.gharkhana;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadfarhanbashir.gharkhana.fragments.CategoriesFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.ChefAddItemFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.ChefItemsFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.ChefOrdersFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.ConsumerOrdersFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.ItemsFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.LoginFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.MainFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.ProfileFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.SignupFragment;
import com.example.muhammadfarhanbashir.gharkhana.fragments.TestFragment;
import com.example.muhammadfarhanbashir.gharkhana.helpers.DBHandler;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.RestClient;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.interfaces.MyApi;
import com.example.muhammadfarhanbashir.gharkhana.models.Categories;
import com.example.muhammadfarhanbashir.gharkhana.models.HeaderClass;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.makeText;
import static com.example.muhammadfarhanbashir.gharkhana.R.string.address;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    public static FragmentManager fragmentManager;
    public static DBHandler db;
    private SimpleLocation location;
    private static final int PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //if(!MyUtils.ifNetworkPresent(this))
        //{
        //MyUtils.showAlert(this, "Please connect to internet");
        //Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();
        //finish();
        //}

        setContentView(R.layout.activity_home);

        fragmentManager = getSupportFragmentManager();
        db = new DBHandler(this);
        location = new SimpleLocation(HomeActivity.this);


        //SharedPreference.getInstance().clearSharedPreference(this);

//        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
//        enabled = service
//                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST);
            }
            else
            {
                if (!location.hasLocationEnabled())
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                else
                {
                    SharedPreference.getInstance().save(this, "lat", String.valueOf(location.getLatitude()));
                    SharedPreference.getInstance().save(this, "lng", String.valueOf(location.getLongitude()));

                    String address = MyUtils.getCompleteAddressString(this, location.getLatitude(), location.getLongitude());
                    Log.d("address", address);
                    //Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();

                }
            }
        }
        else
        {
            if (!location.hasLocationEnabled())
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
            else
            {
                SharedPreference.getInstance().save(this, "lat", String.valueOf(location.getLatitude()));
                SharedPreference.getInstance().save(this, "lng", String.valueOf(location.getLongitude()));

                //makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();

            }
        }

//Log.d("device_id", FirebaseInstanceId.getInstance().getToken());

        LoginBasicClass user = SharedPreference.getInstance().getUserObject(this);
        //redirection check
        if (SharedPreference.getInstance().getLoggedIn(this)) {

            String device_token = SharedPreference.getInstance().getValue(this, "device_token");

            //update device token if refreshed
            if(user.device_id == null || !user.device_id.equals(device_token))
            {
                String end_point = getResources().getString(R.string.end_point);

                RestClient rest_client = new RestClient(end_point);
                MyApi service = rest_client.getService().create(MyApi.class);

                user.device_id = device_token;
                Gson gson = new Gson();
                SharedPreference.getInstance().save(this, "user", gson.toJson(user));

                Call<JsonObject> call = service.editUser(user.user_id, user.first_name, user.last_name, user.contact_number, user.email, user.address, device_token);

                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });
            }


            if (user.role_id.contentEquals(getResources().getString(R.string.consumer_roleid))) {
                if (!SharedPreference.getInstance().getValue(this, "selected_category_id").equals("")) {
                    String selected_category_id = SharedPreference.getInstance().getValue(this, "selected_category_id");
                    SharedPreference.getInstance().removeValue(this, "selected_category_id");

                    Bundle bundle = new Bundle();
                    bundle.putString("selected_category_id", selected_category_id);
                    ItemsFragment items = new ItemsFragment();
                    items.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.content_frame, items).commit();
                } else {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new TestFragment()).commit();
                }


            } else {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).commit();
            }

        } else {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new TestFragment()).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (SharedPreference.getInstance().getLoggedIn(this)) {
            Menu menu = navigationView.getMenu();

            SharedPreference.getInstance().save(this, "lat", user.latitude);
            SharedPreference.getInstance().save(this, "lng", user.longitude);

            View header_view = navigationView.inflateHeaderView(R.layout.nav_header_home);
            TextView drawer_username = (TextView) header_view.findViewById(R.id.drawer_username);
            drawer_username.setText(user.getFullName());

            if (user.role_id.contentEquals(getResources().getString(R.string.consumer_roleid))) {
                MenuItem nav_search_dish = menu.findItem(R.id.nav_search_dish);
                MenuItem nav_previous_orders = menu.findItem(R.id.nav_previous_orders);

                nav_search_dish.setVisible(true);
                nav_previous_orders.setVisible(true);
            } else {
                MenuItem nav_add_dish = menu.findItem(R.id.nav_add_dish);
                MenuItem nav_my_dishes = menu.findItem(R.id.nav_my_dishes);
                MenuItem nav_my_orders = menu.findItem(R.id.nav_my_orders);

                nav_add_dish.setVisible(true);
                nav_my_dishes.setVisible(true);
                nav_my_orders.setVisible(true);
            }
        }


    }


    @Override
    public void onLocationChanged(Location location) {


//        double lat = (double) (location.getLatitude());
//        double lng = (double) (location.getLongitude());
//        Log.d("lat", lat+""+lng);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_account) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_search_dish) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new TestFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_previous_orders) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ConsumerOrdersFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_add_dish) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new TestFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_my_dishes) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_my_orders) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefOrdersFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_logout) {
            MyUtils.logout(this);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoOrderNow(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
            {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST);
            }
            else
            {
                if (!location.hasLocationEnabled())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                else
                {

                    SharedPreference.getInstance().save(this, "lat", String.valueOf(location.getLatitude()));
                    SharedPreference.getInstance().save(this, "lng", String.valueOf(location.getLongitude()));

                    Log.e("LAT", String.valueOf(location.getLatitude()));
                    Log.e("LNG", String.valueOf(location.getLongitude()));

                    SharedPreference.getInstance().save(this, "guest", "true");
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new TestFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
                }
            }
        }
        else
        {
            if (!location.hasLocationEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Ghar Khana wants your location to proceed, you need to turn on your GPS.");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.show();

            } else {

                SharedPreference.getInstance().save(this, "lat", String.valueOf(location.getLatitude()));
                SharedPreference.getInstance().save(this, "lng", String.valueOf(location.getLongitude()));

                Log.e("LAT", String.valueOf(location.getLatitude()));
                Log.e("LNG", String.valueOf(location.getLongitude()));

                SharedPreference.getInstance().save(this, "guest", "true");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new TestFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
            }
        }

    }

    public void gotoChefItems(View view) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).addToBackStack(null).commit();
    }

    public void gotoChefOrders(View view) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefOrdersFragment()).addToBackStack(null).commit();
    }

    public void gotoProfile(View view) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).addToBackStack(null).commit();
    }


    public void gotoSignup(View view) {

        SharedPreference.getInstance().removeValue(this, "guest");
        fragmentManager.beginTransaction().replace(R.id.content_frame, new SignupFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null).commit();
//        Intent intent = new Intent(this, SignupActivity.class);
//        startActivity(intent);

    }

    public void gotoLogin(View view) {

        fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null).commit();
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);

    }

    public void openDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                             String permissions[], int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (!location.hasLocationEnabled()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
        }else if (requestCode==2) {
            if (!location.hasLocationEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("Ghar Khana wants your location to proceed, you need to turn on your GPS.");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.show();

            } else {

                SharedPreference.getInstance().save(this, "lat", String.valueOf(location.getLatitude()));
                SharedPreference.getInstance().save(this, "lng", String.valueOf(location.getLongitude()));

                Log.e("LAT", String.valueOf(location.getLatitude()));
                Log.e("LNG", String.valueOf(location.getLongitude()));

                SharedPreference.getInstance().save(this, "guest", "true");
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, new TestFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
            }
        }

    }
}
