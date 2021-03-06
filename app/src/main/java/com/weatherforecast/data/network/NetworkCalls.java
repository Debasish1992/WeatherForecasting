package com.weatherforecast.data.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.weatherforecast.R;
import com.weatherforecast.interfaces.CitiAccessCallbacks;
import com.weatherforecast.interfaces.WeatherCallbacks;
import com.weatherforecast.utils.ShowLogs;

public class NetworkCalls {

    Context context;
    RequestQueue requestQueue;


    /**
     * Function responsible for initiating the context and callbacks
     *
     * @param ctx
     */
    public NetworkCalls(Context ctx) {
        this.context = ctx;
        requestQueue = Volley.newRequestQueue(context);
    }


    /**
     * Function responsible for fetching the weather data from cloud for 20 cities
     *
     * @param cityIds          ids of 20 cities
     * @param callbackWithData callback to inform the Activity
     */
    public void fetchCitiesWeatherForecastData(String cityIds, CitiAccessCallbacks callbackWithData) {
        ShowLogs.displayLog("Api Called");
        String apiUrl = context.getResources().getString(R.string.api_end_point_several_cities) +
                cityIds +
                "&"+
                context.getResources().getString(R.string.group_api_key_txt) +
                context.getResources().getString(R.string.api_key)+"&"+context.getResources().getString(R.string.temp_unit);
        // Encoding the URL before Calling the API
        //apiUrl = UrlEncoder.encode(apiUrl);
        ShowLogs.displayLog(apiUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        ShowLogs.displayLog(response.toString());
                        callbackWithData.fetchDataFromApi(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callbackWithData.fetchDataFromApi(error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Function responsible for fetching weather details from cloud using Lat and Lng
     *
     * @param lat       Latitude
     * @param lng       longitude
     * @param cityId    city Id
     * @param callbacks CallBack
     */
    public void fetchDataForACity(int lat, int lng, String cityId, WeatherCallbacks callbacks) {
        String apiUrl = null;
        try {
            apiUrl = context.getResources().getString(R.string.api_end_point_city_specific) +
                    "lat=" + lat + "&lon=" + lng + "&" +
                    context.getResources().getString(R.string.group_api_key_txt) +
                    context.getResources().getString(R.string.api_key) + "&" + context.getResources().getString(R.string.temp_unit);
            ShowLogs.displayLog(apiUrl);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            ShowLogs.displayLog(response.toString());
                            callbacks.onSuccessFUlDataFetchedForACity(response.toString(), "currentCity");
                        }
                    },
                    error -> {
                        callbacks.onSuccessFUlDataFetchedForACity(error.getLocalizedMessage(), "currentCity");
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Function responsible for getting weather details through CityId
     *
     * @param cityId City Is
     * @param callbacks CallBack
     */
    public void fetchDataForACityThroughId(String cityId, WeatherCallbacks callbacks) {
        String apiUrl = null;
        try {
            apiUrl = context.getResources().getString(R.string.api_end_point_city_specific) +
                    "id=" + cityId + "&" +
                    context.getResources().getString(R.string.group_api_key_txt) +
                    context.getResources().getString(R.string.api_key) + "&" + context.getResources().getString(R.string.temp_unit);
            ShowLogs.displayLog(apiUrl);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                    new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            ShowLogs.displayLog(response.toString());
                            callbacks.onSuccessFUlDataFetchedForACity(response.toString(), cityId);
                        }
                    },
                    error -> {
                        callbacks.onSuccessFUlDataFetchedForACity(error.getLocalizedMessage(), cityId);
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


