package ru.practicum.explore_with_me.stats.server.exception;

public class InvalidDateTimeException extends RuntimeException {
    public InvalidDateTimeException() {
        super("StartTime should be before EndTime");
    }
}
