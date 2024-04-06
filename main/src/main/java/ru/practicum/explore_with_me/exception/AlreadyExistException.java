package ru.practicum.explore_with_me.exception;

import lombok.Getter;

@Getter
public class AlreadyExistException extends RuntimeException {
    private final String message;

    public AlreadyExistException(String message) {
        this.message = message;
    }
}
