package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Notes extends AppCompatActivity {
    private EditText noteInput;  // Change this to match the XML component type
    private Button submitButton;
    private List<String> notesList;       // List of strings for notes
    private String destinationID = "destinationID1";  // Assuming you have destinationID

    // Firebase Database references
    private FirebaseFirestore db;
    private CollectionReference destinationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);  // Make sure this is the correct layout

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        destinationsRef = db.collection("Destinations");

        // Initialize views
        noteInput = findViewById(R.id.et_note_input);  // Ensure this matches the XML ID
        submitButton = findViewById(R.id.btn_submit_note);

        notesList = new ArrayList<>();

        // Set up Submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNote();
            }
        });

        //Dont edit this
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.destinations);

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

    private void submitNote() {
        String noteText = noteInput.getText().toString().trim();  // Get the text from the EditText

        if (TextUtils.isEmpty(noteText)) {
            Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show();
        } else {
            // Add the new note to the list
            notesList.add(noteText);
        }

        // Update the document in Firestore with the new list of notes
        destinationsRef.document(destinationID).update("notes", notesList)
                .addOnSuccessListener(aVoid -> {
                    // If the note is added successfully, clear the input field and show
                    // a success message
                    noteInput.setText("");  // Clear the input field
                    Toast.makeText(Notes.this, "Note added",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(Notes.this,
                        "Failed to add note: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
