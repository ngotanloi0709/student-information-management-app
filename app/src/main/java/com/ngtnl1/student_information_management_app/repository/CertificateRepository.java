package com.ngtnl1.student_information_management_app.repository;

import com.ngtnl1.student_information_management_app.model.Certificate;

import javax.inject.Inject;

public class CertificateRepository extends BaseRepository<Certificate> {
    @Inject
    public CertificateRepository() {
        super("certificates");
    }
}