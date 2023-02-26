package ru.practicum.shareit.exceptions;

public class ValidationFailException extends RuntimeException {
    public ValidationFailException(String message) {
        super(message);
    }
}
