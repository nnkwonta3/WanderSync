package com.example.myapplication.view;


import java.util.List;

//Observer Pattern
public interface TravelPostObserver {
    void updateTravelPosts(List<TravelPost> travelPosts);
}
