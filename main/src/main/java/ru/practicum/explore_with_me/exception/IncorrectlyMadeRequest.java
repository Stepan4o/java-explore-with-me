package ru.practicum.explore_with_me.exception;

import lombok.Getter;

@Getter
public class IncorrectlyMadeRequest extends RuntimeException {
    private final String message;
    public IncorrectlyMadeRequest(String message) {
        this.message = message;
    }
}
