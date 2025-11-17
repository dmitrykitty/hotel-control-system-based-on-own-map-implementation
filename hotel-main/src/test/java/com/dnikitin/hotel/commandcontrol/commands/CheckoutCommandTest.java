package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
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
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutCommandTest {

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
    public void successfulCheckoutPrintsAmountAndFreesRoom() {
        Hotel hotel = new Hotel();
        Room room = new Room(201, 120.0, 2);
        hotel.addRoom(room);
        // check in 2 days ago so amount = 2 * 120.0
        Reservation res = new Reservation(new Guest("Main"), List.of(), LocalDate.now().minusDays(2), 5);
        room.checkIn(res);

        String input = String.join(System.lineSeparator(),
                "201"
        ) + System.lineSeparator();
        Scanner scanner = new Scanner(input);

        CheckoutCommand cmd = new CheckoutCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        String out = outContent.toString();
        assertAll(
                () -> assertTrue(out.contains("Total due")),
                () -> assertTrue(hotel.getRoom(201).isFree())
        );
    }

    @Test
    public void checkoutUnknownRoomPrintsError() {
        Hotel hotel = new Hotel();
        String input = "404" + System.lineSeparator();
        Scanner scanner = new Scanner(input);

        CheckoutCommand cmd = new CheckoutCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        String err = errContent.toString();
        assertAll(
                () -> assertTrue(err.startsWith("Error:")),
                () -> assertTrue(err.contains("does not exist"))
        );
    }

    @Test
    public void checkoutBeforeCheckinDatePrintsError() {
        Hotel hotel = new Hotel();
        Room room = new Room(305, 90.0, 1);
        hotel.addRoom(room);
        Reservation res = new Reservation(new Guest("Futurist"), List.of(), LocalDate.now().plusDays(1), 2);
        room.checkIn(res);

        String input = "305" + System.lineSeparator();
        Scanner scanner = new Scanner(input);

        CheckoutCommand cmd = new CheckoutCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.contains("can't checkout before your checkin date"));
        assertFalse(hotel.getRoom(305).isFree());
    }
}
