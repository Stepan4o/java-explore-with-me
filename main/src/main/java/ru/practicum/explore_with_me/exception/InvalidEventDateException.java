package ru.practicum.explore_with_me.exception;

import java.time.LocalDateTime;

public class InvalidEventDateException extends RuntimeException {
    private static final String message =
            "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %tF %tT";

    public InvalidEventDateException(LocalDateTime actualDateTime) {
        super(String.format(message, actualDateTime, actualDateTime));
    }

}
