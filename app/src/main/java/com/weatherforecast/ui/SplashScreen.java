package com.weatherforecast.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.weatherforecast.R;
import com.weatherforecast.databinding.ActivityMainBinding;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.SplashUiCallbacks;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.SplashViewModel;

import io.realm.Realm;

public class SplashScreen extends AppCompatActivity implements SplashUiCallbacks {
    ActivityMainBinding activityMainBinding;
    SplashViewModel splashViewModel;
    Realm realm;
    SplashUiCallbacks splashUiCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        splashUiCallbacks = this;
        initObjects();
    }

    void initObjects(){
        realm = realm.getDefaultInstance();
        ShowLogs.displayLog("Toatl no of weather row count " + realm.where(WeatherModel.class).findAll() + "");

        splashViewModel = new SplashViewModel(SplashScreen.this, realm, splashUiCallbacks);
    }

    @Override
    public void onSuccessfullyDataSavedInDb(boolean status) {
        if(status){
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void letUserRedirectToHome(boolean status) {
        if(status){
            SplashScreen.this.finish();
            startActivity(new Intent(SplashScreen.this, HomeScreen.class));
        }
    }
}