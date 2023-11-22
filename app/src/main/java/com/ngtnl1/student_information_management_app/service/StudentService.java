package com.ngtnl1.student_information_management_app.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.ngtnl1.student_information_management_app.model.Student;
import com.ngtnl1.student_information_management_app.repository.StudentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StudentService {
    StudentRepository studentRepository;

    @Inject
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Task<DocumentReference> createStudent(Student student) {
        return studentRepository.create(student);
    }

    public Task<QuerySnapshot> findAllStudent() {
        return studentRepository.findAll();
    }

    public Task<Void> deleteStudent(String id) {
        return studentRepository.remove(id);
    }
}
