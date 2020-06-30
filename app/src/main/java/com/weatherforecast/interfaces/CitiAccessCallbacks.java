package com.weatherforecast.interfaces;

import com.weatherforecast.entity.CityModel;

import io.realm.RealmResults;

public interface CitiAccessCallbacks {
    void onSuccessfulCityFetchedAndStoredInLocalDb();
    void getTotalNoOfCitiesFromLocalDb(int count);
    void getAllCities(RealmResults<CityModel> cityModels);
}
