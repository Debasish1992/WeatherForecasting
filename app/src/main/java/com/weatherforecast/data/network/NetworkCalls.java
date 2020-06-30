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
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.utils.UrlEncoder;

import org.json.JSONObject;

public class NetworkCalls {

    Context context;
    CitiAccessCallbacks CitiAccess;
    RequestQueue requestQueue;


    /**
     * Function responsible for initiating the context and callbacks
     *
     * @param ctx
     * @param callbackWithData
     */
    public NetworkCalls(Context ctx, CitiAccessCallbacks callbackWithData) {
        this.context = ctx;
        this.CitiAccess = callbackWithData;
        requestQueue = Volley.newRequestQueue(context);
    }


    /**
     * Function responsible for fetching the weather data from cloud
     *
     * @param cityIds
     */
    public void fetchCitiesWeatherForecastData(String cityIds) {
        String apiUrl = context.getResources().getString(R.string.api_end_point_several_cities) +
                cityIds +
                "&"+
                context.getResources().getString(R.string.group_api_key_txt) +
                context.getResources().getString(R.string.api_key);
        // Encoding the URL before Calling the API
        //apiUrl = UrlEncoder.encode(apiUrl);
        ShowLogs.displayLog(apiUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        ShowLogs.displayLog(response.toString());
                        CitiAccess.fetchDataFromApi(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CitiAccess.fetchDataFromApi(error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);

    }
}


