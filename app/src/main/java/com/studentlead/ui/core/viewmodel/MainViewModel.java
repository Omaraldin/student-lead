package com.studentlead.ui.core.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final FirebaseAuth auth;

    private final FirebaseFirestore firestore;

    private final MutableLiveData<Boolean> isLoggedIn =  new MutableLiveData<>();


    @Inject
    public MainViewModel(FirebaseAuth auth, FirebaseFirestore firestore) {
        this.auth = auth;
        this.firestore = firestore;

        checkAuthState();
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    private void checkAuthState() {
        isLoggedIn.setValue(auth.getCurrentUser() != null);
    }
}
