package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class PricesCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    private PricesCommand cmd;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        cmd = new PricesCommand();
        hotel = new Hotel();
        cmd.setHotel(hotel);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testPricesForEmptyHotel() {
        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("HOTEL PRICE")),
                () -> assertTrue(out.contains("Room number")),
                () -> assertTrue(out.contains("Price ( $/night )")),
                () -> assertTrue(out.contains("(No rooms are available)"))
        );
    }

    @Test
    public void testPricesForHotelWithRooms() {
        hotel.addRoom(new Room(10, 99.5, 1));
        hotel.addRoom(new Room(20, 150.0, 2));

        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("HOTEL PRICE")),
                () -> assertTrue(out.contains("10")),
                () -> assertTrue(out.contains("20")),
                () -> assertTrue(out.contains("99.5")),
                () -> assertTrue(out.contains("150.0"))
        );
    }
}
