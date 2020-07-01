package com.weatherforecast.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.weatherforecast.R;
import com.weatherforecast.interfaces.AlertActionClicked;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.GalleryViewModel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WeatherFragment extends Fragment implements AlertActionClicked {

    private String[] permissions = new String[]{ACCESS_FINE_LOCATION};
    private final int REQUEST_LOCATION_DIALOG = 999;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        checkAndGetLocation();
        return root;
    }

    private void checkForPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_CODE_LOCATION = 300;
            requestPermissions(permissions, REQUEST_CODE_LOCATION);
            return;
        } else {
            getCurrentLocation();
        }
    }

    private void enableGPSLocation() {
        ShowLogs.displayLog("GPS IS DISABLED EbaBLe call");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(getActivity())
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(getActivity(), REQUEST_LOCATION_DIALOG);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        } catch (ClassCastException e) {
                            // Ignore, should be an impossible error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(250);
        locationRequest.setFastestInterval(250);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    ShowLogs.displayLog("Location : " + location.getLatitude() + ":" + location.getLongitude());
                }
            }
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


    private void checkAndGetLocation() {
        ShowLogs.displayLog("Inside GPS Enable");
        if (!isGPSEnabled()) {
            enableGPSLocation();
        } else {
            checkForPermission();
        }
    }

    private boolean isGPSEnabled() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 300: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShowLogs.displayLog("GPS IS PERMISSION GRANTED");
                    getCurrentLocation();
                } else {
                    ShowLogs.displayLog("GPS IS PERMISSION NOT GRANTED");
                    displayPermissionError();
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }


    private void displayPermissionError() {
        ShowLogs.displayLog("GPS IS permission error");
        ShowLogs.displayAlertMessage(getActivity(), getString(R.string.allow_permission_message),
                getString(R.string.allow_permission_message), new AlertActionClicked() {
                    @Override
                    public void onPositiveButtonClicked() {
                        checkForPermission();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
    }

    @Override
    public void onPositiveButtonClicked() {

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    public void locationEnableStatus(boolean status) {
        ShowLogs.displayLog(status + "");
        if (status) {
            ShowLogs.displayLog("Location Enabled");
            checkForPermission();
        }
    }
}