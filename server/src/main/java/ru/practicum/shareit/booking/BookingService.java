package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDto add(Long userId, AddBookingDto bookingDto);

    BookingDto consider(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllBookerBookings(Long bookerId, String state, Integer from, Optional<Integer> size);

    List<BookingDto> getAllOwnerBookings(Long ownerId, String state, Integer from, Optional<Integer> size);
}
