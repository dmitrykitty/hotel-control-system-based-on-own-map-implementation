package com.dnikitin.hotel.exceptions;

/**
 * Thrown when a check-in is attempted with more guests than the room's capacity allows.
 */
public class RoomSmallCapacityException extends RuntimeException{
    public RoomSmallCapacityException(String msg){
        super(msg);
    }
}
