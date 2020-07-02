package com.weatherforecast.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weatherforecast.R;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.utils.ShowLogs;

import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder>  {

    List<CityModel> cityModelList;
    List<WeatherModel> weatherModels;
    Context context;

    public CityListAdapter(List<CityModel> cityModelList, List<WeatherModel> getWeatherData, Context context) {
        this.cityModelList = cityModelList;
        this.weatherModels = getWeatherData;
        ShowLogs.displayLog("Adapter Arraylist " + cityModelList.toString());
        this.context = context;
    }

    @NonNull
    @Override
    public CityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_citi_card_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CityListAdapter.ViewHolder holder, int position) {
        CityModel cityModel = cityModelList.get(position);
        //if(!TextUtils.isEmpty(cityModel.getState())){
            holder.tvCityName.setText(cityModel.getName());
         //   holder.tvCityTemp.setText(weatherModels.get(position).getTemp() + "");
        //}



        //holder.tvCityTemp.setText("Temprature " + cityModel.getWeatherModel().get(0).getTemp());
        //holder.tvCityWeather.setText("Max Temprature " + cityModel.getWeatherModel().get(0).getMaxTemp() + "| Min Temprature " + cityModel.getWeatherModel().get(0).getMinTemp() + "| Humidity " + cityModel.getWeatherModel().get(0).getHumidity());
    }

    @Override
    public int getItemCount() {
        return cityModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCityImage;
        TextView tvLastUpdated, tvCityName, tvCityTemp, tvCityWeather;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCityImage = itemView.findViewById(R.id.imgPlaceImage);
            tvCityName = itemView.findViewById(R.id.txtCityName);
            tvCityTemp = itemView.findViewById(R.id.txtTemprature);
            tvCityWeather = itemView.findViewById(R.id.txtWeather);
            tvLastUpdated = itemView.findViewById(R.id.txtLastUpdate);
        }
    }
}
