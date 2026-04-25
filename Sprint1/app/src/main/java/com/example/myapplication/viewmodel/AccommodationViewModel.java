package com.example.myapplication.viewmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AccommodationViewModel {
    private String errorMessage;

    // Method to validate and prepare accommodation data
    public HashMap<String, Object> prepareAccommodationData(String location, String checkIn,
                                                            String checkOut, int numRooms,
                                                            String roomType, List<String> userId) {
        // Validate input data
        if (location == null || location.trim().isEmpty()) {
            errorMessage = "Invalid location";
            return null;
        }
        if (checkIn == null || checkIn.trim().isEmpty()) {
            errorMessage = "Invalid check-in";
            return null;
        }
        if (checkOut == null || checkOut.trim().isEmpty()) {
            errorMessage = "Invalid check-out";
            return null;
        }
        if (numRooms <= 0) {
            errorMessage = "Invalid number of rooms";
            return null;
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            errorMessage = "Invalid room type";
            return null;
        }
        if (userId == null || userId.isEmpty()) {
            errorMessage = "Invalid user-id";
            return null;
        }
        for (String user : userId) {
            if (user == null || user.trim().isEmpty()) {
                errorMessage = "Invalid user-id";
                return null;
            }
        }

        // If all validations pass, prepare the accommodation data
        HashMap<String, Object> accommodation = new HashMap<>();
        accommodation.put("Location", location);
        accommodation.put("CheckIn", checkIn);
        accommodation.put("CheckOut", checkOut);
        accommodation.put("NumRooms", numRooms);
        accommodation.put("RoomType", roomType);
        accommodation.put("User", Collections.singletonList(userId));
        // Directly use userId list, no need for Collections.singletonList.
        // The list is NECESSARY, its for checking shared users, pls don't change!

        return accommodation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
