package com.ngtnl1.student_information_management_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Certificate {
    private String id;
    private String name;

    public Certificate(String name) {
        this.name = name;
    }
}
