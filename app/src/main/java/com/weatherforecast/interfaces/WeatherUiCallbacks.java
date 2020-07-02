package com.weatherforecast.interfaces;

import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;

import io.realm.RealmResults;

public interface WeatherUiCallbacks {

    void getCitySearchedResults(RealmResults<CityModel> cityModels);
    void getCityForecastData(RealmResults<WeatherModel> getWeatherData, String cityId);
    void onDataSearched(boolean status);
    void getWeatherDetailsCount(int count, String cityId);
}
