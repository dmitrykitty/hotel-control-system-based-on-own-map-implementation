package com.dnikitin.hotel.exceptions;

/**
 * Thrown when there is an error reading or writing persistent hotel data,
 * for example from a CSV file.
 */
public class HotelDataException extends Exception{

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval).
     */
    public HotelDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
