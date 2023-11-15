package com.ngtnl1.student_information_management_app.controller.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ngtnl1.student_information_management_app.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomepageFragment extends Fragment {
    public HomepageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_homepage, container, false);

        return view;
    }
}
