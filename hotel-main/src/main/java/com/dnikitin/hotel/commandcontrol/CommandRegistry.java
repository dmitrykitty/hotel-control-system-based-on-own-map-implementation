package com.dnikitin.hotel.commandcontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import org.reflections.Reflections;

/**
 * A factory class that discovers, registers, and creates command instances.
 * It uses reflection to find all classes annotated with {@link CommandName}
 * and registers them automatically.
 */
public class CommandRegistry {

    private final Map<String, Class<? extends Command>> commandMap = new HashMap<>();

    /**
     * Creates a new CommandRegistry and immediately scans for and
     * registers all available commands.
     */
    public CommandRegistry() {
        autoRegisterCommands();
    }

    /**
     * Uses reflection to scan the `commands` package for classes that
     * extend {@link Command} and are annotated with {@link CommandName}.
     */
    private void autoRegisterCommands() {
        Reflections reflections = new Reflections("com.dnikitin.hotel.commandcontrol.commands");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> commandClass : commandClasses) {
            if (commandClass.isAnnotationPresent(CommandName.class)) {
                String commandName = commandClass.getAnnotation(CommandName.class).value().toLowerCase();
                commandMap.put(commandName, commandClass);
            }
        }
    }

    /**
     * Creates a new instance of a command based on its registered name.
     *
     * @param commandName The name of the command (e.g., "checkin").
     * @return A new, uninitialized instance of the corresponding {@link Command}.
     * @throws IllegalArgumentException if the commandName is not recognized.
     * @throws RuntimeException         if the command instance cannot be created (e.g., no default constructor).
     */
    public Command createCommand(String commandName) {
        Class<? extends Command> commandClass = commandMap.get(commandName);
        if (commandClass == null) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
        try {
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create command instance", e);
        }
    }
}