package com.weatherforecast.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weatherforecast.R;
import com.weatherforecast.adapters.CityListAdapter;
import com.weatherforecast.databinding.FragmentHomeBinding;
import com.weatherforecast.entity.CityModel;
import com.weatherforecast.entity.WeatherModel;
import com.weatherforecast.interfaces.HomeFragmentUiCallback;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class HomeFragment extends Fragment implements HomeFragmentUiCallback {
    HomeViewModel homeViewModel;
    FragmentHomeBinding binding;
    Realm realm;
    RecyclerView rvCityList;
    Observer cityListObserver;
    List <CityModel> cityModelList;
    List<WeatherModel> getWeatherModel;
    CityListAdapter cityListAdapter;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // initObserver();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initViewModel();
        initObjects();
        initViews(binding.getRoot());
        homeViewModel.initObject(getActivity(), realm, this);
        binding.setHomeViewModel(homeViewModel);
        homeViewModel.getAllCities();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObserver();
    }

    private void initObserver() {
        homeViewModel.cityList.observe(getViewLifecycleOwner(),cityModels -> {
            ShowLogs.displayLog("CITY LIST DAT IN FRAGMENT : "+cityModels.size());
            cityModelList.clear();
            cityModelList.addAll(cityModels);
            cityListAdapter.notifyDataSetChanged();
        });
    }

    void initObjects(){
        cityModelList = new ArrayList<>();
        getWeatherModel = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        cityListAdapter = new CityListAdapter(cityModelList,getWeatherModel,getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initViewModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    void initViews(View view){
        rvCityList = view.findViewById(R.id.rvCityList);
        rvCityList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCityList.setItemAnimator(new DefaultItemAnimator());
        rvCityList.setAdapter(cityListAdapter);
    }

    /*void initObserver(){
        cityListObserver = (Observer<List<CityModel>>) cityModels -> {
            ShowLogs.displayLog("fetched Data " + cityModels.toString());
            cityModelList.clear();
            cityModelList.addAll(cityModels);
            cityListAdapter.notifyDataSetChanged();
        };
        homeViewModel.cityList.observe(getViewLifecycleOwner(), cityListObserver);
    }*/

    @Override
    public void onSuccessfulDataFetchedFromLocalDb(List<CityModel> cityList, List<WeatherModel> getWeatherListModel) {
        ShowLogs.displayLog("CallBack Data " + cityList.toString());
        cityModelList.clear();
        getWeatherModel.clear();
        cityModelList.addAll(cityList);
        getWeatherModel.addAll(getWeatherListModel);
       // rvCityList.setAdapter(cityListAdapter);
        cityListAdapter.notifyDataSetChanged();
    }
}