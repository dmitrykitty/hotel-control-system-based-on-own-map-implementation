package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandConstants;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoadCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @TempDir
    Path tempDir;

    @Mock
    private Scanner mockScanner;

    private LoadCommand cmd;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        cmd = new LoadCommand();
        hotel = new Hotel();
        cmd.setHotel(hotel);
        cmd.setScanner(mockScanner);
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void loadFromProvidedFilePathSuccess() throws IOException {
        String header = "RoomNumber;Capacity;Price;GuestName;CheckinDate;Duration;AdditionalGuests\n";
        String row = "401;1;99.0;;;;\n";
        Path file = tempDir.resolve("state.csv");
        Files.writeString(file, header + row);

        when(mockScanner.nextLine()).thenReturn(file.toString());

        cmd.execute();

        String out = outContent.toString();

        assertTrue(out.contains("SUCCESSFULLY READ AND SAVED 1 ROOMS"));

        assertEquals(1, hotel.getRooms().size());
        Room room = hotel.getRoom(401);
        assertNotNull(room);
        assertTrue(room.isFree());
        assertEquals(99.0, room.getPrice());
    }

    @Test
    public void loadFromMissingFilePrintsError() {
        String missingFile = tempDir.resolve("missing.csv").toString();
        when(mockScanner.nextLine()).thenReturn(missingFile);

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.startsWith("Error loading hotel state:"));
        assertTrue(err.contains(missingFile));

        assertTrue(hotel.getRooms().isEmpty());
    }

    @Test
    public void loadFromDefaultFilePrintsErrorIfMissing() {
        when(mockScanner.nextLine()).thenReturn("");

        cmd.execute();

        String err = errContent.toString();

        assertTrue(err.startsWith("Error loading hotel state:"));
        assertTrue(err.contains(CommandConstants.DEFAULT_FILENAME));

        assertTrue(hotel.getRooms().isEmpty());
    }
}
