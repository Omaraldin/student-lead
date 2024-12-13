package com.studentlead.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class LoginRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    @Inject
    public LoginRepository(FirebaseAuth auth, FirebaseFirestore firestore) {
        this.auth = auth;
        this.firestore = firestore;
    }

    public Task<QuerySnapshot> findUserByNationalId(String nationalId) {
        return firestore.collection("users")
                .whereEqualTo("nationalId", nationalId)
                .get();
    }

    public Task<QuerySnapshot> findUserByStudentCode(String studentCode) {
        return firestore.collection("students")
                .whereEqualTo("studentCode", studentCode)
                .get();
    }

    public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }
}