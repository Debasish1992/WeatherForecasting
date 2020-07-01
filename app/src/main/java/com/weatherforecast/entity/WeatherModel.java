package com.weatherforecast.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WeatherModel extends RealmObject {

    @PrimaryKey
    String weatherId;
    String cityId;
    String cityName;
    double temp;
    double minTemp;
    double maxTemp;
    double feelsLike;
    int humidity;
    String weatherMain;
    String weatherDesc;
    double windSpeed;
    long timeStamp;

    public WeatherModel() {
    }

    public WeatherModel(String weatherId, String cityId, String cityName,
                        double temp, double minTemp, double maxTemp, double feelsLike,
                        int humidity, String weatherMain, String weatherDesc, double windSpeed,
                        long timeStamp) {
        this.weatherId = weatherId;
        this.cityId = cityId;
        this.cityName = cityName;
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

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
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