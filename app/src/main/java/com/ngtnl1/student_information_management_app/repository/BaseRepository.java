package com.ngtnl1.student_information_management_app.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.ngtnl1.student_information_management_app.model.Student;

public class BaseRepository<T> {

    protected final String collectionName;
    protected FirebaseFirestore db;

    public BaseRepository(String collectionName) {
        this.collectionName = collectionName;
        this.db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public Task<DocumentReference> create(T item) {
        return db.collection(collectionName).add(item);
    }

    public Task<Void> update(String id, T item) {
        return db.collection(collectionName).document(id).set(item);
    }

    public Task<Void> remove(String id) {
        return db.collection(collectionName).document(id).delete();
    }

    public Task<QuerySnapshot> findAll() {
        return db.collection(collectionName).get();
    }

    public Task<DocumentSnapshot> find(String id) {
        return db.collection(collectionName).document(id).get();
    }
}
