package com.dnikitin.hotel.exceptions;

public class HotelDataException extends Exception{
    public HotelDataException(String message) {
        super(message);
    }

    //exception wrapping
    public HotelDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
