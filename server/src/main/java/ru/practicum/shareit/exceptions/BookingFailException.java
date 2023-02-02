package ru.practicum.shareit.exceptions;

public class BookingFailException extends RuntimeException {
    public BookingFailException(String message) {
        super(message);
    }
}
