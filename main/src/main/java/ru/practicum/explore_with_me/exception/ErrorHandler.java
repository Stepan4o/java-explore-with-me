package ru.practicum.explore_with_me.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConflictException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInvalidEmail(
            final RuntimeException exception
    ) {
        log.error(exception.getMessage());
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
        log.error(exception.getMessage());
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason("The required object was not found.")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            IncorrectStatusException.class,
            IncorrectDateTime.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final Exception exception) {
        String message;
        String status = HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase();
        String reason = "Incorrectly made request.";

        if (exception instanceof MissingServletRequestParameterException) {
            String param = ((MissingServletRequestParameterException) exception).getParameterName();
            log.error("Incorrect input parameter: '{}'", param);
            message = String.format("Incorrect input parameter: %s", param);
        } else if (exception instanceof MethodArgumentNotValidException) {
            FieldError error = Objects.requireNonNull(((MethodArgumentNotValidException) exception).getFieldError());
            log.error("Invalid input '{}' -> {}", error.getField(), error.getDefaultMessage());
            message = String.format("Incorrect input data %s -> %s", error.getField(), error.getDefaultMessage());
        } else {
            log.error(exception.getMessage());
            message = exception.getMessage();
        }
        return ErrorResponse.builder()
                .status(status)
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
