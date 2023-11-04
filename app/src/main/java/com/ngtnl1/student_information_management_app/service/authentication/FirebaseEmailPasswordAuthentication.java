package com.ngtnl1.student_information_management_app.service.authentication;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.repository.UserRepository;

public class FirebaseEmailPasswordAuthentication {
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    
    public FirebaseEmailPasswordAuthentication() {
        firebaseAuth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
    }

    public Task<AuthResult> logIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> register(String email, String password, String username, String phone, String dateOfBirth) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(getUserUid(), email, username, phone);
                        return userRepository.create(user)
                                .continueWithTask(createTask -> {
                                    if (createTask.isSuccessful()) {
                                        return task;
                                    } else {
                                        throw createTask.getException();
                                    }
                                });
                    } else {
                        throw task.getException();
                    }
                });
    }

    public String getUserUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public String getUserEmail() {
        return firebaseAuth.getCurrentUser().getEmail();
    }

    public boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getFirebaseErrorMessage(Exception exception) {
        try {
            switch (exception.getMessage()) {
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
