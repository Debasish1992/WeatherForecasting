package com.weatherforecast.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.weatherforecast.R;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.HomeViewModel;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder>  {

    List<CityModel> cityModelList;
    Context context;
    HomeViewModel homeViewModel;

    public CityListAdapter(HomeViewModel viewModel, List<CityModel> cityModelList, Context context) {
        this.cityModelList = cityModelList;
        ShowLogs.displayLog("CityData" + cityModelList.toString());
        this.context = context;
        this.homeViewModel = viewModel;
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
        holder.tvCityName.setText(cityModel.getName() + ", " + cityModel.getCountry());
        Glide.with(context)
                .load(cityModel.getImage())
                .centerCrop()
                .into(holder.ivCityImage);

        holder.tvCityTemp.setText("Temprature - " + cityModel.getTemp() + (char) 0x00B0 + "C" + "\n\n" + "Last Updated at " + toDate(cityModel.getUpdatedAt()));
        holder.tvCityWeather.setText(cityModel.getWeatherMain() + " | " + cityModel.getWeatherDesc() + "\n\n"+ "Max Temprature - " + cityModel.getMaxTemp() + (char) 0x00B0 + "C" + " | Min Temprature - " + cityModel.getMinTemp() + (char) 0x00B0 + "C" + " | Humidity - " + cityModel.getHumidity());

        holder.tvLastUpdated.setOnClickListener(v -> {
            int pos  = position;
            double latitude = cityModelList.get(pos).getLat();
            double longitude = cityModelList.get(pos).getLng();
            homeViewModel.openLocationInGoogleMap(latitude,longitude);
        });

    }



    // Converting timestamp to date
    private String toDate(long timestamp) {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a");
        return df.format(timestamp);
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
