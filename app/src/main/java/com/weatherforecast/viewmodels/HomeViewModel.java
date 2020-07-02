package com.weatherforecast.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weatherforecast.data.RealmManager;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.HomeFragmentUiCallback;
import com.weatherforecast.utils.ShowLogs;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class HomeViewModel extends ViewModel implements CitiAccessCallbacks {

    public MutableLiveData<List<CityModel>> cityList;
    Realm realm;
    RealmManager realmManager;
    Context ctx;
    HomeFragmentUiCallback homeFragmentUiCallback;

    public HomeViewModel() {
        cityList = new MutableLiveData<>();
    }

    public void initObject(Context context, Realm fromRealm,
                         HomeFragmentUiCallback homeFragmentUiCallback) {
        this.realm = fromRealm;
        this.ctx = context;
        this.homeFragmentUiCallback = homeFragmentUiCallback;
        realmManager = RealmManager.getInstance();

    }

    public void getAllCities(){
        ShowLogs.displayLog("CITY LIST MAIN CALL");
        // Calling the Weather API after getting all the Ids from realm
        realmManager.getAllCities(realm, this);
    }


    public void onRefreshList(){

    }


    @Override
    public void onSuccessfulCityFetchedAndStoredInLocalDb(boolean status) {

    }

    @Override
    public void getTotalNoOfCitiesFromLocalDb(int count) {

    }

    @Override
    public void getAllCities(RealmResults<CityModel> cityModels) {

        // Converting the Realm Model to List for doing operati
        realm.beginTransaction();
        List<CityModel> cityModelArrays = realm.copyFromRealm(cityModels);
        realm.commitTransaction();
        ShowLogs.displayLog("CITY LIST DAT IN MODEL : "+cityModelArrays.size());
        List<WeatherModel> getWeatherDataFromLocal = new ArrayList<>();
        // Fetching weather data for the city
        for (int i = 0; i < cityModelArrays.size(); i++) {
            String getCityId = cityModelArrays.get(i).getId();
            RealmResults<WeatherModel> weatherModel = realmManager.getCityWeatherDetails(realm, getCityId);
            if(!weatherModel.isEmpty())
                getWeatherDataFromLocal.add(weatherModel.get(0));
        }
        ShowLogs.displayLog("CITY LIST DAT IN MODEL1 : "+ getWeatherDataFromLocal.toString());
        homeFragmentUiCallback.onSuccessfulDataFetchedFromLocalDb(cityModelArrays, getWeatherDataFromLocal);
       // cityList.setValue(cityModelArrays);

        //ShowLogs.displayLog("After Adding the weather " + cityModelArrays.get(0).getWeatherModel().getCityName());
       // cityList.setValue(cityModelArrays);
      //  homeFragmentUiCallback.onSuccessfulDataFetchedFromLocalDb(cityModelArrays);
    }

    @Override
    public void fetchDataFromApi(String allCityData) {

    }
}