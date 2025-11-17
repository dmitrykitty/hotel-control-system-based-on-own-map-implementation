package com.dnikitin.hotel.commandcontrol.commandutils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a {@link com.dnikitin.hotel.commandcontrol.Command}
 * class with its invocation name (e.g., "checkin").
 * This is used by the {@link com.dnikitin.hotel.commandcontrol.CommandRegistry}
 * for auto-registration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandName {
    /**
     * @return The invocation name of the command.
     */
    String value();
}
