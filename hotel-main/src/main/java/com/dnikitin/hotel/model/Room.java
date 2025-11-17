package com.dnikitin.hotel.model;

import com.dnikitin.hotel.exceptions.RoomFreeException;
import com.dnikitin.hotel.exceptions.RoomOccupiedException;
import com.dnikitin.hotel.exceptions.RoomSmallCapacityException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a single hotel room, managing its price, capacity, and current reservation state.
 */
public class Room {
    private final int roomNumber;
    private final double price;
    private final int capacity;

    private Reservation reservation;

    /**
     * Constructs a new Room.
     *
     * @param number   The unique room number.
     * @param price    The price per night.
     * @param capacity The maximum number of guests.
     */
    public Room(int number, double price, int capacity) {
        this.roomNumber = number;
        this.price = price;
        this.capacity = capacity;
        this.reservation = null;
    }

    /**
     * Checks if the room is currently free (not reserved).
     *
     * @return true if the room is free, false otherwise.
     */
    public boolean isFree() {
        return reservation == null;
    }

    /**
     * Checks a guest into the room with the given reservation details.
     *
     * @param reservation The reservation to assign to the room.
     * @throws RoomOccupiedException      if the room is already occupied.
     * @throws RoomSmallCapacityException if the number of guests exceeds the room's capacity.
     */
    public void checkIn(Reservation reservation) {
        if (!isFree()) {
            throw new RoomOccupiedException("Room " + roomNumber + " already occupied");
        }
        if (reservation.additionalGuests().size() + 1 > capacity) {
            throw new RoomSmallCapacityException("Too many guest. Room capacity is" + capacity);
        }
        this.reservation = reservation;
    }

    /**
     * Checks a guest out of the room. This frees the room and calculates the total bill
     * based on the check-in date and the current date.
     *
     * @return The calculated total price for the stay.
     * @throws RoomFreeException if the room is already free.
     */
    public double checkOut() {
        if (isFree()) {
            throw new RoomFreeException("Room " + roomNumber + " is free");
        }

        long diff = ChronoUnit.DAYS.between(reservation.checkinDate(), LocalDate.now());
        double finalPrice = price * (diff == 0 ? 1 : diff);
        reservation = null;

        return finalPrice;
    }

    // GETTERS
    public int getRoomNumber() {
        return roomNumber;
    }

    public double getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public Reservation getReservation() {
        return reservation;
    }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return roomNumber == room.roomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNumber);
    }
}
