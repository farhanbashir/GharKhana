package com.example.muhammadfarhanbashir.gharkhana;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.example.muhammadfarhanbashir.gharkhana.helpers.DBHandler;
import com.example.muhammadfarhanbashir.gharkhana.helpers.MyUtils;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {


    public static FragmentManager fragmentManager;
    public static DBHandler db;


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

        //SharedPreference.getInstance().clearSharedPreference(this);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else
        {
            Criteria criteria = new Criteria();

            String provider = service.getBestProvider(criteria, false);
            // API 23: we have to check if ACCESS_FINE_LOCATION and/or ACCESS_COARSE_LOCATION permission are granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {

                // No one provider activated: prompt GPS
                if (provider == null || provider.equals(""))
                {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

                service.requestLocationUpdates(provider, 10000, 50, this);
                Location location = service.getLastKnownLocation(provider);
                // Initialize the location fields
                if (location != null)
                {
                    System.out.println("Provider " + provider + " has been selected.");
                    onLocationChanged(location);
                } else
                {
                    Log.d("error", "Location not available");
                }
                // One or both permissions are denied.
            }
            else
            {

            }

        }





        LoginBasicClass user = SharedPreference.getInstance().getUserObject(this);
        //redirection check
        if(SharedPreference.getInstance().getLoggedIn(this))
        {
            if(user.role_id.contentEquals(getResources().getString(R.string.consumer_roleid)))
            {
                if(!SharedPreference.getInstance().getValue(this, "selected_category_id").equals(""))
                {
                    String selected_category_id = SharedPreference.getInstance().getValue(this, "selected_category_id");
                    SharedPreference.getInstance().removeValue(this, "selected_category_id");

                    Bundle bundle = new Bundle();
                    bundle.putString("selected_category_id", selected_category_id);
                    ItemsFragment items = new ItemsFragment();
                    items.setArguments(bundle);

                    fragmentManager.beginTransaction().replace(R.id.content_frame, items).addToBackStack(null).commit();
                }
                else
                {
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new CategoriesFragment()).addToBackStack(null).commit();
                }


            }
            else
            {
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).addToBackStack(null).commit();
            }

        }
        else
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MainFragment()).commit();
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

        if(SharedPreference.getInstance().getLoggedIn(this))
        {
            Menu menu = navigationView.getMenu();

            if(user.role_id.contentEquals(getResources().getString(R.string.consumer_roleid)))
            {
                MenuItem nav_search_dish = menu.findItem(R.id.nav_search_dish);
                MenuItem nav_previous_orders = menu.findItem(R.id.nav_previous_orders);

                nav_search_dish.setVisible(true);
                nav_previous_orders.setVisible(true);
            }
            else
            {
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

        SharedPreference.getInstance().save(this, "lat", String.valueOf(location.getLatitude()));
        SharedPreference.getInstance().save(this, "lng", String.valueOf(location.getLongitude()));

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
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
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

        if (id == R.id.nav_my_account)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).addToBackStack(null).commit();
        }
        else if(id == R.id.nav_search_dish)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new CategoriesFragment()).addToBackStack(null).commit();
        }
        else if(id == R.id.nav_previous_orders)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ConsumerOrdersFragment()).addToBackStack(null).commit();
        }
        else if(id == R.id.nav_add_dish)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefAddItemFragment()).addToBackStack(null).commit();
        }
        else if(id == R.id.nav_my_dishes)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).addToBackStack(null).commit();
        }
        else if(id == R.id.nav_my_orders)
        {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefOrdersFragment()).addToBackStack(null).commit();
        }
        else if (id == R.id.nav_logout)
        {
            MyUtils.logout(this);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoOrderNow(View view)
    {
        SharedPreference.getInstance().save(this,"guest","true");
        fragmentManager.beginTransaction().replace(R.id.content_frame, new CategoriesFragment()).addToBackStack(null).commit();
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
    }

    public void gotoChefItems(View view)
    {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefItemsFragment()).addToBackStack(null).commit();
    }

    public void gotoChefOrders(View view)
    {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ChefOrdersFragment()).addToBackStack(null).commit();
    }

    public void gotoProfile(View view)
    {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).addToBackStack(null).commit();
    }

    public void gotoSignup(View view)
    {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new SignupFragment()).addToBackStack(null).commit();
//        Intent intent = new Intent(this, SignupActivity.class);
//        startActivity(intent);
    }

    public void gotoLogin(View view)
    {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).addToBackStack(null).commit();
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }

    public void openDrawer()
    {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
    }
}
