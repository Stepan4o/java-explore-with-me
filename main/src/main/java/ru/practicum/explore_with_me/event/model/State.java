package ru.practicum.explore_with_me.event.model;

import ru.practicum.explore_with_me.exception.IncorrectStatusException;

public enum State {
    PENDING, PUBLISHED, CANCELED;

    public static State convertStatus(String status) {
        try {
            return State.valueOf(status);
        } catch (RuntimeException exception) {
            throw new IncorrectStatusException(String.format("State is incorrect: %s", status));
        }
    }
}
