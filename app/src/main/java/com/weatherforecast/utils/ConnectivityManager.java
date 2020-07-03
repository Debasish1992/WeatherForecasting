package com.weatherforecast.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class ConnectivityManager {

    public static void turnOnMobileData(Context context){
       // Intent intent = new Intent(Intent.ACTION_MAIN);
        Intent settingsIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
      /*  settingsIntent.setComponent(new ComponentName("com.android.settings",
                "com.android.settings.Settings$DataUsageSummaryActivity"));*/
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(settingsIntent);
    }
}
