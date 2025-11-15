package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.model.Room;

import java.util.Scanner;

@CommandName("view")
public class ViewCommand extends Command {

    @Override
    public void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What nr of the room you are interested in?");

        try {
            int roomNumber = Integer.parseInt(scanner.nextLine());
            Room room = hotel.getRoom(roomNumber);
            if (room == null) {
                System.err.println("Incorrect room number");
            } else {
                hotel.showRoomInfo(room);

            }
        } catch (NumberFormatException e){
            System.err.println("Error: provided non integers character");
        }
    }
}
