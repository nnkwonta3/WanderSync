
//Builder Design Pattern
package com.example.myapplication.view;
//Builder Pattern
public class TravelPost {
    private String startDate;
    private String endDate;
    private String destination;
    private String accommodations;
    private String dining;
    private String notes;
    private String tripCreator;
    private String errorMessage;

    private TravelPost(Builder builder) {
        if (builder.startDate == null || builder.startDate.trim().isEmpty()) {
            errorMessage = "Start date invalid!";
            return;
        }
        if (builder.endDate == null || builder.endDate.trim().isEmpty()) {
            errorMessage = "End date invalid!";
            return;
        }
        if (builder.destination == null || builder.destination.trim().isEmpty()) {
            errorMessage = "Destination invalid!";
            return;
        }
        if (builder.accommodations == null || builder.accommodations.trim().isEmpty()) {
            errorMessage = "Accommodations invalid!";
            return;
        }
        if (builder.tripCreator == null || builder.tripCreator.trim().isEmpty()) {
            errorMessage = "Trip creator invalid!";
            return;
        }
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.destination = builder.destination;
        this.accommodations = builder.accommodations;
        this.dining = builder.dining;
        this.notes = builder.notes;
        this.tripCreator = builder.tripCreator;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDestination() {
        return destination;
    }

    public String getAccommodations() {
        return accommodations;
    }

    public String getDining() {
        return dining;
    }

    public String getNotes() {
        return notes;
    }

    public String getTripCreator() {
        return tripCreator;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setStartDate(String startDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            errorMessage = "Start date invalid!";
        }
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        if (endDate == null || endDate.trim().isEmpty()) {
            errorMessage = "End date invalid!";
        }
        this.endDate = endDate;
    }

    //Builder Pattern Design
    public static class Builder {
        private String startDate;
        private String endDate;
        private String destination;
        private String accommodations;
        private String dining;
        private String notes;
        private String tripCreator;



        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setDestination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder setAccommodations(String accommodations) {
            this.accommodations = accommodations;
            return this;
        }

        public Builder setDining(String dining) {
            this.dining = dining;
            return this;
        }

        public Builder setNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder setTripCreator(String tripCreator) {
            this.tripCreator = tripCreator;
            return this;
        }


        public TravelPost build() {
            return new TravelPost(this);
        }
    }
}
