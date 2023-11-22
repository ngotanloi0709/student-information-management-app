package com.ngtnl1.student_information_management_app.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String email;
    private String name = "";
    private String age = "";
    private String phone = "";
    private boolean isLocked = false;
    private String role;
    private List<String> loginHistory;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.role = "USER";
    }

    public User(String email, String username, String age, String phone, boolean isLocked, String role) {
        this.email = email;
        this.name = username;
        this.age = age;
        this.phone = phone;
        this.isLocked = isLocked;
        this.role = role;
    }
}
