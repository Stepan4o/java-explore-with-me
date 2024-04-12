package ru.practicum.explore_with_me.exception;

import lombok.Getter;

@Getter
public class IncorrectDateTime extends RuntimeException {
    private final String message;

    public IncorrectDateTime(String message) {
        this.message = message;
    }
}
