package com.dnikitin.hotel.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a reservation for a hotel room.
 *
 * @param mainGuest        The primary guest for the reservation.
 * @param additionalGuests A list of other guests staying in the room.
 * @param checkinDate      The date the reservation starts.
 * @param duration         The duration of the stay in nights.
 */
public record Reservation(
        Guest mainGuest,
        List<Guest> additionalGuests,
        LocalDate checkinDate,
        int duration) {
}