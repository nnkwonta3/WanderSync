package com.example.myapplication.view;

import com.google.firebase.firestore.FirebaseFirestore;

public class TravelLogDatabase {
    private static TravelLogDatabase instance;
    private static FirebaseFirestore db;

    private TravelLogDatabase() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized TravelLogDatabase getInstance() {
        if (instance == null) {
            instance = new TravelLogDatabase();
        }
        return instance;
    }

}
