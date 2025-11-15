package com.dnikitin.hotel.commandcontrol;

import com.dnikitin.hotel.model.Hotel;

public abstract class Command {
    protected Hotel hotel;


    public final void setHotel(Hotel hotel){
        this.hotel = hotel;
    }

    public abstract void execute();
}
