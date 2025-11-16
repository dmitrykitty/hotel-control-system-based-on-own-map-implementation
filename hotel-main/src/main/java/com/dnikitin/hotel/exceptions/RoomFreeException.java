package com.dnikitin.hotel.exceptions;

public class RoomFreeException extends RuntimeException{
    public RoomFreeException(String msg){
        super(msg);
    }
}
