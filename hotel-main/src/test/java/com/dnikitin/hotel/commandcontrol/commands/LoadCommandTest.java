package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class LoadCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void loadFromProvidedFilePathSuccess() throws IOException {
        String header = "RoomNumber;Capacity;Price;GuestName;CheckinDate;Duration;AdditionalGuests";
        String row = "401;1;99.0;;;;";
        Path file = tempDir.resolve("state.csv");
        Files.writeString(file, header + System.lineSeparator() + row + System.lineSeparator());

        Hotel hotel = new Hotel();
        Scanner scanner = new Scanner(file.toString() + System.lineSeparator());

        LoadCommand cmd = new LoadCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        Room r = hotel.getRoom(401);
        assertNotNull(r);
        assertTrue(r.isFree());
    }

    @Test
    public void loadFromMissingFilePrintsError() {
        Hotel hotel = new Hotel();
        String missing = tempDir.resolve("missing.csv").toString();
        Scanner scanner = new Scanner(missing + System.lineSeparator());

        LoadCommand cmd = new LoadCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.startsWith("Error loading hotel state:"));
    }
}
