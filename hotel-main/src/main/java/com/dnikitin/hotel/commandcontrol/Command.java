package com.dnikitin.hotel.commandcontrol;

import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Reservation;
import com.dnikitin.hotel.model.Room;

import java.util.List;

public abstract class Command {
    protected Hotel hotel;


    public final void setHotel(Hotel hotel){
        this.hotel = hotel;
    }

    public abstract void execute();
}
