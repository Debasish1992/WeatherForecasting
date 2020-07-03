package com.weatherforecast.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weatherforecast.data.RealmManager;
import com.weatherforecast.data.network.NetworkCalls;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.ConnectionChecker;
import com.weatherforecast.interfaces.WeatherCallbacks;
import com.weatherforecast.interfaces.WeatherUiCallbacks;
import com.weatherforecast.utils.NetworkConnectionChecker;
import com.weatherforecast.utils.ShowLogs;

import io.realm.Realm;
import io.realm.RealmResults;

public class WeatherViewModel extends ViewModel implements WeatherCallbacks {

    private MutableLiveData<String> mText;
    Realm getRealm;
    Context context;
    WeatherUiCallbacks callbacks;
    RealmManager realmManager;
    String cityIdDb;
    ConnectionChecker connectionChecker;

    public WeatherViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }


    /**
     * Initializing Objects
     *
     * @param contxt            Current Context
     * @param realm             realm Instance
     * @param callbacks         CallBack
     * @param connectionChecker Internet connection checker
     */
    public void initObjects(Context contxt, Realm realm, WeatherUiCallbacks callbacks, ConnectionChecker connectionChecker) {
        this.getRealm = realm;
        this.context = contxt;
        this.callbacks = callbacks;
        this.connectionChecker = connectionChecker;
        realmManager = RealmManager.getInstance();
    }


    /**
     * Web service call to get weather data from cloud for current location
     *
     * @param lat
     * @param lng
     * @param cityId
     */
    public void callForecastFunction(double lat, double lng, String cityId) {
        try {
            this.cityIdDb = cityId;
            int latitide = (int) lat;
            int longitude = (int) lng;
            if (NetworkConnectionChecker.isConnected(context)) {
                NetworkCalls networkCalls = new NetworkCalls(context);
                networkCalls.fetchDataForACity(latitide, longitude, null, this);
            } else {
                connectionChecker.isConnected(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Function responsible for fetching weather Data for next 5 days using cityId
     *
     * @param cityId CIty Id
     */
    public void getWeatherDataForFiveDays(String cityId) {
        try {
            if (NetworkConnectionChecker.isConnected(context)) {
                NetworkCalls networkCalls = new NetworkCalls(context);
                networkCalls.fetchDataForACityThroughId(cityId, this);
            } else {
                connectionChecker.isConnected(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Fetching weather data from local db
     *
     * @param cityId city Id
     */
    public void getDataWeatherDataFromLocal(String cityId) {
        RealmResults<WeatherModel> getWeatherData = realmManager.getCityWeatherDetails(getRealm, cityId);
        callbacks.getCityForecastData(getWeatherData, cityId);
    }


    /**
     * Function to get Weather Object count for a specific city Using Object Id
     *
     * @param cityId
     */
    public void getWeatherDetailsCount(String cityId) {
        int count = realmManager.getWeatherDetailsCount(getRealm, cityId);
        callbacks.getWeatherDetailsCount(count, cityId);
    }

    @Override
    public void onSuccessfulWeatherDataSaveInRealm(boolean status, String cityIdDb) {
        if (status) {
            // Getting city Weather from local DB using city Id
            RealmResults<WeatherModel> getWeatherData = realmManager.getCityWeatherDetails(getRealm, cityIdDb);
            callbacks.getCityForecastData(getWeatherData, cityIdDb);
        } else {
            Toast.makeText(context, "Data did not Stored in Realm", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getTotalNoOfRowsFromWeatherTable(int count) {

    }

    @Override
    public void onSuccessFUlDataFetchedForACity(String response, String cityId) {
        // Deleting Previous City record
        realmManager.deleteRowsForCityWeather(getRealm, cityId);

        // Saving the Updated City weather Data
        if (cityId != null && cityId.equalsIgnoreCase("currentCity")) {
            realmManager.saveCurrentCityWeatherData(getRealm, response, cityId, this);
        } else {
            realmManager.saveWeatherData(getRealm, response, cityId, this);
        }
    }


    /**
     * Function For tracking the text change in the city search view
     *
     * @param phrase
     * @param start
     * @param before
     * @param count
     */
    public void onTextChanged(CharSequence phrase, int start, int before, int count) {
        ShowLogs.displayLog(phrase.toString());
        if (phrase.toString().length() > 0) {
            callbacks.onDataSearched(true);
            RealmResults<CityModel> searchedResults = realmManager.getSearchedCityModel(getRealm, phrase.toString());
            callbacks.getCitySearchedResults(searchedResults);
        } else {
            callbacks.onDataSearched(false);
            callbacks.getCitySearchedResults(null);
        }

    }
}