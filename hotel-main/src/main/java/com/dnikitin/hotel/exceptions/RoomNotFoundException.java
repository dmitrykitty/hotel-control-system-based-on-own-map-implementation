package com.dnikitin.hotel.exceptions;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String msg){
        super(msg);
    }
}
