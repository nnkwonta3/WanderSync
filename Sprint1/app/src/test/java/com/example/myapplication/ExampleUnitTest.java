package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.myapplication.model.User;
import com.example.myapplication.view.TravelLogDatabase;
import com.example.myapplication.view.TravelPost;
import com.example.myapplication.viewmodel.AccommodationViewModel;
import com.example.myapplication.viewmodel.Destination;
import com.example.myapplication.viewmodel.DestinationAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private DestinationAdapter adapter;
    private ArrayList<Destination> destinations;

    @Before
    public void setUp() {
        destinations = new ArrayList<>();

        adapter = new DestinationAdapter(destinations);
    }

    @Test
    public void testEmptyList() {
        // makes sure new list is empty
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testGetItems() {
        // adding placeholder locations to destination list
        destinations.add(new Destination("Location 1", "01/01/2024", "05/01/2024", 5));

        destinations.add(new Destination("Location2", "06/01/2024", "10/01/2024", 5));

        destinations.add(new Destination("Location 3", "11/01/2024", "15/01/2024", 5));

        // checking that item count is equal to expected count
        assertEquals(3, adapter.getItemCount());
    }
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEmailNull() {
        User user = new User("test@gmail.com", "test123");

        user.setEmail(null);
        assertEquals(user.getErrorMessage(), "Invalid email");
        assertEquals(user.getEmail(), "test@gmail.com");
    }

    @Test
    public void testEmailWhitespace() {
        User user = new User("test@gmail.com", "test123");

        user.setEmail("      ");
        assertEquals(user.getErrorMessage(), "Invalid email");
        assertEquals(user.getEmail(), "test@gmail.com");
    }

    @Test
    public void testPasswordNull() {
        User user = new User("test@gmail.com", "test123");

        user.setPassword(null);
        assertEquals(user.getErrorMessage(), "Invalid password");
        assertEquals(user.getPassword(), "test123");
    }

    @Test
    public void testPasswordWhitespace() {
        User user = new User("test@gmail.com", "test123");

        user.setPassword("      ");
        assertEquals(user.getErrorMessage(), "Invalid password");
        assertEquals(user.getPassword(), "test123");
    }

    @Test
    public void testLocationNull() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setLocation(null);
        assertEquals(destination.getErrorMessage(), "Invalid location");
        assertEquals(destination.getLocation(), "Bahamas");
    }

    @Test
    public void testLocationWhitespace() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setLocation("      ");
        assertEquals(destination.getErrorMessage(), "Invalid location");
        assertEquals(destination.getLocation(), "Bahamas");
    }

    @Test
    public void testStartDateNull() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setStartDate(null);
        assertEquals(destination.getErrorMessage(), "Invalid start date");
        assertEquals(destination.getStartDate(), "05/11/2024");
    }

    @Test
    public void testStartDateWhitespace() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setStartDate("      ");
        assertEquals(destination.getErrorMessage(), "Invalid start date");
        assertEquals(destination.getStartDate(), "05/11/2024");
    }

    @Test
    public void testEndDateNull() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setEndDate(null);
        assertEquals(destination.getErrorMessage(), "Invalid end date");
        assertEquals(destination.getEndDate(), "10/11/2024");
    }

    @Test
    public void testEndDateWhitespace() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setEndDate("      ");
        assertEquals(destination.getErrorMessage(), "Invalid end date");
        assertEquals(destination.getEndDate(), "10/11/2024");
    }

    @Test
    public void numDaysInvalid() {
        Destination destination = new Destination("Bahamas", "05/11/2024", "10/11/2024", 5);

        destination.setDays(0);
        assertEquals(destination.getErrorMessage(), "Invalid number of days");
        assertEquals(destination.getDays(), 5);
    }

    @Test
    public void testPrepareAccommodationData() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024 at 5:52";
        String checkOut = "29/11/2024 at 18:42";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        List<List<String>> check = new ArrayList<>();
        check.add(userId);

        assertEquals(location, accommodationData.get("Location"));
        assertEquals(checkIn, accommodationData.get("CheckIn"));
        assertEquals(checkOut, accommodationData.get("CheckOut"));
        assertEquals(numRooms, accommodationData.get("NumRooms"));
        assertEquals(roomType, accommodationData.get("RoomType"));
        assertEquals(check, accommodationData.get("User"));
    }

    @Test
    public void testAccommodationLocationNull() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = null;
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid location");
    }

    @Test
    public void testAccommodationLocationEmpty() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "";
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid location");
    }

    @Test
    public void testAccommodationCheckInNull() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = null;
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid check-in");
    }

    @Test
    public void testAccommodationCheckInEmpty() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid check-in");
    }

    @Test
    public void testAccommodationCheckOutNull() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = null;
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid check-out");
    }

    @Test
    public void testAccommodationCheckOutEmpty() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = "";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid check-out");
    }

    @Test
    public void testAccommodationNumRoomsInvalid() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 0;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid number of rooms");
    }

    @Test
    public void testAccommodationRoomTypeNull() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = null;
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid room type");
    }

    @Test
    public void testAccommodationRoomTypeEmpty() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "";
        List<String> userId = new ArrayList<String>();
        userId.add("user123");

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid room type");
    }

    @Test
    public void testAccommodationUserIDNull() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = null;

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid user-id");
    }

    @Test
    public void testAccommodationUserIDEmpty() {
        AccommodationViewModel accommodationService = new AccommodationViewModel();

        String location = "Grand Hyatt New York";
        String checkIn = "12/01/2024";
        String checkOut = "12/05/2024";
        int numRooms = 2;
        String roomType = "King Suite";
        List<String> userId = new ArrayList<String>();

        HashMap<String, Object> accommodationData = accommodationService.prepareAccommodationData(location, checkIn, checkOut, numRooms, roomType, userId);

        assertNull(accommodationData);
        assertEquals(accommodationService.getErrorMessage(), "Invalid user-id");
    }

    @Test
    public void testTravelLogDatabase() {
        TravelLogDatabase firstInstance = TravelLogDatabase.getInstance();

        TravelLogDatabase secondInstance = TravelLogDatabase.getInstance();

        assertSame(firstInstance, secondInstance);
    }

    @Test
    public void testBuilderCreatesTravelPostSuccessfully() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertEquals("2024-12-06", post.getStartDate());
        assertEquals("2024-12-10", post.getEndDate());
        assertEquals("Italy", post.getDestination());
        assertEquals("Grand Hyatt", post.getAccommodations());
        assertEquals("Fettucine alfredo", post.getDining());
        assertEquals("Pack warm clothes", post.getNotes());
        assertEquals("John Doe", post.getTripCreator());
        assertNull(post.getErrorMessage());
    }

    @Test
    public void testBuilderWithMissingStartDate() {
        TravelPost post = new TravelPost.Builder()
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getStartDate());
        assertEquals("Start date invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithEmptyStartDate() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate(" ")
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getStartDate());
        assertEquals("Start date invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithMissingEndDate() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getEndDate());
        assertEquals("End date invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithEmptyEndDate() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate(" ")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull("End date null", post.getEndDate());
        assertEquals("End date invalid!", post.getErrorMessage());
    }
    @Test
    public void testBuilderWithMissingDestination() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getDestination());
        assertEquals("Destination invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithEmptyDestination() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setDestination(" ")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getDestination());
        assertEquals("Destination invalid!", post.getErrorMessage());
    }
//
    @Test
    public void testBuilderWithMissingAccommodations() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getAccommodations());
        assertEquals("Accommodations invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithEmptyAccommodations() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setAccommodations(" ")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator("John Doe")
                .build();

        assertNull(post.getAccommodations());
        assertEquals("Accommodations invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithMissingTripCreator() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .build();

        assertNull(post.getTripCreator());
        assertEquals("Trip creator invalid!", post.getErrorMessage());
    }

    @Test
    public void testBuilderWithEmptyTripCreator() {
        TravelPost post = new TravelPost.Builder()
                .setStartDate("2024-12-06")
                .setEndDate("2024-12-10")
                .setDestination("Italy")
                .setAccommodations("Grand Hyatt")
                .setDining("Fettucine alfredo")
                .setNotes("Pack warm clothes")
                .setTripCreator(" ")
                .build();

        assertNull(post.getTripCreator());
        assertEquals("Trip creator invalid!", post.getErrorMessage());
    }
}