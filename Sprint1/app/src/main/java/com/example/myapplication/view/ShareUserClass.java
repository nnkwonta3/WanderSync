package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShareUserClass extends AppCompatActivity {
    private Button shareUserButton;
    private Button cancelButton;
    private TextInputEditText editShareUser;
    private TextInputEditText editTripName;
    private String currentUserId; // This will hold the current user's email
    private HashMap<String, ArrayList<String>> userTrips = new HashMap<>();
    // userId + " Trip " + tripnumber to user array mapping
    private FirebaseFirestore firestore; // Firestore instance
    private FirebaseAuth auth; // Firebase Authentication instance
    private CollectionReference destinationsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_user_class);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Get the current user's email
        if (currentUser != null) {
            currentUserId = currentUser.getEmail(); // Get the email of the signed-in user
        } else {
            // Handle the case where the user is not signed in
            Toast.makeText(this, "No user is signed in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no user is signed in
            return;
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        shareUserButton = findViewById(R.id.btn_share);
        cancelButton = findViewById(R.id.btn_cancel);
        editShareUser = findViewById(R.id.et_share_user);
        editTripName = findViewById(R.id.et_trip_name);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ShareUserClass.this, MainActivity.class));
            }
        });

        shareUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newUserId = editShareUser.getText().toString().trim();
                shareWithAllTrips(newUserId);
                startActivity(new Intent(ShareUserClass.this, MainActivity.class));

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.logistics);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.dining) {
                            startActivity(new Intent(getApplicationContext(),
                                    DiningEstablishments.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemId == R.id.logistics) {
                            return true;
                        } else if (itemId == R.id.accommodations) {
                            startActivity(new Intent(getApplicationContext(),
                                    Accommodations.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemId == R.id.destinations) {
                            startActivity(new Intent(getApplicationContext(), Destinations.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemId == R.id.travelcommunity) {
                            startActivity(new Intent(getApplicationContext(),
                                    TravelCommunity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        }
                        return false;
                    }
                });
    }


    public void shareWithAllTrips(String userIdToAdd) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String tripName = editTripName.getText().toString().trim();
        // Get the trip name entered by the user

        if (currentUserId == null || userIdToAdd.isEmpty()) {
            Toast.makeText(this, "Invalid input: user ID is missing",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (tripName.isEmpty() || userIdToAdd.isEmpty()) {
            Toast.makeText(this, "Please provide all required details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Access the "Destinations" collection and then add user for only trip selected
        db.collection("Destinations")
                .whereEqualTo("location", tripName) // Match the trip name (Location)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Update the "UserId" array field to include the new user
                            db.collection("Destinations")
                                    .document(document.getId())
                                    .update("UserId", FieldValue.arrayUnion(userIdToAdd))
                                    .addOnSuccessListener(aVoid -> Toast.makeText(this,
                                            "User added to trip successfully",
                                            Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this,
                                            "Failed to add user to trip",
                                            Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        // Trip not found
                        Toast.makeText(this, "This trip does not exist. Try again.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error accessing database",
                            Toast.LENGTH_SHORT).show();
                });

        db.collection("Dining Reservations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> userList = (List<String>) document.get("user");

                            if (userList != null && userList.contains(currentUserId)) {
                                db.collection("Dining Reservations").
                                        document(document.getId())
                                        .update("user", FieldValue.arrayUnion(userIdToAdd))
                                        .addOnSuccessListener(aVoid -> Toast.makeText(this,
                                                "User added to trip: " + document.getId(),
                                                Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this,
                                                "Error updating trip: " + document.getId(),
                                                Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else if (task.getResult().isEmpty()) {
                        Toast.makeText(this, "No trips found for user",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error fetching trips",
                                Toast.LENGTH_SHORT).show();
                    }
                });


        db.collection("Accommodations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> userList = (List<String>) document.get("User");

                            // Check if currentUserId exists in the userList before updating
                            if (userList != null && userList.contains(currentUserId)) {
                                db.collection("Accommodations").
                                        document(document.getId())
                                        .update("User", FieldValue.arrayUnion(userIdToAdd))
                                        .addOnSuccessListener(aVoid -> Toast.makeText(this,
                                                "User added to trip: " + document.getId(),
                                                Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this,
                                                "Error updating trip: " + document.getId(),
                                                Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else if (task.getResult().isEmpty()) {
                        Toast.makeText(this, "No trips found for user", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error fetching trips", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("Travel Posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> userList = (List<String>) document.get("User");

                            // Check if currentUserId exists in the userList before updating
                            if (userList != null && userList.contains(currentUserId)) {
                                db.collection("Travel Posts").document(document.getId())
                                        .update("User", FieldValue.arrayUnion(userIdToAdd))
                                        .addOnSuccessListener(aVoid -> Toast.makeText(this,
                                                "User added to trip: " + document.getId(),
                                                Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this,
                                                "Error updating trip: " + document.getId(),
                                                Toast.LENGTH_SHORT).show());
                            }
                        }
                    } else if (task.getResult().isEmpty()) {
                        Toast.makeText(this, "No trips found for user", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error fetching trips", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}