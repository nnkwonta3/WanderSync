package com.example.myapplication.view;

import static com.example.myapplication.view.Login.editTextEmail;

import android.content.Intent;
import android.graphics.Typeface;
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

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button shareUser;
    private LinearLayout destinationsListLayout;
    private FirebaseFirestore db;
    private Button graphButton;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        shareUser = findViewById(R.id.share_user_btn);
        graphButton = findViewById(R.id.allotted_v_planned);
        userId = Objects.requireNonNull(editTextEmail.getText()).toString();  // Firebase UserID


        destinationsListLayout = findViewById(R.id.destinations_list_layout);
        db = FirebaseFirestore.getInstance();


        Button notesButton = findViewById(R.id.notes_button);


        shareUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShareUserClass.class));
            }
        });

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Notes.class));
            }
        });


        //Should go to the Logistics With Graph Class, implement the graph there.

        graphButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LogisticsWithGraph.class));
            }
        });


        // Set Home selected
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

        loadDestinations();


    }

    private void loadDestinations() {
        db.collection("Destinations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    destinationsListLayout.removeAllViews();  // Clear previous views

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Get the location and user list
                        String location = document.getString("location");
                        List<String> userList = (List<String>) document.get("UserId");

                        // Check if user list is not null and contains the current userId,
                        // and has other users
                        if (userList != null && userList.contains(userId)) {
                            // Check if there are other users besides the current user
                            boolean hasOtherUsers = false;
                            for (String user : userList) {
                                if (!user.equals(userId)) {
                                    hasOtherUsers = true;
                                    break;
                                }
                            }

                            // Only show the location if there are other users besides the
                            // current user
                            if (hasOtherUsers) {
                                // Display the location as a header
                                TextView locationView = new TextView(MainActivity.this);
                                locationView.setText(location);  // Set the location name
                                locationView.setPadding(16, 16, 16, 8);
                                // Padding for location
                                locationView.setTextSize(30);  // Set font size for location
                                locationView.setTypeface(null, Typeface.BOLD);
                                // Make location bold
                                destinationsListLayout.addView(locationView);

                                // Add users to the list, excluding the current userId
                                for (String user : userList) {
                                    if (!user.equals(userId)) {  // Skip current userId
                                        TextView userView = new TextView(MainActivity.this);
                                        userView.setText(user);
                                        userView.setPadding(16, 8, 16, 8);
                                        userView.setTextSize(25);

                                        // Add the contributor TextView to the layout
                                        destinationsListLayout.addView(userView);

                                        // Add a separator line after each contributor
                                        View separator = new View(MainActivity.this);
                                        separator.setLayoutParams(new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                2  // Thickness of the line
                                        ));
                                        separator.setBackgroundColor(getResources().
                                                getColor(android.R.color.black));
                                        destinationsListLayout.addView(separator);
                                    }
                                }

                                // Add a blank line or another separator after each location
                                View blankSeparator = new View(MainActivity.this);
                                blankSeparator.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        20  // Space between locations
                                ));
                                destinationsListLayout.addView(blankSeparator);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error loading destinations",
                            Toast.LENGTH_SHORT).show();
                });
    }


}