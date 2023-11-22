package com.ngtnl1.student_information_management_app.repository;

import com.ngtnl1.student_information_management_app.model.Student;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StudentRepository extends BaseRepository<Student> {
    @Inject
    public StudentRepository() {
        super("students");
    }
}

