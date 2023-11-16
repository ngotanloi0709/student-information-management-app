package com.ngtnl1.student_information_management_app.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String id;
    private String email;
    private String name;
    private String age;
    private String phone;
    private boolean isLocked;
    private String role;
    private List<String> loginHistory;

    public User(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = "USER";
    }
}
