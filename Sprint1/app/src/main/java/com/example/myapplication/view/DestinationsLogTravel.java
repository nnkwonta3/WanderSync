package com.example.myapplication.view;


import static com.example.myapplication.view.Login.editTextEmail;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore; // custom adapter
import com.example.myapplication.viewmodel.Destination;
import com.example.myapplication.viewmodel.DestinationAdapter;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DestinationsLogTravel extends AppCompatActivity {
    private TextInputEditText editTravelLocation;
    private TextInputEditText editEstimatedStart;
    private TextInputEditText editEstimatedEnd;
    private String userId;
    private int trips = 1;  // Updated variable name to follow naming conventions

    private RecyclerView recyclerDestinations;
    private DestinationAdapter destinationAdapter;
    private List<Destination> destinationList;
    private FirebaseFirestore db;
    private CollectionReference destinationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_destinations_log_travel);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        destinationsRef = db.collection("Destinations");


        recyclerDestinations = findViewById(R.id.recycler_destinations);
        destinationList = new ArrayList<>();
        destinationAdapter = new DestinationAdapter(destinationList);
        recyclerDestinations.setLayoutManager(new LinearLayoutManager(this));
        recyclerDestinations.setAdapter(destinationAdapter);

        // Load destinations from Firestore
        loadDestinations();

        // Initialize UI components
        editTravelLocation = findViewById(R.id.et_travel_location);
        editEstimatedStart = findViewById(R.id.et_estimated_start);
        editEstimatedEnd = findViewById(R.id.et_estimated_end);
        userId = Objects.requireNonNull(editTextEmail.getText()).toString();  // Firebase UserID

        // Set up DatePickers
        editEstimatedStart.setOnClickListener(view -> showDatePickerDialog(editEstimatedStart));
        editEstimatedEnd.setOnClickListener(view -> showDatePickerDialog(editEstimatedEnd));

        // Set up submit button
        Button submitTravelButton = findViewById(R.id.btn_submit);
        submitTravelButton.setOnClickListener(view -> {
            String travelLocation = editTravelLocation.getText().toString();
            String startDate = editEstimatedStart.getText().toString().trim();
            String endDate = editEstimatedEnd.getText().toString().trim();

            if (validateDates() && validateDestination()) {
                try {
                    saveTravelLog(travelLocation, startDate, endDate);
                } catch (ParseException e) {
                    Toast.makeText(this, "Error saving trip: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        });

        // Navigation buttons
        setupNavigationButtons();

        // Bottom navigation setup
        setupBottomNavigation();
    }

    private void showDatePickerDialog(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year1, month1, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dateField.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    private boolean validateDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String startDateStr = editEstimatedStart.getText().toString();
            String endDateStr = editEstimatedEnd.getText().toString();

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all date fields.", Toast.LENGTH_SHORT).show();
                return false;
            }

            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            Date currentDate = Calendar.getInstance().getTime();

            if (startDate.before(currentDate)) {
                Toast.makeText(this, "Start date cannot be earlier than today.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (endDate.before(startDate)) {
                Toast.makeText(this, "End date cannot be earlier than start date.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private boolean validateDestination() {
        String destinationLoc = (editTravelLocation.getText()).toString().trim();
        if (destinationLoc.isEmpty()) {
            Toast.makeText(this, "Please provide a valid location.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveTravelLog(String travelLocation, String startDate, String endDate)
            throws ParseException {

        long days = daysBetween(startDate, endDate);

        // Retrieve all documents to check for the presence of userId and travelLocation
        destinationsRef.get()
                .addOnSuccessListener(querySnapshot -> {
                    final boolean[] foundMatchingLog = {false};
                    // Flag to check if we found a matching log
                    Map<String, Object> travelLog = new HashMap<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        List<String> userIds = (List<String>) document.get("UserId");
                        String existingLocation = (String) document.get("location");

                        // Check if both userId and location match
                        if (userIds != null && userIds.contains(userId)
                                && existingLocation.equals(travelLocation)) {
                            // If both userId and location match, update the existing document
                            travelLog = document.getData();
                            travelLog.put("start_date", startDate);
                            travelLog.put("end_date", endDate);
                            travelLog.put("Days", days);

                            // Update the existing document
                            document.getReference().set(travelLog)
                                    .addOnSuccessListener(aVoid -> {
                                        foundMatchingLog[0] = true;
                                        // Set flag indicating an update occurred
                                        Toast.makeText(this,
                                                "Travel log updated successfully!",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this,
                                                "Error updating travel log: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                            // Exit loop after updating
                            return;
                        }
                    }

                    // If no matching userId and location was found, create a new document
                    if (!foundMatchingLog[0]) {
                        travelLog = new HashMap<>();
                        travelLog.put("location", travelLocation);
                        travelLog.put("start_date", startDate);
                        travelLog.put("end_date", endDate);
                        travelLog.put("UserId", Collections.singletonList(userId));
                        travelLog.put("Days", days);

                        // Use trips as a counter for naming the new document
                        int count = trips + 1; // Update the trip count for the document name
                        String documentName = userId + " " + count;

                        // Create a new travel log document
                        destinationsRef.document(documentName).set(travelLog)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this,
                                            "Travel log saved successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    clearFields();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error saving travel log: "
                                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error retrieving documents: "
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void clearFields() {
        editTravelLocation.setText("");
        editEstimatedStart.setText("");
        editEstimatedEnd.setText("");
    }


    public static long daysBetween(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse(date1);
        Date endDate = sdf.parse(date2);
        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    private void setupNavigationButtons() {
        findViewById(R.id.calculate_vacation_time_btn).setOnClickListener(view ->
                startActivity(new Intent(this, DestinationsCalculateVacationTime.class)));

        findViewById(R.id.btn_cancel).setOnClickListener(view ->
                startActivity(new Intent(this, Destinations.class)));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.destinations);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.logistics) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.destinations) {
                return true;
            } else if (itemId == R.id.accommodations) {
                startActivity(new Intent(getApplicationContext(), Accommodations.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.travelcommunity) {
                startActivity(new Intent(getApplicationContext(), TravelCommunity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.dining) {
                startActivity(new Intent(getApplicationContext(), DiningEstablishments.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadDestinations() {
        destinationsRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error loading data.", Toast.LENGTH_SHORT).show();
                return;
            }
            destinationList.clear();  // Clear the existing list of destinations
            trips = 0;  // Reset the trips count on load

            if (querySnapshot != null) {
                List<Destination> allTrips = new ArrayList<>();

                // Collect all trips where the UserId matches
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    // Check if the UserId field is a list and contains the userId
                    Object userIdField = document.get("UserId");

                    if (userIdField instanceof List) {
                        List<String> userIds = (List<String>) userIdField;
                        if (userIds.contains(userId)) {
                            String location = document.getString("location");
                            String startDate = document.getString("start_date");
                            String endDate = document.getString("end_date");
                            long days = document.getLong("Days") != null
                                    ? document.getLong("Days") : 0;
                            allTrips.add(new Destination(location, startDate, endDate, days));
                        }
                    }
                }

                // Sort the trips by their entry time (assuming newer trips are last)
                Collections.sort(allTrips, (d1, d2) -> {
                    // Assuming "start_date" is in a comparable format such as "yyyy-MM-dd"
                    return d2.getStartDate().compareTo(d1.getStartDate());
                });

                // Limit the list to only the last 5 trips
                List<Destination> lastFiveTrips = allTrips.size() > 5
                        ? allTrips.subList(0, 5) : allTrips;

                // Add the last five trips to the destination list
                destinationList.addAll(lastFiveTrips);
                trips = destinationList.size();  // Update the trips count

                destinationAdapter.notifyDataSetChanged();  // Notify adapter to update the UI
            }
        });
    }


}
