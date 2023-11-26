package com.ngtnl1.student_information_management_app.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {
    private String id;
    private String name;
    private String major;
    private boolean isFemale;
    private String age;
    private String email;
    private String phone;
    private List<String> certificates;

    public Student(String name, String major, String sex, String age, String email, String phone) {
        this.name = name;
        this.major = major;
        if (sex.equals("Nam")) {
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
        this.age = age;
        this.email = email;
        this.phone = phone;
    }
}
