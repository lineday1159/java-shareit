package ru.practicum.shareit.exception;

public class DataErrorException extends RuntimeException {
    public DataErrorException(final String message) {
        super(message);
    }
}