package com.weatherforecast.interfaces;

public interface WeatherCallbacks {
    void onSuccessfulWeatherDataSaveInRealm(boolean status, String cityId);
    void getTotalNoOfRowsFromWeatherTable(int count);
    void onSuccessFUlDataFetchedForACity(String response, String cityId);
}
