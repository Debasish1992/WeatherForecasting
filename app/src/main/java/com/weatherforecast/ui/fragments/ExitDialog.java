package com.weatherforecast.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.weatherforecast.R;
import com.weatherforecast.interfaces.AlertActionClicked;
import com.weatherforecast.utils.ShowLogs;

public class ExitDialog extends Fragment implements AlertActionClicked {
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.blank_frag, container, false);
        ShowLogs.displayAlertMessage(getActivity(), "Exit App!!", "Are you sure you want to exit the app?", this);
        return v;
    }

    @Override
    public void onPositiveButtonClicked() {
        getActivity().finishAffinity();
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
