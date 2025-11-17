package com.dnikitin.hotel.model;

import com.dnikitin.hotel.exceptions.RoomFreeException;
import com.dnikitin.hotel.exceptions.RoomOccupiedException;
import com.dnikitin.hotel.exceptions.RoomSmallCapacityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {
    private Room room;
    private Guest guest;
    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        room = new Room(101, 200.0, 2);
        guest = new Guest("John Doe");
        reservation = new Reservation(guest, List.of(), LocalDate.now(), 2);
    }

    @Test
    public void TestFreeRoomAndNullReservation() {
        assertAll(
                () -> assertTrue(room.isFree()),
                () -> assertNull(room.getReservation())
        );
    }


    @Test
    public void TestNotFreeRoomAndNotNullReservationAfterCheckin() {
        room.checkIn(reservation);
        assertAll(
                () -> assertNotNull(room.getReservation()),
                () -> assertFalse(room.isFree())
        );
    }

    @Test
    public void TestCheckInFailsIfRoomOccupied() {
        room.checkIn(reservation);
        Reservation anotherReservation = new Reservation(guest, List.of(), LocalDate.now(), 3);

        assertThrows(RoomOccupiedException.class, () -> room.checkIn(anotherReservation));
    }

    @Test
    public void TestCheckinFailsTooManyGuests() {
        Reservation anotherReservation = new Reservation(guest, List.of(
                new Guest("A"),
                new Guest("B"),
                new Guest("C")
        ), LocalDate.now(), 3);

        assertThrows(RoomSmallCapacityException.class, () -> room.checkIn(anotherReservation));
    }

    @Test
    public void checkOutSuccess() {
        room.checkIn(reservation);
        assertFalse(room.isFree());

        room.checkOut();

        assertAll(
                () -> assertTrue(room.isFree()),
                () -> assertNull(room.getReservation())
        );
    }

    @Test
    public void checkoutFailsForFreeRoom() {
        assertAll(
                () -> assertTrue(room.isFree()),
                () -> assertThrows(RoomFreeException.class, () -> room.checkOut())
        );
    }

    @Test
    public void checkOutCalculatesPriceForOneDayIfSameDay() {
        // Check-in and check-out on the same day counts as 1 day
        room.checkIn(reservation);
        double price = room.checkOut();
        assertEquals(room.getPrice(), price);
    }

    @Test
    public void checkOutCalculatesPriceForMultipleDays() {
        int duration = 2;
        LocalDate checkinDate = LocalDate.now().minusDays(duration);
        Reservation oldReservation = new Reservation(guest, List.of(), checkinDate, 5);
        room.checkIn(oldReservation);

        double price = room.checkOut();

        // 2 days * 200.0/day
        assertEquals(room.getPrice() * duration, price);
    }


}
