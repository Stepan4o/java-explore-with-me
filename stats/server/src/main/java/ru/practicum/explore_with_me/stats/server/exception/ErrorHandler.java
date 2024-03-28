package ru.practicum.explore_with_me.stats.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({
            InvalidDateTimeException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final Exception exception) {
        if (exception instanceof MethodArgumentNotValidException) {
            FieldError error = Objects.requireNonNull(((MethodArgumentNotValidException) exception).getFieldError());
            log.warn("Field -> '{}' : '{}'", error.getField(), error.getDefaultMessage());
            return new ErrorResponse(
                    String.format("Field -> '%s' : '%s'", error.getField(), error.getDefaultMessage())
            );
        } else if (exception instanceof InvalidDateTimeException) {
            log.warn(exception.getMessage());
            return new ErrorResponse(exception.getMessage());
        } else {
            return new ErrorResponse("exception.getMessage()");
        }
    }
}
