package com.weatherforecast.data;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.interfaces.CitiAccessCallbacks;

import org.json.JSONObject;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

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
    public void getTotalNoOfCities(Realm realm, CitiAccessCallbacks callbacks){
        try{
            int getCount = (int) realm.where(CityModel.class).count();
           callbacks.getTotalNoOfCitiesFromLocalDb(getCount);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void saveCitiData(Realm realm, JSONObject citiObject){
        try{
            realm.beginTransaction();
            realm.createObjectFromJson(CityModel.class, citiObject);
            realm.commitTransaction();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    /**
     * Function responsible for getting all the cities from the DataBase
     * @param realm Realm instance
     * @param callback Callback for the Data Access
     */
    public void getAllCities(Realm realm, CitiAccessCallbacks callback){
        try{
            RealmResults<CityModel> getAllCities = realm.where(CityModel.class).findAll();
            callback.getAllCities(getAllCities);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}


