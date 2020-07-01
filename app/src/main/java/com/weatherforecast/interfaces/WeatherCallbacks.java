package com.weatherforecast.interfaces;

public interface WeatherCallbacks {
    void onSuccessfulWeatherDataSaveInRealm(boolean status);
    void getTotalNoOfRowsFromWeatherTable(int count);
}
