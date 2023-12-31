package com.ngtnl1.student_information_management_app.service;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserService {
    FirebaseAuth firebaseAuth;
    UserRepository userRepository;
    AppStatusService appStatusService;
    Context context;
    public String current_role = "";

    @Inject
    public UserService(FirebaseAuth firebaseAuth, UserRepository userRepository, AppStatusService appStatusService, Context context) {
        this.firebaseAuth = firebaseAuth;
        this.userRepository = userRepository;
        this.appStatusService = appStatusService;
        this.context = context;
    }

    public Task<AuthResult> logIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            getUserDataRaw().addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);

                if (user == null) {
                    user = new User(email);
                }

                addLoginHistory(user);
                userRepository.update(user.getEmail(), user);
            });
        });
    }

    public void getRole() {
        getUserDataRaw().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                current_role = user.getRole();
            }
        });
    }

    private void addLoginHistory(User user) {
        List<String> loginHistory = user.getLoginHistory();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        if (loginHistory != null) {
            loginHistory.add(sdf.format(new Date(System.currentTimeMillis())));
            user.setLoginHistory(loginHistory);
        } else {
            loginHistory = new ArrayList<>();
            loginHistory.add(sdf.format(new Date(System.currentTimeMillis())));
            user.setLoginHistory(loginHistory);
        }
    }

    public Task<AuthResult> register(String email, String password, String username) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(email, username);
                        addLoginHistory(user);
                        return userRepository.createUser(user)
                                .continueWithTask(createTask -> {
                                    if (createTask.isSuccessful()) {
                                        return task;
                                    } else {
                                        throw Objects.requireNonNull(createTask.getException());
                                    }
                                });
                    } else {
                        throw Objects.requireNonNull(task.getException());
                    }
                });
    }

    public Task<DocumentSnapshot> getUserDataRaw() {
        return userRepository.find(getUserEmail());
    }

    public String getUserEmail() {
        return firebaseAuth.getCurrentUser().getEmail();
    }

    public Task<QuerySnapshot> findAllUser() {
        return userRepository.findAll();
    }

    public Task<Void> setUser(User user) {
        return userRepository.update(user.getEmail(), user);
    }

    public Task<Void> createUser(User user) {
        return userRepository.createUser(user);
    }

    public Task<Void> deleteUser(String email) {
        return userRepository.remove(email);
    }

    public boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getFirebaseErrorMessage(Exception exception) {
        try {
            if (!appStatusService.isOnline()) {
                return "Không có kết nối internet.";
            }
            switch (Objects.requireNonNull(exception.getMessage())) {
                case "An internal error has occurred. [ INVALID_LOGIN_CREDENTIALS ]":
                    return "Sai tên email hoặc mật khẩu.";
                case "The email address is already in use by another account.":
                    return "Email đã được sử dụng để đăng ký.";
                default:
                    return "Có lỗi xảy ra. Vui lòng thử lại.";
            }
        } catch (Exception e) {
            return "Có lỗi xảy ra. Vui lòng thử lại.";
        }
    }
}
