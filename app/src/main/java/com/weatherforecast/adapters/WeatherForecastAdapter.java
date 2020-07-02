package com.weatherforecast.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.text.TimeZoneFormat;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.weatherforecast.R;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.utils.DateUtils;
import com.weatherforecast.utils.ShowLogs;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.TimeZone.getTimeZone;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {

    Context context;
    List<WeatherModel> cityModels;

    public WeatherForecastAdapter(Context context, List<WeatherModel> cityModels) {
        this.context = context;
        this.cityModels = cityModels;
    }

    @NonNull
    @Override
    public WeatherForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wether_forecase_items, parent, false);
        return new WeatherForecastAdapter.ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvWeather.setText(cityModels.get(position).getWeatherMain());
        holder.tvDate.setText(/*getDate(*/cityModels.get(position).getWeatherDesc() /*)*/);
        holder.tvMinTemp.setText("Min - " + cityModels.get(position).getMinTemp());
        holder.tvMaxtemp.setText("Max - " + cityModels.get(position).getMaxTemp() + " | ");

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
   /* public static String getDate(long time) throws ParseException {

    }*/

    @Override
    public int getItemCount() {
        return cityModels.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeather, tvDate, tvMinTemp, tvMaxtemp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.txtDate);
            tvWeather = itemView.findViewById(R.id.txtWeather);
            tvMinTemp = itemView.findViewById(R.id.tvMinTemp);
            tvMaxtemp = itemView.findViewById(R.id.tvMaxTemp);
        }
    }
}
