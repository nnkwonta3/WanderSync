package com.example.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationViewModel extends ViewModel {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();

    public RegistrationViewModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void register(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Invalid email");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            errorMessage.setValue("Invalid password");
            return;
        }

        isLoading.setValue(true);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create user document in Firestore
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("duration", 0);
                        userData.put("endDate", "");
                        userData.put("startDate", "");

                        db.collection("Users")
                                .document(email)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {
                                    isLoading.setValue(false);
                                    registrationSuccess.setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Failed to create user profile");
                                });
                    } else {
                        isLoading.setValue(false);
                        errorMessage.setValue("Registration failed");
                    }
                });
    }

    // LiveData getters
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }
}