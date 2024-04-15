package ru.practicum.explore_with_me.request.model;

import ru.practicum.explore_with_me.exception.IncorrectStatusException;

public enum RequestStatus {
    PENDING, CONFIRMED, REJECTED, CANCELED;

    public static RequestStatus convertStatus(String status) {
        try {
            return RequestStatus.valueOf(status);
        } catch (RuntimeException exception) {
            throw new IncorrectStatusException(String.format("State is incorrect: %s", status));
        }
    }

}
