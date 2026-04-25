package com.example.myapplication.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class DestinationAdapter extends
        RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder> {
    private List<Destination> destinationList;

    public DestinationAdapter(List<Destination> destinationList) {
        this.destinationList = destinationList;
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_destination,
                parent, false);
        return new DestinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        Destination destination = destinationList.get(position);
        holder.locationTextView.setText(destination.getLocation());
        holder.durationTextView.setText("Planned: " + destination.getDays() + " days");
        holder.datesTextView.setText(destination.getStartDate() + " to "
                + destination.getEndDate());
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    static class DestinationViewHolder extends RecyclerView.ViewHolder {
        private TextView locationTextView;
        private TextView durationTextView;
        private TextView datesTextView;

        public DestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.location_text);
            durationTextView = itemView.findViewById(R.id.duration_text);
            datesTextView = itemView.findViewById(R.id.dates_text);
        }
    }
}
