package com.ngtnl1.student_information_management_app.model;

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
    private String major_class;
    private boolean isFemale;
    private String birthday;
    private String address;
    private String email;
    private String phoneNumber;
}
