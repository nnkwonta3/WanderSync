package com.example.myapplication.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.viewmodel.Destination;
import com.example.myapplication.viewmodel.DestinationAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DestinationsCalculateVacationTime extends AppCompatActivity {
    private TextInputEditText editEstimatedStart;
    private TextInputEditText editEstimatedEnd;
    private Button calculateButton;
    private TextInputEditText duration;
    private FirebaseFirestore db;
    private String userId = Objects.requireNonNull(
            FirebaseAuth.getInstance().getCurrentUser()).getEmail();  // Firebase UserID
    private TextInputEditText userName;
    private int trips = 1;

    private RecyclerView recyclerView;
    private DestinationAdapter adapter;
    private List<Destination> destinationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_destinations_calculate_vacation_time);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        editEstimatedStart = findViewById(R.id.et_start_date);
        editEstimatedEnd = findViewById(R.id.et_end_date);
        calculateButton = findViewById(R.id.btn_calculate);
        duration = findViewById(R.id.et_duration);

        editEstimatedStart.setOnClickListener(view -> openDatePicker(editEstimatedStart));
        editEstimatedEnd.setOnClickListener(view -> openDatePicker(editEstimatedEnd));

        // Set initial button disabled state
        calculateButton.setEnabled(false);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_destinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        destinationList = new ArrayList<>();
        adapter = new DestinationAdapter(destinationList);
        recyclerView.setAdapter(adapter);

        loadDestinations();

        // Load and display travel destinations


        // Set listeners to enable button only when two fields are filled
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                checkButtonEnable();
            }
        };

        editEstimatedStart.addTextChangedListener(textWatcher);
        editEstimatedEnd.addTextChangedListener(textWatcher);
        duration.addTextChangedListener(textWatcher);

        calculateButton.setOnClickListener(v -> calculateMissingValue());

        Button logButton = (Button) findViewById(R.id.btn_log_travel);

        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(
                        DestinationsCalculateVacationTime.this,
                        DestinationsLogTravel.class));
            }
        });

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

    private void openDatePicker(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                DestinationsCalculateVacationTime.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/"
                            + selectedYear;
                    dateTextView.setText(selectedDate);

                    // Validate the start date against the current date
                    if (dateTextView == editEstimatedStart) {
                        validateStartDate(selectedYear, selectedMonth, selectedDay);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void validateStartDate(int year, int month, int day) {
        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, day);
        Date selectedDate = selectedCalendar.getTime();
        Date currentDate = new Date(); // Get the current date

        // Check if the selected date is before the current date
        if (selectedDate.before(currentDate)) {
            Toast.makeText(this, "Start date cannot be before the current date",
                    Toast.LENGTH_SHORT).show();
            editEstimatedStart.setText(""); // Clear the invalid input
        }
    }

    private void checkButtonEnable() {
        if (!editEstimatedStart.getText().toString().isEmpty()
                && !editEstimatedEnd.getText().toString().isEmpty()
                || !editEstimatedStart.getText().toString().isEmpty()
                && !duration.getText().toString().isEmpty()
                || !editEstimatedEnd.getText().toString().isEmpty()
                && !duration.getText().toString().isEmpty()) {
            calculateButton.setEnabled(true);
        } else {
            calculateButton.setEnabled(false);
        }
    }

    private void calculateMissingValue() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            // Case 1: Both start and end dates are provided, calculate duration
            if (!editEstimatedStart.getText().toString().isEmpty()
                    && !editEstimatedEnd.getText().toString().isEmpty()) {
                Date startDate = dateFormat.parse(editEstimatedStart.getText().toString());
                Date endDate = dateFormat.parse(editEstimatedEnd.getText().toString());

                if (startDate != null && endDate != null) {
                    // Check if end date is earlier than start date
                    if (endDate.before(startDate)) {
                        Toast.makeText(this,
                                "End date cannot be earlier than start date",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Calculate the duration in days
                    long diff = endDate.getTime() - startDate.getTime();
                    long days = diff / (1000 * 60 * 60 * 24);
                    duration.setText(String.valueOf(days));
                }
            } else if (!editEstimatedStart.getText().toString().isEmpty()
                    && !duration.getText().toString().isEmpty()) {
                Date startDate = dateFormat.parse(editEstimatedStart.getText().toString());
                long days = Long.parseLong(duration.getText().toString());

                // Check if start date is before current date
                if (startDate != null) {
                    Date currentDate = new Date(); // Get current date
                    if (startDate.before(currentDate)) {
                        Toast.makeText(this,
                                "Start date cannot be before the current date",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Calculate end date
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DAY_OF_MONTH, (int) days);
                    editEstimatedEnd.setText(dateFormat.format(calendar.getTime()));
                }
            } else if (!editEstimatedEnd.getText().toString().isEmpty()
                    && !duration.getText().toString().isEmpty()) {
                Date endDate = dateFormat.parse(editEstimatedEnd.getText().toString());
                long days = Long.parseLong(duration.getText().toString());

                if (endDate != null) {
                    // Calculate start date
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(endDate);
                    calendar.add(Calendar.DAY_OF_MONTH, (int) -days);
                    Date calculatedStartDate = calendar.getTime();

                    // Check for negative duration
                    if (calculatedStartDate.before(endDate)) {
                        Date currentDate = new Date(); // Get current date
                        // Check if calculated start date is before the current date
                        if (calculatedStartDate.before(currentDate)) {
                            Toast.makeText(this,
                                    "Calculated start date cannot be before the current date",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            editEstimatedStart.setText(dateFormat.format(calculatedStartDate));
                        }
                    } else {
                        Toast.makeText(this, "Duration cannot be negative",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // Final Check: Ensure all values are present before saving
            String startDateStr = editEstimatedStart.getText().toString();
            String endDateStr = editEstimatedEnd.getText().toString();
            String durationStr = duration.getText().toString();

            // Check if all three fields are filled
            if (!startDateStr.isEmpty() && !endDateStr.isEmpty() && !durationStr.isEmpty()) {
                // Create vacation data map
                Map<String, Object> vacationData = new HashMap<>();
                vacationData.put("startDate", startDateStr);
                vacationData.put("endDate", endDateStr);
                vacationData.put("duration", Integer.parseInt(durationStr));

                // Save in user's vacation sub-collection
                db.collection("Users").document(userId)
                        .set(vacationData) // Note: Use `set` for merging; consider using
                        // `set(vacationData, SetOptions.merge())` if needed
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Vacation details saved successfully",
                                    Toast.LENGTH_SHORT).show();
                            trips++;
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error saving vacation details",
                                        Toast.LENGTH_SHORT).show()
                );
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid duration value", Toast.LENGTH_SHORT).show();
        }
    }


    private String tripConverter(int nums) {
        return "Trip " + trips;
    }
    private void loadDestinations() {
        db.collection("Destinations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                destinationList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Check if the UserId field is a list and contains the userId
                    Object userIdField = document.get("UserId");

                    if (userIdField instanceof List) {
                        List<String> userIds = (List<String>) userIdField;
                        if (userIds.contains(userId)) {
                            String location = document.getString("location");
                            String startDate = document.getString("start_date");
                            String endDate = document.getString("end_date");
                            long days = document.getLong("Days") != null ? document.getLong(
                                    "Days") : 0;
                            destinationList.add(new Destination(location, startDate, endDate,
                                    days));
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh RecyclerView with new data
            } else {
                Toast.makeText(this, "Error loading data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
