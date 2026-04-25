package com.example.myapplication.view;

import static com.example.myapplication.view.Login.editTextEmail;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TravelCommunity extends AppCompatActivity {
    private List<TravelPostObserver> observers = new ArrayList<>();
    private Button travelPostBtn;
    private FirebaseFirestore db;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_travel_community);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.travelcommunity);


        userId = Objects.requireNonNull(editTextEmail.getText()).toString();  // Firebase UserID
        travelPostBtn = findViewById(R.id.new_travel_button);
        db = FirebaseFirestore.getInstance();

        addObserver(new TravelPostListView(this));



        loadTravelPosts();


        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.logistics) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.travelcommunity) {
                        return true;
                    } else if (itemId == R.id.accommodations) {
                        startActivity(new Intent(getApplicationContext(), Accommodations.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.destinations) {
                        startActivity(new Intent(getApplicationContext(), Destinations.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } else if (itemId == R.id.dining) {
                        startActivity(new Intent(getApplicationContext(),
                                DiningEstablishments.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    return false;
                }
                });

        travelPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the dialog layout
                LayoutInflater inflater = LayoutInflater.from(TravelCommunity.this);
                View dialogView = inflater.inflate(R.layout.activity_add_post, null);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(TravelCommunity.this);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();

                dialog.setOnShowListener(dialogInterface -> {
                    android.view.WindowManager.LayoutParams lp = new android.view.WindowManager.LayoutParams();
                    android.view.Window window = dialog.getWindow();
                    if (window != null) {
                        lp.copyFrom(window.getAttributes());
                        lp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
                        window.setAttributes(lp);
                    }
                });

                // Get references to dialog elements
                EditText startDateTime = dialogView.findViewById(R.id.enter_start);
                EditText endDateTime = dialogView.findViewById(R.id.enter_end);
                EditText destination = dialogView.findViewById(R.id.enter_destination);
                EditText accommodations = dialogView.findViewById(R.id.enter_accommodations);
                EditText dining = dialogView.findViewById(R.id.enter_dining);
                EditText notes = dialogView.findViewById(R.id.enter_notes);
                Button addTravelPostButton = dialogView.findViewById(R.id.add_travel_post_button);

                startDateTime.setOnClickListener(view -> {
                    showDatePickerDialog(startDateTime);
                });

                endDateTime.setOnClickListener(view -> {
                    showDatePickerDialog(endDateTime);
                });

                // Create and show the dialog
                dialog.show();

                // Set click listener for "Add Travel Post" button in the dialog
                addTravelPostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Retrieve user input
                        String start = startDateTime.getText().toString().trim();
                        String end = endDateTime.getText().toString().trim();
                        String dest = destination.getText().toString().trim();
                        String accom = accommodations.getText().toString().trim();
                        String dine = dining.getText().toString().trim();
                        String note = notes.getText().toString().trim();

                        // Validate inputs (optional)

                        if (!start.isEmpty() && !end.isEmpty() && !dest.isEmpty()
                                && !accom.isEmpty() && !note.isEmpty()) {
                            if (isValidEndDate(start, end)) {
                                saveTravelPost(start, end, dest, accom, dine, note);
                                dialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Please fill out all fields", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    //Using Builder Design Pattern
    private void saveTravelPost(String start, String end, String destination,
                                String accommodations, String dining, String notes) {
        TravelPost travelPost = new TravelPost.Builder()
                .setStartDate(start)
                .setEndDate(end)
                .setDestination(destination)
                .setAccommodations(accommodations)
                .setDining(dining)
                .setNotes(notes)
                .setTripCreator(userId)
                .build();


        HashMap<String, Object> travelPostMap = new HashMap<>();
        travelPostMap.put("Start", travelPost.getStartDate());
        travelPostMap.put("End", travelPost.getEndDate());
        travelPostMap.put("Destination", travelPost.getDestination());
        travelPostMap.put("Accommodations", travelPost.getAccommodations());
        travelPostMap.put("Dining", travelPost.getDining());
        travelPostMap.put("Notes", travelPost.getNotes());
        travelPostMap.put("User", Collections.singletonList(userId));
        travelPostMap.put("Trip Creator", travelPost.getTripCreator());


        db.collection("Travel Posts")
                .add(travelPostMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Travel Post added", Toast.LENGTH_SHORT).show();
                    loadTravelPosts();  // Reload reservations after adding new one
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error saving travel post", Toast.LENGTH_SHORT).show());
    }




    private boolean isValidEndDate(String checkInDate, String checkOutDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);

            if (checkIn != null && checkOut != null && checkOut.before(checkIn)) {
                Toast.makeText(this, "End date cannot be before start date.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showDatePickerDialog(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (
                view, year1, month1, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                dateField.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    //Observer Pattern
    private void loadTravelPosts() {
        db.collection("Travel Posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> travelPostsList = new ArrayList<>(
                            queryDocumentSnapshots.getDocuments());

                    // Define the date format for sorting by "Start" dates in "d/M/yyyy"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy",
                            Locale.getDefault());
                    travelPostsList.sort((doc1, doc2) -> {
                        try {
                            Date startDate1 = dateFormat.parse(doc1.getString("Start"));
                            Date startDate2 = dateFormat.parse(doc2.getString("Start"));
                            return startDate1.compareTo(startDate2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    List<TravelPost> travelPosts = new ArrayList<>();
                    for (DocumentSnapshot document : travelPostsList) {
                        String start = document.getString("Start");
                        String end = document.getString("End");
                        String destination = document.getString("Destination");
                        String accommodation = document.getString("Accommodations");
                        String dining = document.getString("Dining");
                        String notes = document.getString("Notes");
                        String tripCreator = document.getString("Trip Creator");

                        // Create a TravelPost object
                        TravelPost travelPost = new TravelPost.Builder()
                                .setStartDate(start)
                                .setEndDate(end)
                                .setDestination(destination)
                                .setAccommodations(accommodation)
                                .setDining(dining)
                                .setNotes(notes)
                                .setTripCreator(tripCreator)
                                .build();

                        travelPosts.add(travelPost);
                    }

                    // Notify all observers (UI elements) about the updated travel posts
                    notifyObservers(travelPosts);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TravelCommunity.this,
                            "Error loading travel posts", Toast.LENGTH_SHORT).show();
                });
    }

    public void addObserver(TravelPostObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(List<TravelPost> travelPosts) {
        for (TravelPostObserver observer : observers) {
            observer.updateTravelPosts(travelPosts);
        }
    }

}