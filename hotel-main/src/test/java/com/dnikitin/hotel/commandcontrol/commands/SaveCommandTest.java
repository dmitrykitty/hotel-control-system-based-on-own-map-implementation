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
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SaveCommandTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @TempDir
    Path tempDir;

    @Mock
    private Scanner mockScanner;

    private SaveCommand cmd;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        cmd = new SaveCommand();
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
    public void testSaveToProvidedFileSuccess() throws Exception {
        hotel.addRoom(new Room(101, 150.0, 1));
        Path file = tempDir.resolve("save_ok.csv");

        when(mockScanner.nextLine()).thenReturn(file.toString());

        cmd.execute();

        String out = outContent.toString();
        assertTrue(out.contains("SUCCESSFULLY SAVED TO THE FILE 1 ROOMS"));

        assertTrue(Files.exists(file));
        String content = Files.readString(file);
        assertTrue(content.startsWith("RoomNumber;Capacity;Price"));
        assertTrue(content.contains("101;1;150.0"));
    }


    @Test
    public void testSaveToDirectoryPrintsError() {
        when(mockScanner.nextLine()).thenReturn(tempDir.toString());

        cmd.execute();

        String err = errContent.toString();
        assertTrue(err.startsWith("Error saving hotel state:"));

        String out = outContent.toString();
        assertFalse(out.contains("Successfully saved"));
    }
}
