package com.ngtnl1.student_information_management_app.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.ngtnl1.student_information_management_app.model.User;
import com.ngtnl1.student_information_management_app.repository.UserRepository;
import com.ngtnl1.student_information_management_app.service.authentication.FirebaseEmailPasswordAuthentication;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserService {
    UserRepository userRepository;
    FirebaseEmailPasswordAuthentication firebaseEmailPasswordAuthentication;

    @Inject
    public UserService(UserRepository userRepository, FirebaseEmailPasswordAuthentication firebaseEmailPasswordAuthentication) {
        this.userRepository = userRepository;
        this.firebaseEmailPasswordAuthentication = firebaseEmailPasswordAuthentication;
    }

    public Task<QuerySnapshot> getAllUser() {
        return userRepository.findAll();
    }

    public void setUserData(User user) {
        userRepository.update(user.getId(), user);
    }
}
