package com.dnikitin.hotel.model;

import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.exceptions.HotelDataException;
import com.dnikitin.hotel.exceptions.RoomNotFoundException;
import com.dnikitin.map.MyMap;
import com.dnikitin.map.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Hotel {

    private final Map<Integer, Room> rooms;
    private static final String[] STATE_HEADERS = {
            "RoomNumber", "Capacity", "Price",
            "GuestName", "CheckinDate", "Duration", "AdditionalGuests"
    };
    private static final CSVFormat STATE_FORMAT = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setDelimiter(';')
            .setHeader(STATE_HEADERS)
            .setSkipHeaderRecord(true)
            .get();


    public Hotel() {
        this.rooms = new MyMap<Integer, Room>();
    }

    public void addRoom(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }

    public void loadRoomsFromFile(String path) throws HotelDataException {
        Map<Integer, Room> tempRooms = new MyMap<>();
        long currentLine = 1;

        try (Reader reader = new FileReader(path);
             CSVParser parser = new CSVParser(reader, STATE_FORMAT)) {

            for (CSVRecord record : parser) {
                currentLine = record.getRecordNumber();

                int roomNumber = Integer.parseInt(record.get("RoomNumber"));
                double price = Double.parseDouble(record.get("Price"));
                int capacity = Integer.parseInt(record.get("Capacity"));

                Room room = new Room(roomNumber, price, capacity);

                String mainGuestName = record.get("GuestName");

                if (mainGuestName != null && !mainGuestName.isBlank()) {
                    Guest mainGuest = new Guest(mainGuestName);
                    LocalDate checkin = LocalDate.parse(record.get("CheckinDate"));
                    int duration = Integer.parseInt(record.get("Duration"));

                    List<Guest> additionalGuests = new ArrayList<>();
                    String additionalGuestsString = record.get("AdditionalGuests");

                    if (additionalGuestsString != null && !additionalGuestsString.isBlank()) {
                        // "|" as separator for additional guests
                        String[] guestNames = additionalGuestsString.split("\\|");

                        for (String guestName : guestNames) {
                            additionalGuests.add(new Guest(guestName));
                        }
                    }

                    Reservation reservation = new Reservation(
                            mainGuest,
                            additionalGuests,
                            checkin,
                            duration
                    );
                    room.checkIn(reservation);

                }
                tempRooms.put(roomNumber, room);
            }
            this.rooms.clear();

            for (java.util.Map.Entry<Integer, Room> entry : (Iterable<java.util.Map.Entry<Integer, Room>>) tempRooms) {
                this.rooms.put(entry.getKey(), entry.getValue());
            }
            ConsoleFormatter.printHeader("Successfully read and saved " + rooms.size() + " rooms");
        } catch (IOException e) {
            throw new HotelDataException("Error reading file (I/O): " + path, e);
        } catch (Exception e) {
            throw new HotelDataException("Error parsing data in file near line " +
                    currentLine + ": " + e.getMessage(), e);
        }
    }

    public void saveRoomsToFile(String path) throws HotelDataException {
        List<Room> roomsList = getRooms();

        try (Writer writer = new FileWriter(path);
             CSVPrinter printer = new CSVPrinter(writer, STATE_FORMAT)) {
            for (Room room : roomsList) {
                if (room.isFree()) {
                    printer.printRecord(
                            room.getRoomNumber(),
                            room.getCapacity(),
                            room.getPrice(),
                            "", "", "", ""
                    );
                } else {
                    Reservation res = room.getReservation();
                    Guest guest = res.mainGuest();

                    String additionalGuestsString = res.additionalGuests().stream()
                            .map(Guest::name)
                            .collect(Collectors.joining("|"));

                    printer.printRecord(
                            room.getRoomNumber(),
                            room.getCapacity(),
                            room.getPrice(),
                            guest.name(),
                            res.checkinDate().toString(),
                            res.duration(),
                            additionalGuestsString
                    );
                }
            }
            ConsoleFormatter.printHeader("Successfully saved to the file " + rooms.size() + " rooms");
        } catch (IOException e) {
            throw new HotelDataException("Error reading file (I/O): " + path, e);
        }
    }

    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    public List<Room> getRooms() {
        List<Room> allRooms = new ArrayList<>();
        // O(n)
        for (java.util.Map.Entry<Integer, Room> entry : (Iterable<java.util.Map.Entry<Integer, Room>>) this.rooms) {
            allRooms.add(entry.getValue());
        }
        return allRooms;
    }

    public boolean checkIn(int roomNumber, Guest mainGuest, List<Guest> others, LocalDate checkInDate, int duration) {
        if (!rooms.contains(roomNumber)) {
            throw new RoomNotFoundException("Room with number" + roomNumber + " does not exists");
        }
        Reservation reservation = new Reservation(mainGuest, others, checkInDate, duration);
        return rooms.get(roomNumber).checkIn(reservation);
    }

    public boolean checkIn(int roomNumber, Guest mainGuest, List<Guest> others, int duration) {
        LocalDate checkInDate = LocalDate.now();
        return checkIn(roomNumber, mainGuest, others, checkInDate, duration);
    }

    public void showRoomInfo(Room room) {
        ConsoleFormatter.printHeader("Information about room " + room.getRoomNumber());
        ConsoleFormatter.printProperty("Price per night", room.getPrice() + "$");
        ConsoleFormatter.printProperty("Room capacity", room.getCapacity());

        if (room.isFree()) {
            ConsoleFormatter.printProperty("Status", "free");

        } else {
            ConsoleFormatter.printProperty("Status", "busy");

            Reservation reservation = room.getReservation();

            ConsoleFormatter.printHeader("Reservation information");
            ConsoleFormatter.printProperty("Main guest", reservation.mainGuest().name());
            ConsoleFormatter.printProperty("Checkin date", reservation.checkinDate().toString());

            LocalDate checkoutDate = reservation.checkinDate().plusDays(reservation.duration());
            ConsoleFormatter.printProperty("Checkout date", checkoutDate.toString());

            List<Guest> additionalGuests = reservation.additionalGuests();
            if (additionalGuests.isEmpty()) {
                ConsoleFormatter.printProperty("Additional guests", "None");
            } else {
                ConsoleFormatter.printProperty("Additional guests", additionalGuests.size() + " persons");
                for (Guest additionalGuest : additionalGuests) {
                    ConsoleFormatter.printProperty("", additionalGuest.name());
                }
            }
        }
    }
}
