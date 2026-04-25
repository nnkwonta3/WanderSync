package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LogisticsWithGraph extends AppCompatActivity {
    private int allottedDays;
    private int plannedDays;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private PieChart pieChart; // Add PieChart variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logistics_with_graph);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Don't worry if you get an error here.
        bottomNavigationView.setSelectedItemId(R.id.logistics);

        plannedDays = (int) Destinations.getVacationDays();


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Button graphButton = findViewById(R.id.allotted_v_planned);
        pieChart = findViewById(R.id.pieChart); // Initialize PieChart
        //draw a pie chart when clicked
        graphButton.setOnClickListener((l) -> drawGraph());

        // Get current user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getEmail();

            // Retrieve allottedDays from Firestore
            firestore.collection("Users").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long duration = documentSnapshot.getLong("duration");
                            if (duration != null) {
                                allottedDays = duration.intValue(); //allotted days variable
                            } else {
                                Log.e("Logistics_With_Graph", "Duration field is missing");
                                Toast.makeText(this, "Duration data missing",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Logistics_With_Graph", "User document does not exist");
                            Toast.makeText(this, "User data missing",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Logistics_With_Graph", "Error fetching duration", e);
                        Toast.makeText(this, "Error fetching allotted days",
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
        }

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

    private void drawGraph() {
        // Prepare data for pie chart
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(allottedDays, "Allotted Days"));
        entries.add(new PieEntry(plannedDays, "Planned Days"));

        PieDataSet dataSet = new PieDataSet(entries, "Vacation Days");

        // Set colors using getResources().getColor()
        int[] colors = {
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green)
        };
        dataSet.setColors(colors);

        dataSet.setValueTextSize(16f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}