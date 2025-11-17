package com.dnikitin.hotel.commandcontrol.commands;

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
public class ViewCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @Mock
    private Scanner mockScanner;

    private ViewCommand cmd;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        cmd = new ViewCommand();
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
    public void viewFreeRoomPrintsInfo() {
        hotel.addRoom(new Room(777, 300.0, 3));
        when(mockScanner.nextLine()).thenReturn("777");

        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("INFORMATION ABOUT ROOM 777")),
                () -> assertTrue(out.contains("Price per night : 300.0$")),
                () -> assertTrue(out.contains("Room capacity   : 3")),
                () -> assertTrue(out.contains("Status          : free")),
                () -> assertFalse(out.contains("RESERVATION INFORMATION"))
        );
    }

    @Test
    public void viewBusyRoomPrintsGuestInfo() {
        LocalDate checkinDate = LocalDate.now().minusDays(2);
        int duration = 5;
        String expectedCheckoutDate = checkinDate.plusDays(duration).toString();

        Reservation res = new Reservation(
                new Guest("Alice Smith"),
                List.of(new Guest("Bob Johnson")),
                checkinDate,
                duration
        );
        Room busyRoom = new Room(101, 200, 2);
        busyRoom.checkIn(res);
        hotel.addRoom(busyRoom);

        when(mockScanner.nextLine()).thenReturn("101");
        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("INFORMATION ABOUT ROOM 101")),
                () -> assertTrue(out.contains("Status          : busy")),
                () -> assertTrue(out.contains("RESERVATION INFORMATION")),
                () -> assertTrue(out.contains("Main guest      : Alice Smith")),
                () -> assertTrue(out.contains("Checkin date    : " + checkinDate)),
                () -> assertTrue(out.contains("Checkout date   : " + expectedCheckoutDate)),
                () -> assertTrue(out.contains("Additional guests : 1 persons")),
                () -> assertTrue(out.contains("Bob Johnson"))
        );
    }

    @Test
    public void viewUnknownRoomPrintsError() {
        when(mockScanner.nextLine()).thenReturn("1");

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.contains("Incorrect room number"));
        assertFalse(outContent.toString().contains("Information about room"));
    }

    @Test
    public void viewWithInvalidInputPrintsError() {
        when(mockScanner.nextLine()).thenReturn("abc");

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.contains("Error: Invalid number provided. Please enter digits only."));
        assertFalse(outContent.toString().contains("Information about room"));
    }
}
