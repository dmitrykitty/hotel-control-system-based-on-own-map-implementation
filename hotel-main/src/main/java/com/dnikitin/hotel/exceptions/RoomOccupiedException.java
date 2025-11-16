package com.dnikitin.hotel.exceptions;

public class RoomOccupiedException extends RuntimeException{
    public RoomOccupiedException(String msg){
        super(msg);
    }
}
