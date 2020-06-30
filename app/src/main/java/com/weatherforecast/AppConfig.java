package com.weatherforecast;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppConfig extends Application {

    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        intiRealm();
    }

    // Initializing the Realm Configuration
    private void intiRealm() {
        Realm.init(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
