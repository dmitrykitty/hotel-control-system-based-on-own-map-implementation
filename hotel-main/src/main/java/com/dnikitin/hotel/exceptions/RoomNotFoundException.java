package com.dnikitin.hotel.exceptions;

/**
 * Thrown when an operation is attempted on a room number that does not exist in the hotel.
 */
public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String msg){
        super(msg);
    }
}
