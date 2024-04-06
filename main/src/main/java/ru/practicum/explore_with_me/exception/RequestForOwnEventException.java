package ru.practicum.explore_with_me.exception;

public class RequestForOwnEventException extends RuntimeException {
    public RequestForOwnEventException() {
        super("Initiator can't send request for own event");
    }
}
