package com.weatherforecast.utils;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;

public class ParseJSON {


    /**
     * Function responsible for getting the Json File from the Assert folder and then parsing the Json making a String.
     *
     * @param activity Context
     * @return JSON String
     */
    public static String loadJSONFromAsset(Activity activity) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("city_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
