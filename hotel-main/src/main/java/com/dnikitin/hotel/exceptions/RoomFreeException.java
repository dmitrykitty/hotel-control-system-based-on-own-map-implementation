package com.dnikitin.hotel.exceptions;

/**
 * Thrown when an operation (like checkout) is attempted on a room that is already free.
 */
public class RoomFreeException extends RuntimeException{
    public RoomFreeException(String msg){
        super(msg);
    }
}
