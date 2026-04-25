package com.example.myapplication.view;

import static com.example.myapplication.view.Login.editTextEmail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.viewmodel.Destination;
import com.example.myapplication.viewmodel.DestinationAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Destinations extends
        // Variables
        AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;

    private static long totalDays = 0;
    private TextView vacationDays;
    private FirebaseFirestore db;
    private LinearLayout destinationListLayout;
    private String userId;
    private RecyclerView recyclerView;
    private DestinationAdapter adapter;
    private List<Destination> destinationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_destinations);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycler_destinations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        destinationList = new ArrayList<>();
        adapter = new DestinationAdapter(destinationList);
        recyclerView.setAdapter(adapter);
        userId = Objects.requireNonNull(editTextEmail.getText()).toString();  // Firebase UserID


        vacationDays = findViewById(R.id.result_number);
        vacationDays.setText("0");

        loadDestinations();


        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.destinations);
        Button logButton = (Button) findViewById(R.id.btn_log_travel);
        Button noteButton = (Button) findViewById(R.id.btn_add_note);

        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Destinations.this,
                        DestinationsLogTravel.class));
            }
        });

        noteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Destinations.this, Notes.class));
            }
        });


        // When this is pressed removes all the trips from the User.
        Button resetButton = findViewById(R.id.reset_button);
        // Assuming the button ID is reset_button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the current user's ID

                // Query for documents in "Destinations" collection where "UserId"
                // array contains currentUserId
                db.collection("Destinations")
                        .whereArrayContains("UserId", userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Loop through each matching document and delete it
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("Destinations").
                                            document(document.getId()).delete()
                                            .addOnSuccessListener(aVoid -> {
                                                // Optionally notify the user or log
                                                // the success of each deletion
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(Destinations.this,
                                                        "Error deleting document: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            });
                                }
                                // After deleting all relevant documents, clear the list and update
                                destinationList.clear();
                                adapter.notifyDataSetChanged();
                                vacationDays.setText("0"); // Reset the vacation days display
                                Toast.makeText(Destinations.this,
                                        "Your destinations have been reset.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Destinations.this,
                                        "Error retrieving documents.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button calculateTravelButton = (Button) findViewById(R.id.calculate_vacation_time_btn);

        calculateTravelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Destinations.this,
                        DestinationsCalculateVacationTime.class));
            }
        });

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
                        } else if (itemId == R.id.destinations) {
                            return true;
                        } else if (itemId == R.id.accommodations) {
                            startActivity(new Intent(getApplicationContext(),
                                    Accommodations.class));
                            overridePendingTransition(0, 0);
                            return true;
                        } else if (itemId == R.id.travelcommunity) {
                            startActivity(new Intent(getApplicationContext(),
                                    TravelCommunity.class));
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

    private void loadDestinations() {
        db.collection("Destinations").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                destinationList.clear();

                totalDays = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {
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
                            destinationList.add(new Destination(location,
                                    startDate, endDate, days));
                            totalDays += days; // Add days to total
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh RecyclerView with new data
                vacationDays.setText(String.valueOf(totalDays));

            } else {
                Toast.makeText(this, "Error loading data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static long getVacationDays() {
        return totalDays;
    }
}