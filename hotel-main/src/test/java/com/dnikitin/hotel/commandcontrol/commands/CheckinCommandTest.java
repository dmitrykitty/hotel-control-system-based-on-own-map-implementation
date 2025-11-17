package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class CheckinCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void successfulCheckinWithTodayDateAndNoAdditionalGuests() {
        // Arrange
        Hotel hotel = new Hotel();
        Room room = new Room(101, 100.0, 2);
        hotel.addRoom(room);

        String input = String.join(System.lineSeparator(),
                "101",            // room number
                "John Doe",       // main guest
                "0",              // additional guests count
                "2",              // duration
                ""                // empty -> today
        ) + System.lineSeparator();
        Scanner scanner = new Scanner(input);

        CheckinCommand cmd = new CheckinCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        // Act
        cmd.execute();

        // Assert
        assertNotNull(hotel.getRoom(101).getReservation());
        assertTrue(outContent.toString().contains("Check-in completed successfully"));
        assertEquals("John Doe", hotel.getRoom(101).getReservation().mainGuest().name());
        assertEquals(0, hotel.getRoom(101).getReservation().additionalGuests().size());
    }

    @Test
    public void checkinForUnknownRoomPrintsError() {
        // Arrange: no rooms added
        Hotel hotel = new Hotel();
        String input = String.join(System.lineSeparator(),
                "404",            // room number that doesn't exist
                "John Doe",       // would be main guest
                "0",              // additional guests count
                "1",              // duration
                ""                // date
        ) + System.lineSeparator();
        Scanner scanner = new Scanner(input);

        CheckinCommand cmd = new CheckinCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        // Act
        cmd.execute();

        // Assert
        String err = errContent.toString();
        assertTrue(err.startsWith("Error:"));
        assertTrue(err.contains("does not exist"));
    }
}
