package com.dnikitin.hotel.exceptions;

/**
 * Thrown when a requested command is not registered in the system.
 */
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
