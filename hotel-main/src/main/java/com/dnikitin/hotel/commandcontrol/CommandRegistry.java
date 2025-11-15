package com.dnikitin.hotel.commandcontrol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;
import org.reflections.Reflections;

public class CommandRegistry {

    private final Map<String, Class<? extends Command>> commandMap = new HashMap<>();

    public CommandRegistry() {
        autoRegisterCommands();
    }

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

    // Create a command instance based on the string
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