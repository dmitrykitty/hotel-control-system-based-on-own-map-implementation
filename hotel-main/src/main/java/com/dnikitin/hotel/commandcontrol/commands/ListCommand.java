package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Reservation;
import com.dnikitin.hotel.model.Room;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles the logic for listing all rooms in the hotel, showing their
 * status, main guest, and reservation dates.
 */
@CommandName("list")
public class ListCommand extends Command {

    @Override
    public void execute() {
        if (hotel == null) {
            throw new IllegalStateException("Command not initialized. 'hotel' is null.");
        }

        List<Room> rooms = hotel.getRooms();

        String format = "| %-8s | %-10s | %-25s | %-13s | %-13s |%n";
        int tableWidth = 85; // 8 + 10 + 15 + 12 + 12 + (separatory)

        ConsoleFormatter.printHeader("ALL ROOMS INFORMATION");
        ConsoleFormatter.printSeparator(tableWidth);
        ConsoleFormatter.printRow(format, "Room Nr", "Status", "Main guest", "Checkin date", "Checkout date");
        ConsoleFormatter.printSeparator(tableWidth);


        if (rooms.isEmpty()) {
            ConsoleFormatter.printRow(format, " (No room available)", "---", "---", "---", "---");
        } else {
            for (Room room : rooms) {
                showRoomInfo(room, format);
            }
        }

        ConsoleFormatter.printSeparator(tableWidth);
        System.out.println();

    }

    private void showRoomInfo(Room room, String format) {
        if (room.isFree()) {
            ConsoleFormatter.printRow(format, room.getRoomNumber(), "Free", "---", "---", "---");
        } else {
            String checkoutDate = "---";

            Reservation res = room.getReservation();
            if (res != null) {
                Guest guest = res.mainGuest();
                LocalDate checkin = res.checkinDate();
                String guestName = (guest != null) ? guest.name() : "---";
                String checkinDate = (checkin != null) ? checkin.toString() : "---";

                if (checkin != null) {
                    checkoutDate = checkin.plusDays(res.duration()).toString();
                }

                ConsoleFormatter.printRow(format,
                        room.getRoomNumber(),
                        "Occupied",
                        guestName,
                        checkinDate,
                        checkoutDate
                );
            }
        }
    }
}

