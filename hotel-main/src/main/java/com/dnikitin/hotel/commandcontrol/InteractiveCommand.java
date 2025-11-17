package com.dnikitin.hotel.commandcontrol;

import java.util.Scanner;

/**
 * An interface for commands that require interactive input from the user.
 * This allows the main application loop to inject a shared {@link Scanner}
 * into the commands that need it.
 */
public interface InteractiveCommand {
    /**
     * Injects the shared {@link Scanner} for reading user input.
     *
     * @param scanner The Scanner instance.
     */
    void setScanner(Scanner scanner);
}
