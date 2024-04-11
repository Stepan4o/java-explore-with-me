package ru.practicum.explore_with_me.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@UtilityClass
public class TimeFormatter {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);

    public LocalDateTime parseToTime(String time) {
        try {
            return LocalDateTime.parse(time, timeFormatter);
        } catch (RuntimeException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
