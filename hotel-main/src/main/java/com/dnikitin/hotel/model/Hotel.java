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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the entire hotel, managing all rooms, reservations,
 * and data persistence (loading/saving to CSV).
 */
public class Hotel {

    private final Map<Integer, Room> rooms;
    private static final String[] STATE_HEADERS = {
            "RoomNumber", "Capacity", "Price",
            "GuestName", "CheckinDate", "Duration", "AdditionalGuests"
    };
    /**
     * CSV format for parsing (reading) files.
     * Assumes file has a header and skips it.
     */
    private static final CSVFormat STATE_FORMAT_PARSER = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setDelimiter(';')
            .setHeader(STATE_HEADERS)
            .setSkipHeaderRecord(true)   // skip header when reading
            .get();

    /**
     * CSV format for printing (writing) files.
     * Includes the header in the output.
     */
    private static final CSVFormat STATE_FORMAT_PRINTER = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setDelimiter(';')
            .setHeader(STATE_HEADERS)
            .setSkipHeaderRecord(false)  // write header when saving
            .get();


    /**
     * Constructs a new, empty Hotel.
     */
    public Hotel() {
        this.rooms = new MyMap<Integer, Room>();
    }

    /**
     * Adds a new room to the hotel's room map.
     *
     * @param room The room to add.
     */
    public void addRoom(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }

    /**
     * Loads the entire hotel state (rooms and reservations) from a CSV file.
     * This will clear any existing room data in the hotel.
     *
     * @param path The file system path to the CSV file.
     * @throws HotelDataException if an I/O error occurs or if the data
     * in the file is malformed (e.g., bad number/date).
     */
    public void loadRoomsFromFile(String path) throws HotelDataException {
        Map<Integer, Room> tempRooms = new MyMap<>();
        long currentLine = 1;

        try (Reader reader = new FileReader(path);
             CSVParser parser = new CSVParser(reader, STATE_FORMAT_PARSER)) {

            for (CSVRecord csvRecord : parser) {
                currentLine = csvRecord.getRecordNumber();

                int roomNumber = Integer.parseInt(csvRecord.get("RoomNumber").trim());
                double price = Double.parseDouble(csvRecord.get("Price").trim());
                int capacity = Integer.parseInt(csvRecord.get("Capacity").trim());

                Room room = new Room(roomNumber, price, capacity);

                String mainGuestName = csvRecord.get("GuestName").trim();

                if (mainGuestName != null && !mainGuestName.isBlank()) {
                    Guest mainGuest = new Guest(mainGuestName);
                    LocalDate checkin = LocalDate.parse(csvRecord.get("CheckinDate").trim());
                    int duration = Integer.parseInt(csvRecord.get("Duration").trim());

                    String additionalGuestsString = csvRecord.get("AdditionalGuests");
                    List<Guest> additionalGuests = parseAdditionalGuestsList(additionalGuestsString);

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
        } catch (DateTimeParseException e) {
            throw new HotelDataException("Error parsing data in file near line " +
                    currentLine + ": " + e.getParsedString(), e);
        } catch (NumberFormatException e) {
            throw new HotelDataException("Error parsing data in file near line " +
                    currentLine + ": " + e.getMessage(), e);
        }
    }

    /**
     * Saves the current hotel state (all rooms and reservations) to a CSV file.
     * The output is sorted by room number.
     *
     * @param path The file system path to write to.
     * @throws HotelDataException if an I/O error occurs during writing.
     */
    public void saveRoomsToFile(String path) throws HotelDataException {
        List<Room> roomsList = getRooms();
        roomsList.sort(Comparator.comparingInt(Room::getRoomNumber));

        try (Writer writer = new FileWriter(path);
             CSVPrinter printer = new CSVPrinter(writer, STATE_FORMAT_PRINTER)) {
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
            throw new HotelDataException("Error writing file (I/O): " + path, e);
        }
    }

    /**
     * Retrieves a room by its number.
     *
     * @param roomNumber The number of the room to find.
     * @return The {@link Room} object, or {@code null} if not found.
     */
    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    /**
     * Gets a list of all rooms in the hotel.
     *
     * @return A {@link List} containing all {@link Room} objects.
     */
    public List<Room> getRooms() {
        List<Room> allRooms = new ArrayList<>();
        // O(n)
        for (java.util.Map.Entry<Integer, Room> entry : (Iterable<java.util.Map.Entry<Integer, Room>>) this.rooms) {
            allRooms.add(entry.getValue());
        }
        return allRooms;
    }

    /**
     * Checks a guest into a specific room with a given date.
     *
     * @param roomNumber    The room number.
     * @param mainGuest     The main guest.
     * @param others        A list of additional guests.
     * @param checkInDate   The specific date of check-in.
     * @param duration      The duration of the stay in nights.
     * @throws RoomNotFoundException if the room number does not exist.
     */
    public void checkIn(int roomNumber, Guest mainGuest, List<Guest> others, LocalDate checkInDate, int duration) {
        if (!rooms.contains(roomNumber)) {
            throw new RoomNotFoundException("Room with number " + roomNumber + " does not exists");
        }
        Reservation reservation = new Reservation(mainGuest, others, checkInDate, duration);
        rooms.get(roomNumber).checkIn(reservation);
    }

    /**
     * Checks a guest into a specific room, assuming the check-in date is today.
     *
     * @param roomNumber    The room number.
     * @param mainGuest     The main guest.
     * @param others        A list of additional guests.
     * @param duration      The duration of the stay in nights.
     * @throws RoomNotFoundException if the room number does not exist.
     */
    public void checkIn(int roomNumber, Guest mainGuest, List<Guest> others, int duration) {
        LocalDate checkInDate = LocalDate.now();
        checkIn(roomNumber, mainGuest, others, checkInDate, duration);
    }

    /**
     * Checks out a guest from a specific room number.
     *
     * @param roomNumber The room number to check out.
     * @return The calculated bill for the stay.
     * @throws RoomNotFoundException if the room number does not exist.
     */
    public double checkOut(int roomNumber) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new RoomNotFoundException("Room with number " + roomNumber + " does not exist");
        }
        return room.checkOut();
    }

    /**
     * Prints a formatted, detailed view of a single room's information
     * and reservation details (if any) to the console.
     *
     * @param room The room to display.
     */
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
                ConsoleFormatter.printProperty("Additional guests", additionalGuests.size());
                for (Guest additionalGuest : additionalGuests) {
                    ConsoleFormatter.printProperty("", additionalGuest.name());
                }
            }
        }
    }

    //PRIVATE HELPERS
    /**
     * Parses the pipe-separated string of additional guests into a List.
     *
     * @param additionalGuestsString The raw string from the CSV record (e.g., "G1|G2").
     * @return A List of Guest objects, or an empty list if the string is blank.
     */
    private List<Guest> parseAdditionalGuestsList(String additionalGuestsString) {
        List<Guest> additionalGuests = new ArrayList<>();
        if (additionalGuestsString != null && !additionalGuestsString.isBlank()) {
            // "|" as separator for additional guests
            String[] guestNames = additionalGuestsString.split("\\|");

            for (String guestName : guestNames) {
                additionalGuests.add(new Guest(guestName));
            }
        }
        return additionalGuests;
    }
}
