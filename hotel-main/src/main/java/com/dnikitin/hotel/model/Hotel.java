package com.dnikitin.hotel.model;

import com.dnikitin.map.MyMap;
import com.dnikitin.map.Map;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Hotel {
    private final Map<Integer, Room> rooms;

    public Hotel() {
        this.rooms = new MyMap<Integer, Room>();
    }

    public void addRoom(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }

    public void loadRooms() {
        //loaded from file. Not implemented yet
    }

    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    public List<Room> getRooms() {
        List<Room> allRooms = new ArrayList<>();
        if (this.rooms instanceof Iterable) {
            // O(n)
            for (java.util.Map.Entry<Integer, Room> entry : (Iterable<java.util.Map.Entry<Integer, Room>>) this.rooms) {
                allRooms.add(entry.getValue());
            }
        } else {
            // O(n*log n))
            for (Integer key : this.rooms.keys()) {
                allRooms.add(this.rooms.get(key));
            }
        }
        return allRooms;
    }

    public boolean checkIn(int roomNumber, Guest mainGuest, List<Guest> others, LocalDate checkInDate, int duration) {
        if (!rooms.contains(roomNumber)) {
            System.err.println("Room with number " + roomNumber + " does not exist");
            return false;
        }
        Reservation reservation = new Reservation(mainGuest, others, checkInDate, duration);
        return rooms.get(roomNumber).checkIn(reservation);
    }

    public boolean checkIn(int roomNumber, Guest mainGuest, List<Guest> others, int duration) {
        LocalDate checkInDate = LocalDate.now();
        return checkIn(roomNumber, mainGuest, others, checkInDate, duration);
    }

}
