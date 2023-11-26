package com.ngtnl1.student_information_management_app.repository;

import com.google.android.gms.tasks.Task;
import com.ngtnl1.student_information_management_app.model.Student;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StudentRepository extends BaseRepository<Student> {
    @Inject
    public StudentRepository() {
        super("students");
    }
    
    public Task<Void> addCertificate(String studentId, String certificate_id) {
        this.find(studentId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Student student = task.getResult().toObject(Student.class);
                student.getCertificates().add(certificate_id);
                this.update(studentId, student);
            }
        });
        return null;
    }
}

