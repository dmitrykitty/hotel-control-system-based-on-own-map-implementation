package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.InteractiveCommand;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class SaveCommandTest {

    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream errContent;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUpStreams() {
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void savesToProvidedFileSuccessfully() throws Exception {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room(101, 150.0, 1));

        Path file = tempDir.resolve("save_ok.csv");
        Scanner scanner = new Scanner(file.toString() + System.lineSeparator());

        SaveCommand cmd = new SaveCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.startsWith("RoomNumber;"));
        assertTrue(content.contains("101;"));
    }

    @Test
    public void savingToDirectoryPrintsError() {
        Hotel hotel = new Hotel();
        // Provide the directory path itself to trigger write error
        String path = tempDir.toString();
        Scanner scanner = new Scanner(path + System.lineSeparator());

        SaveCommand cmd = new SaveCommand();
        cmd.setHotel(hotel);
        ((InteractiveCommand) cmd).setScanner(scanner);

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.startsWith("Error saving hotel state:"));
    }
}
