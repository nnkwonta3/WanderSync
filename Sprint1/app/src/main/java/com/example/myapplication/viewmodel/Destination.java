package com.example.myapplication.viewmodel;

public class Destination {
    private String location;
    private String startDate;
    private String endDate;
    private long days;
    private String errorMessage;

    public Destination(String location, String startDate, String endDate, long days) {
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            errorMessage = ("Invalid location");
            return;
        }
        this.location = location;
    }

    public void setStartDate(String startDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            errorMessage = ("Invalid start date");
            return;
        }
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        if (endDate == null || endDate.trim().isEmpty()) {
            errorMessage = ("Invalid end date");
            return;
        }
        this.endDate = endDate;
    }

    public void setDays(long days) {
        if (days <= 0) {
            errorMessage = ("Invalid number of days");
            return;
        }
        this.days = days;
    }

    public String getLocation() {
        return location;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public long getDays() {
        return days;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
