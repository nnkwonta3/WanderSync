package com.example.myapplication.model;

public class User {
    private String email;
    private String password;
    private String errorMessage;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage = ("Invalid email");
            return;
        }
        this.email = email;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            errorMessage = ("Invalid password");
            return;
        }
        this.password = password;
    }
}
