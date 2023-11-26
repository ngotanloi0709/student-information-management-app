package com.ngtnl1.student_information_management_app.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.ngtnl1.student_information_management_app.model.Certificate;
import com.ngtnl1.student_information_management_app.repository.CertificateRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CertificateService {
    private CertificateRepository certificateRepository;

    @Inject
    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    public Task<DocumentReference> createCertificate(Certificate certificate) {
        return certificateRepository.create(certificate);
    }

    public Task<QuerySnapshot> findAllCertificate() {
        return certificateRepository.findAll();
    }

    public Task<Void> deleteCertificate(String id) {
        return certificateRepository.remove(id);
    }

    public Task<Void> updateCertificate(String id, Certificate certificate) {
        return certificateRepository.update(id, certificate);
    }
}
