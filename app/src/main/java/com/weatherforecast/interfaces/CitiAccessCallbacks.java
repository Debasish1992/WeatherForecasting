package com.weatherforecast.interfaces;

import com.weatherforecast.entity.CityModel;

import org.json.JSONObject;

import io.realm.RealmResults;

public interface CitiAccessCallbacks {
    void onSuccessfulCityFetchedAndStoredInLocalDb(boolean status);
    void getTotalNoOfCitiesFromLocalDb(int count);
    void getAllCities(RealmResults<CityModel> cityModels);
    void fetchDataFromApi(String allCityData);
}
