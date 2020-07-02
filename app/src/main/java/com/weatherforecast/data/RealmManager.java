package com.weatherforecast.data;

import android.util.Log;

import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.WeatherCallbacks;
import com.weatherforecast.utils.DateUtils;
import com.weatherforecast.utils.ShowLogs;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Case;
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
     * @param realm            Realm Instance
     * @param respondData      The Data to be stored
     * @param weatherCallbacks Callback to Ack View Model
     */
    public void saveWeatherData(Realm realm, String respondData, String cityId, WeatherCallbacks weatherCallbacks) {
        ShowLogs.displayLog("The city Id is" + cityId);
        try {
            JSONObject weatherData = new JSONObject(respondData);
            JSONArray listData = weatherData.getJSONArray("list");
            String weatherId = null, weatherMain = null, weatherDesc = null;
            long timStamp;
            String cityIdApi;

            for (int i = 0; i < listData.length(); i++) {
                JSONObject weatherDetails = listData.getJSONObject(i);

                if (weatherDetails.has("id")) {
                    cityIdApi = weatherDetails.getString("id");
                } else {
                    cityIdApi = cityId;
                }

                timStamp = weatherDetails.getLong("dt");
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

                WeatherModel weatherModelFetch = realm.where(WeatherModel.class)
                        .equalTo("weatherId", weatherId)
                        .findFirst();

                if (weatherModelFetch == null) {
                    realm.beginTransaction();
                    WeatherModel weatherModel = realm.createObject(WeatherModel.class, weatherId);
                    weatherModel.setFeelsLike(mainTempFeelsLike);
                    weatherModel.setHumidity(humidity);
                    weatherModel.setMaxTemp(mainTempMax);
                    weatherModel.setMinTemp(mainTempMin);
                    weatherModel.setTemp(mainTemp);
                    weatherModel.setWindSpeed(getWindSpeed);
                    weatherModel.setWeatherDesc(weatherDesc);
                    weatherModel.setWeatherMain(weatherMain);
                    weatherModel.setTimeStamp(timStamp);
                    weatherModel.setCityId(cityIdApi);
                    realm.commitTransaction();
                } else {
                    realm.beginTransaction();
                    weatherModelFetch.setFeelsLike(mainTempFeelsLike);
                    weatherModelFetch.setHumidity(humidity);
                    weatherModelFetch.setMaxTemp(mainTempMax);
                    weatherModelFetch.setMinTemp(mainTempMin);
                    weatherModelFetch.setTemp(mainTemp);
                    weatherModelFetch.setWindSpeed(getWindSpeed);
                    weatherModelFetch.setWeatherDesc(weatherDesc);
                    weatherModelFetch.setWeatherMain(weatherMain);
                    weatherModelFetch.setTimeStamp(timStamp);
                    realm.commitTransaction();
                }
            }
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(true, cityId);
        } catch (Exception ex) {
            ex.printStackTrace();
            ShowLogs.displayLog(ex.getLocalizedMessage());
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(false, null);
        }
    }


    /**
     * Function responsible for getting the weather model data from the city Id
     *
     * @param realm  realm instance
     * @param cityId city id
     * @return Weather Model of the city
     */
    public RealmResults<WeatherModel> getCityWeatherDetails(Realm realm, String cityId) {
        RealmResults<WeatherModel> weatherModels = null;
        WeatherModel weatherModel = null;
        long currentUtcTimeStamp = DateUtils.getCurrentUTCDateTimeStamp();
        try {
            weatherModels = realm.where(WeatherModel.class)
                    .equalTo("cityId", cityId)
                    .findAll();
            ShowLogs.displayLog("weather view model is" + weatherModel + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return weatherModels;
    }


    /**
     * Get Searched Cities
     *
     * @param realm
     * @param searchPhrase
     * @return
     */
    public RealmResults<CityModel> getSearchedCityModel(Realm realm, String searchPhrase) {
        RealmResults<CityModel> cityModels = null;
        try {
            cityModels = realm.where(CityModel.class)
                    .contains("name", searchPhrase, Case.INSENSITIVE)
                    .findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cityModels;
    }

    public CityModel getSearchedCityModelDetails(Realm realm, String cityId) {
        CityModel cityModel = null;
        try {
            cityModel = realm.where(CityModel.class)
                    .contains("id", cityId)
                    .findFirst();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cityModel;
    }
}


