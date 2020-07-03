package com.weatherforecast.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.weatherforecast.R;
import com.weatherforecast.ui.fragments.HomeFragment;
import com.weatherforecast.ui.fragments.WeatherFragment;
import com.weatherforecast.utils.ShowLogs;

public class HomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    private final int REQUEST_LOCATION_DIALOG = 999;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_listing_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_top_cities, R.id.nav_weather, R.id.nav_exit)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }



    void replaceFragment(Fragment fragment, String fragmentTag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.drawer_layout, fragment, fragmentTag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(fragmentTag);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.city_listing_screen, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ShowLogs.displayLog("ShowLOg");
        switch (item.getItemId()) {
            case R.id.nav_top_cities:
                HomeFragment homeFragment = HomeFragment.newInstance();
                replaceFragment(homeFragment, "Home");
                return true;
            case R.id.nav_weather:
                WeatherFragment weatherFragment = WeatherFragment.newInstance();
                replaceFragment(weatherFragment, "Weather");
                return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.drawer_layout);
        Log.d("TAGGG", "GPS onActivityResult: ");
        if (fragment != null && fragment instanceof WeatherFragment){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAGGG", "GPS onRequestPermissionsResult: ");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.drawer_layout);
        if (fragment != null && fragment instanceof WeatherFragment){
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void locationEnableStatus(boolean status) {
        Fragment frg = getSupportFragmentManager()
                .findFragmentById(R.id.container);
        ShowLogs.displayLog("Location Enabled");
        if (frg != null && frg instanceof WeatherFragment) {
            WeatherFragment places = (WeatherFragment) frg;
            places.locationEnableStatus(status);
        }
    }
}