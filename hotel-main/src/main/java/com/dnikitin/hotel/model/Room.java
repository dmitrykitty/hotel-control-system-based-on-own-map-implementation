package com.dnikitin.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Room {
    private final int roomNumber;
    private final int price;
    private final int capacity;

    private Reservation reservation;

    public Room(int number, int price, int capacity) {
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
            System.err.println("Room is already reserved");
            return false;
        }
        if (reservation.additionalGuests().size() + 1 > capacity) {
            System.err.println("To many guests for this room");
            return false;
        }
        this.reservation = reservation;
        return true;
    }

    public double checkOut() {
        if (isFree()) {
            System.err.println("Room is not reserved");
            return 0.0;
        }

        long diff = ChronoUnit.DAYS.between(reservation.checkinDate(), LocalDate.now());
        double finalPrice = price * (diff == 0 ? 1 : diff);
        reservation = null;

        return finalPrice;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getPrice() {
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
