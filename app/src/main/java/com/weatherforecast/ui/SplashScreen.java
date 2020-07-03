package com.weatherforecast.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Toast;

import com.weatherforecast.R;
import com.weatherforecast.databinding.ActivityMainBinding;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.AlertActionClicked;
import com.weatherforecast.interfaces.ConnectionChecker;
import com.weatherforecast.interfaces.SplashUiCallbacks;
import com.weatherforecast.utils.ConnectivityManager;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.SplashViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;

public class SplashScreen extends AppCompatActivity implements SplashUiCallbacks, ConnectionChecker, AlertActionClicked {
    ActivityMainBinding activityMainBinding;
    SplashViewModel splashViewModel;
    Realm realm;
    SplashUiCallbacks splashUiCallbacks;
    ConnectionChecker connectionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportActionBar().hide();
        splashUiCallbacks = this;
        initObjects();
    }


    // Initializing objects
    void initObjects(){
        connectionChecker = this;
        realm = realm.getDefaultInstance();
        splashViewModel = new SplashViewModel(SplashScreen.this, realm, splashUiCallbacks, connectionChecker);
    }

    @Override
    public void onSuccessfullyDataSavedInDb(boolean status) {
    }

    @Override
    public void letUserRedirectToHome(boolean status) {
        if(status){
            redirectUser();
        }
    }

    @Override
    public void isConnected(boolean status) {
        if(!status)
            ShowLogs.displayAlertMessageNoInternet(SplashScreen.this,
                    getResources().getString(R.string.no_internet_message_title),
                    getResources().getString(R.string.no_internet_message),
                    this);

    }

    @Override
    public void onPositiveButtonClicked() {
        ConnectivityManager.turnOnMobileData(SplashScreen.this);
        redirectUser();
    }

    @Override
    public void onNegativeButtonClicked() {
        redirectUser();
    }


    // FUnction redirecting user from Splash screen to home screen
    void redirectUser(){
        SplashScreen.this.finish();
        startActivity(new Intent(SplashScreen.this, HomeScreen.class));
    }


}