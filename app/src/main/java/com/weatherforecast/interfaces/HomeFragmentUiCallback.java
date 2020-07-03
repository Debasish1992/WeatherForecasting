package com.weatherforecast.interfaces;

import androidx.lifecycle.MutableLiveData;

import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;

import java.util.List;

public interface HomeFragmentUiCallback {
    void onSuccessfulDataFetchedFromLocalDb(List<CityModel> cityList);
}
