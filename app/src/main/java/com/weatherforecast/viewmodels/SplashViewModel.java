package com.weatherforecast.viewmodels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.weatherforecast.data.RealmManager;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.SplashUiCallbacks;
import com.weatherforecast.ui.SplashScreen;
import com.weatherforecast.utils.ParseJSON;
import com.weatherforecast.utils.ShowLogs;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

public class SplashViewModel extends ViewModel implements CitiAccessCallbacks {

    Activity act;
    CitiAccessCallbacks citiAccessCallbacks;
    Realm realm;
    RealmManager realmManager;
    SplashUiCallbacks splashUiCallbacks;

    public SplashViewModel(Activity activity, Realm realmObject, SplashUiCallbacks splashUiCallbacks) {
        this.citiAccessCallbacks = this;
        this.act = activity;
        this.realm = realmObject;
        this.splashUiCallbacks = splashUiCallbacks;
        realmManager = RealmManager.getInstance();

        // Getting the total no of cities from Realm
        realmManager.getTotalNoOfCities(realm, citiAccessCallbacks);
    }

    @Override
    public void onSuccessfulCityFetchedAndStoredInLocalDb() {

    }

    @Override
    public void getTotalNoOfCitiesFromLocalDb(int count) {
        ShowLogs.displayLog("The Row Count is "+count);
        try{
            if(count == 0){
                // Getting the String data from JSON
                String getCitiData = ParseJSON.loadJSONFromAsset(act);
                // Parsing the Array
                JSONArray citiArray = new JSONArray(getCitiData);
                for (int i = 0; i < citiArray.length(); i++) {
                    JSONObject getEachCity = citiArray.getJSONObject(i);
                    if(getEachCity != null){
                        // Saving the City Data into Realm
                        realmManager.saveCitiData(realm, getEachCity);
                    }
                }
                splashUiCallbacks.onSuccessfullyDataSavedInDb(true);
            }else{
                realmManager.getAllCities(realm, this);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void getAllCities(RealmResults<CityModel> cityModels) {
        ShowLogs.displayLog(cityModels.toString());
    }
}
