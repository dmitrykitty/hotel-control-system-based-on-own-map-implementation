package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;

import java.util.List;

@CommandName("prices")
public class PricesCommand extends Command {


    @Override
    public void execute() {
        if(hotel == null){
            throw new IllegalStateException("Command not initialized. Call setHotel(hotel) before executing.");
        }
        List<Room> rooms = hotel.getRooms();

        String format = "| %-12s | %20s |%n";
        int tableWidth = 39; // 12 + 20 + 7 (na | | | %n)

        ConsoleFormatter.printHeader("HOTEL PRICE");
        ConsoleFormatter.printSeparator(tableWidth);
        ConsoleFormatter.printRow(format, "Room number", "Price ( $/night )");
        ConsoleFormatter.printSeparator(tableWidth);

        if (rooms.isEmpty()) {
            ConsoleFormatter.printRow(format, " (No rooms are available)", "---");
        } else {
            for (Room room : rooms) {;
                ConsoleFormatter.printRow(format, room.getRoomNumber(), room .getPrice());
            }
        }
        ConsoleFormatter.printSeparator(tableWidth);
        System.out.println();

    }
}
