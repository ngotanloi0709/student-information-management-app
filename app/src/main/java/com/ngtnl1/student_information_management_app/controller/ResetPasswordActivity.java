package com.ngtnl1.student_information_management_app.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ngtnl1.student_information_management_app.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    }
}