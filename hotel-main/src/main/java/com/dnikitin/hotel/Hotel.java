package com.dnikitin.hotel;

import com.dnikitin.hotel.model.Guest;
import com.dnikitin.hotel.model.Reservation;
import com.dnikitin.hotel.model.Room;
import com.dnikitin.map.MyMap;
import com.dnikitin.map.Map;

import java.util.ArrayList;
import java.util.List;


public class Hotel {
    private final Map<Integer, Room> rooms;

    public Hotel() {
        this.rooms = new MyMap<Integer, Room>();
    }

    public void loadRooms(){}

    public Room getRoom(int roomNumber){
        return rooms.get(roomNumber);
    }

    public List<Room> getRooms(){
        List<Room> roomsList = new ArrayList<>();
        List<Integer> roomNumbers = rooms.keys();
        for (Integer roomNumber : roomNumbers) {
            roomsList.add(rooms.get(roomNumber));
        }
        return roomsList;
    }

    public boolean checkIn(int roomNumber, Guest mainGuest, List<Guest> others, int checkInDate, int checkOutDate){
        if(!rooms.contains(roomNumber)){
            System.err.println("Room with number " + roomNumber + " does not exist");
            return false;
        }
        if(rooms.get(roomNumber).getReservation() != null){
            throw new IllegalArgumentException("Room with number " + roomNumber + " is already occupied");
        }
        rooms.get(roomNumber).setReservation(reservation);
        return true;

    }

}
