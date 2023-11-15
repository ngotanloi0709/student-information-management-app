package com.ngtnl1.student_information_management_app.repository;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.ngtnl1.student_information_management_app.model.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository extends BaseRepository<User> {
    @Inject
    public UserRepository() {
        super("users");
    }

    public Task<Void> create(User user) {
        return  db.collection(collectionName)
                .document(user.getId())
                .set(user);
    }

}