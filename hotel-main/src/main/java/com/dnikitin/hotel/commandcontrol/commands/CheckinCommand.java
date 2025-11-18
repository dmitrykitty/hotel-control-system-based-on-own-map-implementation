package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.exceptions.RoomNotFoundException;
import com.dnikitin.hotel.exceptions.RoomOccupiedException;
import com.dnikitin.hotel.exceptions.RoomSmallCapacityException;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Room;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles the interactive logic for checking a guest into a room.
 * It prompts for room number, guest details, duration, and check-in date.
 */
@CommandName("checkin")
public class CheckinCommand extends Command implements InteractiveCommand {
    private Scanner scanner;

    @Override
    public void execute() {
        if (hotel == null || scanner == null) {
            throw new IllegalStateException("Command not initialized. Call setHotel() and setScanner().");
        }
        ConsoleFormatter.printHeader("CHECK-IN");
        try {
            System.out.print("Enter room number: ");
            int roomNumber = Integer.parseInt(scanner.nextLine().trim());

            Room room = hotel.getRoom(roomNumber);
            roomValidation(room, roomNumber);

            System.out.print("Enter main guest full name: ");
            String mainName = scanner.nextLine().trim();
            if (mainName.isBlank()) {
                throw new IllegalArgumentException("Main guest name cannot be empty.");
            }

            Guest mainGuest = new Guest(mainName);
            int capacityLeft = Math.max(0, room.getCapacity() - 1);
            List<Guest> additionalGuests = additionalGuestsValidation(capacityLeft);

            System.out.print("Enter duration of stay (nights): ");
            int duration = Integer.parseInt(scanner.nextLine().trim());
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be at least 1 night.");
            }

            System.out.print("Enter check-in date (YYYY-MM-DD) or press Enter for today: ");
            String dateStr = scanner.nextLine().trim();
            if (dateStr.isBlank()) {
                hotel.checkIn(roomNumber, mainGuest, additionalGuests, duration);
            } else {
                LocalDate checkInDate = LocalDate.parse(dateStr);
                hotel.checkIn(roomNumber, mainGuest, additionalGuests, checkInDate, duration);
            }
            System.out.println("\nCheck-in completed successfully for room " + roomNumber + ".");

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number provided. Please enter digits only.");
        } catch (DateTimeParseException e) {
            System.err.println("Error: Invalid date format. Expected YYYY-MM-DD.");
        } catch (RoomSmallCapacityException | RoomOccupiedException | RoomNotFoundException |
                 IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the interactive logic for validating and collecting additional guests.
     *
     * @param capacityLeft The number of additional guests the room can hold (capacity - 1).
     * @return A list of Guest objects for the additional guests.
     * @throws RoomSmallCapacityException if the user enters a number of guests that exceeds the capacity.
     * @throws IllegalArgumentException   if a guest's name is left blank.
     * @throws NumberFormatException      if the count of guests is not a valid number.
     */
    private List<Guest> additionalGuestsValidation(int capacityLeft) {
        List<Guest> others = new ArrayList<>();
        if (capacityLeft > 0) {
            System.out.print("Enter number of additional guests (0-" + capacityLeft + "): ");
            String countStr = scanner.nextLine().trim();
            int additionalCount = countStr.isBlank() ? 0 : Integer.parseInt(countStr);
            if (additionalCount < 0 || additionalCount > capacityLeft) {
                throw new RoomSmallCapacityException("Number of additional guests must be between 0 and " + capacityLeft + ".");
            }

            for (int i = 1; i <= additionalCount; i++) {
                System.out.print("Enter name of additional guest " + i + ": ");
                String name = scanner.nextLine().trim();
                if (name.isBlank()) {
                    throw new IllegalArgumentException("Guest name cannot be empty.");
                }
                others.add(new Guest(name));
            }
        }
        return others;
    }

    /**
     * Validates a room to ensure it exists and is free for check-in.
     *
     * @param room       The Room object to check.
     * @param roomNumber The room number (used for error messages).
     * @throws RoomNotFoundException if the room is null (does not exist).
     * @throws RoomOccupiedException if the room is not free.
     */
    private void roomValidation(Room room, int roomNumber) {
        if (room == null) {
            throw new RoomNotFoundException("Room with number " + roomNumber + " does not exist.");
        }
        if (!room.isFree()) {
            throw new RoomOccupiedException("Room " + roomNumber + " is already occupied.");
        }
    }

    @Override
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
