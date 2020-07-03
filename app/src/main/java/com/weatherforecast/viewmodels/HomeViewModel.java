package com.weatherforecast.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weatherforecast.data.RealmManager;
import com.weatherforecast.data.network.NetworkCalls;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.ConnectionChecker;
import com.weatherforecast.interfaces.HomeFragmentUiCallback;
import com.weatherforecast.interfaces.WeatherCallbacks;
import com.weatherforecast.utils.NetworkConnectionChecker;
import com.weatherforecast.utils.ParseJSON;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class HomeViewModel extends ViewModel implements CitiAccessCallbacks, WeatherCallbacks {

    public MutableLiveData<List<CityModel>> cityList;
    Realm realm;
    RealmManager realmManager;
    Context ctx;
    HomeFragmentUiCallback homeFragmentUiCallback;
    ConnectionChecker connectionChecker;

    public HomeViewModel() {
        cityList = new MutableLiveData<>();
    }


    /**
     * Function to initialize objects
     *
     * @param context                Context
     * @param fromRealm              Realm Instance
     * @param homeFragmentUiCallback Callback
     * @param checker                Internet checker
     */
    public void initObject(Context context, Realm fromRealm,
                           HomeFragmentUiCallback homeFragmentUiCallback, ConnectionChecker checker) {
        this.realm = fromRealm;
        this.ctx = context;
        this.connectionChecker = checker;
        this.homeFragmentUiCallback = homeFragmentUiCallback;
        realmManager = RealmManager.getInstance();

    }

    // Function will fetch all the cities
    public void getAllCities() {
        // Calling the Weather API after getting all the Ids from realm
        ShowLogs.displayLog("Get All Cities Called");
        realmManager.getAllCities(realm, this);
    }


    /**
     * Callback get trigger when user do pull in the home list
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onRefreshList() {
        ArrayList<String> getCityIds = new ArrayList<>();
        try {
            // Getting the String data from JSON
            String getCitiData = ParseJSON.loadJSONFromAsset(ctx);
            // Parsing the Array
            JSONArray citiArray = new JSONArray(getCitiData);
            for (int i = 0; i < citiArray.length(); i++) {
                JSONObject getEachCity = citiArray.getJSONObject(i);
                String getCityId = getEachCity.getString("id");
                getCityIds.add(getCityId);
            }
            callWeatherAPi(StringUtils.getCommaSeparatedIds(getCityIds));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Function to call API
     *
     * @param ids
     */
    void callWeatherAPi(String ids) {
        getDataFromApi(ids);
    }


    /**
     * Function responsible for getting Data from API
     *
     * @param cities City ids
     */
    void getDataFromApi(String cities) {
        if (NetworkConnectionChecker.isConnected(ctx)) {
            NetworkCalls networkCalls = new NetworkCalls(ctx);
            networkCalls.fetchCitiesWeatherForecastData(cities, this);
        } else {
            connectionChecker.isConnected(false);
        }
    }


    @Override
    public void onSuccessfulCityFetchedAndStoredInLocalDb(boolean status) {

    }

    @Override
    public void getTotalNoOfCitiesFromLocalDb(int count) {

    }

    @Override
    public void getAllCities(RealmResults<CityModel> cityModels) {
        realm.beginTransaction();
        List<CityModel> cityModelArrays = realm.copyFromRealm(cityModels);
        realm.commitTransaction();
        // Responding to the Fragment for the data
        homeFragmentUiCallback.onSuccessfulDataFetchedFromLocalDb(cityModelArrays);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void fetchDataFromApi(String allCityData) {
        if (allCityData != null) {
            ShowLogs.displayLog(allCityData);
            // Saving the API response into DataBase
            realmManager.saveAllCityWeatherData(realm, allCityData, null, this);
        }

    }

    @Override
    public void onSuccessfulWeatherDataSaveInRealm(boolean status, String cityId) {
        if (status) {
            // Getting all the cities from local DB
            realmManager.getAllCities(realm, this);
        } else {
            Toast.makeText(ctx, "There was an error", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getTotalNoOfRowsFromWeatherTable(int count) {

    }

    @Override
    public void onSuccessFUlDataFetchedForACity(String response, String cityId) {

    }

    /**
     * Function responsible for locating the city in Google Map
     * @param latitude
     * @param longitude
     */
    public void openLocationInGoogleMap(double latitude, double longitude) {
        try {
            String url = "http://maps.google.com/maps?q=" + latitude + "," + longitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            ctx.startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}