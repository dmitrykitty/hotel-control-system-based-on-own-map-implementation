package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Room;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@CommandName("checkin")
public class CheckinCommand extends Command {

    @Override
    public void execute() {
        if (hotel == null) {
            throw new IllegalStateException("Command not initialized. 'hotel' is null.");
        }

        Scanner scanner = new Scanner(System.in);

        ConsoleFormatter.printHeader("CHECK-IN WIZARD");
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
        if (!room.isFree()) {
            System.err.println("Error: Room " + roomNumber + " is already occupied.");
            return;
        }

        System.out.print("Enter main guest full name: ");
        String mainName = scanner.nextLine().trim();
        if (mainName.isBlank()) {
            System.err.println("Error: Main guest name cannot be empty.");
            return;
        }
        Guest mainGuest = new Guest(mainName);

        int capacityLeft = Math.max(0, room.getCapacity() - 1);
        int additionalCount = 0;
        if (capacityLeft > 0) {
            System.out.print("Enter number of additional guests (0-" + capacityLeft + "): ");
            String countStr = scanner.nextLine().trim();
            try {
                additionalCount = countStr.isBlank() ? 0 : Integer.parseInt(countStr);
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid number for additional guests.");
                return;
            }
            if (additionalCount < 0 || additionalCount > capacityLeft) {
                System.err.println("Error: Number of additional guests must be between 0 and " + capacityLeft + ".");
                return;
            }
        }

        List<Guest> others = new ArrayList<>();
        for (int i = 1; i <= additionalCount; i++) {
            System.out.print("Enter name of additional guest " + i + ": ");
            String name = scanner.nextLine().trim();
            if (name.isBlank()) {
                System.err.println("Error: Guest name cannot be empty.");
                return;
            }
            others.add(new Guest(name));
        }

        System.out.print("Enter duration of stay (nights): ");
        int duration;
        try {
            duration = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.err.println("Error: Duration must be an integer number of nights.");
            return;
        }
        if (duration <= 0) {
            System.err.println("Error: Duration must be greater than 0.");
            return;
        }

        System.out.print("Enter check-in date (YYYY-MM-DD) or press Enter for today: ");
        String dateStr = scanner.nextLine().trim();
        boolean success;
        if (dateStr.isBlank()) {
            success = hotel.checkIn(roomNumber, mainGuest, others, duration);
        } else {
            try {
                LocalDate checkInDate = LocalDate.parse(dateStr);
                success = hotel.checkIn(roomNumber, mainGuest, others, checkInDate, duration);
            } catch (DateTimeParseException ex) {
                System.err.println("Error: Invalid date format. Expected YYYY-MM-DD.");
                return;
            }
        }

        if (success) {
            System.out.println("Check-in completed successfully for room " + roomNumber + ".");
        } else {
            System.err.println("Check-in failed for room " + roomNumber + ".");
        }
    }
}
