package com.weatherforecast.utils;

import android.util.Log;

import com.weatherforecast.BuildConfig;

import static android.content.ContentValues.TAG;

public class ShowLogs {

    public static void displayLog(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }
}
