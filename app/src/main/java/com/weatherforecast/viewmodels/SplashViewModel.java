package com.weatherforecast.viewmodels;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.weatherforecast.data.RealmManager;
import com.weatherforecast.data.network.NetworkCalls;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.SplashUiCallbacks;
import com.weatherforecast.utils.NetworkConnectionChecker;
import com.weatherforecast.utils.ParseJSON;
import com.weatherforecast.utils.ShowLogs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
    public void onSuccessfulCityFetchedAndStoredInLocalDb(boolean status) {

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
                    if (getEachCity != null) {
                        // Saving the City Data into Realm
                        realmManager.saveCityData(realm, getEachCity, this);
                    }
                }
            } else {
                realmManager.getAllCities(realm, this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void getAllCities(RealmResults<CityModel> cityModels) {
        ShowLogs.displayLog(cityModels.toString());

        if (!cityModels.isEmpty()) {
            ArrayList<String> getAllCityIds = new ArrayList<>();
            for (int i = 0; i < cityModels.size(); i++) {
                getAllCityIds.add(cityModels.get(i).getId());
            }
            // Convert the List of String to String
            String idsStr = String.join(", ", getAllCityIds);
            idsStr = idsStr.replace(" ", "");
            ShowLogs.displayLog("All Ids are" + idsStr);
            getDataFromApi(idsStr);
        }
    }

    @Override
    public void fetchDataFromApi(String respondData) {
        ShowLogs.displayLog(respondData);
    }

    /**
     * Function responsible for fetching data from API
     *
     * @param cities
     */
    void getDataFromApi(String cities) {
        if (NetworkConnectionChecker.isConnected(act)) {
            NetworkCalls networkCalls = new NetworkCalls(act, this);
            networkCalls.fetchCitiesWeatherForecastData(cities);
        }
    }
}
