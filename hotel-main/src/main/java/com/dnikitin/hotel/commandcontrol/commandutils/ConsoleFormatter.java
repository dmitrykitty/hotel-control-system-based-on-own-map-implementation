package com.dnikitin.hotel.commandcontrol.commandutils;

/**
 * A utility class for printing formatted output to the console,
 * such as tables, headers, and properties.
 */
public class ConsoleFormatter {

    private ConsoleFormatter(){}

    /**
     * Prints a single, formatted table row.
     *
     * @param format The printf format string (e.g., "| %-10s | %-15s |")
     * @param args   The arguments to insert into the format string.
     */
    public static void printRow(String format, Object... args) {
        System.out.printf(format, args);
    }

    /**
     * Prints a separator line for a table.
     *
     * @param width The total width of the table.
     */
    public static void printSeparator(int width) {
        // Prints a line of '-' characters of the given width
        System.out.println("+" + "-".repeat(width - 2) + "+");
    }

    /**
     * Prints a formatted header (e.g., for the 'view' command).
     *
     * @param title The title to display in the header.
     */
    public static void printHeader(String title) {
        System.out.println("\n---[ " + title.toUpperCase() + " ]---");
    }

    /**
     * Prints a key-value pair in a clean, aligned format.
     *
     * @param key   The property key.
     * @param value The property value.
     */
    public static void printProperty(String key, Object value) {
        // Aligns the key to 15 characters, left-justified
        System.out.printf("  %-15s : %s%n", key, value.toString());
    }
}