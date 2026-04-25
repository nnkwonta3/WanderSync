package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.viewmodel.RegistrationViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class Registration extends AppCompatActivity {
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonRegistration;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private RegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        editTextEmail = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password);
        buttonRegistration = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        buttonLogin = findViewById(R.id.btn_login);

        buttonLogin.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        buttonRegistration.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Registration.this, "Enter email and password",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.register(email, password);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(Registration.this, error, Toast.LENGTH_SHORT).show());

        viewModel.getRegistrationSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(Registration.this, "Account created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Registration.this, MainActivity.class));
                finish();
            }
        });
    }
}