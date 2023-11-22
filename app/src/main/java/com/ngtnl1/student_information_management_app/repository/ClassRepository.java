package com.ngtnl1.student_information_management_app.repository;

import com.ngtnl1.student_information_management_app.model.MajorClass;
import com.ngtnl1.student_information_management_app.model.User;

import javax.inject.Inject;

public class ClassRepository extends BaseRepository<MajorClass> {
    @Inject
    public ClassRepository() {
        super("classes");
    }
}
