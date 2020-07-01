package com.weatherforecast.data;

import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.WeatherCallbacks;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmManager {
    private static RealmManager realmManager = null;
    final static int TASK_STATUS_COMPLETE = 1;


    public static RealmManager getInstance() {
        if (realmManager == null)
            realmManager = new RealmManager();
        return realmManager;
    }

    /**
     * Function responsible for getting the No of cities from the local DB
     * @param realm Realm Instance
     * @param callbacks Callback to Ack total no of cities
     */
    public void getTotalNoOfCities(Realm realm, CitiAccessCallbacks callbacks) {
        try {
            int getCount = (int) realm.where(CityModel.class).count();
            callbacks.getTotalNoOfCitiesFromLocalDb(getCount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Function responsible for fetching the weather data count
     *
     * @param realm
     * @return
     */
    public int getWeatherDataCount(Realm realm) {
        return (int) realm.where(WeatherModel.class).count();
    }


    /**
     * Function Responsible for saving City Data
     *
     * @param realm      Realm Instance
     * @param citiObject City Json Object
     */
    public void saveCityData(Realm realm,
                             JSONObject citiObject,
                             CitiAccessCallbacks accessCallbacks) {
        try {
            realm.beginTransaction();
            CityModel cityModel = realm.createObject(CityModel.class, citiObject.getString("id"));
            cityModel.setCountry(citiObject.getString("country"));
            cityModel.setState(citiObject.getString("state"));
            cityModel.setName(citiObject.getString("name"));
            JSONObject getCordinatesObject = citiObject.getJSONObject("coord");
            cityModel.setLat(getCordinatesObject.getDouble("lat"));
            cityModel.setLng(getCordinatesObject.getDouble("lon"));
            realm.commitTransaction();
            accessCallbacks.onSuccessfulCityFetchedAndStoredInLocalDb(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            accessCallbacks.onSuccessfulCityFetchedAndStoredInLocalDb(true);
        }
    }


    /**
     * Function responsible for getting all the cities from the DataBase
     *
     * @param realm    Realm instance
     * @param callback Callback for the Data Access
     */
    public void getAllCities(Realm realm, CitiAccessCallbacks callback) {
        try {
            RealmResults<CityModel> getAllCities = realm.where(CityModel.class).findAll();
            callback.getAllCities(getAllCities);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Saving Weather Data in Realm
     *
     * @param realm Realm Instance
     * @param respondData The Data to be stored
     * @param weatherCallbacks Callback to Ack View Model
     */
    public void saveWeatherData(Realm realm, String respondData, WeatherCallbacks weatherCallbacks) {
        try {
            JSONObject weatherData = new JSONObject(respondData);
            JSONArray listData = weatherData.getJSONArray("list");
            String weatherId = null, weatherMain = null, weatherDesc = null;

            for (int i = 0; i < listData.length(); i++) {
                JSONObject weatherDetails = listData.getJSONObject(i);
                String cityId = weatherDetails.getString("id");
                String cityName = weatherDetails.getString("name");
                long timStamp = weatherDetails.getLong("dt");
                JSONArray weatherDataArray = weatherDetails.getJSONArray("weather");
                for (int j = 0; j < weatherDataArray.length(); j++) {
                    JSONObject getWeather = weatherDataArray.getJSONObject(j);
                    weatherId = getWeather.getString("id");
                    weatherMain = getWeather.getString("main");
                    weatherDesc = getWeather.getString("description");
                }
                JSONObject mainJsonObject = weatherDetails.getJSONObject("main");
                double mainTemp = mainJsonObject.getDouble("temp");
                double mainTempMax = mainJsonObject.getDouble("temp_max");
                double mainTempMin = mainJsonObject.getDouble("temp_min");
                double mainTempFeelsLike = mainJsonObject.getDouble("feels_like");
                int humidity = mainJsonObject.getInt("humidity");

                JSONObject windJsonObject = weatherDetails.getJSONObject("wind");
                double getWindSpeed = windJsonObject.getDouble("speed");

                WeatherModel weatherModel = new WeatherModel(weatherId, cityId, cityName, mainTemp, mainTempMin, mainTempMax, mainTempFeelsLike,
                        humidity, weatherMain, weatherDesc, getWindSpeed, timStamp);
                realm.beginTransaction();
                realm.insertOrUpdate(weatherModel);
                realm.commitTransaction();
            }
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(false);
        }
    }
}


