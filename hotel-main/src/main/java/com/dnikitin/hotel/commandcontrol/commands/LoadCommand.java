package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandConstants;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.exceptions.HotelDataException;

import java.util.Scanner;

@CommandName("load")
public class LoadCommand extends Command implements InteractiveCommand {
    Scanner scanner;

    @Override
    public void execute() {
        if(hotel == null){
            throw new IllegalStateException("Command not initialized. Call setHotel(hotel) before executing.");
        }
        System.out.println("Enter filename to load (press Enter for '" + CommandConstants.DEFAULT_FILENAME + "'): ");
        String filename = scanner.nextLine();

        if (filename.isBlank()) {
            filename = CommandConstants.DEFAULT_FILENAME;
        }

        try {
            hotel.loadRoomsFromFile(filename);
        } catch (HotelDataException e) {
            System.err.println("Error loading hotel state: " + e.getMessage());
        }
    }

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
