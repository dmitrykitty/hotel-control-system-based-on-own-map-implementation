package com.dnikitin.hotel.model;

import com.dnikitin.hotel.exceptions.RoomOccupiedException;
import com.dnikitin.hotel.exceptions.RoomSmallCapacityException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Room {
    private final int roomNumber;
    private final double price;
    private final int capacity;

    private Reservation reservation;

    public Room(int number, double price, int capacity) {
        this.roomNumber = number;
        this.price = price;
        this.capacity = capacity;
        this.reservation = null;
    }

    public boolean isFree() {
        return reservation == null;
    }

    public boolean checkIn(Reservation reservation) {
        if (!isFree()) {
            throw new RoomOccupiedException("Room" + roomNumber + "already occupied");
        }
        if (reservation.additionalGuests().size() + 1 > capacity) {
            throw new RoomSmallCapacityException("Too many guest. Room capacity is" + capacity);
        }
        this.reservation = reservation;
        return true;
    }

    public double checkOut() {
        if (isFree()) {
            throw new RoomOccupiedException("Room" + roomNumber + "already occupied");
        }

        long diff = ChronoUnit.DAYS.between(reservation.checkinDate(), LocalDate.now());
        double finalPrice = price * (diff == 0 ? 1 : diff);
        reservation = null;

        return finalPrice;
    }

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
