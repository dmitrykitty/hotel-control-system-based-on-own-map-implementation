package com.dnikitin.hotel.model;

import com.dnikitin.hotel.exceptions.HotelDataException;
import com.dnikitin.hotel.exceptions.RoomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HotelTest {
    Hotel hotel;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        hotel = new Hotel();
    }

    @Test
    public void addAndGetRoomSuccess() {
        Room room = new Room(101, 100, 1);
        hotel.addRoom(room);

        Room retrieved = hotel.getRoom(101);
        assertAll(
                () -> assertEquals(room, retrieved),
                () -> assertEquals(1, hotel.getRooms().size())
        );
    }

    @Test
    public void getNonExistentRoomReturnsNull() {
        assertNull(hotel.getRoom(999));
    }

    @Test
    public void checkInNonExistentRoomFails() {
        Room rm = new Room(999, 250.0, 3);
        int roomNumber = rm.getRoomNumber();
        Guest guest = new Guest("Test");
        List<Guest> additionalGuests = List.of();
        RoomNotFoundException roomNotFoundException = assertThrows(RoomNotFoundException.class,
                () -> hotel.checkIn(roomNumber, guest, additionalGuests, 1)
        );

        assertEquals("Room with number " + roomNumber + " does not exists",
                roomNotFoundException.getMessage()
        );
    }

    @Test
    public void testSuccessCheckInWithCheckInDate() {
        Room room = new Room(999, 250.0, 3);
        hotel.addRoom(room);
        hotel.checkIn(room.getRoomNumber(), new Guest("A"), List.of(), LocalDate.now(), 3);

        assertNotNull(hotel.getRoom(room.getRoomNumber()).getReservation());
    }

    @Test
    public void checkoutNonExistentRoomFails() {
        Hotel h = new Hotel();
        RoomNotFoundException ex = assertThrows(RoomNotFoundException.class,
                () -> h.checkOut(404));
        assertTrue(ex.getMessage().contains("404"));
    }

    @Test
    public void checkoutReturnsBillAndFreesRoom() {
        Hotel h = new Hotel();
        Room r = new Room(101, 100.0, 2);
        h.addRoom(r);
        h.checkIn(101, new Guest("A"), List.of(), LocalDate.now().minusDays(2), 5);

        double bill = h.checkOut(101);

        assertAll(
                () -> assertEquals(200.0, bill),
                () -> assertTrue(h.getRoom(101).isFree())
        );
    }

    @Test
    public void loadRoomsFileNotFound() {
        Hotel newHotel = new Hotel();
        String nonExistingPath = "non_existent_file.csv";
        HotelDataException hotelDataException = assertThrows(HotelDataException.class,
                () -> newHotel.loadRoomsFromFile(nonExistingPath));

        assertEquals("Error reading file (I/O): " + nonExistingPath, hotelDataException.getMessage());
    }

    @Test
    public void loadRoomsFileBadData() throws IOException {
        Path file = tempDir.resolve("bad_data.csv");
        String header = "RoomNumber;Capacity;Price;GuestName;CheckinDate;Duration;AdditionalGuests";
        // "Price" column contains "not-a-number"
        Files.writeString(file, header + "\n" + "101;2;not-a-number;Guest;2024-10-10;2;");
        Hotel newHotel = new Hotel();


        HotelDataException hotelDataException = assertThrows(HotelDataException.class,
                () -> newHotel.loadRoomsFromFile(file.toString())
        );

        assertTrue(hotelDataException.getMessage().contains("Error parsing data in file near line"));

    }

    @Test
    public void testSaveAndLoadRoomsSuccess() throws HotelDataException, IOException {
        Room room101 = new Room(101, 150, 1);
        Room room102 = new Room(102, 250, 2);
        String header = "RoomNumber;Capacity;Price;GuestName;CheckinDate;Duration;AdditionalGuests";

        hotel.addRoom(room101);
        hotel.addRoom(room102);

        Guest guest = new Guest("Alice");
        List<Guest> others = List.of(new Guest("Bob"));
        hotel.checkIn(102, guest, others, LocalDate.parse("2024-10-10"), 3);

        Path file = tempDir.resolve("test_hotel.csv");
        hotel.saveRoomsToFile(file.toString());

        List<String> lines = Files.readAllLines(file);
        assertAll(
                () -> assertEquals(3, lines.size()), // 2 rooms + headers
                () -> assertEquals(header, lines.getFirst()), //headers
                () -> assertTrue(lines.get(1).contains("101;1;150.0;;;;")), // Room 101 is free
                () -> assertTrue(lines.get(2).contains("102;2;250.0;Alice;2024-10-10;3;Bob"))// Room 102 occupied
        );

        Hotel newHotel = new Hotel();
        newHotel.loadRoomsFromFile(file.toString());

        assertEquals(2, newHotel.getRooms().size());

        Room loaded101 = newHotel.getRoom(101);
        assertAll(
                () -> assertNotNull(loaded101),
                () -> assertTrue(loaded101.isFree()),
                () -> assertEquals(room101.getPrice(), loaded101.getPrice()),
                () -> assertEquals(room101.getCapacity(), loaded101.getCapacity())
        );

        Room loaded102 = newHotel.getRoom(102);
        assertAll(
                () -> assertNotNull(loaded102),
                () -> assertFalse(loaded102.isFree()),
                () -> assertEquals(room102.getReservation().mainGuest().name(),
                        loaded102.getReservation().mainGuest().name()),
                () -> assertEquals(room102.getReservation().additionalGuests().getFirst().name(),
                        loaded102.getReservation().additionalGuests().getFirst().name())

        );

    }


    @Test
    public void loadRoomsParsesAdditionalGuestsPipeSeparated() throws Exception {
        String header = "RoomNumber;Capacity;Price;GuestName;CheckinDate;Duration;AdditionalGuests";
        String row = "303;3;300.0;Main;2025-01-01;2;G1|G2";
        Path file = tempDir.resolve("guests.csv");
        Files.writeString(file, header + System.lineSeparator() + row);

        Hotel newHotel = new Hotel();
        newHotel.loadRoomsFromFile(file.toString());

        Room r = newHotel.getRoom(303);
        assertNotNull(r);
        assertFalse(r.isFree());
        assertEquals(2, r.getReservation().additionalGuests().size());
    }

    @Test
    public void saveRoomsToFileWriteErrorMessage() {
        Hotel h = new Hotel();
        // Try saving into a directory path to provoke IOException
        HotelDataException ex = assertThrows(HotelDataException.class,
                () -> h.saveRoomsToFile(tempDir.toString()));
        assertTrue(ex.getMessage().startsWith("Error writing file (I/O):"));
    }
}
