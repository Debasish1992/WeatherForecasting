package com.weatherforecast.utils;

import android.content.Context;
import android.util.Log;

import com.weatherforecast.BuildConfig;
import com.weatherforecast.interfaces.AlertActionClicked;

import static android.content.ContentValues.TAG;

public class ShowLogs {

    public static void displayLog(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void displayAlertMessage(Context context, String title, String message,
                                           AlertActionClicked alertActionClicked) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Enter City", (dialog, which) -> alertActionClicked.onPositiveButtonClicked());
        builder.setNegativeButton("No Thanks", (dialog, which) -> alertActionClicked.onNegativeButtonClicked());
        builder.create();
        builder.show();
    }

    public static void displayAlertMessageNoInternet(Context context, String title, String message,
                                                     AlertActionClicked alertActionClicked) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> alertActionClicked.onPositiveButtonClicked());
        builder.setNegativeButton("Cancel", (dialog, which) -> alertActionClicked.onNegativeButtonClicked());
        builder.create();
        builder.show();
    }


}
