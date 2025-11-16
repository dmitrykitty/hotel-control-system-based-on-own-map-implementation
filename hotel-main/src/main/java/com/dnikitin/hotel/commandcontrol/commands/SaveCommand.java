package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;

import java.util.Scanner;

@CommandName("save")
public class SaveCommand extends Command implements InteractiveCommand {

    Scanner scanner;

    @Override
    public void execute() {
        if(hotel == null){
            throw new IllegalStateException("Command not initialized. Call setHotel(hotel) before executing.");
        }
        System.out.println("Provide the name of the file where you want to save hotel state:");
        String filename = scanner.nextLine();

        if(filename.isBlank()){

        }

    }

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
