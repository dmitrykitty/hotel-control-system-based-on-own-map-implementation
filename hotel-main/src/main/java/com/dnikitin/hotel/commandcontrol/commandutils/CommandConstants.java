package com.dnikitin.hotel.commandcontrol.commandutils;

/**
 * Utility class holding shared constants for command-related operations,
 * such as default filenames.
 */
public final class CommandConstants {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CommandConstants(){}

    /**
     * The default filename used for loading and saving the hotel state
     * if the user provides no other name.
     */
    public static final String DEFAULT_FILENAME = "hotel_state.csv";
}
