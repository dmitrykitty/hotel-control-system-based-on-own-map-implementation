package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;

import java.util.List;

@CommandName("prices")
public class PricesCommand extends Command {


    @Override
    public void execute() {
        if(hotel == null){
            throw new NullPointerException("");
        }
        List<Room> rooms = hotel.getRooms();

        for (Room room : rooms) {
            System.out.println(room.getRoomNumber() + ": " + room.getPrice() + "$");
        }

    }
}
