package ru.practicum.explore_with_me.event.model;

import ru.practicum.explore_with_me.exception.IncorrectStatusException;

public enum AdminStateAction {
    PUBLISH_EVENT, REJECT_EVENT;

    public static AdminStateAction toEnum(String action) {
        try {
            return AdminStateAction.valueOf(action);
        } catch (RuntimeException exception) {
            throw new IncorrectStatusException(String.format("State is incorrect: %s", action));
        }
    }
}
