package com.studentlead.ui.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.studentlead.R;
import com.studentlead.data.model.LoginState;
import com.studentlead.data.repository.LoginRepository;
import com.studentlead.util.Patterns;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private final LoginRepository repository;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>(LoginState.initial());


    @Inject
    public LoginViewModel(LoginRepository repository) {
        this.repository = repository;
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public void login(String identifier, String password) {
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            loginState.setValue(LoginState.errorResource(R.string.required_fields));
            return;
        }

        loginState.setValue(LoginState.loading());

        if (Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
            loginWithEmail(identifier, password);
        } else if (Patterns.NATIONAL_ID.matcher(identifier).matches()) {
            tryNationalId(identifier, password);
        } else if (Patterns.STUDENT_CODE.matcher(identifier).matches()) {
            tryStudentCode(identifier, password);
        } else {
            loginState.setValue(LoginState.errorResource(R.string.user_identifier_not_found));
        }

    }

    private void tryNationalId(String nationalId, String password) {
        repository.findUserByNationalId(nationalId)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String email = document.getString("email");
                        if (email != null) {
                            loginWithEmail(email, password);
                        } else {
                            loginState.setValue(LoginState.errorResource(R.string.email_not_found_nid));
                        }
                    } else {
                        loginState.setValue(LoginState.errorResource(R.string.no_user_nid));
                    }
                })
                .addOnFailureListener(e -> loginState.setValue(LoginState.error(e.getMessage())));
    }

    private void tryStudentCode(String studentCode, String password) {
        repository.findUserByStudentCode(studentCode)
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String email = document.getString("email");
                        if (email != null) {
                            loginWithEmail(email, password);
                        } else {
                            loginState.setValue(LoginState.errorResource(R.string.email_not_found_sc));
                        }
                    } else {
                        loginState.setValue(LoginState.errorResource(R.string.user_not_found_sc));
                    }
                })
                .addOnFailureListener(e -> loginState.setValue(LoginState.error(e.getMessage())));
    }

    private void loginWithEmail(String email, String password) {
        repository.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> loginState.setValue(LoginState.success()))
                .addOnFailureListener(e -> loginState.setValue(LoginState.error(e.getMessage())));
    }
}