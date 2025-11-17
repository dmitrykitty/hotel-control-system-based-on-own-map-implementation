package com.dnikitin.hotel.commandcontrol;

import com.dnikitin.hotel.commandcontrol.commands.CheckinCommand;
import com.dnikitin.hotel.commandcontrol.commands.CheckoutCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandRegistryTest {

    @Test
    public void autoRegistersAnnotatedCommandsAndCreatesInstances() {
        CommandRegistry registry = new CommandRegistry();

        Command checkin = registry.createCommand("checkin");
        Command checkout = registry.createCommand("checkout");

        assertAll(
                () -> assertNotNull(checkin),
                () -> assertNotNull(checkout),
                () -> assertInstanceOf(CheckinCommand.class, checkin),
                () -> assertInstanceOf(CheckoutCommand.class, checkout)
        );
    }

    @Test
    public void unknownCommandThrowsHelpfulError() {
        CommandRegistry registry = new CommandRegistry();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registry.createCommand("does-not-exist"));

        assertTrue(ex.getMessage().startsWith("Unknown command:"));
    }
}
