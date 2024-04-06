package ru.practicum.explore_with_me.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final String message;

    public ConflictException(String message) {
        this.message = message;
    }
}
