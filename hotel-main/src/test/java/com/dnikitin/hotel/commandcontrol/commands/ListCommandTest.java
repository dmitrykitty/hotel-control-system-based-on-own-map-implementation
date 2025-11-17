package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Reservation;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    private Hotel hotel;
    private Command cmd;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        outContent = new ByteArrayOutputStream();

        hotel = new Hotel();
        cmd = new ListCommand();
        cmd.setHotel(hotel);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void listsRoomsWithStatuses() {
        Room free = new Room(101, 100.0, 1);
        Room busy = new Room(102, 200.0, 2);
        hotel.addRoom(free);
        hotel.addRoom(busy);

        LocalDate checkinDate = LocalDate.now().minusDays(1);
        int duration = 3;
        String expectedCheckoutDate = checkinDate.plusDays(duration).toString();

        busy.checkIn(new Reservation(new Guest("Alice"), List.of(), checkinDate, duration));

        cmd.setHotel(hotel);
        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("ALL ROOMS INFORMATION")),
                () -> assertTrue(out.contains("101")),
                () -> assertTrue(out.contains("102")),
                () -> assertTrue(out.contains("Free")),
                () -> assertTrue(out.contains("Occupied")),
                () -> assertTrue(out.contains("Alice")),
                () -> assertTrue(out.contains(expectedCheckoutDate))
        );
    }

    @Test
    public void testListEmptyHotel() {
        cmd.execute();
        String out = outContent.toString();

        assertTrue(out.contains("ALL ROOMS INFORMATION"));
        assertTrue(out.contains("(No room available)"));
    }
}
