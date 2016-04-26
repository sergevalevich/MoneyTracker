package com.valevich.moneytracker.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.valevich.moneytracker.R;

public class StatisticsFragment extends Fragment {

    public StatisticsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        // Inflate the layout for this fragment
        return rootView;
    }

}
