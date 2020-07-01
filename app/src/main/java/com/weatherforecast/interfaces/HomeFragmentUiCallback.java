package com.weatherforecast.interfaces;

import androidx.lifecycle.MutableLiveData;

import com.weatherforecast.entity.CityModel;

import java.util.List;

public interface HomeFragmentUiCallback {
    void onSuccessfulDataFetchedFromLocalDb(List<CityModel> cityList);
}
