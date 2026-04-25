

package com.example.myapplication.view;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.viewmodel.LoginViewModel;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
public class Login extends AppCompatActivity {
    protected static TextInputEditText editTextEmail;
    protected TextInputEditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegistration;
    private ProgressBar progressBar;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        editTextEmail = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        buttonRegistration = findViewById(R.id.btn_register);

        buttonRegistration.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Registration.class));
            finish();
        });

        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.login(email, password);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show());

        viewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        });
    }

    public static String getEmail() {
        return editTextEmail.toString();
    }
}