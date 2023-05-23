package ru.practicum.shareit.Exception;

public class ValidationException extends RuntimeException {
    public ValidationException(final String message) {
        super(message);
    }
}