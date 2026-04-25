package com.example.myapplication.view;

import static com.example.myapplication.view.Login.editTextEmail;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Map;
import java.util.Objects;

public class DiningEstablishments extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userId;
    private LinearLayout reservationListLayout;  // Layout for displaying reservations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dining_establishments);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        reservationListLayout = findViewById(R.id.reservation_list); // Link to the layout

        userId = Objects.requireNonNull(editTextEmail.getText()).toString();  // Firebase UserID

        Button newReservationButton = findViewById(R.id.new_reservation_button);
        newReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReservationPopup();
            }
        });

        // Set Home selected for BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.dining);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        int itemID = item.getItemId();
                        if (itemID == R.id.logistics) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemID == R.id.dining) {
                            return true;
                        } else if (itemID == R.id.travelcommunity) {
                            startActivity(new Intent(getApplicationContext(),
                                    TravelCommunity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemID == R.id.destinations) {
                            startActivity(new Intent(getApplicationContext(), Destinations.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemID == R.id.accommodations) {
                            startActivity(new Intent(getApplicationContext(),
                                    Accommodations.class));
                            overridePendingTransition(0, 0);
                            return true;
                        }
                        return false;
                    }

                });
        loadReservations();
    }

    private void loadReservations() {
        db.collection("Dining Reservations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reservationListLayout.removeAllViews();  // Clear existing views
                    List<DocumentSnapshot> reservationList = new ArrayList<>(
                            queryDocumentSnapshots.getDocuments());

                    // Define the date-time format that matches "05:59 PM 2024-11-10"
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                            "hh:mm a yyyy-MM-dd", Locale.getDefault());
                    reservationList.sort((doc1, doc2) -> {
                        try {
                            Date date1 = dateTimeFormat.parse(doc1.getString("time")
                                    + " " + doc1.getString("date"));
                            Date date2 = dateTimeFormat.parse(doc2.getString("time")
                                    + " " + doc2.getString("date"));
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    // Get current date and time for expired check
                    Date currentDate = new Date();

                    for (DocumentSnapshot document : reservationList) {
                        List<String> userList = (List<String>) document.get("user");

                        // Check if userId is in the user list
                        if (userList != null && userList.contains(userId)) {
                            String time = document.getString("time");
                            String date = document.getString("date");
                            String location = document.getString("location");
                            String website = document.getString("website");
                            String restaurant = document.getString("restaurant");

                            try {
                                // Parse combined time and date string in the specified format
                                Date reservationDate = dateTimeFormat.parse(time + " "
                                        + date);

                                // Create a new TextView for each reservation
                                TextView reservationView = new TextView(
                                        DiningEstablishments.this);
                                StringBuilder reservationText = new StringBuilder();

                                reservationText.append(location).append("\n")
                                        .append(restaurant).append(" - ").append(time)
                                        .append("\n").append(website);

                                // Check if reservation is expired after reload
                                if (reservationDate != null
                                        && reservationDate.before(currentDate)) {
                                    reservationText.append("\nStatus: Expired").append(" (").
                                            append(date).append(")");
                                    reservationView.setTextColor(Color.RED);
                                } else {
                                    reservationText.append("\nStatus: Upcoming").append(" (").
                                            append(date).append(")");
                                }

                                reservationView.setText(reservationText.toString());
                                reservationView.setTypeface(null, android.graphics.Typeface.BOLD);
                                reservationView.setTextSize(19);
                                reservationView.setPadding(16, 16, 16, 16);

                                // Add the TextView to the layout
                                reservationListLayout.addView(reservationView);

                                // Add a separator line
                                View separator = new View(DiningEstablishments.this);
                                separator.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        5  // Thickness of the line
                                ));
                                separator.setBackgroundColor(getResources().
                                        getColor(android.R.color.black));
                                reservationListLayout.addView(separator);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DiningEstablishments.this,
                            "Error loading reservations", Toast.LENGTH_SHORT).show();
                });
    }


    private void showAddReservationPopup() {
        // Create the dialog for adding reservation
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_add_reservation);

        EditText timeEditText = dialog.findViewById(R.id.enter_time);
        EditText locationEditText = dialog.findViewById(R.id.enter_location);
        EditText websiteEditText = dialog.findViewById(R.id.enter_website);
        EditText restaurantEditText = dialog.findViewById(R.id.enter_restaurant);

        Button addReservationButton = dialog.findViewById(R.id.add_reservation_button);

        final Calendar selectedCalendar = Calendar.getInstance();
        final String[] selectedDate = {""};  // To store the date
        final String[] selectedTime = {""};  // To store the time

        // Show DatePickerDialog when timeEditText is clicked
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(DiningEstablishments.this,
                        (view, year, month, dayOfMonth) -> {
                            selectedCalendar.set(year, month, dayOfMonth);

                            // Format the selected date
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd", Locale.getDefault());
                            selectedDate[0] = dateFormat.format(selectedCalendar.getTime());

                            // Show TimePickerDialog after selecting a date
                            TimePickerDialog timePickerDialog = new TimePickerDialog(
                                    DiningEstablishments.this,
                                    (timePicker, hourOfDay, minute) -> {
                                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        selectedCalendar.set(Calendar.MINUTE, minute);

                                        // Format the selected time
                                        SimpleDateFormat timeFormat = new SimpleDateFormat(
                                                "hh:mm a", Locale.getDefault());
                                        selectedTime[0] = timeFormat.format(
                                                selectedCalendar.getTime());

                                        // Display both date and time in timeEditText for
                                        // user confirmation
                                        timeEditText.setText(selectedDate[0] + " "
                                                + selectedTime[0]);
                                    },
                                    selectedCalendar.get(Calendar.HOUR_OF_DAY),
                                    selectedCalendar.get(Calendar.MINUTE),
                                    false);
                            timePickerDialog.show();
                        },
                        selectedCalendar.get(Calendar.YEAR),
                        selectedCalendar.get(Calendar.MONTH),
                        selectedCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // Button click listener for adding reservation
        addReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = selectedDate[0].trim();
                String time = selectedTime[0].trim();
                String location = locationEditText.getText().toString().trim();
                String website = websiteEditText.getText().toString().trim();
                String restaurant = restaurantEditText.getText().toString().trim();

                // Validate inputs
                if (date.isEmpty() || time.isEmpty() || location.isEmpty()
                        || website.isEmpty() || restaurant.isEmpty()) {
                    Toast.makeText(DiningEstablishments.this,
                            "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Combine date and time into a single string and parse it
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm a", Locale.getDefault());
                try {
                    Date selectedDateTime = dateTimeFormat.parse(date + " " + time);
                    Date currentDateTime = new Date();

                    // Check if selected date and time are in the past
                    if (selectedDateTime != null && selectedDateTime.before(currentDateTime)) {
                        Toast.makeText(DiningEstablishments.this,
                                "Cannot select a past date and time",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Save reservation to Firebase if date and time are valid
                    saveReservation(date, time, location, website, restaurant);
                    dialog.dismiss();  // Close the dialog after saving

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(DiningEstablishments.this,
                            "Error parsing date and time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }


    private void saveReservation(String date, String time, String location,
                                 String website, String restaurant) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", date);  // Store the date as a separate field
        reservation.put("time", time);  // Store the time as a separate field
        reservation.put("location", location);
        reservation.put("website", website);
        reservation.put("restaurant", restaurant);
        reservation.put("user", Collections.singletonList(userId));

        // Add reservation data to Firestore
        db.collection("Dining Reservations")
                .add(reservation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Reservation added", Toast.LENGTH_SHORT).show();
                    loadReservations();  // Reload reservations after adding new one
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding reservation", Toast.LENGTH_SHORT).show();
                });
    }

}