package com.dnikitin.hotel.commandcontrol.commandutils;

public class ConsoleFormatter {

    /**
     * Drukuje pojedynczą, sformatowaną linię tabeli.
     * @param format Format printf (np. "| %-10s | %-15s |")
     * @param args Argumenty do wstawienia w format
     */
    public static void printRow(String format, Object... args) {
        System.out.printf(format, args);
    }

    /**
     * Drukuje linię oddzielającą w tabeli.
     * @param width Całkowita szerokość tabeli
     */
    public static void printSeparator(int width) {
        // Drukuje linię złożoną ze znaków '-' o podanej szerokości
        System.out.println("+" + "-".repeat(width - 2) + "+");
    }

    /**
     * Drukuje sformatowany nagłówek (np. dla komendy 'view')
     */
    public static void printHeader(String title) {
        System.out.println("\n---[ " + title.toUpperCase() + " ]---");
    }

    /**
     * Drukuje klucz i wartość w ładnym, wyrównanym formacie.
     */
    public static void printProperty(String key, Object value) {
        // Wyrównuje klucz do 15 znaków, w lewo
        System.out.printf("  %-15s : %s%n", key, value.toString());
    }
}