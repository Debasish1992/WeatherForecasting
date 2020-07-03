package com.weatherforecast.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.WeatherCallbacks;
import com.weatherforecast.utils.DateUtils;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.utils.UUIDGenarator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmManager {
    private static RealmManager realmManager = null;
    final static int TASK_STATUS_COMPLETE = 1;
    ArrayList<String> imageArray;


    // Instance of the Realm Mamager
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

            addImagesToArray();
            realm.beginTransaction();
            CityModel cityModel = realm.createObject(CityModel.class, citiObject.getString("id"));
            cityModel.setCountry(citiObject.getString("country"));
            cityModel.setState(citiObject.getString("state"));
            cityModel.setName(citiObject.getString("name"));
            JSONObject getCordinatesObject = citiObject.getJSONObject("coord");
            cityModel.setLat(getCordinatesObject.getDouble("lat"));
            cityModel.setLng(getCordinatesObject.getDouble("lon"));
            cityModel.setImage(getRandom());
            realm.commitTransaction();
            accessCallbacks.onSuccessfulCityFetchedAndStoredInLocalDb(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            accessCallbacks.onSuccessfulCityFetchedAndStoredInLocalDb(true);
        }
    }


    // Adding images to the array
    public void addImagesToArray() {
        imageArray = new ArrayList<>();
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/aquib-akhter-oBCc0Hw6LrQ-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/conor-samuel--iPuEST6f9Y-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/eva-dang-EXdXLrZXS9Q-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/gilly-8vzFINl6zV8-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/lance-anderson-uevmkfCH98Q-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/luis-fernando-felipe-alves-sskHWbE5c6E-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/satyajeet-mazumdar-fCsmYKhiHGo-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/shubham-rath-KyZzn2RnpDQ-unsplash.jpg");
        imageArray.add("https://f002.backblazeb2.com/file/Trot-Dev/assignments/william-mccue-1jZbU_XuyvU-unsplash.jpg");
    }


    // Function to get Random number from the array of images
    public String getRandom() {
        int rnd = new Random().nextInt(imageArray.size());
        return imageArray.get(rnd);
    }




    /**
     * Function responsible for getting all the cities from the DataBase
     *
     * @param realm    Realm instance
     * @param callback Callback for the Data Access
     */
    public void getAllCities(Realm realm, CitiAccessCallbacks callback) {
        ShowLogs.displayLog("Call Reached Realm Manager");
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
        try {
            JSONObject weatherData = new JSONObject(respondData);
            JSONObject getCityObject = weatherData.getJSONObject("city");
            String getCityIdFromAPI = getCityObject.getString("id");
            JSONArray listData = weatherData.getJSONArray("list");
            String weatherId = null, weatherMain = null, weatherDesc = null;
            long timStamp;

            for (int i = 0; i < listData.length(); i++) {
                JSONObject weatherDetails = listData.getJSONObject(i);
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
                realm.beginTransaction();
                String objectId = UUIDGenarator.randomUUID(4);
                WeatherModel weatherModel = realm.createObject(WeatherModel.class, objectId);
                weatherModel.setWeatherId(weatherId);
                weatherModel.setFeelsLike(mainTempFeelsLike);
                weatherModel.setHumidity(humidity);
                weatherModel.setMaxTemp(mainTempMax);
                weatherModel.setMinTemp(mainTempMin);
                weatherModel.setTemp(mainTemp);
                weatherModel.setWindSpeed(getWindSpeed);
                weatherModel.setWeatherDesc(weatherDesc);
                weatherModel.setWeatherMain(weatherMain);
                weatherModel.setTimeStamp(timStamp);
                weatherModel.setCityId(getCityIdFromAPI);
                if(cityId != null && cityId.equalsIgnoreCase("currentCity")){
                    weatherModel.setCurrentCity(true);
                }else{
                    weatherModel.setCurrentCity(false);
                }
                realm.commitTransaction();
            }
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(true, cityId);
        } catch (Exception ex) {
            ex.printStackTrace();
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(false, null);
        }
    }


    /**
     * Function saving City weather information
     *
     * @param realm            Real Instance
     * @param respondData      Data to save
     * @param cityId           CityId
     * @param weatherCallbacks CallBak
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveAllCityWeatherData(Realm realm, String respondData, String cityId, WeatherCallbacks weatherCallbacks) {
        try {
            JSONObject weatherData = new JSONObject(respondData);
            JSONArray listData = weatherData.getJSONArray("list");
            String weatherId = null, weatherMain = null, weatherDesc = null, getCityIdFromAPI;
            long timStamp;
            long updatedAt = DateUtils.getCurrentUTCDateTimeStamp();

            for (int i = 0; i < listData.length(); i++) {
                JSONObject weatherDetails = listData.getJSONObject(i);
                timStamp = weatherDetails.getLong("dt");
                getCityIdFromAPI = weatherDetails.getString("id");

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
                realm.beginTransaction();

                CityModel cityWeatherModel = realm.where(CityModel.class)
                        .equalTo("id", getCityIdFromAPI)
                        .findFirst();

                if (cityWeatherModel != null) {
                    cityWeatherModel.setFeelsLike(mainTempFeelsLike);
                    cityWeatherModel.setHumidity(humidity);
                    cityWeatherModel.setMaxTemp(mainTempMax);
                    cityWeatherModel.setMinTemp(mainTempMin);
                    cityWeatherModel.setTemp(mainTemp);
                    cityWeatherModel.setWeatherDesc(weatherDesc);
                    cityWeatherModel.setWeatherMain(weatherMain);
                    cityWeatherModel.setTimeSTamp(timStamp);
                    cityWeatherModel.setUpdatedAt(System.currentTimeMillis());
                }
                realm.commitTransaction();
            }
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(true, cityId);
        } catch (Exception ex) {
            ex.printStackTrace();
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
        try {
            if(cityId != null && cityId.equalsIgnoreCase("currentCity")){
                weatherModels = realm.where(WeatherModel.class)
                        .equalTo("isCurrentCity", true)
                        .sort("timeStamp", Sort.ASCENDING)
                        .findAll();
            }else{
                weatherModels = realm.where(WeatherModel.class)
                        .equalTo("cityId", cityId)
                        .sort("timeStamp", Sort.ASCENDING)
                        .findAll();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return weatherModels;
    }


    /**
     * Getting Weather Object record count from the Local DB
     *
     * @param realm  Realm Instance
     * @param cityId City Id
     * @return Count of the Object
     */
    public int getWeatherDetailsCount(Realm realm, String cityId) {
        if (cityId != null && cityId.equalsIgnoreCase("currentCity")) {
            return (int) realm.where(WeatherModel.class).equalTo("isCurrentCity", true).count();
        } else {
            return (int) realm.where(WeatherModel.class).equalTo("cityId", cityId).count();
        }
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

    /**
     * Function saving weather data for current city
     *
     * @param realm            Realm Instance
     * @param respondData      Data to save
     * @param cityId           City Id
     * @param weatherCallbacks Callback
     */
    public void saveCurrentCityWeatherData(Realm realm, String respondData, String cityId, WeatherCallbacks weatherCallbacks) {
        ShowLogs.displayLog("The city Id is from View Model" + cityId);
        int counter = 0;
        try {
            JSONObject weatherData = new JSONObject(respondData);
            JSONArray listData = weatherData.getJSONArray("list");
            String weatherId = null, weatherMain = null, weatherDesc = null, getCityIdFromAPI;
            long timStamp;

            for (int i = 0; i < listData.length(); i++) {
                JSONObject weatherDetails = listData.getJSONObject(i);
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
                realm.beginTransaction();
                String objectId = UUIDGenarator.randomUUID(4);
                WeatherModel weatherModel = realm.createObject(WeatherModel.class, objectId);
                weatherModel.setWeatherId(weatherId);
                weatherModel.setFeelsLike(mainTempFeelsLike);
                weatherModel.setHumidity(humidity);
                weatherModel.setMaxTemp(mainTempMax);
                weatherModel.setMinTemp(mainTempMin);
                weatherModel.setTemp(mainTemp);
                weatherModel.setWindSpeed(getWindSpeed);
                weatherModel.setWeatherDesc(weatherDesc);
                weatherModel.setWeatherMain(weatherMain);
                weatherModel.setTimeStamp(timStamp);
                weatherModel.setCityId(null);
                if(cityId != null && cityId.equalsIgnoreCase("currentCity")){
                    weatherModel.setCurrentCity(true);
                }else{
                    weatherModel.setCurrentCity(false);
                }
                realm.commitTransaction();
                counter += 1;
                ShowLogs.displayLog("The counter is " + counter);
            }
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(true, cityId);
        } catch (Exception ex) {
            ex.printStackTrace();
            ShowLogs.displayLog("Error occured" + ex.getLocalizedMessage());
            weatherCallbacks.onSuccessfulWeatherDataSaveInRealm(false, null);
        }
    }


    /**
     * Deleting Data from Local Db for a specific City
     *
     * @param realm
     * @param cityId
     */
    public void deleteRowsForCityWeather(Realm realm, String cityId) {
        RealmResults<WeatherModel> result = null;
        try {
            if (cityId != null && cityId.equals("currentCity")) {
                result = realm.where(WeatherModel.class).equalTo("isCurrentCity", true).findAll();
            } else {
                result = realm.where(WeatherModel.class).equalTo("cityId", cityId).findAll();
            }
            realm.beginTransaction();
            result.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


