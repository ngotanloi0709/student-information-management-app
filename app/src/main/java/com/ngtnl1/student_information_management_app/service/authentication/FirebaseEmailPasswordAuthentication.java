package com.ngtnl1.student_information_management_app.service.authentication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.repository.UserRepository;
import com.ngtnl1.student_information_management_app.service.appstatus.InternetStatus;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FirebaseEmailPasswordAuthentication {
    FirebaseAuth firebaseAuth;
    UserRepository userRepository;
    InternetStatus internetStatus;

    @Inject
    public FirebaseEmailPasswordAuthentication(FirebaseAuth firebaseAuth, UserRepository userRepository, InternetStatus internetStatus) {
        this.firebaseAuth = firebaseAuth;
        this.userRepository = userRepository;
        this.internetStatus = internetStatus;
    }

    public Task<AuthResult> logIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> register(String email, String password, String username) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(firebaseAuth.getUid(), email, username);

                        return userRepository.create(user)
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

    public User getUserData() {
        return userRepository.find(getUserUid()).getResult().toObject(User.class);
    }

    public Task<DocumentSnapshot> getUserDataRaw() {
        return userRepository.find(getUserUid());
    }

    public String getUserUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public String getUsername() {
        return getUserData().getName();
    }

    public String getUserEmail() {
        return getUserData().getEmail();
    }

    public String getUserRole() {
        return getUserData().getRole();
    }

    public boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getFirebaseErrorMessage(Exception exception) {
        try {
            if (!internetStatus.isOnline()) {
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
