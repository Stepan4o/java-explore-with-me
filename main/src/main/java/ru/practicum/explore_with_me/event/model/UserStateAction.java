package ru.practicum.explore_with_me.event.model;

import ru.practicum.explore_with_me.exception.IncorrectStatusException;

public enum UserStateAction {
    SEND_TO_REVIEW, CANCEL_REVIEW;

    public static UserStateAction toEnum(String action) {
        try {
            return UserStateAction.valueOf(action);
        } catch (RuntimeException exception) {
            throw new IncorrectStatusException(String.format("State is incorrect: %s", action));
        }
    }
}
