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
public class RegisterActivity extends AppCompatActivity {
    @Inject
    UserService userService;
    @Inject
    AuthValidationService authValidationService;
    private EditText editTextRegisterUsername;
    private EditText editTextRegisterEmail;
    private EditText editTextRegisterPassword;
    private EditText editTextRegisterRepeatPassword;
    private Button buttonRegisterRegister;
    private Button buttonRegisterChangeToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setOnClickListeners();
    }

    private void initViews() {
        editTextRegisterUsername = findViewById(R.id.editTextRegisterUsername);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        editTextRegisterRepeatPassword = findViewById(R.id.editTextRegisterRepeatPassword);
        buttonRegisterRegister = findViewById(R.id.buttonRegisterRegister);
        buttonRegisterChangeToLogin = findViewById(R.id.buttonRegisterChangeToLogin);
    }

    private void setOnClickListeners() {
        buttonRegisterRegister.setOnClickListener(v -> register());
        buttonRegisterChangeToLogin.setOnClickListener(v -> changeToLogin());
    }

    private void register() {
        String username = editTextRegisterUsername.getText().toString();
        String email = editTextRegisterEmail.getText().toString();
        String password = editTextRegisterPassword.getText().toString();
        String repeatPassword = editTextRegisterRepeatPassword.getText().toString();

        if (isValidInput(username, email, password, repeatPassword)) {
            userService.register(email, password, username)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        String errorMessage = userService.getFirebaseErrorMessage(e);
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean isValidInput(String username, String email, String password, String repeatPassword) {
        boolean isValid = true;

        if (password.isEmpty()) {
            editTextRegisterPassword.setError("Vui lòng nhập mật khẩu!");
            editTextRegisterPassword.requestFocus();
            isValid = false;
        } if (password.length() < 6) {
            editTextRegisterPassword.setError("Mật khẩu cần ít nhất 6 ký tự!");
            editTextRegisterPassword.requestFocus();
            isValid = false;
        } else if (!authValidationService.isRepeatPasswordValid(password, repeatPassword)) {
            editTextRegisterRepeatPassword.setError("Mật khẩu nhập lại không khớp!");
            editTextRegisterRepeatPassword.requestFocus();
            isValid = false;
        }


        if (email.isEmpty()) {
            editTextRegisterEmail.setError("Vui lòng nhập email!");
            editTextRegisterEmail.requestFocus();
            isValid = false;
        } else if (!authValidationService.isEmailValid(email)) {
            editTextRegisterEmail.setError("Email không hợp lệ!");
            editTextRegisterEmail.requestFocus();
            isValid = false;
        }

        if (username.isEmpty()) {
            editTextRegisterUsername.setError("Vui lòng nhập tên người dùng!");
            editTextRegisterUsername.requestFocus();
            isValid = false;
        } else if (!authValidationService.isUsernameValid(username)) {
            editTextRegisterUsername.setError("Tên người dùng không hợp lệ!");
            editTextRegisterUsername.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void changeToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (userService.isUserSignedIn()) {
            finish();
        }
    }
}