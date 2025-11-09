package com.dnikitin.hotel.model;

import java.time.LocalDate;
import java.util.List;

public record Reservation(
        Guest mainGuest,
        List<Guest> additionalGuests,
        LocalDate checkinDate,
        int duration) {
}