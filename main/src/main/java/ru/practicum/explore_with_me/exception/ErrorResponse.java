package ru.practicum.explore_with_me.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

import static ru.practicum.explore_with_me.stats.dto.consts.Constants.TIME_PATTERN;

@AllArgsConstructor
@Value
@Builder
public class ErrorResponse {
    String status;
    String reason;
    String message;
    @JsonFormat(pattern = TIME_PATTERN)
    LocalDateTime timestamp;
}
