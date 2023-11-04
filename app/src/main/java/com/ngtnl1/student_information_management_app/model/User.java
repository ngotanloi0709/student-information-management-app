package com.ngtnl1.student_information_management_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String id;
    private String email;
    private String fullname;
    private String phone;
}
