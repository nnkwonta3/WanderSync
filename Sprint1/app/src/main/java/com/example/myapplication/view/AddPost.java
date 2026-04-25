package com.example.myapplication.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPost extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextInputEditText startDate;
    private TextInputEditText endDate;
    private EditText accoms;
    private EditText dinings;
    private EditText notes;
    private Button addPostButton;
    private EditText dest;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_post);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        startDate = findViewById(R.id.enter_start);
        endDate = findViewById(R.id.enter_end);
        accoms = findViewById(R.id.enter_accommodations);
        dinings = findViewById(R.id.enter_dining);
        notes = findViewById(R.id.enter_notes);
        dest = findViewById(R.id.enter_destination);
        addPostButton = findViewById(R.id.add_travel_post_button);

        startDate.setOnClickListener(v -> showDateTimePicker(startDate));
        endDate.setOnClickListener(v -> showDateTimePicker(endDate));

        addPostButton.setOnClickListener(v -> savePost());
    }

    private void showDateTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (
                view, selectedYear, selectedMonth, selectedDay) -> {
                calendar.set(Calendar.YEAR, selectedYear);
                calendar.set(Calendar.MONTH, selectedMonth);
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (
                    timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String dateTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm",
                        calendar).toString();
                editText.setText(dateTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void savePost() {
        String startDateValue = startDate.getText().toString();
        String endDateValue = endDate.getText().toString();
        String accommodationsValue = accoms.getText().toString();
        String diningValue = dinings.getText().toString();
        String notesValue = notes.getText().toString();
        String destination = dest.getText().toString();
        FirebaseUser currentUser = auth.getCurrentUser();
        String tripCreator = currentUser.getEmail();

        if (TextUtils.isEmpty(startDateValue) || TextUtils.isEmpty(endDateValue)
                || TextUtils.isEmpty(destination)) {
            Toast.makeText(this,
                    "Please enter start and end date/time, and Destination",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> travelPost = new HashMap<>();
        travelPost.put("Start", startDateValue);
        travelPost.put("End", endDateValue);
        travelPost.put("Accommodation", accommodationsValue);
        travelPost.put("Dining", diningValue);
        travelPost.put("Notes", notesValue);
        travelPost.put("Destination", destination);
        travelPost.put("trip_creator", tripCreator);

        db.collection("Travel Posts")
            .add(travelPost)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Travel post added successfully!", Toast.LENGTH_SHORT).show();
                // Optionally clear fields after success
                startDate.setText("");
                endDate.setText("");
                accoms.setText("");
                dinings.setText("");
                dest.setText("");
                notes.setText("");

                Intent intent = new Intent(AddPost.this, TravelCommunity.class);
                startActivity(intent);
                finish();
            })
            .addOnFailureListener(e -> Toast.makeText(this,
                    "Failed to add travel post: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show());


    }
}