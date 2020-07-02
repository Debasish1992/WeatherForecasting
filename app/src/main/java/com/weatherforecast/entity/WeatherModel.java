package com.weatherforecast.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WeatherModel extends RealmObject {

    @PrimaryKey
    String objectId;
    String weatherId;
    String cityId;
    double temp;
    double minTemp;
    double maxTemp;
    double feelsLike;
    int humidity;
    String weatherMain;
    String weatherDesc;
    double windSpeed;
    long timeStamp;
    boolean isCurrentCity;

    public WeatherModel() {
    }

    public WeatherModel(String weatherId, double temp, double minTemp, double maxTemp,
                        double feelsLike, int humidity, String weatherMain, String weatherDesc, double windSpeed, long timeStamp) {
        this.weatherId = weatherId;
        this.temp = temp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.weatherMain = weatherMain;
        this.weatherDesc = weatherDesc;
        this.windSpeed = windSpeed;
        this.timeStamp = timeStamp;
    }

    public boolean isCurrentCity() {
        return isCurrentCity;
    }

    public void setCurrentCity(boolean currentCity) {
        isCurrentCity = currentCity;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }


    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }


    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
