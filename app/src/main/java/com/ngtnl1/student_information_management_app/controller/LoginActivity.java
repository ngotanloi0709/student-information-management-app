package com.ngtnl1.student_information_management_app.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ngtnl1.student_information_management_app.R;
import com.ngtnl1.student_information_management_app.service.UserService;
import com.ngtnl1.student_information_management_app.service.AuthValidationService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    @Inject
    UserService userService;
    @Inject
    AuthValidationService authValidationService;
    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private Button buttonLoginLogin;
    private Button buttonLoginChangeToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLoginLogin = findViewById(R.id.buttonLoginLogin);
        buttonLoginChangeToRegister = findViewById(R.id.buttonLoginChangeToRegister);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        buttonLoginLogin.setOnClickListener(v -> login());
        buttonLoginChangeToRegister.setOnClickListener(v -> changeToRegister());
    }

    private void login() {
        String email = editTextLoginEmail.getText().toString();
        String password = editTextLoginPassword.getText().toString();

        if (isValidInput(email, password)) {
            userService.logIn(email, password)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        changeToMain();
                    })
                    .addOnFailureListener(e -> {
                        String errorMessage = userService.getFirebaseErrorMessage(e);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean isValidInput(String email, String password) {
        boolean isValid = true;

        if (password.isEmpty()) {
            editTextLoginPassword.setError("Vui lòng nhập mật khẩu!");
            editTextLoginPassword.requestFocus();
            isValid = false;
        } else if (!authValidationService.isPasswordValid(password)) {
            editTextLoginPassword.setError("Mật khẩu cần ít nhất 6 ký tự!");
            editTextLoginPassword.requestFocus();
            isValid = false;
        }

        if (email.isEmpty()) {
            editTextLoginEmail.setError("Vui lòng nhập email!");
            editTextLoginEmail.requestFocus();
            isValid = false;
        } else if (!authValidationService.isEmailValid(email)) {
            editTextLoginEmail.setError("Email không hợp lệ!");
            editTextLoginEmail.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void changeToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void changeToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (userService.isUserSignedIn()) {
            changeToMain();
        }
    }
}