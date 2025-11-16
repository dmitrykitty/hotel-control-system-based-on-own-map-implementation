package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.exceptions.RoomFreeException;
import com.dnikitin.hotel.exceptions.RoomNotFoundException;
import com.dnikitin.hotel.model.Room;

import java.time.LocalDate;
import java.util.Scanner;

@CommandName("checkout")
public class CheckoutCommand extends Command implements InteractiveCommand {

    private Scanner scanner;

    @Override
    public void execute() {
        if (hotel == null) {
            throw new IllegalStateException("Command not initialized. 'hotel' is null.");
        }
        ConsoleFormatter.printHeader("CHECK-OUT");
        try {
            System.out.print("Enter room number: ");
            int roomNumber = Integer.parseInt(scanner.nextLine().trim());

            Room room = hotel.getRoom(roomNumber);
            if (room == null) {
                throw new RoomNotFoundException("Room with number " + roomNumber + " does not exist.");
            }
            if(room.getReservation().checkinDate().isAfter(LocalDate.now()))
                throw new IllegalArgumentException("You can't checkout before your checkin date");

            double amount = room.checkOut();
            System.out.printf("Room %d has been checked out. Total due: %.2f$%n", roomNumber, amount);
            System.out.println();
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number provided. Please enter digits only.");
        } catch (RoomNotFoundException | RoomFreeException | IllegalArgumentException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
