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
public class CheckoutCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @Mock
    private Scanner mockScanner;

    private CheckoutCommand cmd;
    private Hotel hotel;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        cmd = new CheckoutCommand();
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
    public void successfulCheckoutPrintsAmountAndFreesRoom() {
        Room room = new Room(201, 120.0, 2);
        hotel.addRoom(room);

        // 2 * 120.0 = 240.0
        Reservation res = new Reservation(new Guest("Main"), List.of(), LocalDate.now().minusDays(2), 5);
        room.checkIn(res);

        when(mockScanner.nextLine()).thenReturn("201");

        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("Total due: 240.00$")),
                () -> assertTrue(hotel.getRoom(201).isFree())
        );
    }

    @Test
    public void checkoutUnknownRoomPrintsError() {
        when(mockScanner.nextLine()).thenReturn("404");

        cmd.execute();

        String err = errContent.toString();
        assertAll(
                () -> assertTrue(err.contains("Error: Room with number 404 does not exist.")),
                () -> assertFalse(outContent.toString().contains("Total due"))
        );
    }

    @Test
    public void checkoutBeforeCheckinDatePrintsError() {
        Room room = new Room(305, 90.0, 1);
        hotel.addRoom(room);
        Reservation res = new Reservation(new Guest("Futurist"), List.of(), LocalDate.now().plusDays(1), 2);
        room.checkIn(res);

        when(mockScanner.nextLine()).thenReturn("305");

        cmd.execute();

        String err = errContent.toString();
        assertAll(
                () -> assertTrue(err.contains("Error: You can't checkout before your checkin date")),
                () -> assertFalse(hotel.getRoom(305).isFree())
        );
    }

    @Test
    public void checkoutFromFreeRoomPrintsError() {
        hotel.addRoom(new Room(101, 100.0, 1));
        when(mockScanner.nextLine()).thenReturn("101");

        cmd.execute();

        String err = errContent.toString();
        // Oczekujemy błędu o wolnym pokoju.
        // UWAGA: To może rzucić NullPointerException, jeśli twój kod
        // nie sprawdza, czy pokój jest wolny PRZED `room.getReservation()`.
        // Jeśli tak, test ujawni błąd w `CheckoutCommand`.
        assertTrue(err.contains("Error: Room 101 is free"));
    }

    @Test
    public void checkoutWithInvalidInputPrintsError() {
        when(mockScanner.nextLine()).thenReturn("abc");

        cmd.execute();
        String err = errContent.toString();
        assertTrue(err.contains("Error: Invalid number provided. Please enter digits only."));
    }
}
