package com.dnikitin.hotel.exceptions;

/**
 * Thrown when a command class exists but cannot be instantiated
 * (e.g. due to constructor failure or visibility issues).
 */
public class CommandCreationException extends RuntimeException{
    public CommandCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
