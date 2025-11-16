package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.model.Room;

import java.util.Scanner;

@CommandName("checkout")
public class CheckoutCommand extends Command {

    @Override
    public void execute() {
        if (hotel == null) {
            throw new IllegalStateException("Command not initialized. 'hotel' is null.");
        }

        Scanner scanner = new Scanner(System.in);
        ConsoleFormatter.printHeader("CHECK-OUT WIZARD");
        System.out.print("Enter room number: ");

        int roomNumber;
        try {
            roomNumber = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: provided non-integer room number.");
            return;
        }

        Room room = hotel.getRoom(roomNumber);
        if (room == null) {
            System.err.println("Error: Room with number " + roomNumber + " does not exist.");
            return;
        }
        if (room.isFree()) {
            System.err.println("Error: Room " + roomNumber + " is not currently occupied.");
            return;
        }

        double amount = room.checkOut();
        System.out.printf("Room %d has been checked out. Total due: %.2f$%n", roomNumber, amount);
    }
}
