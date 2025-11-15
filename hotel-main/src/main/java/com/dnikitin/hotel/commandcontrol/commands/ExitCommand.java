package com.dnikitin.hotel.commandcontrol.commands;

import com.dnikitin.hotel.commandcontrol.Command;
import com.dnikitin.hotel.commandcontrol.commandutils.CommandName;

@CommandName("exit")
public class ExitCommand extends Command {
    @Override
    public void execute() {
        System.exit(0);
    }
}
