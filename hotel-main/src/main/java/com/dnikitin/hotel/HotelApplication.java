package com.dnikitin.hotel;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.CommandRegistry;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.exceptions.HotelDataException;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HotelApplication {
    public static void main(String[] args) {

        // CONFIGURATION PHASE

        Hotel hotel = new Hotel();
        if (args.length > 0) {
            String filename = args[0];
            System.out.println("Attempting to load hotel state from argument: " + filename);
            try {
                hotel.loadRoomsFromFile(filename);
            } catch (HotelDataException e) {
                System.err.println("ERROR: Could not load data from file: " + e.getMessage());
                System.out.println("Loading default hardcoded data instead.");
                initializeHotelData(hotel); // Fallback to default data
            }
        } else {
            System.out.println("No file path provided. Loading default hardcoded data.");
            initializeHotelData(hotel);
        }

        CommandRegistry commandFactory = new CommandRegistry();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to the Hotel Management System.");
        System.out.println("Type 'help' for a list of commands or 'exit' to quit.");

        // MAIN APPLICATION LOOP

        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine();

            // Empty input -> continue
            if (input == null || input.isBlank()) {
                continue;
            }

            // Exit command
            if (input.equalsIgnoreCase("exit")) {
                running = false;
                continue;
            }

            // Help command
            if (input.equalsIgnoreCase("help")) {
                showMenu();
                continue;
            }

            // FACTORY AND STRATEGY PATTERN
            try {
                Command command = commandFactory.createCommand(input.toLowerCase());

                command.setHotel(hotel);
                if (command instanceof InteractiveCommand) {
                    ((InteractiveCommand) command).setScanner(scanner);
                }

                command.execute();

            } catch (IllegalArgumentException e) {
                System.err.println("ERROR: Unknown command '" + input + "'. Type 'help' for options.");
            } catch (IllegalStateException e) {
                System.err.println("INTERNAL ERROR: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Shutting down application... Goodbye!");
    }


    private static void initializeHotelData(Hotel hotel) {
        System.out.println("Loading default room configuration...");

        hotel.addRoom(new Room(101, 250, 2)); // nr, price, capacity
        hotel.addRoom(new Room(102, 250, 2));
        hotel.addRoom(new Room(103, 275, 2));
        hotel.addRoom(new Room(104, 220, 1));

        hotel.addRoom(new Room(201, 400, 3));
        hotel.addRoom(new Room(202, 450, 4));
        hotel.addRoom(new Room(203, 650, 4));

        hotel.addRoom(new Room(301, 180, 1));
        hotel.addRoom(new Room(302, 180, 1));

        hotel.checkIn(101, new Guest("John Doe"), new ArrayList<>(), 2);
        hotel.checkIn(202, new Guest("Alice Smith"),
                List.of(new Guest("Bob Smith"), new Guest("Charlie Smith")), 5);

        System.out.println("Loaded " + hotel.getRooms().size() + " rooms.");
    }


    private static void showMenu() {
        ConsoleFormatter.printHeader("HOTEL MANAGEMENT SYSTEM - AVAILABLE COMMANDS");

        ConsoleFormatter.printProperty("list", "Show summary of all rooms and occupancy.");
        ConsoleFormatter.printProperty("prices", "Display the price list for all rooms.");
        ConsoleFormatter.printProperty("view", "Show detailed information for a specific room.");
        ConsoleFormatter.printProperty("checkin", "Check a guest into a room.");
        ConsoleFormatter.printProperty("checkout", "Check a guest out of a room.");
        ConsoleFormatter.printProperty("save", "Save the current hotel state to a file (Bonus 2).");
        ConsoleFormatter.printProperty("load", "Load hotel state from a file (Bonus 1).");
        ConsoleFormatter.printProperty("help", "Display this help menu.");
        ConsoleFormatter.printProperty("exit", "Exit the application.");
    }
}
