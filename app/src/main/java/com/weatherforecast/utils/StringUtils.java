package com.weatherforecast.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class StringUtils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCommaSeparatedIds(ArrayList<String> originalString) {
        String idsStr = null;
        try {
            idsStr = String.join(", ", originalString);
            idsStr = idsStr.replace(" ", "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return idsStr;
    }
}
