package com.example.myapplication.view;

import static com.example.myapplication.view.Login.editTextEmail;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.myapplication.viewmodel.AccommodationViewModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class Accommodations extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId;
    private LinearLayout accommodationsListLayout;

    private FirebaseFirestore db;
    private FloatingActionButton fabAddAccommodation;
    private AccommodationViewModel accommodationService = new AccommodationViewModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accomodations);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(editTextEmail.getText()).toString();  // Firebase UserID
        accommodationsListLayout = findViewById(R.id.accommodations_list); // Link to the layout

        fabAddAccommodation = findViewById(R.id.fab_add_accommodation);
        fabAddAccommodation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAccommodationPopup();
            }
        });
        loadAccommodations();


        bottomNavigationView.setSelectedItemId(R.id.accommodations);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.logistics) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemId == R.id.accommodations) {
                            return true;
                        } else if (itemId == R.id.travelcommunity) {
                            startActivity(new Intent(getApplicationContext(),
                                    TravelCommunity.class));
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
    }

    private void loadAccommodations() {
        db.collection("Accommodations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    accommodationsListLayout.removeAllViews();  // Clear existing view
                    List<DocumentSnapshot> accommodationsList = new ArrayList<>(
                            queryDocumentSnapshots.getDocuments());

                    // Define the date format for "CheckIn" dates in "d/M/yyyy"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy",
                            Locale.getDefault());
                    accommodationsList.sort((doc1, doc2) -> {
                        try {
                            Date checkIn1 = dateFormat.parse(doc1.getString("CheckIn"));
                            Date checkIn2 = dateFormat.parse(doc2.getString("CheckIn"));
                            return checkIn1.compareTo(checkIn2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    // Get current date for expired check
                    Date currentDate = new Date();

                    for (DocumentSnapshot document : accommodationsList) {
                        List<String> userList = (List<String>) document.get("User");

                        // Check if userId is in the user list
                        if (userList != null && userList.contains(userId)) {
                            String location = document.getString("Location");
                            String checkIn = document.getString("CheckIn");
                            String checkOut = document.getString("CheckOut");
                            int numRooms = document.getLong("NumRooms").intValue();
                            String roomType = document.getString("RoomType");

                            try {
                                // Parse check-in date in the specified format
                                Date checkInDate = dateFormat.parse(checkIn);

                                // Create a new TextView for each accommodation
                                TextView accommodationView = new TextView(
                                        Accommodations.this);
                                StringBuilder accommodationText = new StringBuilder();

                                accommodationText.append("Location: ").append(location).append("\n")
                                        .append("Check-in: ").append(checkIn).append("\n")
                                        .append("Check-out: ").append(checkOut).append("\n")
                                        .append("Number of Rooms: ").append(numRooms).append("\n")
                                        .append("Room Type: ").append(roomType);

                                // Check if accommodation is expired
                                if (checkInDate != null && checkInDate.before(currentDate)) {
                                    accommodationText.append("\nStatus: Expired");
                                    accommodationView.setTextColor(Color.RED);
                                } else {
                                    accommodationText.append("\nStatus: Upcoming");
                                }

                                accommodationView.setText(accommodationText.toString());
                                accommodationView.setTypeface(null, android.graphics.Typeface.BOLD);
                                accommodationView.setTextSize(19);
                                accommodationView.setPadding(16, 16, 16, 16);

                                // Add the TextView to the layout
                                accommodationsListLayout.addView(accommodationView);

                                // Add a separator line
                                View separator = new View(Accommodations.this);
                                separator.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        5  // Thickness of the line
                                ));
                                separator.setBackgroundColor(getResources().getColor(
                                        android.R.color.black));
                                accommodationsListLayout.addView(separator);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Accommodations.this, "Error loading accommodations",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddAccommodationPopup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_accomodation);

        EditText locationInput = dialog.findViewById(R.id.input_location);
        EditText checkInInput = dialog.findViewById(R.id.input_check_in);
        EditText checkOutInput = dialog.findViewById(R.id.input_check_out);
        Spinner numRoomsSpinner = dialog.findViewById(R.id.spinner_num_rooms);
        Spinner roomTypeSpinner = dialog.findViewById(R.id.spinner_room_type);
        Button btnSave = dialog.findViewById(R.id.btn_save);

        Integer[] roomNumbers = {1, 2, 3, 4};
        ArrayAdapter<Integer> numRoomsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roomNumbers);
        numRoomsSpinner.setAdapter(numRoomsAdapter);

        String[] roomTypes = {"King Suite", "2 Single Beds", "Queen Room"};
        ArrayAdapter<String> roomTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roomTypes);
        roomTypeSpinner.setAdapter(roomTypeAdapter);

        // Check-in date picker
        checkInInput.setOnClickListener(view -> {
            showDatePickerDialog(checkInInput, true);
        });

        // Check-out date picker
        checkOutInput.setOnClickListener(view -> {
            showDatePickerDialog(checkOutInput, false);
        });

        btnSave.setOnClickListener(view -> {
            String location = locationInput.getText().toString();
            String checkIn = checkInInput.getText().toString();
            String checkOut = checkOutInput.getText().toString();
            int numRooms = (int) numRoomsSpinner.getSelectedItem();
            String roomType = (String) roomTypeSpinner.getSelectedItem();

            if (!location.isEmpty() && !checkIn.isEmpty() && !checkOut.isEmpty()) {
                if (isValidCheckInDate(checkIn) && isValidCheckOutDate(checkIn, checkOut)) {
                    saveAccommodation(location, checkIn, checkOut, numRooms, roomType);
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(Accommodations.this, "Please fill out all fields",
                        Toast.LENGTH_SHORT).show();
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showDatePickerDialog(EditText dateField, boolean isCheckIn) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                dateField.setText(date);
                showTimePickerDialog(dateField, isCheckIn); // Show time picker after date
            }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(EditText dateField, boolean isCheckIn) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this, (view, hourOfDay, minute1) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d",
                    hourOfDay, minute1);
            String dateTime = dateField.getText().toString() + "  at  " + time;
            dateField.setText(dateTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private boolean isValidCheckInDate(String checkInDate) {
        // Update the format to include both date and time
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy ' at ' HH:mm", Locale.getDefault());
        Date currentDate = new Date();  // Get current date and time

        try {
            Date checkIn = sdf.parse(checkInDate);
            if (checkIn != null && checkIn.before(currentDate)) {
                Toast.makeText(this, "Check-in date and time cannot be in the past.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid check-in date format.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private boolean isValidCheckOutDate(String checkInDate, String checkOutDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy ' at ' HH:mm", Locale.getDefault());

        try {
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);

            if (checkIn != null && checkOut != null && checkOut.before(checkIn)) {
                Toast.makeText(this, "Check-out date cannot be before check-in date.",
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


    private void saveAccommodation(String location, String checkIn, String checkOut, int numRooms,
                                   String roomType) {
        String userId = Objects.requireNonNull(editTextEmail.getText()).toString();
        Map<String, Object> accommodation = new HashMap<>();
        accommodation.put("Location", location);
        accommodation.put("CheckIn", checkIn);
        accommodation.put("CheckOut", checkOut);
        accommodation.put("NumRooms", numRooms);
        accommodation.put("RoomType", roomType);
        accommodation.put("User", Collections.singletonList(userId));

        db.collection("Accommodations")
                .add(accommodation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Accommodation added", Toast.LENGTH_SHORT).show();
                    loadAccommodations();  // Reload reservations after adding new one
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Error saving accommodation", Toast.LENGTH_SHORT).show());
    }



    private boolean isExpired(String checkInDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy ' at ' HH:mm",
                Locale.getDefault());
        Date currentDate = new Date();
        try {
            Date checkIn = sdf.parse(checkInDate);
            return checkIn != null && checkIn.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}