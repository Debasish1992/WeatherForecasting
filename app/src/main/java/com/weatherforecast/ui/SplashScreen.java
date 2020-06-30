package com.weatherforecast.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.weatherforecast.R;
import com.weatherforecast.databinding.ActivityMainBinding;
import com.weatherforecast.interfaces.SplashUiCallbacks;
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
}