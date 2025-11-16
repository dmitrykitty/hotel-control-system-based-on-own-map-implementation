package com.dnikitin.hotel;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.CommandRegistry;
import com.dnikitin.hotel.commandcontrol.commandutils.ConsoleFormatter;
import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Hotel;
import com.dnikitin.hotel.model.Room;

import java.util.ArrayList;
import java.util.Scanner;

public class HotelRunner {
    public static void main(String[] args) {

        // --- 1. FAZA KONFIGURACJI ---

        // Stwórz główny obiekt stanu aplikacji
        Hotel hotel = new Hotel();

        // Wypełnij hotel danymi (zamiast wczytywania z pliku na tym etapie)
        initializeHotelData(hotel);

        // Stwórz fabrykę komend.
        // Fabryka automatycznie przeskanuje pakiety w poszukiwaniu klas @CommandName.
        CommandRegistry commandFactory = new CommandRegistry();

        // Stwórz scanner (POZA pętlą!) do czytania wejścia od użytkownika
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("Witaj w systemie zarządzania hotelem.");
        System.out.println("Wpisz 'exit', aby zakończyć, lub 'help' (jeśli masz taką komendę).");

        // --- 2. GŁÓWNA PĘTLA APLIKACJI (REPL) ---

        while (running) {
            System.out.print("\n> "); // Znak zachęty
            String input = scanner.nextLine();

            // Przypadek 1: Puste wejście
            if (input == null || input.isBlank()) {
                continue;
            }

            // Przypadek 2: Komenda wyjścia (specjalna obsługa)
            if (input.equalsIgnoreCase("exit")) {
                running = false;
                continue; // Pomiń resztę pętli
            }

            if (input.equalsIgnoreCase("help")) {
                showMenu(); // Wywołaj metodę pomocniczą
                continue; // Pomiń resztę pętli
            }

            // --- 3. WZORZEC FABRYKI I STRATEGII W AKCJI ---
            try {
                // 1. Użyj FABRYKI, aby stworzyć "pusty" obiekt komendy
                Command command = commandFactory.createCommand(input.toLowerCase());

                // 2. Wstrzyknij stan (Hotel) do komendy (zgodnie z Twoim projektem)
                command.setHotel(hotel);

                // 3. Wykonaj STRATEGIĘ (polimorfizm)
                command.execute();

            } catch (IllegalArgumentException e) {
                // Ten błąd jest rzucany przez Twoją fabrykę, gdy komenda jest nieznana
                System.err.println("BŁĄD: Nieznana komenda '" + input + "'");
            } catch (IllegalStateException e) {
                // Ten błąd rzucisz, jeśli hotel nie został ustawiony (dobra praktyka)
                System.err.println("BŁĄD WEWNĘTRZNY: " + e.getMessage());
            } catch (Exception e) {
                // Złap wszystkie inne błędy z logiki komendy (np. zły format daty)
                System.err.println("Wystąpił nieoczekiwany błąd: " + e.getMessage());
                // e.printStackTrace(); // Odkomentuj to, aby pomóc w debugowaniu
            }
        }

        // --- 4. SPRZĄTANIE ---
        scanner.close();
        System.out.println("Zamykanie aplikacji... Do widzenia!");
    }

    /**
     * Metoda pomocnicza do "zahardkodowania" danych hotelu na starcie.
     */
    private static void initializeHotelData(Hotel hotel) {
        System.out.println("Ładowanie konfiguracji pokoi...");
        hotel.addRoom(new Room(101, 250, 2)); // nr, cena, pojemność
        hotel.addRoom(new Room(102, 300, 2));
        hotel.addRoom(new Room(201, 400, 3));
        hotel.addRoom(new Room(202, 450, 4));
        hotel.addRoom(new Room(301, 180, 1));

        // Możesz tu też dodać "testową" rezerwację, aby 'list' nie był pusty
        hotel.checkIn(101, new Guest("Jan Kowalski"), new ArrayList<>(), 2);

        System.out.println("Załadowano " + hotel.getRooms().size() + " pokoi.");
    }

    private static void showMenu() {
        ConsoleFormatter.printHeader("HOTEL MANAGEMENT SYSTEM - AVAILABLE COMMANDS");

        ConsoleFormatter.printProperty("list", "Show summary of all rooms and occupancy.");
        ConsoleFormatter.printProperty("prices", "Display the price list for all rooms.");
        ConsoleFormatter.printProperty("view", "Show detailed information for a specific room.");
        ConsoleFormatter.printProperty("checkin", "Check a guest into a room.");
        ConsoleFormatter.printProperty("checkout", "Check a guest out of a room.");
        ConsoleFormatter.printProperty("save", "Save the current hotel state to a file.");
        ConsoleFormatter.printProperty("help", "Display this help menu.");
        ConsoleFormatter.printProperty("exit", "Exit the application.");
    }
}
