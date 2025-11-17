package com.dnikitin.hotel.commandcontrol;

import com.dnikitin.hotel.model.Hotel;

/**
 * Abstract base class for all commands in the system, implementing the Strategy Pattern.
 * Each command represents a single user action.
 */
public abstract class Command {
    protected Hotel hotel;


    /**
     * Injects the main hotel model into the command.
     * This must be called before {@link #execute()}.
     *
     * @param hotel The main hotel state object.
     */
    public final void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    /**
     * Executes the specific logic of this command.
     */
    public abstract void execute();
}
