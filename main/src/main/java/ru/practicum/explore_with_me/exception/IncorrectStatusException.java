package ru.practicum.explore_with_me.exception;

import lombok.Getter;

@Getter
public class IncorrectStatusException extends RuntimeException {
    private final String message;

    public IncorrectStatusException(String message) {
        this.message = message;
    }
}
