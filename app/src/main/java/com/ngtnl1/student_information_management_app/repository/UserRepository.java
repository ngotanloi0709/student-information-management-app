package com.ngtnl1.student_information_management_app.repository;

import com.ngtnl1.student_information_management_app.model.User;

public class UserRepository extends BaseRepository<User> {
    public UserRepository() {
        super("users");
    }
}
