package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Reservation;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckinCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @Mock
    private Scanner mockScanner;

    private CheckinCommand cmd;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        cmd = new CheckinCommand();
        hotel = new Hotel();
        cmd.setHotel(hotel);
        cmd.setScanner(mockScanner);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void successfulCheckinWithTodayDateAndNoAdditionalGuests() {

        Room room = new Room(101, 100.0, 2);
        hotel.addRoom(room);

        when(mockScanner.nextLine())
                .thenReturn("101")           // Enter room number:
                .thenReturn("John Doe")      // Enter main guest full name:
                .thenReturn("0")             // Enter number of additional guests:
                .thenReturn("2")             // Enter duration of stay (nights):
                .thenReturn("");             // Enter check-in date (or press Enter for today):

        cmd.execute();

        Reservation res = hotel.getRoom(101).getReservation();
        assertNotNull(res);
        assertTrue(outContent.toString().contains("Check-in completed successfully"));
        assertEquals("John Doe", res.mainGuest().name());
        assertEquals(0, res.additionalGuests().size());
        assertEquals(LocalDate.now(), res.checkinDate());
    }


    @Test
    public void successfulCheckinWithSpecificDateAndGuests() {
        Room room = new Room(202, 300.0, 4);
        hotel.addRoom(room);

        when(mockScanner.nextLine())
                .thenReturn("202")             // room number
                .thenReturn("Alice")           // main guest
                .thenReturn("2")               // 2 additional guests
                .thenReturn("Bob")             // guest 1
                .thenReturn("Charlie")         // guest 2
                .thenReturn("5")               // duration
                .thenReturn("2025-12-20");     // specific date

        cmd.execute();

        Reservation res = hotel.getRoom(202).getReservation();
        assertNotNull(res);
        assertTrue(outContent.toString().contains("Check-in completed successfully"));
        assertEquals("Alice", res.mainGuest().name());
        assertEquals(2, res.additionalGuests().size());
        assertEquals("Bob", res.additionalGuests().getFirst().name());
        assertEquals(LocalDate.parse("2025-12-20"), res.checkinDate());
    }

    @Test
    public void checkinForUnknownRoomPrintsError() {
        when(mockScanner.nextLine()).thenReturn("404");

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.contains("Error: Room with number 404 does not exist."));
        assertFalse(outContent.toString().contains("Check-in completed successfully"));
    }

    @Test
    public void checkinForOccupiedRoomPrintsError() {
        Room room = new Room(101, 100.0, 2);
        hotel.addRoom(room);
        hotel.checkIn(101, new Guest("Existing Guest"), List.of(), 5);

        when(mockScanner.nextLine()).thenReturn("101"); //the same room

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.contains("Error: Room 101 is already occupied."));
    }

    @Test
    public void checkinFailsCapacityExceeded() {
        Room room = new Room(101, 100.0, 2);
        hotel.addRoom(room);

        // 1 main guest + 2 addtional -> 3 > 2(capacity)
        when(mockScanner.nextLine())
                .thenReturn("101")           // room number
                .thenReturn("John Doe")      // main guest
                .thenReturn("2");            // 2 additional guests

        cmd.execute();
        String err = errContent.toString();
        assertTrue(err.contains("Error: Number of additional guests must be between 0 and 1."));
    }

    @Test
    public void checkinWithInvalidDatePrintsError() {
        hotel.addRoom(new Room(101, 100.0, 2));

        when(mockScanner.nextLine())
                .thenReturn("101")
                .thenReturn("John Doe")
                .thenReturn("0")
                .thenReturn("2")
                .thenReturn("not-a-date"); // not date-type

        cmd.execute();
        String err = errContent.toString();
        assertTrue(err.contains("Error: Invalid date format. Expected YYYY-MM-DD."));
    }
}
