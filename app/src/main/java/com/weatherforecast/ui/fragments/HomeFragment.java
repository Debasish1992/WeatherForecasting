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
import com.weatherforecast.interfaces.AlertActionClicked;
import com.weatherforecast.interfaces.ConnectionChecker;
import com.weatherforecast.interfaces.HomeFragmentUiCallback;
import com.weatherforecast.utils.ConnectivityManager;
import com.weatherforecast.utils.ShowLogs;
import com.weatherforecast.viewmodels.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class HomeFragment extends Fragment implements HomeFragmentUiCallback, ConnectionChecker, AlertActionClicked {
    HomeViewModel homeViewModel;
    FragmentHomeBinding binding;
    Realm realm;
    RecyclerView rvCityList;
    Observer cityListObserver;
    List <CityModel> cityModelList;
    List<WeatherModel> getWeatherModel;
    CityListAdapter cityListAdapter;
    androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

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
        homeViewModel.initObject(getActivity(), realm, this, this);
        binding.setHomeViewModel(homeViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObserver();
    }

    private void initObserver() {
        homeViewModel.cityList.observe(getViewLifecycleOwner(),cityModels -> {
            cityModelList.clear();
            cityModelList.addAll(cityModels);
            cityListAdapter.notifyDataSetChanged();
        });
    }

    void initObjects(){
        cityModelList = new ArrayList<>();
        getWeatherModel = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        cityListAdapter = new CityListAdapter(homeViewModel,cityModelList,getActivity() );
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.getAllCities();
    }

    private void initViewModel() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    void initViews(View view){
        swipeRefreshLayout = view.findViewById(R.id.pullToRefreshCityList);
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
    public void onSuccessfulDataFetchedFromLocalDb(List<CityModel> cityList) {
        swipeRefreshLayout.setRefreshing(false);
        cityModelList.clear();
        getWeatherModel.clear();
        cityModelList.addAll(cityList);
        cityListAdapter.notifyDataSetChanged();
        homeViewModel.getNetworkConnectionStatus();
    }

    @Override
    public void isConnected(boolean status) {
        if(!status){
            swipeRefreshLayout.setRefreshing(false);
            ShowLogs.displayAlertMessageNoInternet(getActivity(),
                    getResources().getString(R.string.no_internet_message_title),
                    getResources().getString(R.string.no_internet_message),
                    this);
        }
    }

    @Override
    public void onPositiveButtonClicked() {
        ConnectivityManager.turnOnMobileData(getActivity());

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}