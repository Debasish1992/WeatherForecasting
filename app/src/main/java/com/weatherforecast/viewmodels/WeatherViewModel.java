package com.weatherforecast.viewmodels;

import android.content.Context;
import android.util.Log;
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

    public void initObjects(Context contxt, Realm realm, WeatherUiCallbacks callbacks, ConnectionChecker connectionChecker) {
        this.getRealm = realm;
        this.context = contxt;
        this.callbacks = callbacks;
        this.connectionChecker = connectionChecker;
        realmManager = RealmManager.getInstance();
    }

    public void callForecastFunction(double lat, double lng, String cityId) {
        try {
            this.cityIdDb = cityId;
            int latitide = (int) lat;
            int longitude = (int) lng;
            if (NetworkConnectionChecker.isConnected(context)) {
                NetworkCalls networkCalls = new NetworkCalls(context);
                networkCalls.fetchDataForACity(latitide, longitude,null,this);
            }else{
                connectionChecker.isConnected(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getWeatherDataForFiveDays(String cityId){
        try{
            if (NetworkConnectionChecker.isConnected(context)) {
                NetworkCalls networkCalls = new NetworkCalls(context);
                networkCalls.fetchDataForACityThroughId(cityId,this);
            }else{
                connectionChecker.isConnected(false);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void  getDataWeatherDataFromLocal(String cityId){
        RealmResults<WeatherModel> getWeatherData = realmManager.getCityWeatherDetails(getRealm, cityId);
        callbacks.getCityForecastData(getWeatherData, cityId);
    }

    public void getWeatherDetailsCount(String cityId){
        int count = realmManager.getWeatherDetailsCount(getRealm, cityId);
        callbacks.getWeatherDetailsCount(count, cityId);
    }

    @Override
    public void onSuccessfulWeatherDataSaveInRealm(boolean status, String cityIdDb) {
        if (status) {
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
        ShowLogs.displayLog("City Data Fetched from API" + cityId);
        realmManager.saveWeatherData(getRealm, response, cityId,this);
    }

    public void onTextChanged(CharSequence phrase, int start, int before, int count) {
        ShowLogs.displayLog(phrase.toString());
        if (phrase.toString().length() > 0) {
            callbacks.onDataSearched(true);
            RealmResults<CityModel> searchedResults = realmManager.getSearchedCityModel(getRealm, phrase.toString());
            callbacks.getCitySearchedResults(searchedResults);
        } else {
            callbacks.getCitySearchedResults(null);
        }

    }
}