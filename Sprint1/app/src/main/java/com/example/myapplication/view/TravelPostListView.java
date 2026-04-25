//Observer Pattern
package com.example.myapplication.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;

public class TravelPostListView implements TravelPostObserver {
    private final Context context;
    private final LinearLayout travelPostsLayout;

    public TravelPostListView(Context context) {
        this.context = context;
        this.travelPostsLayout = ((Activity) context).findViewById(R.id.travel_post_list);
    }

    @Override
    public void updateTravelPosts(List<TravelPost> travelPosts) {
        // Clear the current view
        travelPostsLayout.removeAllViews();

        // Iterate over the travel posts and display them
        for (TravelPost post : travelPosts) {
            TextView travelPostView = new TextView(context);
            StringBuilder travelPostText = new StringBuilder();
            travelPostText.append("Trip Creator: ").append(post.getTripCreator()).append("\n")
                    .append("Destinations: ").append(post.getDestination()).append("\n")
                    .append("Start: ").append(post.getStartDate()).append("\n")
                    .append("End: ").append(post.getEndDate()).append("\n")
                    .append("Accommodation: ").append(post.getAccommodations()).append("\n")
                    .append("Dining: ").append(post.getDining()).append("\n")
                    .append("Notes: ").append(post.getNotes());



            travelPostView.setText(travelPostText.toString());
            travelPostView.setTypeface(null, android.graphics.Typeface.BOLD);
            travelPostView.setTextSize(19);
            travelPostView.setPadding(16, 16, 16, 16);

            // Add the TextView to the layout
            travelPostsLayout.addView(travelPostView);

            // Add a separator line
            View separator = new View(context);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    5
            ));
            separator.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            travelPostsLayout.addView(separator);
        }
    }
}
