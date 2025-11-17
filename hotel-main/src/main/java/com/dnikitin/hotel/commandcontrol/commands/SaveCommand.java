package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandConstants;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.exceptions.HotelDataException;

import java.util.Scanner;

/**
 * Handles the interactive logic for saving the current hotel state to a CSV file.
 * It prompts the user for a filename.
 */
@CommandName("save")
public class SaveCommand extends Command implements InteractiveCommand {

    Scanner scanner;

    @Override
    public void execute() {
        if (hotel == null) {
            throw new IllegalStateException("Command not initialized. Call setHotel(hotel) before executing.");
        }
        System.out.println("Provide the name of the file where you want to save hotel state or press Enter for "
                + CommandConstants.DEFAULT_FILENAME + " filename:");
        String filename = scanner.nextLine();

        if (filename.isBlank()) {
            filename = CommandConstants.DEFAULT_FILENAME;
        }
        try {
            hotel.saveRoomsToFile(filename);
        } catch (HotelDataException e) {
            System.err.println("Error saving hotel state: " + e.getMessage());
        }

    }

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
