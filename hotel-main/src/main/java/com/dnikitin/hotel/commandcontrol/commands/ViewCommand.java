package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.model.Room;

import java.util.Scanner;

/**
 * Handles the interactive logic for viewing detailed information about a single room.
 * It prompts the user for a room number.
 */
@CommandName("view")
public class ViewCommand extends Command implements InteractiveCommand {

    private Scanner scanner;

    @Override
    public void execute() {
        System.out.println("What nr of the room you are interested in?");

        try {
            int roomNumber = Integer.parseInt(scanner.nextLine().trim());
            Room room = hotel.getRoom(roomNumber);
            if (room == null) {
                System.err.println("Incorrect room number");
            } else {
                hotel.showRoomInfo(room);

            }
        } catch (NumberFormatException e){
            System.err.println("Error: Invalid number provided. Please enter digits only.");
        }
    }

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
