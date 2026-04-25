# WanderSync

WanderSync is a collaborative travel management application that simplifies planning and organizing trips for both solo travelers and groups. The platform enables users to create, manage, and share travel itineraries in real time, integrating destinations, accommodations, dining plans, transportation, and logistics into a unified system.

---

## Project Overview

WanderSync is designed to streamline the travel planning process by allowing multiple users to collaborate on a shared itinerary. It provides structured tools for organizing trip details while maintaining flexibility for personalization and group coordination.

The application supports real-time updates, shared notes, and itinerary tracking, making it easier for groups to stay aligned throughout the planning process.

---

## Core Features

### Collaborative Itinerary Management
- Create and manage detailed travel plans
- Share itineraries with other users
- Real-time updates across all collaborators

### User Authentication
- Secure account creation and login
- Firebase Authentication for credential management
- User-specific data storage and access control

### Destination Tracking
- Log travel destinations with planned dates
- View and manage trip timelines
- Persistent storage of destination data

### Vacation Time Calculator
- Input trip start date, end date, or duration
- Automatically calculates the missing value
- Tracks planned versus available travel days

### Data Visualization
- Visual comparison of planned vs available travel time
- Integrated charting for better decision-making

### Dining and Accommodation Management
- Add and manage dining reservations
- Store accommodation details including room type and duration
- Organized views grouped by date and time

### Transportation Planning
- Track transportation methods and schedules
- Integrate travel logistics into the itinerary

### Travel Community
- Share completed itineraries with other users
- Explore community travel posts
- View trip details including destinations, accommodations, dining, and notes

### Notes and Collaboration
- Add personal or shared notes to trips
- View contributions from all collaborators
- Invite users to participate in trip planning

---

## Tech Stack

### Frontend
- Android (Java)

### Backend and Services
- Firebase Authentication
- Firebase Realtime Database / Firestore

### Libraries and Tools
- MPAndroidChart (data visualization)

---

## System Architecture

- **Client:** Android application interface for user interaction
- **Backend Services:** Firebase for authentication and data storage
- **Database Structure:**
  - User database (credentials and personal data)
  - Destination database
  - Dining and accommodation databases
  - Travel community database

---

## Key Functional Components

### Authentication System
- Firebase-based user authentication
- Secure login and registration flows
- User session persistence

### Database Integration
- Structured storage of all travel-related data
- Linked data models between users, destinations, and itineraries

### Real-Time Collaboration
- Shared itinerary editing
- Synchronized updates across users

### Design Patterns
- Implementation of Observer or Strategy pattern for scalable feature handling

---

## Future Improvements

- Enhance real-time collaboration performance
- Improve UI/UX for large group planning
- Add advanced filtering and search for itineraries
- Introduce notifications for itinerary updates
- Expand analytics for travel insights
- Support cross-platform (web or iOS)

---

## How to Run

1. Clone the repository:

2. Open the project in Android Studio

3. Configure Firebase:
- Add `google-services.json`
- Enable Authentication and Database services

4. Build and run the application on an emulator or device

---

## Notes

- This project was developed as part of a multi-sprint software engineering course
- Features were implemented incrementally across four development phases
- Requirements evolved throughout development

---

## Contributions

Contributions are welcome. Fork the repository and submit a pull request for any improvements or new features.

---

## Contact

For questions or collaboration inquiries, reach out via GitHub.
