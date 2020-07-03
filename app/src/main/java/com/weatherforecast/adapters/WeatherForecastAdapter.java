package com.weatherforecast.adapters;

import android.annotation.SuppressLint;
import android.content.Context;

import android.icu.text.TimeZoneFormat;
import android.os.Build;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        holder.tvDate.setText(cityModels.get(position).getWeatherDesc()+ "\n\n" + getDateCurrentTimeZone(cityModels.get(position).getTimeStamp()));
        holder.tvMinTemp.setText("Min - " + cityModels.get(position).getMinTemp() + (char) 0x00B0 + "C");
        holder.tvMaxtemp.setText("Max - " + cityModels.get(position).getMaxTemp() + (char) 0x00B0 + "C" + " | ");

    }

    @SuppressLint("NewApi")
    public static String getDateCurrentTimeZone(long timeStamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timeStamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a");
            Date currenTimeZone = (Date) calendar.getTime();
            ShowLogs.displayLog("Formatted Date is " + sdf.format(currenTimeZone));
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }


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
