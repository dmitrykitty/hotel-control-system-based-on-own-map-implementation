package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;

import java.util.Scanner;

@CommandName("save")
public class SaveCommand extends Command {

    @Override
    public void execute() {
        if(hotel == null){
            throw new IllegalStateException("Command not initialized. Call setHotel(hotel) before executing.");
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Provide name of the file you want to save hotel state:");
        String filename = scanner.nextLine();

        if(filename.isBlank()){

        }

    }
}
