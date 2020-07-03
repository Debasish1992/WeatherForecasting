package com.weatherforecast.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.weatherforecast.adapters.SearchListAdapter;
import com.weatherforecast.adapters.WeatherForecastAdapter;
import com.weatherforecast.databinding.FragmentGalleryBinding;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.AlertActionClicked;
import com.weatherforecast.interfaces.ConnectionChecker;
import com.weatherforecast.interfaces.WeatherUiCallbacks;
import com.weatherforecast.utils.ConnectivityManager;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.WeatherViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class WeatherFragment extends Fragment implements AlertActionClicked, WeatherUiCallbacks, SearchListAdapter.ItemClickListener, ConnectionChecker {

    private String[] permissions = new String[]{ACCESS_FINE_LOCATION};
    private final int REQUEST_LOCATION_DIALOG = 999;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationManager manager;
    WeatherViewModel weatherViewModel;
    FragmentGalleryBinding binding;
    List<WeatherModel> cityModelList;
    Realm realm;
    WeatherForecastAdapter weatherForecastAdapter;
    RecyclerView rvForecastData, rvPredictionList;
    WeatherUiCallbacks weatherUiCallbacks;
    List<CityModel> predictionList;
    SearchListAdapter searchListAdapter;
    public static final String currentCityId = "94fgt467389";
    EditText etSearchView;
    TextView tvShowingresult;
    double currentCityLatitude, currentCityLongitude;

    private void initViewModel() {
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
    }



    void initObjects() {
        cityModelList = new ArrayList<>();
        predictionList = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        weatherForecastAdapter = new WeatherForecastAdapter(getActivity(), cityModelList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }
    private WeatherViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery, container, false);
        weatherUiCallbacks = this;
        initViewModel();
        initObjects();
        initViews(binding.getRoot());
        weatherViewModel.initObjects(getActivity(), realm, this, this);
        binding.setWeatherViewModel(weatherViewModel);
        checkAndGetLocation();
        return binding.getRoot();
    }

    private void initViews(View root) {
        etSearchView = root.findViewById(R.id.etSearchBar);
        tvShowingresult = root.findViewById(R.id.tvSearchedTextCity);
        rvForecastData = root.findViewById(R.id.rvCityList);
        rvForecastData.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvForecastData.setItemAnimator(new DefaultItemAnimator());
        rvForecastData.setAdapter(weatherForecastAdapter);


        rvPredictionList = root.findViewById(R.id.rvPredictions);
        rvPredictionList.setVisibility(View.GONE);
        rvPredictionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchListAdapter = new SearchListAdapter(predictionList, getActivity());
        searchListAdapter.setClickListener(this);
        rvPredictionList.setAdapter(searchListAdapter);
    }

    private void checkForPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_CODE_LOCATION = 300;
            ActivityCompat.requestPermissions(getActivity(),permissions, REQUEST_CODE_LOCATION);
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
                    if (mFusedLocationClient != null) {
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                    currentCityLatitude = location.getLatitude();
                    currentCityLongitude = location.getLongitude();
                    weatherViewModel.getWeatherDetailsCount("currentCity");
                    tvShowingresult.setText(getActivity().getString(R.string.showing_weather) + " " + "Current City");
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
        Log.d("TAGGG", "GPS onRequestPermissionsResult fragment: " + ""+requestCode);

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
        ShowLogs.displayAlertMessage(getActivity(), getString(R.string.permission_text),
                getString(R.string.allow_permission_message), new AlertActionClicked() {
                    @Override
                    public void onPositiveButtonClicked() {
                        enableGPSLocation();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
    }

    @Override
    public void onPositiveButtonClicked() {
        ConnectivityManager.turnOnMobileData(getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAGGG", "GPS onActivityResult frag: "+resultCode+":"+requestCode);
        switch (requestCode) {
            case REQUEST_LOCATION_DIALOG:
                switch (resultCode) {
                    case RESULT_OK:
                        checkForPermission();
                        break;
                    case RESULT_CANCELED:
                        displayPermissionError();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position, CityModel prediction) {
        if (prediction != null) {
            String getCityId = prediction.getId();
            hideOnScreenKeyboardForEditText(getActivity(), etSearchView);
            rvPredictionList.setVisibility(View.GONE);
            ShowLogs.displayLog("Clicked City id is" + getCityId);
            tvShowingresult.setText(getActivity().getString(R.string.showing_weather) + " " + prediction.getName());
            if (!TextUtils.isEmpty(getCityId)) {
                weatherViewModel.getWeatherDetailsCount(getCityId);
            }
        }
    }

    @Override
    public void getCitySearchedResults(RealmResults<CityModel> cityModels) {
        if (cityModels != null) {
            ShowLogs.displayLog("Prediction Data" + cityModels.toString());
            rvPredictionList.setVisibility(View.VISIBLE);
            predictionList.clear();
            predictionList.addAll(cityModels);
            searchListAdapter.notifyDataSetChanged();
        } else {
            rvPredictionList.setVisibility(View.GONE);
        }
    }

    @Override
    public void getCityForecastData(RealmResults<WeatherModel> getWeatherData, String cityId) {
        if (!getWeatherData.isEmpty()) {
            ShowLogs.displayLog("CityID" + cityId);
            realm.beginTransaction();
            List<WeatherModel> cityModelArrays = realm.copyFromRealm(getWeatherData);
            realm.commitTransaction();
            rvForecastData.setVisibility(View.VISIBLE);
            tvShowingresult.setVisibility(View.VISIBLE);
            etSearchView.setText(null);
            cityModelList.clear();
            cityModelList.addAll(cityModelArrays);
            weatherForecastAdapter.notifyDataSetChanged();
        } else {
            weatherViewModel.callForecastFunction(0, 0, cityId);
        }

    }

    @Override
    public void onDataSearched(boolean status) {
        if (status) {
            rvForecastData.setVisibility(View.GONE);
            tvShowingresult.setVisibility(View.GONE);
        }else{
            rvForecastData.setVisibility(View.VISIBLE);
            tvShowingresult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void getWeatherDetailsCount(int count, String cityId) {
            if(cityId != null && cityId.equalsIgnoreCase("currentCity")){
                weatherViewModel.getDataWeatherDataFromLocal(cityId);
                weatherViewModel.callForecastFunction(currentCityLatitude, currentCityLongitude, "");
            }else{
                weatherViewModel.getDataWeatherDataFromLocal(cityId);
                weatherViewModel.getWeatherDataForFiveDays(cityId);
            }
    }

    public static void hideOnScreenKeyboardForEditText(Activity activity, EditText editText) {
        ((InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void isConnected(boolean status) {
        if (!status) {
            ShowLogs.displayAlertMessageNoInternet(getActivity(),
                    getResources().getString(R.string.no_internet_message_title),
                    getResources().getString(R.string.no_internet_message),
                    this);
        }
    }
}