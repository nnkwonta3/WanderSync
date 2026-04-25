package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddReservation extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText timeEditText;
    private EditText locationEditText;
    private EditText websiteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_reservation);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Button addReservationButton = findViewById(R.id.add_reservation_button);
        timeEditText = findViewById(R.id.enter_time);
        locationEditText = findViewById(R.id.enter_location);
        websiteEditText = findViewById(R.id.enter_website);
        addReservationButton = findViewById(R.id.add_reservation_button);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveReservation();
                finish();
            }
        });

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.dining);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.travelcommunity) {
                        startActivity(new Intent(getApplicationContext(), TravelCommunity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.dining) {
                        return true;
                    } else if (itemId == R.id.accommodations) {
                        startActivity(new Intent(getApplicationContext(), Accommodations.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.destinations) {
                        startActivity(new Intent(getApplicationContext(), Destinations.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.logistics) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    return false;
                }
            });

    }
    //Saving the registrations in the database
    private void saveReservation() {
        String time = timeEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String website = websiteEditText.getText().toString();
        String userEmail = auth.getCurrentUser() != null ? auth.getCurrentUser().getEmail()
                : "Anonymous";

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("time", time);
        reservation.put("location", location);
        reservation.put("website", website);
        reservation.put("user", userEmail);

        db.collection("Dining Reservations")
                .add(reservation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reservation added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding reservation", Toast.LENGTH_SHORT).show();
                });

    }
}