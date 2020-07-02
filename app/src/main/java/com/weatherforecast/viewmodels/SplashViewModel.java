package com.weatherforecast.viewmodels;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModel;

import com.weatherforecast.data.RealmManager;
import com.weatherforecast.data.network.NetworkCalls;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.SplashUiCallbacks;
import com.weatherforecast.interfaces.WeatherCallbacks;
import com.weatherforecast.ui.SplashScreen;
import com.weatherforecast.utils.NetworkConnectionChecker;
import com.weatherforecast.utils.ParseJSON;
import com.weatherforecast.utils.ShowLogs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class SplashViewModel extends ViewModel implements CitiAccessCallbacks, WeatherCallbacks {

    Activity act;
    CitiAccessCallbacks citiAccessCallbacks;
    Realm realm;
    RealmManager realmManager;
    SplashUiCallbacks splashUiCallbacks;
    WeatherCallbacks callBackForWeather;

    public SplashViewModel(Activity activity, Realm realmObject, SplashUiCallbacks splashUiCallbacks) {
        this.citiAccessCallbacks = this;
        callBackForWeather = this;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void getTotalNoOfCitiesFromLocalDb(int count) {
        ShowLogs.displayLog("The Row Count is "+count);
        ArrayList<String> idsArray = new ArrayList<>();
        try{
            if(count == 0){
                // Getting the String data from JSON
                String getCitiData = ParseJSON.loadJSONFromAsset(act);
                // Parsing the Array
                JSONArray citiArray = new JSONArray(getCitiData);
                for (int i = 0; i < citiArray.length(); i++) {
                    JSONObject getEachCity = citiArray.getJSONObject(i);
                    String getCityId = getEachCity.getString("id");
                    idsArray.add(getCityId);
                    if (getEachCity != null) {
                        // Saving the City Data into Realm
                        realmManager.saveCityData(realm, getEachCity, this);
                    }
                }
                // Checking for previous data
                int getCount = realmManager.getWeatherDataCount(realm);

                if (getCount == 0)
                    // Calling the Weather API after getting all the Ids
                    callWeatherAPi(getCommaSeparatedIds(idsArray));
            } else {
                // Calling the Weather API after getting all the Ids from realm
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
        // Checking for previous data
        int getCount = realmManager.getWeatherDataCount(realm);

        if (!cityModels.isEmpty() && getCount == 0) {
            ArrayList<String> getAllCityIds = new ArrayList<>();
            for (int i = 0; i < cityModels.size(); i++) {
                getAllCityIds.add(cityModels.get(i).getId());
            }
            // Convert the List of String to String
            String idsStr = getCommaSeparatedIds(getAllCityIds);
            ShowLogs.displayLog("All Ids are" + idsStr);
            // Calling the Weather API after getting all the Ids
            callWeatherAPi(idsStr);
        }else{
            splashUiCallbacks.letUserRedirectToHome(true);
        }
    }


    // Function responsible for fetching Data from Weather API
    void callWeatherAPi(String ids) {
        getDataFromApi(ids);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    String getCommaSeparatedIds(ArrayList<String> originalString) {
        String idsStr = null;
        try {
            idsStr = String.join(", ", originalString);
            idsStr = idsStr.replace(" ", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return idsStr;
    }

    @Override
    public void fetchDataFromApi(String respondData) {
        if (respondData != null) {
            ShowLogs.displayLog(respondData);
            // Saving the API response into DataBase
            realmManager.saveWeatherData(realm, respondData, null,this);
        }
    }

    /**
     * Function responsible for fetching data from API
     *
     * @param cities
     */
    void getDataFromApi(String cities) {
        if (NetworkConnectionChecker.isConnected(act)) {
            NetworkCalls networkCalls = new NetworkCalls(act);
            networkCalls.fetchCitiesWeatherForecastData(cities, this);
        }
    }

    @Override
    public void onSuccessfulWeatherDataSaveInRealm(boolean status, String cityId) {
        if (status) {
            splashUiCallbacks.letUserRedirectToHome(true);
            Toast.makeText(act, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(act, "There was an error", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void getTotalNoOfRowsFromWeatherTable(int count) {

    }

    @Override
    public void onSuccessFUlDataFetchedForACity(String response) {

    }
}
