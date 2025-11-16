package com.dnikitin.hotel.exceptions;

public class RoomSmallCapacityException extends RuntimeException{
    public RoomSmallCapacityException(String msg){
        super(msg);
    }
}
