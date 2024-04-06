package ru.practicum.explore_with_me.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    // TODO требуется суровый рефакторинг

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidEmail(
            final DataIntegrityViolationException exception
    ) {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Integrity constraint has been violated.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidEmail(
            final ConflictException exception
    ) {
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Integrity constraint has been violated.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(
            final NotFoundException exception
    ) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason("The required object was not found.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidActualTimeException(
            final InvalidEventDateException exception
    ) {
        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase())
                .reason("For the requested operation the conditions are not met.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException exception) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
