package com.dnikitin.hotel.exceptions;

/**
 * Thrown when an operation (like check-in) is attempted on a room that is already occupied.
 */
public class RoomOccupiedException extends RuntimeException{
    public RoomOccupiedException(String msg){
        super(msg);
    }
}
