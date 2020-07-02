package com.weatherforecast.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weatherforecast.R;
import com.weatherforecast.entity.CityModel;

import java.util.List;


public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {
    List<CityModel> predictionList;
    Activity activity;
    private ItemClickListener mClickListener;

    public SearchListAdapter(List<CityModel> predictionList, Activity activity) {
        this.predictionList = predictionList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_places_display,
                parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CityModel currentItem = predictionList.get(position);
        holder.placeName.setText(currentItem.getName());
        holder.placeDesc.setText(currentItem.getCountry());
        holder.itemView.setTag(currentItem);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return predictionList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView placeName, placeDesc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.tvCityName);
            placeDesc = itemView.findViewById(R.id.tvCityDesc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), predictionList.get(getAdapterPosition()));
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, CityModel prediction);
    }
}
